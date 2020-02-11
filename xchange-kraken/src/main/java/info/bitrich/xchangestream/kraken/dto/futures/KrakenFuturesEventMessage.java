package info.bitrich.xchangestream.kraken.dto.futures;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.kraken.dto.enums.KrakenEventType;

import java.beans.ConstructorProperties;

/**
 * @author pchertalev
 */
public class KrakenFuturesEventMessage {

    @JsonProperty(value = "event", required = true)
    private final KrakenEventType event;

    private String message;

    @ConstructorProperties("event")
    public KrakenFuturesEventMessage(KrakenEventType event) {
        this.event = event;
    }

    public KrakenEventType getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }
}
