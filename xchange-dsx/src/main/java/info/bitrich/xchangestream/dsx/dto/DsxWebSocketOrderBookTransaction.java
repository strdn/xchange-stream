package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pavel Chertalev
 */
public class DsxWebSocketOrderBookTransaction extends DsxWebSocketBaseTransaction {

    private static final String ORDERBOOK_METHOD_CHANNEL = "updateOrderbook";
    private DsxWebSocketOrderBookParams params;

    public DsxWebSocketOrderBookTransaction(@JsonProperty("method") String method, @JsonProperty("params") DsxWebSocketOrderBookParams params) {
        super(method);
        this.params = params;
    }

    public DsxWebSocketOrderBookParams getParams() {
        return params;
    }

    public DsxWebSocketOrderBook toDsxOrderBook(DsxWebSocketOrderBook orderbook) {
        if (channel.equals(ORDERBOOK_METHOD_CHANNEL)) {
            orderbook.updateOrderBook(this);
            return orderbook;
        }
        return new DsxWebSocketOrderBook(this);
    }
}
