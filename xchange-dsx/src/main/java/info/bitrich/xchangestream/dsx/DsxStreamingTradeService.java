package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTradeMessage;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.exceptions.ExchangeSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class DsxStreamingTradeService implements StreamingTradeService {

    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingTradeService.class);

    private final DsxStreamingService service;

    public DsxStreamingTradeService(DsxStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<Order> getOrderChanges(CurrencyPair currencyPair, Object... args) {
        return null;
    }


    public Observable<UserTrade> getUserTrades() {
        return getAuthenticatedTrades()
                .map(t -> new UserTrade.Builder().build());
    }

    @Override
    public Observable<UserTrade> getUserTrades(CurrencyPair currencyPair, Object... args) {
        return getUserTrades()
                .filter(t -> currencyPair.equals(t.getCurrencyPair()));
    }

    public Observable<DsxTradeMessage> getAuthenticatedTrades() {
        return withAuthenticatedService(DsxStreamingService::getAuthenticatedTrades);
    }

    private <T> Observable<T> withAuthenticatedService(Function<DsxStreamingService, Observable<T>> serviceConsumer) {
        if (!service.isAuthDataProvided()) {
            throw new ExchangeSecurityException("Not authenticated");
        }
        return serviceConsumer.apply(service);
    }
}
