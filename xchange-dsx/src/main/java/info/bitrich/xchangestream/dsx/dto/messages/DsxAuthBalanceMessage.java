package info.bitrich.xchangestream.dsx.dto.messages;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

public class DsxAuthBalanceMessage {

    private final Integer faId;
    private final String currency;
    private final BigDecimal total;
    private final BigDecimal held;
    private final BigDecimal locked;
    private final BigDecimal available;


    @ConstructorProperties({"faId", "currency", "total", "held", "locked", "available"})
    public DsxAuthBalanceMessage(Integer faId, String currency, BigDecimal total, BigDecimal held, BigDecimal locked, BigDecimal available) {
        this.faId = faId;
        this.currency = currency;
        this.total = total;
        this.held = held;
        this.locked = locked;
        this.available = available;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getHeld() {
        return held;
    }

    public BigDecimal getLocked() {
        return locked;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public Integer getFaId() {
        return faId;
    }

    @Override
    public String toString() {
        return "DsxBalanceMessage{" +
                "faId='" + faId + '\'' +
                ", currency='" + currency + '\'' +
                ", total=" + total +
                ", held=" + held +
                ", locked=" + locked +
                ", available=" + available +
                '}';
    }
}
