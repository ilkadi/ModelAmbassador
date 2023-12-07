package org.ehr.ambassador.ports.rest;

import lombok.Builder;
import lombok.Data;
import org.ehr.ambassador.domain.entities.ExternalData;

import java.util.Map;

@Data
@Builder
public class ExternalDataTestImpl implements ExternalData {
    private Map<String, Object> metadata;
    private Map<String, Object> modelData;
}
