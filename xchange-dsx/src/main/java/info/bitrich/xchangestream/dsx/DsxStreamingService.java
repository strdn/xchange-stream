package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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

    private final Map<Integer, Pair <String, String>> requests = new HashMap<>();


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
            int requestId = message.get(JSON_ID).asInt();
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
                    LOG.debug("Message recieved: {}", message.asText());
                    super.handleMessage(message);
                    break;
                case subscriptionFailed:
                    LOG.error("Subscription failed. Error message={}", message.asText());
                    break;
                default:
                    LOG.debug("Message recieved: {}", message.asText());
                    break;
            }
        }
    }


    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        DsxWebSocketSubscriptionMessage subscribeMessage = generateSubscribeMessage(channelName, DsxEventType.subscribe);
        requests.put(subscribeMessage.getRid(), ImmutablePair.of(channelName, subscribeMessage.getChannel()));
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        DsxWebSocketSubscriptionMessage subscribeMessage = generateSubscribeMessage(channelName, DsxEventType.subscribed);
        requests.put(subscribeMessage.getRid(), ImmutablePair.of(channelName, subscribeMessage.getChannel()));
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    private DsxWebSocketSubscriptionMessage generateSubscribeMessage(String channelName, DsxEventType eventType) throws IOException {
        String[] chanelInfo = channelName.split("-");
        if (chanelInfo.length != 3) {
            throw new IOException(eventType + " message: channel name must has format <channelName>-<Symbol> (e.g orderbook-ETHBTC)");
        }
        String channel = chanelInfo[0];
        String instrument = chanelInfo[1].toLowerCase();
        int requestId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        return new DsxWebSocketSubscriptionMessage(requestId, eventType, channel, DsxModeType.valueOf(chanelInfo[2]), instrument, DEFAULT_LIMIT_VALUE);
    }
}
