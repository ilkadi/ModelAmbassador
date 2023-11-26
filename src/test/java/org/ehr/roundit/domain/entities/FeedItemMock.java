package org.ehr.roundit.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class FeedItemMock implements FeedItem {
    private CurrencyAndAmount amount;
    private FeedItemStatus status;
    private String settlementTime;
}
