package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.dsx.dto.enums.DsxInstrumentType;
import io.reactivex.disposables.Disposable;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pavel Chertalev
 */
public class DsxManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(DsxManualExample.class);

    public static void main(String[] args) throws InterruptedException {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(DsxStreamingExchange.class
                .getName());

        exchange.connect().blockingAwait();
        Disposable orderBookObserver = exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USD, DsxInstrumentType.LIVE, 1).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks().get(0));
            LOG.info("First bid: {}", orderBook.getBids().get(0));
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        Thread.sleep(10000);
        orderBookObserver.dispose();


        orderBookObserver = exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USD, DsxInstrumentType.LIVE, 1).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks().get(0));
            LOG.info("First bid: {}", orderBook.getBids().get(0));
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        Thread.sleep(100000);

        exchange.disconnect().subscribe(() -> LOG.info("Disconnected"));
    }
}
