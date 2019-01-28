package info.bitrich.xchangestream.bitfinex;

import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.service.ConnectableService;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.knowm.xchange.ExchangeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MINUTES;

public class BitfinexManualAuthExample {
    private static final Logger LOG = LoggerFactory.getLogger(BitfinexManualAuthExample.class);

    private static final TimedSemaphore rateLimiter = new TimedSemaphore(1, MINUTES, 15);
    private static void rateLimit() {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            LOG.warn("Bitfinex connection throttle control has been interrupted");
        }
    }

    public static void main(String[] args) {
        ExchangeSpecification defaultExchangeSpecification = new ExchangeSpecification(BitfinexStreamingExchange.class);
        defaultExchangeSpecification.setExchangeSpecificParametersItem(ConnectableService.BEFORE_CONNECTION_HANDLER, (Runnable) BitfinexManualAuthExample::rateLimit);

        BitfinexStreamingExchange exchange = (BitfinexStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(defaultExchangeSpecification);
        exchange.setCredentials("API-KEY", "API-SECRET");

        exchange.connectToAuthenticated().blockingAwait();
        exchange.getStreamingAuthenticatedDataService().getAuthenticatedTrades().subscribe(
            t -> LOG.info("AUTH TRADE: {}", t),
            throwable -> LOG.error("ERROR: ", throwable)
        );
        exchange.getStreamingAuthenticatedDataService().getAuthenticatedPreTrades().subscribe(
            t -> LOG.info("AUTH PRE TRADE: {}", t),
            throwable -> LOG.error("ERROR: ", throwable)
        );
        exchange.getStreamingAuthenticatedDataService().getAuthenticatedOrders().subscribe(
            t -> LOG.info("AUTH ORDER: {}", t),
            throwable -> LOG.error("ERROR: ", throwable)
        );
        exchange.getStreamingAuthenticatedDataService().getAuthenticatedBalances().subscribe(
            t -> LOG.info("AUTH BALANCE: {}", t),
            throwable -> LOG.error("ERROR: ", throwable)
        );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exchange.getStreamingAuthenticatedDataService().disconnect().blockingAwait();
        rateLimiter.shutdown();

    }
}
