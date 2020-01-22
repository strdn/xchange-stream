package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannelsType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.dsx.dto.messages.SubscriptionMessageFactory;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rimalon
 */
public class DsxStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingService.class);
    private static final String JSON_ID = "rid";
    private static final String JSON_EVENT = "event";
    private static final String JSON_CHANNEL = "channel";
    private static final String JSON_INSTRUMENT = "instrument";
    private static final String JSON_MODE = "mode";
    private static final Integer DEFAULT_LIMIT_VALUE = 100;

    private final Map<Long, Pair <String, DsxChannelsType>> requests = new HashMap<>();


    public DsxStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) {
        if (message.has(JSON_ID)) {
            long requestId = message.get(JSON_ID).asLong();
            if (requests.containsKey(requestId)) {
                return requests.get(requestId).getKey();
            }
        }
        if (message.has(JSON_CHANNEL)) {
            String channelName = message.get(JSON_CHANNEL).asText();
            if (message.has(JSON_INSTRUMENT)) {
                String instrumentName = message.get(JSON_INSTRUMENT).asText();
                if (message.has(JSON_MODE)){
                    String modeName = message.get(JSON_MODE).asText();
                    return channelName + "-" + instrumentName + "-" + modeName;
                }
                return channelName + "-" + instrumentName;
            }
            return channelName;
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
                    LOG.debug("Message received: {}", message.toString());
                    super.handleMessage(message);
                    break;
                case subscriptionFailed:
                    LOG.error("Subscription failed. Error message={}", message.toString());
                    break;
                default:
                    LOG.debug("Message received: {}", message.toString());
                    break;
            }
        }
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        DsxWebSocketSubscriptionMessage message = DsxChannelsType.getChannelFromChannelName(channelName).getSubscriptionMessageCreator().apply(channelName, args);
        requests.put(message.getRid(), ImmutablePair.of(channelName, message.getChannel()));
        return objectMapper.writeValueAsString(message);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        DsxWebSocketSubscriptionMessage message = SubscriptionMessageFactory.createBasicMessage(channelName, DsxEventType.unsubscribe);
        requests.put(message.getRid(), ImmutablePair.of(channelName, message.getChannel()));
        return objectMapper.writeValueAsString(message);
    }
}
