package info.bitrich.xchangestream.dsx.dto.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author rimalon
 */
public enum DsxEventType {
    HEARTBEAT,

    SUBSCRIBE,
    SUBSCRIBED,
    SUBSCRIPTION_FAILED,

    UNSUBSCRIBE_BOOK,
    UNSUBSCRIBE_TRADE,
    UNSUBSCRIBE_TICKER,

    UNSUBSCRIBE,
    UNSUBSCRIBED,
    UNSUBSCRIPTION_FAILED,

    AUTHORIZE,
    SNAPSHOT,
    UPDATE;

    public static DsxEventType getEvent(String event) {
        return Arrays.stream(DsxEventType.values())
                .filter(e -> StringUtils.equalsIgnoreCase(event, e.name()))
                .findFirst()
                .orElse(null);
    }
}
