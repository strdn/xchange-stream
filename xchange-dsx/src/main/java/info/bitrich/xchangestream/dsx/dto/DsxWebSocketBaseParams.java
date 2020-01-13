package info.bitrich.xchangestream.dsx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pavel Chertalev
 */
public class DsxWebSocketBaseParams {

    protected final String symbol;

    public DsxWebSocketBaseParams(@JsonProperty("symbol") String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}
