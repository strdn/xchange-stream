package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pavel Chertalev
 */
public class DsxManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(DsxManualExample.class);

    public static void main(String[] args) {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(DsxStreamingExchange.class
                .getName());

        exchange.connect().blockingAwait();
        Disposable orderBookObserver = exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USD).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks().get(0));
            LOG.info("First bid: {}", orderBook.getBids().get(0));
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));
        Disposable tradesObserver = exchange.getStreamingMarketDataService().getTrades(CurrencyPair.BTC_USD).subscribe(trade -> {
            LOG.info(trade.toString());
        }, throwable -> LOG.error("ERROR in getting trades: ", throwable));
        Disposable tickerObserver = exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_USD).subscribe(ticker -> {
            LOG.info(ticker.toString());
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));


        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        orderBookObserver.dispose();
        tradesObserver.dispose();
        tickerObserver.dispose();
        exchange.disconnect().subscribe(() -> LOG.info("Disconnected"));

    }
}
