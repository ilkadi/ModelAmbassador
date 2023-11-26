package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.FeedItem;
import org.ehr.roundit.domain.entities.TimePeriod;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;

import java.util.List;

public interface TransactionsAdapter {
    List<FeedItem> getSettledOutPaymentsBetween(String accessToken, String accountUid, TimePeriod timePeriod) throws UnableToProvideDataException;
}
