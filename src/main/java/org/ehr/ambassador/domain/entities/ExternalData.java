package org.ehr.ambassador.domain.entities;

import java.util.Map;

public interface ExternalData {
    Map<String, Object> getMetadata();
    Map<String, Object> getModelData();
}
