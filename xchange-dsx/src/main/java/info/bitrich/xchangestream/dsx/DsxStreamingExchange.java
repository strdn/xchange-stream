package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dsx.DSXExchange;

import static info.bitrich.xchangestream.service.ConnectableService.BEFORE_CONNECTION_HANDLER;

/**
 * @author rimalon
 */
public class DsxStreamingExchange extends DSXExchange implements StreamingExchange {
    private static final String DEFAULT_API_URI = "ws://localhost:8080/stream";
    public static final String DSX_SPEC_PARAMS_API_URI = "API_URI";

    private DsxStreamingService streamingService;
    private DsxStreamingMarketDataService streamingMarketDataService;

    public DsxStreamingExchange() {
    }

    @Override
    protected void initServices() {
        super.initServices();
        Object apiURI = getExchangeSpecification().getExchangeSpecificParametersItem(DSX_SPEC_PARAMS_API_URI);
        streamingService = new DsxStreamingService(apiURI == null ? DEFAULT_API_URI : (String) apiURI);
        streamingService.setBeforeConnectionHandler((Runnable) getExchangeSpecification().getExchangeSpecificParametersItem(BEFORE_CONNECTION_HANDLER));
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
