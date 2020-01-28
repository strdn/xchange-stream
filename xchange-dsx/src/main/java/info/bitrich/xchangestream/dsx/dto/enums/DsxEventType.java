package info.bitrich.xchangestream.dsx.dto.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static info.bitrich.xchangestream.dsx.dto.enums.DsxChannel.book;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxChannel.ticker;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxChannel.trade;

/**
 * @author rimalon
 */
public enum DsxEventType {
    heartbeat,

    subscribeBook,
    subscribeTrade,
    subscribeTicker,

    subscribed,
    subscriptionFailed,

    unsubscribeBook,
    unsubscribeTrade,
    unsubscribeTicker,

    unsubscribed,
    unsubscriptionFailed,

    snapshot,
    update;

    public static DsxEventType getEvent(String event) {
        return Arrays.stream(DsxEventType.values())
                .filter(e -> StringUtils.equalsIgnoreCase(event, e.name()))
                .findFirst()
                .orElse(null);
    }
}
