package org.ehr.ambassador.domain;

import org.ehr.ambassador.domain.entities.ExternalData;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;

public interface AmbassadorService {
    ExternalData handleRequest(ExternalData inputData) throws ProcessingFailedException;
}
