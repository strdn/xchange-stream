package info.bitrich.xchangestream.dsx.dto;

import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderBook;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderLimit;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.reverseOrder;

/**
 * Created by Pavel Chertalev
 */
public class DsxWebSocketOrderBook {
    private Map<BigDecimal, HitbtcOrderLimit> asks;
    private Map<BigDecimal, HitbtcOrderLimit> bids;
    private long sequence = 0;

    public DsxWebSocketOrderBook(DsxWebSocketOrderBookTransaction orderbookTransaction) {
        createFromLevels(orderbookTransaction);
    }

    private void createFromLevels(DsxWebSocketOrderBookTransaction orderbookTransaction) {
        this.asks = new TreeMap<>(BigDecimal::compareTo);
        this.bids = new TreeMap<>(reverseOrder(BigDecimal::compareTo));

        for (HitbtcOrderLimit orderBookItem : orderbookTransaction.getParams().getAsk()) {
            if (orderBookItem.getSize().signum() != 0) {
                asks.put(orderBookItem.getPrice(), orderBookItem);
            }
        }

        for (HitbtcOrderLimit orderBookItem : orderbookTransaction.getParams().getBid()) {
            if (orderBookItem.getSize().signum() != 0) {
                bids.put(orderBookItem.getPrice(), orderBookItem);
            }
        }

        sequence = orderbookTransaction.getParams().getSequence();
    }

    public HitbtcOrderBook toHitbtcOrderBook() {
        HitbtcOrderLimit[] askLimits = asks.entrySet().stream()
                .map(Map.Entry::getValue)
                .toArray(HitbtcOrderLimit[]::new);

        HitbtcOrderLimit[] bidLimits = bids.entrySet().stream()
                .map(Map.Entry::getValue)
                .toArray(HitbtcOrderLimit[]::new);

        return new HitbtcOrderBook(askLimits, bidLimits);
    }

    public void updateOrderBook(DsxWebSocketOrderBookTransaction orderBookTransaction) {
        if (orderBookTransaction.getParams().getSequence() <= sequence) {
            return;
        }
        updateOrderBookItems(orderBookTransaction.getParams().getAsk(), asks);
        updateOrderBookItems(orderBookTransaction.getParams().getBid(), bids);
        sequence = orderBookTransaction.getParams().getSequence();
    }

    private void updateOrderBookItems(HitbtcOrderLimit[] itemsToUpdate, Map<BigDecimal, HitbtcOrderLimit> localItems) {
        for (HitbtcOrderLimit itemToUpdate : itemsToUpdate) {
            localItems.remove(itemToUpdate.getPrice());
            if (itemToUpdate.getSize().signum() != 0) {
                localItems.put(itemToUpdate.getPrice(), itemToUpdate);
            }
        }
    }
}
