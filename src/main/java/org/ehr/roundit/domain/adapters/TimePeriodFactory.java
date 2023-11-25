package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.TimePeriod;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;

public interface TimePeriodFactory {
    TimePeriod getTimePeriodForType(String saverType) throws UnableToProvideDataException;
}
