package info.bitrich.xchangestream.dsx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.dsx.dto.DsxOrderBook;
import info.bitrich.xchangestream.dsx.dto.enums.DsxEventType;
import info.bitrich.xchangestream.dsx.dto.messages.DsxOrderbookMessage;
import info.bitrich.xchangestream.dsx.dto.messages.DsxTradeMessage;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dsx.DSXAdapters;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DsxStreamingMarketDataServiceTest {

    final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

    @Test
    public void getOrderBook() throws IOException {
        JsonNode jsonNode = mapper.readTree(this.getClass().getResource("/example/orderBookSnapshot.json").openStream());
        Assert.assertNotNull(jsonNode);
        DsxOrderbookMessage parsedSnapshot = mapper.readValue(jsonNode.toString(), DsxOrderbookMessage.class);
        DsxOrderBook orderBook = new DsxOrderBook(parsedSnapshot);
        Assert.assertEquals(DsxEventType.SNAPSHOT, parsedSnapshot.getEvent());
        List<LimitOrder> asks = new ArrayList<>();
        LimitOrder askDeleteAfterUpdate = new LimitOrder(OrderType.ASK, BigDecimal.valueOf(15), CurrencyPair.BTC_USD, "", null,BigDecimal.valueOf(103.5));
        asks.add(askDeleteAfterUpdate);
        asks.add(new LimitOrder(OrderType.ASK, BigDecimal.valueOf(0.425), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(103.504)));
        asks.add(new LimitOrder(OrderType.ASK, BigDecimal.valueOf(0.1), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(103.505)));
        Assert.assertArrayEquals(asks.toArray(), DSXAdapters.adaptOrders(new ArrayList<>(orderBook.getAsks().values()), CurrencyPair.BTC_USD, "ask", "").toArray());
        List<LimitOrder> bids = new ArrayList<>();
        LimitOrder bidDeleteAfterUpdate = new LimitOrder(OrderType.BID, BigDecimal.valueOf(2.48502251), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(103.2));
        bids.add(bidDeleteAfterUpdate);
        bids.add(new LimitOrder(OrderType.BID, BigDecimal.valueOf(0.46540304), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(103.1)));
        bids.add(new LimitOrder(OrderType.BID, BigDecimal.valueOf(0.99007913), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(102.091)));
        bids.add(new LimitOrder(OrderType.BID, BigDecimal.valueOf(0.07832332), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(102.083)));
        Assert.assertArrayEquals(bids.toArray(), DSXAdapters.adaptOrders(new ArrayList<>(orderBook.getBids().values()), CurrencyPair.BTC_USD, "bid", "").toArray());
        jsonNode = mapper.readTree(this.getClass().getResource("/example/orderBookUpdate.json").openStream());
        Assert.assertNotNull(jsonNode);
        DsxOrderbookMessage parsedUpdate = mapper.readValue(jsonNode.toString(), DsxOrderbookMessage.class);
        asks.remove(askDeleteAfterUpdate);
        bids.remove(bidDeleteAfterUpdate);
        asks.add(0, new LimitOrder(OrderType.ASK, BigDecimal.valueOf(0.15), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(103.3)));
        bids.add(bids.size(), new LimitOrder(OrderType.BID, BigDecimal.valueOf(0.4653), CurrencyPair.BTC_USD, "",  null, BigDecimal.valueOf(101.082)));
        orderBook = orderBook.toDSXOrderBook(parsedUpdate);
        Assert.assertArrayEquals(asks.toArray(), DSXAdapters.adaptOrders(new ArrayList<>(orderBook.getAsks().values()), CurrencyPair.BTC_USD, "ask", "").toArray());
        Assert.assertArrayEquals(bids.toArray(), DSXAdapters.adaptOrders(new ArrayList<>(orderBook.getBids().values()), CurrencyPair.BTC_USD, "bid", "").toArray());
    }

    @Test
    public void getTrades() throws IOException {
        JsonNode jsonNode = mapper.readTree(this.getClass().getResource("/example/trades.json").openStream());
        Trade[] trades = new Trade[]{
                new Trade(OrderType.ASK, BigDecimal.valueOf(0.04669459), CurrencyPair.BTC_USD, BigDecimal.valueOf(8685.60093), new Date(79521710), "37776598"),
                new Trade(OrderType.ASK, BigDecimal.valueOf(0.0927675), CurrencyPair.BTC_USD, BigDecimal.valueOf(8684.50099), new Date(79521562), "37776596"),
                new Trade(OrderType.ASK, BigDecimal.valueOf(0.00202573), CurrencyPair.BTC_USD, BigDecimal.valueOf(8684.50014), new Date(79521525), "37776595"),
                new Trade(OrderType.BID, BigDecimal.valueOf(0.08884705), CurrencyPair.BTC_USD, BigDecimal.valueOf(8734.98780), new Date(79521286), "37776586"),
                new Trade(OrderType.BID, BigDecimal.valueOf(0.02937918), CurrencyPair.BTC_USD, BigDecimal.valueOf(8734.98810), new Date(79521271), "37776582"),
                new Trade(OrderType.BID, BigDecimal.valueOf(0.01129968), CurrencyPair.BTC_USD, BigDecimal.valueOf(8734.98904), new Date(79521192), "37776576"),
                new Trade(OrderType.BID, BigDecimal.valueOf(0.01081907), CurrencyPair.BTC_USD, BigDecimal.valueOf(8734.98983), new Date(79521148), "37776575"),
                new Trade(OrderType.BID, BigDecimal.valueOf(0.07631111), CurrencyPair.BTC_USD, BigDecimal.valueOf(8734.99027), new Date(79521122), "37776574")};
        Observable<Trade> dsxTradeObservable = Observable.fromArray(new JsonNode[]{jsonNode})
                .map(json -> mapper.readValue(json.toString(), DsxTradeMessage.class))
                .map(DsxTradeMessage::getTrades)
                .flatMap(Observable::fromArray)
                .map(trade -> DSXAdapters.adaptTrade(trade, CurrencyPair.BTC_USD));
        Assert.assertArrayEquals(trades, dsxTradeObservable.toList().blockingGet().toArray());
    }
}