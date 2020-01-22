package info.bitrich.xchangestream.dsx.dto.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author rimalon
 */
public enum DsxEventType {
    ping,
    pong,
    heartbeat,
    subscribe,
    subscribed,
    subscriptionFailed,
    snapshot,
    update,
    unsubscribe;

    public static DsxEventType getEvent(String event) {
        return Arrays.stream(DsxEventType.values())
                .filter(e -> StringUtils.equalsIgnoreCase(event, e.name()))
                .findFirst()
                .orElse(null);
    }
}
