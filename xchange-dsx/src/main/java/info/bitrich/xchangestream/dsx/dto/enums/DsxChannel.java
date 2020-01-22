package info.bitrich.xchangestream.dsx.dto.enums;

import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper;

import java.util.function.BiFunction;

public enum DsxChannel {
    book(DsxSubscriptionHelper::createOrderbookSubscriptionMessage),
    ticker((channelInfo, objects) -> DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, DsxEventType.subscribe)),
    trade(DsxSubscriptionHelper::createTradeSubscriptionMessage);

    public final BiFunction<DsxChannelInfo, Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator;

    DsxChannel(BiFunction<DsxChannelInfo, Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator) {
        this.subscriptionMessageCreator = subscriptionMessageCreator;
    }
}
