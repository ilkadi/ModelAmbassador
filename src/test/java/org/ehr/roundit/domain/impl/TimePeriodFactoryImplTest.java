package org.ehr.roundit.domain.impl;

import org.ehr.roundit.domain.entities.RoundItPeriod;
import org.ehr.roundit.domain.entities.TimePeriod;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimePeriodFactoryImplTest {
    private TimePeriodFactoryImpl timePeriodFactory;

    @BeforeEach
    public void setup() {
        timePeriodFactory = new TimePeriodFactoryImpl();
    }

    @Test
    @DisplayName("Should return correctly presented time period")
    public void happyPath() throws UnableToProvideDataException {
        String saverType = RoundItPeriod.LAST_WEEK.name();

        TimePeriod timePeriod = timePeriodFactory.getTimePeriodForType(saverType, new DateTime(1000000));
        assertEquals("1969-12-25T01:16:40.000Z", timePeriod.getMinTransactionTimestamp());
        assertEquals("1970-01-01T01:16:40.000Z", timePeriod.getMaxTransactionTimestamp());
    }

    @Test
    @DisplayName("Should throw exception if saver type not supported")
    public void unhappyPath() {
        String saverType = "SOME_OTHER_NAME";
        assertThrows(UnableToProvideDataException.class, () ->
                timePeriodFactory.getTimePeriodForType(saverType, new DateTime(1000000)));
    }
}
