package info.bitrich.xchangestream.dsx.dto.enums;

import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper;
import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper.TriFunction;
import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum DsxChannel {
    BOOK(DsxSubscriptionHelper::createBookSubscriptionMessage),
    TICKER((channelInfo, event, objects) -> DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, event)),
    TRADE(DsxSubscriptionHelper::createTradeSubscriptionMessage),
    AUTHORIZED(null);

    public final TriFunction<DsxChannelInfo, DsxEventType,  Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator;

    DsxChannel(TriFunction<DsxChannelInfo, DsxEventType,  Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator) {
        this.subscriptionMessageCreator = subscriptionMessageCreator;
    }

    public static DsxChannel getChannel(String channel) {
        return Arrays.stream(DsxChannel.values())
                .filter(e -> StringUtils.equalsIgnoreCase(channel, e.name()))
                .findFirst()
                .orElse(null);
    }
}
