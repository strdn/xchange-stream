package info.bitrich.xchangestream.dsx;

import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.dsx.dto.messages.DsxAuthBalanceMessage;
import io.reactivex.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.ExchangeSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DsxStreamingAccountService implements StreamingAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingAccountService.class);

    private final DsxStreamingService service;

    public DsxStreamingAccountService(DsxStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<Balance> getBalanceChanges(Currency currency, Object... args) {
        if (args.length == 0 || !Integer.class.isInstance(args[0])) {
            throw new ExchangeException("Specify faId to monitor balance stream");
        }
        Integer faId = (Integer) args[0];
        return getRawAuthenticatedBalances()
                .filter(b -> b.getFaId().equals(faId))
                .filter(b -> currency.getCurrencyCode().equals(b.getCurrency()))
                .map(DsxStreamingMessageAdapter::adaptBalance);
    }

    public Observable<DsxAuthBalanceMessage> getRawAuthenticatedBalances() {
        if (!service.isAuthDataProvided()) {
            throw new ExchangeSecurityException("Not authenticated");
        }
        return service.getAuthenticatedBalances();
    }
}
