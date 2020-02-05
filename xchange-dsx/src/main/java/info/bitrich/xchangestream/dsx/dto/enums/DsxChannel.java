package info.bitrich.xchangestream.dsx.dto.enums;

import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper;
import info.bitrich.xchangestream.dsx.DsxSubscriptionHelper.TriFunction;
import info.bitrich.xchangestream.dsx.dto.DsxChannelInfo;
import info.bitrich.xchangestream.dsx.dto.messages.DsxWebSocketSubscriptionMessage;

import static info.bitrich.xchangestream.dsx.dto.enums.DsxEventType.subscribeBook;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxEventType.subscribeTicker;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxEventType.subscribeTrade;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxEventType.unsubscribeBook;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxEventType.unsubscribeTicker;
import static info.bitrich.xchangestream.dsx.dto.enums.DsxEventType.unsubscribeTrade;

public enum DsxChannel {
    book(subscribeBook, unsubscribeBook, DsxSubscriptionHelper::createBookSubscriptionMessage),
    ticker(subscribeTicker, unsubscribeTicker, (channelInfo, event, objects) -> DsxSubscriptionHelper.createBaseSubscriptionMessage(channelInfo, event)),
    trade(subscribeTrade, unsubscribeTrade, DsxSubscriptionHelper::createTradeSubscriptionMessage);

    public final DsxEventType subscriptionEvent;
    public final DsxEventType unsubscriptionEvent;

    public final TriFunction<DsxChannelInfo, DsxEventType,  Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator;

    DsxChannel(DsxEventType subscriptionEvent, DsxEventType unsubscriptionEvent, TriFunction<DsxChannelInfo, DsxEventType,  Object[], DsxWebSocketSubscriptionMessage> subscriptionMessageCreator) {
        this.subscriptionEvent = subscriptionEvent;
        this.unsubscriptionEvent = unsubscriptionEvent;
        this.subscriptionMessageCreator = subscriptionMessageCreator;
    }
}
