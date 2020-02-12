package info.bitrich.xchangestream.dsx.dto.enums;

import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper;
import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper.TriFunction;
import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;

public enum DsxChannel {
    book(DsxSubscriptionHelper::createBookSubscriptionMessage),
    ticker((channelInfo, event, objects) -> DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, event)),
    trade(DsxSubscriptionHelper::createTradeSubscriptionMessage);

    public final TriFunction<DsxChannelInfo, DsxEventType,  Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator;

    DsxChannel(TriFunction<DsxChannelInfo, DsxEventType,  Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator) {
        this.subscriptionMessageCreator = subscriptionMessageCreator;
    }
}
