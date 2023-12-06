package org.ehr.ambassador.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExternalDataDomainImpl implements ExternalData {
    private Map<String, Object> metadata;
    private Map<String, Object> modelData;
}
