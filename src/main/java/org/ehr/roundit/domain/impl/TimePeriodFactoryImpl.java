package org.ehr.roundit.domain.impl;

import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.domain.adapters.TimePeriodFactory;
import org.ehr.roundit.domain.entities.RoundItPeriod;
import org.ehr.roundit.domain.entities.TimePeriod;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimePeriodFactoryImpl implements TimePeriodFactory {
    private final DateTimeFormatter defaultFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public TimePeriod getTimePeriodForType(String saverType, DateTime endDate) throws UnableToProvideDataException {
        RoundItPeriod roundItPeriod;
        try {
            roundItPeriod = RoundItPeriod.valueOf(saverType);
        } catch (Exception e) {
            String failureMessage = String.format("Failed to map saver type %s to supported ones. ", saverType);
            log.warn(failureMessage, e);
            throw new UnableToProvideDataException(failureMessage, e);
        }

        DateTime beginDate = roundItPeriod.getBeginningDateByEndDate().apply(endDate);
        return TimePeriod.builder()
                .minTransactionTimestamp(
                        defaultFormatter.print(beginDate.toLocalDateTime()))
                .maxTransactionTimestamp(
                        defaultFormatter.print(endDate.toLocalDateTime()))
                .build();
    }
}