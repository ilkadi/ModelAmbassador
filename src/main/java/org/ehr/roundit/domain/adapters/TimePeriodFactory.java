package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.TimePeriod;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.joda.time.DateTime;

public interface TimePeriodFactory {
    TimePeriod getTimePeriodForType(String saverType, DateTime endDate) throws UnableToProvideDataException;
}
