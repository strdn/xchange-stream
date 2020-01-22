package info.bitrich.xchangestream.dsx.dto.enums;

import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.dsx.dto.messages.SubscriptionMessageFactory;

import java.util.function.BiFunction;

public enum DsxChannelsType {
    book(SubscriptionMessageFactory::createOrderbookSubscriptionMessage),
    ticker((channelName, args) -> SubscriptionMessageFactory.createBasicMessage(channelName, DsxEventType.subscribe)),
    trade(SubscriptionMessageFactory::createTradeSubscriptionMessage);

    private BiFunction<String, Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator;

    DsxChannelsType(BiFunction<String, Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator) {
        this.subscriptionMessageCreator = subscriptionMessageCreator;
    }

    public BiFunction<String, Object[], DsxWebSocketSubscriptionMessage> getSubscriptionMessageCreator() {
        return subscriptionMessageCreator;
    }

    public static DsxChannelsType getChannelFromChannelName(String channelName) {
        return DsxChannelsType.valueOf(channelName.split("-")[0]);
    }
}
