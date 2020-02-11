package info.bitrich.xchangestream.kraken.dto.futures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bitrich.xchangestream.kraken.dto.enums.KrakenEventType;
import info.bitrich.xchangestream.kraken.dto.enums.futures.KrakenFuturesFeed;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * @author pchertalev
 */
public class KrakenFuturesSubscriptionMessage extends KrakenFuturesEventMessage {

    @JsonProperty
    private final KrakenFuturesFeed feed;

    @JsonProperty(value = "product_ids", required = true)
    private final List<String> productIds;

    @JsonCreator
    @ConstructorProperties({"event", "product_ids"})
    public KrakenFuturesSubscriptionMessage(@JsonProperty("event") KrakenEventType event, @JsonProperty("feed") KrakenFuturesFeed feed,
                                            @JsonProperty("product_ids") List<String> productIds) {
        super(event);
        this.feed = feed;
        this.productIds = productIds;
    }

    public KrakenFuturesFeed getFeed() {
        return feed;
    }

    public List<String> getProductIds() {
        return productIds;
    }
}
