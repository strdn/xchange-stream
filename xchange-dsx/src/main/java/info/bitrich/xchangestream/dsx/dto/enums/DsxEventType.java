package info.bitrich.xchangestream.dsx.dto.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author rimalon
 */
public enum DsxEventType {
    heartbeat,

    subscribe,
    subscribed(subscribe),
    unsubscribe,
    unsubscribed(unsubscribe),
    subscriptionFailed(subscribe),
    unsubscriptionFailed(unsubscribe),

    snapshot,
    update;

    public final DsxEventType sourceEvent;

    DsxEventType() {
        this(null);
    }

    DsxEventType(DsxEventType sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public static DsxEventType getEvent(String event) {
        return Arrays.stream(DsxEventType.values())
                .filter(e -> StringUtils.equalsIgnoreCase(event, e.name()))
                .findFirst()
                .orElse(null);
    }
}
