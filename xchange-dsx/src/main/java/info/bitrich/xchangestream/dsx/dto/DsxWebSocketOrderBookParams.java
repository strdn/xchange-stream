package info.bitrich.xchangestream.dsx.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderLimit;

/**
 * Created by Pavel Chertalev
 */
public class DsxWebSocketOrderBookParams extends DsxWebSocketBaseParams {

    private final HitbtcOrderLimit[] ask;
    private final HitbtcOrderLimit[] bid;
    private final long sequence;

    public DsxWebSocketOrderBookParams(@JsonProperty("property") String symbol, @JsonProperty("sequence") long sequence,
                                       @JsonProperty("ask") HitbtcOrderLimit[] ask, @JsonProperty("bid") HitbtcOrderLimit[] bid) {
        super(symbol);
        this.ask = ask;
        this.bid = bid;
        this.sequence = sequence;
    }

    public HitbtcOrderLimit[] getAsk() {
        return ask;
    }

    public HitbtcOrderLimit[] getBid() {
        return bid;
    }

    public long getSequence() {
        return sequence;
    }

}
