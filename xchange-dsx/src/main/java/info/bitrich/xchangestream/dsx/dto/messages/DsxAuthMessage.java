package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;

public class DsxAuthMessage extends DsxEventMessage {

    private final String apiKey;
    private final String authNonce;
    private final String authSig;

    @JsonCreator
    public DsxAuthMessage(@JsonProperty("event") DsxEventType event,
                          @JsonProperty("apiKey") String apiKey,
                          @JsonProperty("authNonce") String authNonce,
                          @JsonProperty("authSig") String authSig) {
        super(event);
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
}
