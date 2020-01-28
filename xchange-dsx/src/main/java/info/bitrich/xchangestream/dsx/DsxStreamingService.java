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
        if (message.has(JSON_EVENT)) {
            String eventName = message.get(JSON_EVENT).asText();
            DsxEventType dsxEvent = DsxEventType.getEvent(eventName);
            if (dsxEvent != null) {
                switch (dsxEvent) {
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

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        DsxWebSocketSubscriptionMessage message = channelInfo.getChannel().subscriptionMessageCreator.apply(channelInfo, channelInfo.getChannel().subscriptionEvent, args);
        requests.put(message.getRid(), channelInfo);
        LOG.info("Subscription message for channel {} has been generated. RequestId {}", channelName, message.getRid());
        return objectMapper.writeValueAsString(message);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        DsxChannelInfo channelInfo = DsxSubscriptionHelper.parseChannelName(channelName);
        DsxWebSocketSubscriptionMessage message = DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, channelInfo.getChannel().unsubscriptionEvent);
        requests.put(message.getRid(), channelInfo);
        LOG.info("Unsubscription message for channel {} has been generated. RequestId {}", channelName, message.getRid());
        return objectMapper.writeValueAsString(message);
    }
}
