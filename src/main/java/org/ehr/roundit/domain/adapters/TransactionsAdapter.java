package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.FeedItem;

import java.util.List;

public interface TransactionsAdapter {
    List<FeedItem> getSettledTransactionsBetween(String accountUid, String minTransactionTimestamp, String maxTransactionTimestamp);
}
