package org.ehr.roundit.adapters.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehr.roundit.domain.entities.Direction;
import org.ehr.roundit.domain.entities.FeedItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedItemImpl implements FeedItem {
    private CurrencyAndAmountImpl amount;
    private Direction direction;
}
