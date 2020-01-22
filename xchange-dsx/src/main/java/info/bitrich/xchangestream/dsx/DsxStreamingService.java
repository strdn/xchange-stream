package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static info.bitrich.xchangestream.dsx.DsxSubscriptionHelper.CHANNEL_DELIMITER;

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

    private final Map<Long, DsxChannelInfo> requests = new HashMap<>();

    public DsxStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
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
        String eventName = message.get(JSON_EVENT).asText();
        DsxEventType dsxEvent = DsxEventType.getEvent(eventName);
        if (dsxEvent != null) {
            switch (dsxEvent) {
                case snapshot:
                case update:
                    LOG.debug("message received: {}", message.toString());
                    super.handleMessage(message);
                    break;
                case heartbeat:
                    LOG.debug("heartbeat has been received");
                    break;
                case subscribed:
                case unsubscribed:
                    if (message.has(JSON_REQUEST_ID)) {
                        long requestId = message.get(JSON_REQUEST_ID).asLong();
                        DsxChannelInfo channelInfo = requests.remove(requestId);
                        if (channelInfo != null) {
                            LOG.info("{} {} request has been successfully completed", channelInfo, dsxEvent.sourceEvent);
                            return;
                        }
                    }
                    LOG.info("unknown {} request has been successfully completed", dsxEvent.sourceEvent);
                    return;
                case unsubscriptionFailed:
                case subscriptionFailed:
                    if (message.has(JSON_REQUEST_ID)) {
                        long requestId = message.get(JSON_REQUEST_ID).asLong();
                        DsxChannelInfo channelInfo = requests.remove(requestId);
                        if (channelInfo != null) {
                            LOG.info("{} {} request has been failed", channelInfo, dsxEvent.sourceEvent);
                            return;
                        }
                    }
                    //TODO: extract correct error from message to log
                    LOG.info("unknown {} request has been failed", dsxEvent.sourceEvent);
                    break;
                default:
                    LOG.debug("unhandled message received: {}", message.toString());
                    break;
            }
        }
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        DsxWebSocketSubscriptionMessage message = channelInfo.getChannel().subscriptionMessageCreator.apply(channelInfo, args);
        requests.put(message.getRid(), channelInfo);
        LOG.info("{} subscription message has been generated {}", channelInfo, message.getRid());
        return objectMapper.writeValueAsString(message);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        DsxWebSocketSubscriptionMessage message = DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, DsxEventType.unsubscribe);
        requests.put(message.getRid(), channelInfo);
        LOG.info("{} unsubscription message has been generated {}", channelInfo, message.getRid());
        return objectMapper.writeValueAsString(message);
    }
}
