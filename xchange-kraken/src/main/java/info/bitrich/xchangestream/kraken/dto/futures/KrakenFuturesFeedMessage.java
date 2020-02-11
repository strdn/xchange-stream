package info.bitrich.xchangestream.kraken.dto.futures;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.kraken.dto.enums.KrakenEventType;
import info.bitrich.xchangestream.kraken.dto.enums.futures.KrakenFuturesFeed;

import java.beans.ConstructorProperties;

/**
 * @author pchertalev
 */
public class KrakenFuturesFeedMessage extends KrakenFuturesEventMessage {

    @JsonProperty("feed")
    private final KrakenFuturesFeed feed;

    @ConstructorProperties({"event", "feed"})
    public KrakenFuturesFeedMessage(KrakenEventType event, KrakenFuturesFeed feed) {
        super(event);
        this.feed = feed;
    }

    public KrakenFuturesFeed getFeed() {
        return feed;
    }
}
