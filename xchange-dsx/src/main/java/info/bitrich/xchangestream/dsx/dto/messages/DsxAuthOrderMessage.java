package info.bitrich.xchangestream.dsx.dto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.dsx.dto.trade.DSXOrder;

import java.math.BigDecimal;

public class DsxAuthOrderMessage extends DSXOrder {

    private final Long number;

    public DsxAuthOrderMessage(
            @JsonProperty("number") Long number,
            @JsonProperty("pair") String pair,
            @JsonProperty("type") Type type,
            @JsonProperty("volume") BigDecimal volume,
            @JsonProperty("remainingVolume") BigDecimal remainingVolume,
            @JsonProperty("rate") BigDecimal rate,
            @JsonProperty("status") int status,
            @JsonProperty("orderType") OrderType orderType,
            @JsonProperty("timestampCreated") String timestampCreated) {
        super(pair, type, volume, remainingVolume, rate, status, orderType, timestampCreated);
        this.number = number;
    }

    public Long getNumber() {
        return number;
    }
}
