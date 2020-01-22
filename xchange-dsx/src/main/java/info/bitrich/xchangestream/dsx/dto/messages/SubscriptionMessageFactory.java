package info.bitrich.xchangestream.dsx.dto.messages;

import info.bitrich.xchangestream.dsx.dto.enums.DsxChannelsType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.enums.DsxModeType;

import java.util.UUID;

public class SubscriptionMessageFactory {
    private final static int DEFAULT_LIMIT_VALUE = 100;
    private final static int DEFAULT_PREV_DEALS_COUNT_VALUE = 100;


    public static DsxWebSocketOrderbookSubscriptionMessage createOrderbookSubscriptionMessage(String channelName, Object [] args) {
        String[] chanelInfo = channelName.split("-");
        String instrument = chanelInfo[1].toLowerCase();
        long requestId = UUID.randomUUID().getMostSignificantBits();
        int limit = checkSecondIntegerArgument(DEFAULT_LIMIT_VALUE, args);
        return new DsxWebSocketOrderbookSubscriptionMessage(requestId, DsxEventType.subscribe, DsxChannelsType.book, DsxModeType.valueOf(chanelInfo[2]), instrument, limit);
    }

    public static DsxWebSocketTradeSubscriptionMessage createTradeSubscriptionMessage(String channelName, Object [] args) {
        String[] chanelInfo = channelName.split("-");
        String instrument = chanelInfo[1].toLowerCase();
        long requestId = UUID.randomUUID().getMostSignificantBits();
        int prevDealsCount = checkSecondIntegerArgument(DEFAULT_PREV_DEALS_COUNT_VALUE, args);
        return new DsxWebSocketTradeSubscriptionMessage(requestId, DsxEventType.subscribe, DsxChannelsType.trade, DsxModeType.valueOf(chanelInfo[2]), instrument, prevDealsCount);
    }

    public static DsxWebSocketSubscriptionMessage createBasicMessage(String channelName, DsxEventType eventType) {
        String[] chanelInfo = channelName.split("-");
        String instrument = chanelInfo[1].toLowerCase();
        long requestId = UUID.randomUUID().getMostSignificantBits();
        return new DsxWebSocketSubscriptionMessage(requestId, eventType, DsxChannelsType.getChannelFromChannelName(channelName), DsxModeType.valueOf(chanelInfo[2]), instrument);
    }

    private static int checkSecondIntegerArgument(int defaultValue, Object... args){
        if (args.length >= 2 && args[1] instanceof Integer){
            return (int) args[1];
        } else {
            return defaultValue;
        }
    }
}
