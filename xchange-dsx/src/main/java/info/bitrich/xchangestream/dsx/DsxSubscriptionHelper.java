package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketOrderbookSubscriptionMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketTradeSubscriptionMessage;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DsxSubscriptionHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DsxSubscriptionHelper.class);

    private final static int DEFAULT_LIMIT_VALUE = 100;
    private final static int DEFAULT_PREV_DEALS_COUNT_VALUE = 100;

    public final static String CHANNEL_DELIMITER = "-";

    public static DsxWebSocketOrderbookSubscriptionMessage createOrderbookSubscriptionMessage(DsxChannelInfo channelInfo, Object[] args) {
        return new DsxWebSocketOrderbookSubscriptionMessage(
                generateRequestId(),
                DsxEventType.subscribe,
                DsxChannel.book,
                channelInfo.getInstrumentType(),
                channelInfo.getInstrument(),
                getIndexedValue("order book depth limit", args, 1, Integer.class, DEFAULT_LIMIT_VALUE));
    }

    public static DsxWebSocketTradeSubscriptionMessage createTradeSubscriptionMessage(DsxChannelInfo channelInfo, Object[] args) {
        return new DsxWebSocketTradeSubscriptionMessage(
                generateRequestId(),
                DsxEventType.subscribe,
                DsxChannel.book,
                channelInfo.getInstrumentType(),
                channelInfo.getInstrument(),
                getIndexedValue("previous deal count", args, 1, Integer.class, DEFAULT_PREV_DEALS_COUNT_VALUE));
    }

    public static DsxWebSocketSubscriptionMessage createBaseSubscriptionMessage(DsxChannelInfo channelInfo, DsxEventType eventType) {
        return new DsxWebSocketSubscriptionMessage(
                generateRequestId(),
                eventType,
                channelInfo.getChannel(),
                channelInfo.getInstrumentType(),
                channelInfo.getInstrument()
        );
    }

    public static DsxChannelInfo parseChannelName(String channelName) {
        String[] channelNameParts = StringUtils.split(channelName, CHANNEL_DELIMITER);
        if (channelNameParts.length < 3) {
            throw new RuntimeException("Invalid channel name: " + channelName);
        }
        return new DsxChannelInfo(
                DsxChannel.valueOf(channelNameParts[0]),
                channelNameParts[1],
                DsxInstrumentType.valueOf(channelNameParts[2])
        );
    }

    public static String createChannelName(DsxChannel channels, CurrencyPair currencyPair, Object... args) {
        String instrument = currencyPair.base.toString() + currencyPair.counter.toString();
        DsxInstrumentType instrumentType = getIndexedValue("instrument type", args, 0, DsxInstrumentType.class, DsxInstrumentType.LIVE);
        return channels.name() + CHANNEL_DELIMITER + instrument.toLowerCase() + CHANNEL_DELIMITER + instrumentType;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getIndexedValue(String name, Object[] args, int index, Class<T> expectedClass, T defaultValue) {
        if (args.length >= index + 1 && expectedClass != null && expectedClass.isAssignableFrom(args[index].getClass())) {
            return (T) args[index];
        }
        LOG.warn("Parameter '{}' was not correctly specified, so the default value {} is used", name, defaultValue);
        return defaultValue;
    }

    private static long generateRequestId() {
        return UUID.randomUUID().getMostSignificantBits();
    }
}
