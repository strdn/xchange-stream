package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxAuthMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTradeMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final PublishSubject<DsxTradeMessage> subjectTrade = PublishSubject.create();

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
                    case authorize:
                        LOG.debug("Message received: {}", message.toString());
                        processAuthorizeResponse(message);
                        break;
                    case snapshot:
                    case update:
                        LOG.debug("Message received: {}", message.toString());
                        super.handleMessage(message);
                        break;
                    case heartbeat:
                        LOG.debug("Heartbeat has been received");
                        break;
                    case subscribed:
                    case unsubscribed:
                    case unsubscriptionFailed:
                    case subscriptionFailed:
                        processRequestId(message, dsxEvent);
                        break;
                    default:
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

    private void processAuthorizeResponse(JsonNode message) {
        LOG.info("Process authorize msg {}", message);
        if (message.get(JSON_ERROR_CODE) == null) {
            isAuthorized = true;
        }
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        channelInfo.setLastTradeId(lastTradeIds.computeIfAbsent(channelName, chName -> 0L));
        DsxWebSocketSubscriptionMessage message = channelInfo.getChannel().subscriptionMessageCreator.apply(channelInfo, DsxEventType.subscribe, args);
        requests.put(message.getRid(), channelInfo);
        LOG.info("Subscription message for channel {} has been generated. RequestId {}", channelName, message.getRid());
        return objectMapper.writeValueAsString(message);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        DsxWebSocketSubscriptionMessage message = DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, DsxEventType.unsubscribe);
        requests.put(message.getRid(), channelInfo);
        LOG.info("Unsubscription message for channel {} has been generated. RequestId {}", channelName, message.getRid());
        return objectMapper.writeValueAsString(message);
    }

    public Observable<DsxTradeMessage> getAuthenticatedTrades() {
        return subjectTrade.share();
    }

    boolean isAuthDataProvided() {
        return StringUtils.isNotEmpty(apiKey) && StringUtils.isNotEmpty(apiSecret);
    }

    private String apiKey;
    private String apiSecret;

    private boolean isAuthorized = false;

    void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    private final SynchronizedValueFactory<Long> nonceFactory;

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

            DsxAuthMessage message = new DsxAuthMessage(
                    DsxEventType.authorize, apiKey, String.valueOf(nonce), signature.toLowerCase());
            sendObjectMessage(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOG.error("auth. Sign failed error={}", e.getMessage());
        }
    }

}
