package info.bitrich.xchangestream.dsx.dto.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author rimalon
 */
public enum DsxEventType {
    heartbeat,

    subscribe,
    subscribed,
    subscriptionFailed,

    unsubscribeBook,
    unsubscribeTrade,
    unsubscribeTicker,

    unsubscribe,
    unsubscribed,
    unsubscriptionFailed,

    authorize,
    snapshot,
    update;

    public static DsxEventType getEvent(String event) {
        return Arrays.stream(DsxEventType.values())
                .filter(e -> StringUtils.equalsIgnoreCase(event, e.name()))
                .findFirst()
                .orElse(null);
    }
}
