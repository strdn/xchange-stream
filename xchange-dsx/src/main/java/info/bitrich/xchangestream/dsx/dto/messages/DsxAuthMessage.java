package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

public class DsxAuthMessage extends DsxWebSocketSubscriptionMessage {

    private final String apiKey;
    private final String authNonce;
    private final String authSig;
    private final DsxChannel channel;
    private final long dealsFrom;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public DsxAuthMessage(@JsonProperty("rid") long rid,
                          @JsonProperty("event") DsxEventType event,
                          @JsonProperty("channel") DsxChannel channel,
                          @JsonProperty(value = "dealsFrom", defaultValue = "0L") long dealsFrom,
                          @JsonProperty("apiKey") String apiKey,
                          @JsonProperty("authNonce") String authNonce,
                          @JsonProperty("authSig") String authSig) {
        super(rid, event);
        this.channel = channel;
        this.dealsFrom = dealsFrom;
        this.apiKey = apiKey;
        this.authNonce = authNonce;
        this.authSig = authSig;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAuthNonce() {
        return authNonce;
    }

    public String getAuthSig() {
        return authSig;
    }

    public DsxChannel getChannel() {
        return channel;
    }

    public long getDealsFrom() {
        return dealsFrom;
    }
}
