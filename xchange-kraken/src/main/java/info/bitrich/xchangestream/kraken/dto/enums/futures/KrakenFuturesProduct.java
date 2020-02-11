package info.bitrich.xchangestream.kraken.dto.enums.futures;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public enum KrakenFuturesProduct {

    FI("Inverse Futures", true),
    FV("Vanilla Futures", true),
    PI("Perpetual Inverse Futures", false),
    PV("Perpetual Vanilla Futures", false),
    IN("Real Time Index", false),
    RR("Reference Rate", false);

    public final static DateTimeFormatter MATURITY_DATE_FORMAT = DateTimeFormatter.ofPattern("YYMMdd");
    private final static String CHANNEL_DELIMITER_CHAR = "_";

    public final String codeName;
    public final boolean supportMaturityDate;

    KrakenFuturesProduct(String codeName, boolean supportMaturityDate) {
        this.codeName = codeName;
        this.supportMaturityDate = supportMaturityDate;
    }

    /**
     * @param maturityDate 16:00 UTC
     * @return channelName
     */
    public String getChannelName(String pair, LocalDate maturityDate) {
        if (maturityDate != null && supportMaturityDate) {
            return getChannelName(pair) + MATURITY_DATE_FORMAT.format(maturityDate);
        }
        return getChannelName(pair);
    }

    public String getChannelName(String pair) {
        return this.name() + CHANNEL_DELIMITER_CHAR + StringUtils.upperCase(pair);
    }

}
