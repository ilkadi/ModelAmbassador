package org.ehr.roundit.domain.entities;

import org.joda.time.DateTime;

import java.util.function.Function;

public enum RoundItPeriod {
    LAST_WEEK(endDate -> endDate.minusDays(7));

    private final Function<DateTime, DateTime> beginningDateByEndDate;

    RoundItPeriod(Function<DateTime, DateTime> beginningDateByEndDate) {
        this.beginningDateByEndDate = beginningDateByEndDate;
    }

    public Function<DateTime, DateTime> getBeginningDateByEndDate() {
        return beginningDateByEndDate;
    }
}
