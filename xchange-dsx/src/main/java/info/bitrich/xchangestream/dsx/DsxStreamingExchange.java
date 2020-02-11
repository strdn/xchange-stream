package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;

/**
 * @author rimalon
 */
public class DsxStreamingExchange extends HitbtcExchange implements StreamingExchange {
    private static final String API_URI = "ws://localhost:8080/stream";

    private DsxStreamingService streamingService;
    private DsxStreamingMarketDataService streamingMarketDataService;
    private DsxStreamingTradeService streamingTradeService;

    @Override
    protected void initServices() {
        super.initServices();
        this.streamingService = createStreamingService();
        this.streamingMarketDataService = new DsxStreamingMarketDataService(streamingService);
        this.streamingTradeService = new DsxStreamingTradeService(streamingService);
    }

    private DsxStreamingService createStreamingService() {
        DsxStreamingService streamingService = new DsxStreamingService(API_URI, getNonceFactory());
        applyStreamingSpecification(getExchangeSpecification(), streamingService);
        if (StringUtils.isNotEmpty(exchangeSpecification.getApiKey())) {
            streamingService.setApiKey(exchangeSpecification.getApiKey());
            streamingService.setApiSecret(exchangeSpecification.getSecretKey());
        }
        return streamingService;
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        return streamingService.connect();
    }

    @Override
    public Completable disconnect() {
        return streamingService.disconnect();
    }

    @Override
    public boolean isAlive() {
        return streamingService.isSocketOpen();
    }

    @Override
    public Observable<Throwable> reconnectFailure() {
        return streamingService.subscribeReconnectFailure();
    }

    @Override
    public Observable<Object> connectionSuccess() {
        return streamingService.subscribeConnectionSuccess();
    }

    @Override
    public ExchangeSpecification getDefaultExchangeSpecification() {
        ExchangeSpecification spec = super.getDefaultExchangeSpecification();
        spec.setShouldLoadRemoteMetaData(false);

        return spec;
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }

    @Override
    public StreamingTradeService getStreamingTradeService() {
        if (streamingService.isAuthDataProvided()) {
            streamingService.authorize();
        }
        return streamingTradeService;
    }
    
    @Override
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }
}
