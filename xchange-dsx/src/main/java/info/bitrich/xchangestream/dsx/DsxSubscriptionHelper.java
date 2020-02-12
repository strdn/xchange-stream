package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketBookSubscriptionMessage;
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
    private final static long DEFAULT_PREV_DEALS_ID_VALUE = 0;

    public final static String CHANNEL_DELIMITER = "-";

    @FunctionalInterface
    public interface TriFunction<S, T, U, R> {
        R apply(S s, T t, U u);
    }

    public static DsxWebSocketBookSubscriptionMessage createBookSubscriptionMessage(DsxChannelInfo channelInfo, DsxEventType eventType, Object[] args) {
        return new DsxWebSocketBookSubscriptionMessage(
                generateRequestId(),
                eventType,
                channelInfo.getChannel(),
                channelInfo.getInstrument(), channelInfo.getInstrumentType(),
                getIndexedValue("order book depth limit", args, 1, Integer.class, DEFAULT_LIMIT_VALUE));
    }

    public static DsxWebSocketTradeSubscriptionMessage createTradeSubscriptionMessage(DsxChannelInfo channelInfo, DsxEventType event, Object[] args) {
        Long previousDealId = getIndexedValue("previous deal id", args, 1, Long.class, DEFAULT_PREV_DEALS_ID_VALUE);
        Long lastTradeId = channelInfo.getLastTradeId();
        if (lastTradeId != null && lastTradeId == 0) {
            lastTradeId = previousDealId;
        }
        return new DsxWebSocketTradeSubscriptionMessage(generateRequestId(), event, channelInfo.getChannel(), channelInfo.getInstrument(), channelInfo.getInstrumentType(), lastTradeId);
    }

    public static DsxWebSocketSubscriptionMessage createBaseSubscriptionMessage(DsxChannelInfo channelInfo, DsxEventType eventType) {
        return new DsxWebSocketSubscriptionMessage(generateRequestId(), eventType, channelInfo.getChannel(), channelInfo.getInstrument(), channelInfo.getInstrumentType());
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
