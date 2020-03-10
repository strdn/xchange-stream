package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.dsx.dto.messages.DsxAuthBalanceMessage;
import io.reactivex.annotations.Nullable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dsx.dto.trade.ClientDeal;
import org.knowm.xchange.dsx.dto.trade.DSXOrder;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.StreamSupport.stream;
import static org.knowm.xchange.dto.Order.OrderType.ASK;
import static org.knowm.xchange.dto.Order.OrderType.BID;

public class DsxStreamingMessageAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(DsxStreamingMessageAdapter.class);

    static Stream<DsxAuthBalanceMessage> adaptBalances(JsonNode balances) {
        Iterator<Map.Entry<String, JsonNode>> iterator = balances.fields();
        return Stream.of(StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(e -> {
                            Iterable<JsonNode> faBalances = () -> e.getValue().iterator();
                            return stream(faBalances.spliterator(), false)
                                    .map(b -> createBalanceMessage(Integer.parseInt(e.getKey()), b))
                                    .peek(o -> LOG.debug("Balance: {}", o));
                        }
                )
        ).flatMap(x -> x.flatMap(z -> z));
    }

    @Nullable
    static DsxAuthBalanceMessage adaptBalance(JsonNode balance) {
        Map.Entry<String, JsonNode> balanceObject = balance.fields().next();
        if (balanceObject == null) {
            return null;
        }
        DsxAuthBalanceMessage balanceMessage = createBalanceMessage(Integer.parseInt(balanceObject.getKey()), balanceObject.getValue().get(0));
        LOG.debug("Balance: {}", balanceMessage);
        return balanceMessage;
    }

    static Balance adaptBalance(DsxAuthBalanceMessage authBalance) {
        return new Balance(
                Currency.getInstance(authBalance.getCurrency()),
                authBalance.getTotal(),
                authBalance.getAvailable()
        );
    }


    static Stream<DSXOrder> adaptOrders(JsonNode orders) {
        Iterable<JsonNode> iterator = () -> orders.iterator();
        return stream(iterator.spliterator(), false)
                .map(DsxStreamingMessageAdapter::createOrderMessage)
                .peek(o -> LOG.debug("New order: {}", o));
    }

    @Nullable
    static DSXOrder adaptOrder(JsonNode order) {
        JsonNode orderObject = order.get(0);
        if (orderObject == null) {
            return null;
        }
        DSXOrder aom = createOrderMessage(orderObject);
        LOG.debug("New order: {}", aom);
        return aom;
    }

    static Order adaptOrder(DSXOrder authOrder) {

        return authOrder.getOrderType().equals(DSXOrder.OrderType.limit) ?
                new LimitOrder(
                    authOrder.getType().equals(DSXOrder.Type.buy) ? Order.OrderType.ASK : Order.OrderType.BID,
                    authOrder.getVolume(), new CurrencyPair(authOrder.getPair()), null, DateUtils.fromMillisUtc(Long.parseLong(authOrder.getTimestampCreated())),
                    authOrder.getRate())  :
                new MarketOrder(authOrder.getType().equals(DSXOrder.Type.buy) ? Order.OrderType.ASK : Order.OrderType.BID,
                        authOrder.getVolume(), new CurrencyPair(authOrder.getPair()));
    }

    //trades
    static Stream<ClientDeal> adaptTrades(JsonNode trades) {
        Iterable<JsonNode> iterator = () -> trades.iterator();
        return stream(iterator.spliterator(), false)
                .map(DsxStreamingMessageAdapter::createTradeMessage)
                .peek(o -> LOG.debug("New trade: {}", o));
    }

    @Nullable
    static ClientDeal adaptTrade(JsonNode order) {
        JsonNode tradeObject = order.get(0);
        if (tradeObject == null) {
            return null;
        }
        ClientDeal atm = createTradeMessage(tradeObject);
        LOG.debug("New trade: {}", atm);
        return atm;
    }

    static UserTrade adaptTrade(ClientDeal tradeMessage) {
        return new UserTrade.Builder()
                .currencyPair(new CurrencyPair(tradeMessage.getPair()))
                .feeAmount(tradeMessage.getCommission())
                .feeCurrency(new Currency(tradeMessage.getCommissionCurrency()))
                .id(Long.toString(tradeMessage.getNumber()))
                .orderId(Long.toString(tradeMessage.getOrderId()))
                .originalAmount(tradeMessage.getVolume())
                .price(tradeMessage.getRate())
                .timestamp(DateUtils.fromMillisUtc(tradeMessage.getTimestamp()))
                .type(tradeMessage.getType().equals(DSXOrder.Type.buy) ? ASK : BID)
                .build();
    }

    private static DsxAuthBalanceMessage createBalanceMessage(Integer faId, JsonNode balance) {
        if (balance.size() < 5) {
            LOG.error("createBalanceObject unexpected record size={}, record={}", balance.size(), balance.toString());
            return null;
        }
        String currency = balance.get("currency").asText();
        BigDecimal total = balance.get("total").decimalValue();
        BigDecimal held = balance.get("held").decimalValue();
        BigDecimal locked = balance.get("locked").decimalValue();
        BigDecimal available = balance.get("available").asText().equals("null") ? total.subtract(held).subtract(locked) : balance.get("available").decimalValue();

        return new DsxAuthBalanceMessage(faId, currency, total, held, locked, available);
    }

    private static ClientDeal createTradeMessage(JsonNode trade) {

        long number = trade.get("number").asLong();
        String pair = trade.get("pair").asText();
        DSXOrder.Type type = DSXOrder.Type.valueOf(trade.get("type").asText().toLowerCase());
        BigDecimal volume = trade.get("volume").decimalValue();
        BigDecimal rate = trade.get("rate").decimalValue();
        long orderId = trade.get("orderId").asLong();
        long timestamp = trade.get("timestamp").asLong();
        BigDecimal commission = trade.get("commission").decimalValue();
        String commissionCurrency = trade.get("commissionCurrency").asText();

        return new ClientDeal(number, pair, type.toString(), volume, rate, orderId, timestamp, commission, commissionCurrency);
    }

    private static DSXOrder createOrderMessage(JsonNode order) {

        String pair = order.get("pair").asText();
        DSXOrder.Type type = DSXOrder.Type.valueOf(order.get("type").asText().toLowerCase());
        BigDecimal volume = order.get("volume").decimalValue();
        BigDecimal remainingVolume = order.get("remainingVolume").decimalValue();
        BigDecimal rate = order.get("rate").decimalValue();
        int status = order.get("status").asInt();
        DSXOrder.OrderType orderType = DSXOrder.OrderType.valueOf(order.get("orderType").asText().toLowerCase());
        String timestampCreated = order.get("timestampCreated").asText();

        return new DSXOrder(pair, type, volume, remainingVolume, rate, status, orderType, timestampCreated);
    }
}
