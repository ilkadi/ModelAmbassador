package org.ehr.roundit.domain.entities;

public interface FeedItem {
    CurrencyAndAmount getAmount();
    FeedItemStatus getStatus();
    String getSettlementTime();
}
