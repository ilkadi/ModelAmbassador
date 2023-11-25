package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.FeedItem;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;

import java.util.List;

public interface RoundItCalculator {
    CurrencyAndAmount calculateTotalRoundUp(List<FeedItem> settledPayments) throws UnableToProvideDataException;
}
