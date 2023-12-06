package org.ehr.ambassador.domain.adapters;

import org.ehr.ambassador.domain.entities.ExternalData;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;

public interface DecayMonitoringAdapter {
    void exportInputData(ExternalData inputData) throws ProcessingFailedException;
    void exportOutputData(ExternalData outputData) throws ProcessingFailedException;
}
