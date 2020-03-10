package info.bitrich.xchangestream.dsx.dto;

import info.bitrich.xchangestream.dsx.dto.messages.DsxOrderbookMessage;
import org.knowm.xchange.exceptions.ExchangeException;

import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

/**
 * @author rimalon
 */
public class DsxOrderBook {
    private TreeMap<BigDecimal, BigDecimal[]> asks;
    private TreeMap<BigDecimal, BigDecimal[]> bids;
    private long timestamp;

    public DsxOrderBook(DsxOrderbookMessage message) {
        createFromMessage(message);
    }

    public TreeMap<BigDecimal, BigDecimal[]> getBids() {
        return bids;
    }

    public TreeMap<BigDecimal, BigDecimal[]> getAsks() {
        return asks;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(long value) { this.timestamp = value; }

    private void createFromMessage(DsxOrderbookMessage message) {
        asks = message.getAsks().stream().collect(
                Collectors.toMap(bookPriceAndVolume -> bookPriceAndVolume[0], // get price from orderbook item
                        Function.identity(),
                        (bookPriceAndVolume1, bookPriceAndVolume2) -> bookPriceAndVolume1,
                        () -> new TreeMap<>(BigDecimal::compareTo)));
        bids = message.getBids().stream().collect(
                Collectors.toMap(bookPriceAndVolume -> bookPriceAndVolume[0],
                        Function.identity(),
                        (bookPriceAndVolume1, bookPriceAndVolume2) -> bookPriceAndVolume1,
                        () -> new TreeMap<>(reverseOrder(BigDecimal::compareTo))));
        timestamp = message.getTimestamp();
    }

    public DsxOrderBook toDSXOrderBook(DsxOrderbookMessage message) {
        switch (message.getEvent()) {
            case SNAPSHOT: {
                return message.getTimestamp() < this.getTimestamp() ? this : new DsxOrderBook(message);
            }
            case UPDATE: {
                return this.updateOrderBook(message);
            }
        }
        throw new ExchangeException("Incorrect event type:" + message.getEvent());
    }

    public DsxOrderBook updateOrderBook(DsxOrderbookMessage message) {
        if (message.getTimestamp() < this.getTimestamp()) {
            return this;
        } else {
            for (BigDecimal[] orderBookItem : message.getAsks()) {
                asks.remove(orderBookItem[0]);
                if (orderBookItem[1].compareTo(BigDecimal.ZERO) != 0) {
                    asks.put(orderBookItem[0], orderBookItem);
                }
            }
            for (BigDecimal[] orderBookItem : message.getBids()) {
                bids.remove(orderBookItem[0]);
                if (orderBookItem[1].compareTo(BigDecimal.ZERO) != 0) {
                    bids.put(orderBookItem[0], orderBookItem);
                }
            }
            this.setTimestamp(message.getTimestamp());
            return this;
        }
    }
}
