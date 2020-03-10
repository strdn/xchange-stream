package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.dsx.dto.DsxOrderBook;
import info.bitrich.xchangestream.dsx.dto.enums.DsxChannel;
import info.bitrich.xchangestream.dsx.dto.messages.DsxOrderbookMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTikerMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTradeMessage;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dsx.DSXAdapters;
import org.knowm.xchange.dsx.dto.marketdata.DSXTrade;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static info.bitrich.xchangestream.dsx.DsxSubscriptionHelper.createChannelName;

/**
 * @author rimalon
 */
public class DsxStreamingMarketDataService implements StreamingMarketDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingMarketDataService.class);

    private final DsxStreamingService service;
    private ConcurrentHashMap<String, DsxOrderBook> orderbooks = new ConcurrentHashMap<>();

    public DsxStreamingMarketDataService(DsxStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channelName = createChannelName(DsxChannel.BOOK, currencyPair, args);
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName, args);
        return jsonNodeObservable
                .map(jsonNode -> mapper.readValue(jsonNode.toString(), DsxOrderbookMessage.class))
                .map(parsedMessage -> {
                    DsxOrderBook orderbook = parsedMessage.toDsxOrderBook(orderbooks.get(channelName));
                    orderbooks.put(channelName, orderbook);
                    List<LimitOrder> asks = DSXAdapters.adaptOrders(new ArrayList<>(orderbook.getAsks().values()), currencyPair, "ask", "");
                    List<LimitOrder> bids = DSXAdapters.adaptOrders(new ArrayList<>(orderbook.getBids().values()), currencyPair, "bid", "");
                    return new OrderBook(new Date(orderbook.getTimestamp()), asks, bids);
                });
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        String channelName = createChannelName(DsxChannel.TRADE, currencyPair, args);
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName, args);
        return jsonNodeObservable
                .map(jsonNode -> mapper.readValue(jsonNode.toString(), DsxTradeMessage.class))
                .map(DsxTradeMessage::getTrades)
                .flatMap(trades ->
                        Observable.fromIterable(
                                Arrays.stream(trades)
                                        .sorted(Comparator.comparingLong(DSXTrade::getTid))
                                        .collect(Collectors.toList())
                        )
                )
                .doOnNext(trade -> service.setLastTradeId(channelName, trade.getTid()))
                .map(trade -> DSXAdapters.adaptTrade(trade, currencyPair));
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channelName = createChannelName(DsxChannel.TICKER, currencyPair, args);
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName);
        return jsonNodeObservable
                .map(jsonNode -> mapper.readValue(jsonNode.toString(), DsxTikerMessage.class))
                .map(parsedMessage -> DSXAdapters.adaptTicker(parsedMessage.getTicker(), currencyPair));
    }

}
