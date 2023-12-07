package org.ehr.ambassador.ports.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.ambassador.domain.AmbassadorService;
import org.ehr.ambassador.domain.entities.ExternalData;
import org.ehr.ambassador.ports.rest.entities.ExternalDataRestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("v1/model-ambassador")
@RequiredArgsConstructor
public class ModelAmbassadorRestController {

    @Autowired
    private AmbassadorService ambassadorService;

    @PostMapping("/process-with-model")
    public ResponseEntity<ExternalData> processWithModel(@RequestBody ExternalDataRestImpl jsonData) {
        try {
            ExternalData outputData = ambassadorService.handleRequest(jsonData);
            return ResponseEntity.ok().body(outputData);
        } catch (Exception e) {
            log.error("Failed to process model request: ", e);
        }
        return ResponseEntity.unprocessableEntity().build();
    }
}
