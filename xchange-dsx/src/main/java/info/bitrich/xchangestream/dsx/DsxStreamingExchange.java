package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dsx.DSXExchange;

/**
 * @author rimalon
 */
public class DsxStreamingExchange extends DSXExchange implements StreamingExchange {
    private static final String DEFAULT_API_URI = "ws://localhost:8080/stream";

    private DsxStreamingService streamingService;
    private DsxStreamingMarketDataService streamingMarketDataService;

    public DsxStreamingExchange() {
    }

    @Override
    protected void initServices() {
        super.initServices();
        Object apiURI = getExchangeSpecification().getExchangeSpecificParametersItem("API_URI");
        streamingService = new DsxStreamingService(apiURI == null ? DEFAULT_API_URI : (String) apiURI);
        streamingMarketDataService = new DsxStreamingMarketDataService(streamingService);
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
    public void useCompressedMessages(boolean compressedMessages) {
        streamingService.useCompressedMessages(compressedMessages);
    }
}
