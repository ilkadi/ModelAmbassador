package org.ehr.ambassador.adapters.storage;

import org.ehr.ambassador.domain.adapters.DecayMonitoringAdapter;
import org.ehr.ambassador.domain.entities.ExternalData;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.springframework.stereotype.Component;

@Component
public class DecayMonitoringAdapterS3Impl implements DecayMonitoringAdapter {
    @Override
    public void exportInputData(ExternalData inputData) throws ProcessingFailedException {
        // implementation for exporting line-delim json data to S3
    }

    @Override
    public void exportOutputData(ExternalData outputData) throws ProcessingFailedException {
        // implementation for exporting line-delim json data to S3
    }
}
