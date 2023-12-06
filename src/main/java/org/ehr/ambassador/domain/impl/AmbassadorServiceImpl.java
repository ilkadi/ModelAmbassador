package org.ehr.ambassador.domain.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.ambassador.domain.AmbassadorService;
import org.ehr.ambassador.domain.adapters.DecayMonitoringAdapter;
import org.ehr.ambassador.domain.adapters.ModelAdapter;
import org.ehr.ambassador.domain.entities.ExternalData;
import org.ehr.ambassador.domain.entities.ExternalDataDomainImpl;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmbassadorServiceImpl implements AmbassadorService {
    private final ModelAdapter modelAdapter;
    private final DecayMonitoringAdapter decayMonitoringAdapter;

    @Override
    public ExternalData handleRequest(ExternalData inputData) throws ProcessingFailedException {
        var metadata = inputData.getMetadata();
        var modelInputData = inputData.getModelData();
        decayMonitoringAdapter.exportInputData(inputData);

        var modelOutputData = modelAdapter.processWithModel(modelInputData);
        var outputData = ExternalDataDomainImpl.builder().metadata(metadata).modelData(modelOutputData).build();
        decayMonitoringAdapter.exportOutputData(outputData);

        return outputData;
    }
}
