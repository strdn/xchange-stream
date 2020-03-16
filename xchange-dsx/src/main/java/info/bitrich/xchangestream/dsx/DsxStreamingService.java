package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxAuthMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxAuthBalanceMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxAuthOrderMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.dsx.dto.trade.ClientDeal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.SynchronizedValueFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static info.bitrich.xchangestream.dsx.DsxStreamingMessageAdapter.adaptBalance;
import static info.bitrich.xchangestream.dsx.DsxStreamingMessageAdapter.adaptBalances;
import static info.bitrich.xchangestream.dsx.DsxSubscriptionHelper.CHANNEL_DELIMITER;
import static org.knowm.xchange.service.BaseParamsDigest.HMAC_SHA_512;

/**
 * @author rimalon
 */
public class DsxStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingService.class);
    private static final String JSON_REQUEST_ID = "rid";
    private static final String JSON_EVENT = "event";
    private static final String JSON_CHANNEL = "channel";
    private static final String JSON_INSTRUMENT = "instrument";
    private static final String JSON_INSTRUMENT_TYPE = "instrumentType";
    private static final String JSON_ERROR_CODE = "errorCode";

    private final Map<Long, DsxChannelInfo> requests = new ConcurrentHashMap<>();
    private final Map<String, Long> lastTradeIds = new ConcurrentHashMap<>();

    private final PublishSubject<ClientDeal> subjectTrade = PublishSubject.create();
    private final PublishSubject<DsxAuthBalanceMessage> subjectBalance = PublishSubject.create();
    private final PublishSubject<DsxAuthOrderMessage> subjectOrder = PublishSubject.create();

    private final SynchronizedValueFactory<Long> nonceFactory;

    private String apiKey;
    private String apiSecret;
    private boolean isAuthorized = false;

    public Observable<ClientDeal> getAuthenticatedTrades() {
        return subjectTrade.share();
    }

    public Observable<DsxAuthBalanceMessage> getAuthenticatedBalances() {
        return subjectBalance.share();
    }

    public Observable<DsxAuthOrderMessage> getAuthenticatedOrders() {
        return subjectOrder.share();
    }

    public DsxStreamingService(String apiUrl, SynchronizedValueFactory<Long> nonceFactory) {
        super(apiUrl, Integer.MAX_VALUE);
        this.nonceFactory = nonceFactory;
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
    }

    public void setLastTradeId(String channelName, long lastTradeId) {
        this.lastTradeIds.put(channelName, lastTradeId);
    }

    void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    boolean isAuthDataProvided() {
        return StringUtils.isNotEmpty(apiKey) && StringUtils.isNotEmpty(apiSecret);
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) {
        if (message.has(JSON_CHANNEL) && message.has(JSON_INSTRUMENT) && message.has(JSON_INSTRUMENT_TYPE)) {
            return message.get(JSON_CHANNEL).asText() + CHANNEL_DELIMITER +
                    message.get(JSON_INSTRUMENT).asText() + CHANNEL_DELIMITER +
                    message.get(JSON_INSTRUMENT_TYPE).asText();
        }
        return null;
    }

    @Override
    protected void handleMessage(JsonNode message) {
        if (message.has(JSON_EVENT)) {
            String eventName = message.get(JSON_EVENT).asText();
            DsxEventType dsxEvent = DsxEventType.getEvent(eventName);
            if (dsxEvent != null) {
                switch (dsxEvent) {
                    case AUTHORIZE:
                        LOG.debug("Message received: {}", message.toString());
                        processAuthorizeResponse(message);
                        break;
                    case SNAPSHOT:
                        LOG.debug("Snapshot message received: {}", message.toString());
                        break;
                    case UPDATE:
                        LOG.debug("Message received: {}", message.toString());
                        super.handleMessage(message);
                        break;
                    case HEARTBEAT:
                        LOG.debug("Heartbeat has been received");
                        break;
                    case SUBSCRIBED:
                    case UNSUBSCRIBED:
                    case UNSUBSCRIPTION_FAILED:
                    case SUBSCRIPTION_FAILED:
                        processRequestId(message, dsxEvent);
                        break;
                    default:
                }
            } else {
                String channelName = message.get(JSON_CHANNEL).asText();
                DsxChannel channel = DsxChannel.getChannel(channelName);
                if (DsxChannel.AUTHORIZED == channel) {
                    processAuthorizedMessage(message);
                }
            }
        } else {
            LOG.debug("Unknown message received: {}", message.toString());
        }
    }

    private void processRequestId(JsonNode message, DsxEventType event) {
        if (message.has(JSON_REQUEST_ID) && message.get(JSON_REQUEST_ID).isLong()) {
            Long requestId = message.get(JSON_REQUEST_ID).asLong();
            DsxChannelInfo channelInfo = requests.remove(requestId);
            if (channelInfo != null) {
                LOG.info("Request for channel '{}' has been processed. Result: '{}'", channelInfo, event);
                return;
            }
        }
        LOG.warn("Unknown request has been successfully processed. Result: '{}'. Message: {}", event, message.toString());
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        channelInfo.setLastTradeId(lastTradeIds.computeIfAbsent(channelName, chName -> 0L));
        DsxWebSocketSubscriptionMessage message = channelInfo.getChannel().subscriptionMessageCreator.apply(channelInfo, DsxEventType.SUBSCRIBE, args);
        requests.put(message.getRid(), channelInfo);
        LOG.info("Subscription message for channel {} has been generated. RequestId {}", channelName, message.getRid());
        return objectMapper.writeValueAsString(message);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        DsxWebSocketSubscriptionMessage message = DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, DsxEventType.UNSUBSCRIBE);
        requests.put(message.getRid(), channelInfo);
        LOG.info("Unsubscription message for channel {} has been generated. RequestId {}", channelName, message.getRid());
        return objectMapper.writeValueAsString(message);
    }

    private enum AuthorizedChannelDataType {
        BALANCE,
        ORDER,
        TRADE;

        public static AuthorizedChannelDataType getEvent(String type) {
            return Arrays.stream(AuthorizedChannelDataType.values())
                    .filter(e -> StringUtils.equalsIgnoreCase(type, e.name()))
                    .findFirst()
                    .orElse(null);
        }
    }

    private void processAuthorizedMessage(JsonNode message) {
        AuthorizedChannelDataType infoType = AuthorizedChannelDataType.getEvent(message.get("infoType").asText());
        JsonNode payload = message.get("rawPayload");

        switch (infoType) {
            case BALANCE:
                processAuthorizedMessageBalance(payload);
                break;
            case ORDER:
                processAuthorizedMessageOrder(payload);
                break;
            case TRADE:
                processAuthorizedMessageTrade(payload);
                break;
            default:
                break;
        }
    }

    private void processAuthorizedMessageBalance(JsonNode rawPayload) {
        String eventName = rawPayload.get(JSON_EVENT).asText();
        DsxEventType dsxEvent = DsxEventType.getEvent(eventName);
        if (dsxEvent != null) {
            switch (dsxEvent) {
                case SNAPSHOT:
                    adaptBalances(rawPayload.get("balances")).forEach(subjectBalance::onNext);
                    break;
                case UPDATE:
                    DsxAuthBalanceMessage balance = adaptBalance(rawPayload.get("balances"));
                    if (balance != null)
                        subjectBalance.onNext(balance);
                    break;
                default:
                    break;
            }
        }
    }

    private void processAuthorizedMessageOrder(JsonNode rawPayload) {
        String eventName = rawPayload.get(JSON_EVENT).asText();
        DsxEventType dsxEvent = DsxEventType.getEvent(eventName);
        if (dsxEvent != null) {
            switch (dsxEvent) {
                case SNAPSHOT:
                    DsxStreamingMessageAdapter.adaptOrders(rawPayload.get("orders")).forEach(subjectOrder::onNext);
                    break;
                case UPDATE:
                    DsxAuthOrderMessage order = DsxStreamingMessageAdapter.adaptOrder(rawPayload.get("orders"));
                    if (order != null)
                        subjectOrder.onNext(order);
                    break;
                default:
                    break;
            }
        }
    }

    private void processAuthorizedMessageTrade(JsonNode rawPayload) {
        String eventName = rawPayload.get(JSON_EVENT).asText();
        DsxEventType dsxEvent = DsxEventType.getEvent(eventName);
        if (dsxEvent != null) {
            switch (dsxEvent) {
                case SNAPSHOT:
                    DsxStreamingMessageAdapter.adaptTrades(rawPayload.get("trades")).forEach(subjectTrade::onNext);
                    break;
                case UPDATE:
                    ClientDeal trade = DsxStreamingMessageAdapter.adaptTrade(rawPayload.get("trades"));
                    if (trade != null)
                        subjectTrade.onNext(trade);
                    break;
                default:
                    break;
            }
        }
    }

    private void processAuthorizeResponse(JsonNode message) {
        LOG.debug("Process authorize msg {}", message);
        if (message.get(JSON_ERROR_CODE) == null) {
            isAuthorized = true;
        }
    }

    public void authorize() {
        long nonce = nonceFactory.createValue();
        String payload = "AUTH" + nonce;
        String signature;
        try {
            Mac macEncoder = Mac.getInstance(HMAC_SHA_512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_512);
            macEncoder.init(secretKeySpec);
            byte[] result = macEncoder.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            signature = DatatypeConverter.printHexBinary(result);

            DsxAuthMessage message = DsxSubscriptionHelper.createAuthMessage(0L, apiKey, String.valueOf(nonce), signature.toLowerCase());
            sendObjectMessage(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOG.error("auth. Sign failed error={}", e.getMessage());
        }
    }

}
