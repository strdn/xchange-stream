package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.dsx.dto.*;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.hitbtc.v2.HitbtcAdapters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel Chertalev
 */
public class DsxStreamingMarketDataService implements StreamingMarketDataService {

    private final DsxStreamingService service;
    private Map<CurrencyPair, DsxWebSocketOrderBook> orderbooks = new HashMap<>();

    public DsxStreamingMarketDataService(DsxStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.base.toString() + currencyPair.counter.toString();
        String channelName = getChannelName("orderbook", pair);
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName);
        return jsonNodeObservable
                .map(s -> mapper.readValue(s.toString(), DsxWebSocketOrderBookTransaction.class))
                .map(s -> {
                    DsxWebSocketOrderBook hitbtcOrderBook = s.toDsxOrderBook(orderbooks.getOrDefault(currencyPair, null));
                    orderbooks.put(currencyPair, hitbtcOrderBook);
                    return HitbtcAdapters.adaptOrderBook(hitbtcOrderBook.toHitbtcOrderBook(), currencyPair);
                });
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        throw new NotYetImplementedForExchangeException("getTrades is not implemented");
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        throw new NotYetImplementedForExchangeException("getTicker is not implemented");
    }

    private String getChannelName(String entityName, String pair) {
        return entityName + "-" + pair;
    }

}
