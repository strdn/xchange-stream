package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import io.reactivex.disposables.Disposable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pavel Chertalev
 */
public class DsxManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(DsxManualExample.class);

    public static void main(String[] args) throws InterruptedException {
        ExchangeSpecification specification = new ExchangeSpecification(DsxStreamingExchange.class);
        specification.setExchangeSpecificParametersItem(DsxStreamingExchange.DSX_SPEC_PARAMS_API_URI, "ws://localhost:8080/stream");
        specification.setShouldLoadRemoteMetaData(false);
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(specification);

        exchange.connect().blockingAwait();
        Disposable orderBookObserver = exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USD, DsxInstrumentType.LIVE, 1).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks().get(0));
            LOG.info("First bid: {}", orderBook.getBids().get(0));
        }, throwable -> LOG.error("Error in getting order book: ", throwable));


        Disposable tradeObserver = exchange.getStreamingMarketDataService().getTrades(CurrencyPair.BTC_USD, DsxInstrumentType.LIVE).subscribe(trade -> {
            LOG.info("Trade: {}", trade);
        }, throwable -> LOG.error("Error in getting trade: ", throwable));

        Disposable tickerObserver = exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_USD, DsxInstrumentType.LIVE).subscribe(ticker -> {
            LOG.info("Ticker: {}", ticker);
        }, throwable -> LOG.error("Error in getting ticker: ", throwable));


        Thread.sleep(10000);
        orderBookObserver.dispose();
        tradeObserver.dispose();

        exchange.disconnect().subscribe(() -> LOG.info("Disconnected"));
    }
}
