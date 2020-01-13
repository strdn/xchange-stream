package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pavel Chertalev
 */
public class DsxWebSocketSubscriptionMessage {

    private final String channel;
    private final String action;
    private final String pair;
    private final int id;

    public DsxWebSocketSubscriptionMessage(@JsonProperty("id") int id, @JsonProperty("channel") String channel, @JsonProperty("pair") String pair, @JsonProperty("action") String action) {
        this.id = id;
        this.channel = channel;
        this.action = action;
        this.pair = pair;
    }

    public String getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public String getPair() {
        return pair;
    }

    public String getAction() {
        return action;
    }
}
