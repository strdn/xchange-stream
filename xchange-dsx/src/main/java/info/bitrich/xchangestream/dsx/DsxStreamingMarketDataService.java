package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.dsx.dto.*;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxOrderbookMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTikerMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTradeMessage;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dsx.DSXAdapters;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rimalon
 */
public class DsxStreamingMarketDataService implements StreamingMarketDataService {

    private final DsxStreamingService service;
    private ConcurrentHashMap<CurrencyPair, DsxOrderBook> orderbooks = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingMarketDataService.class);
    public DsxStreamingMarketDataService(DsxStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.base.toString() + currencyPair.counter.toString();
        String channelName;
        if (args.length == 0){
            LOG.warn("In method getOrderBook the mod parameter was not passed, the default is LIVE");
            channelName = getChannelName("book", pair.toLowerCase(), "LIVE");
        }
        else {
            channelName = getChannelName("book", pair.toLowerCase(), args[0].toString());
        }
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName);
        return jsonNodeObservable
                .map(jsonNode -> mapper.readValue(jsonNode.toString(), DsxOrderbookMessage.class))
                .map(parsedMessage -> {
                            DsxOrderBook orderbook;
                            if (!orderbooks.containsKey(currencyPair)) {
                                if (parsedMessage.getEvent() != DsxEventType.snapshot) {
                                    return new OrderBook(new Date(0), Collections.emptyList(), Collections.emptyList()); //this
                                } else {
                                    orderbook = new DsxOrderBook(parsedMessage);
                                }
                            } else {
                                orderbook = orderbooks.get(currencyPair).toDSXOrderBook(parsedMessage);
                            }
                            orderbooks.put(currencyPair, orderbook);
                            List<LimitOrder> asks = DSXAdapters.adaptOrders(new ArrayList<>(orderbook.getAsks().values()), currencyPair, "ask", "");
                            List<LimitOrder> bids = DSXAdapters.adaptOrders(new ArrayList<>(orderbook.getBids().values()), currencyPair, "bid", "");
                            return new OrderBook(new Date(orderbook.getTimestamp()), asks, bids);
                        }
                );
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.base.toString() + currencyPair.counter.toString();
        String channelName;
        if (args.length == 0){
            LOG.warn("In method getTrades the mod parameter was not passed, the default is LIVE");
            channelName = getChannelName("trade", pair.toLowerCase(), "LIVE");
        }
        else {
            channelName = getChannelName("trade", pair.toLowerCase(), args[0].toString());
        }
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName);
        return jsonNodeObservable
                .map(jsonNode -> mapper.readValue(jsonNode.toString(), DsxTradeMessage.class))
                .map(DsxTradeMessage::getTrades)
                .flatMap(Observable::fromArray)
                .map(trade -> DSXAdapters.adaptTrade(trade, currencyPair));
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.base.toString() + currencyPair.counter.toString();
        String channelName;
        if (args.length == 0){
            LOG.warn("In method getTicker the mod parameter was not passed, the default is LIVE");
            channelName = getChannelName("ticker", pair.toLowerCase(), "LIVE");
        }
        else {
            channelName = getChannelName("ticker", pair.toLowerCase(), args[0].toString());
        }
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName);
        return jsonNodeObservable
                .map(jsonNode -> mapper.readValue(jsonNode.toString(), DsxTikerMessage.class))
                .map(parsedMessage -> DSXAdapters.adaptTicker(parsedMessage.getTicker(), currencyPair));
    }

    private String getChannelName(String entityName, String instrument, String mode) {
        return entityName + "-" + instrument + "-" + mode;
    }
}
