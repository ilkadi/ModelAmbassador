package org.ehr.ambassador.domain.adapters;

import org.ehr.ambassador.domain.entities.ProcessingFailedException;

import java.util.Map;

public interface ModelAdapter {
    Map<String, Object> processWithModel(Map<String, Object> modelData) throws ProcessingFailedException;
}
