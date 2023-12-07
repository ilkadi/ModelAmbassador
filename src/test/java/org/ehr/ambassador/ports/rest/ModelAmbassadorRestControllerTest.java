package org.ehr.ambassador.ports.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehr.ambassador.domain.AmbassadorService;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ModelAmbassadorRestControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AmbassadorService ambassadorService;

    @Test
    @DisplayName("Should relay external message to the ambassador service and include model response back")
    void happyPath() throws Exception {
        Map<String, Object> metadata = Map.of("run-id", "123");
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        Map<String, Object> modelOutputData = Map.of("score", 0.873);

        var inputData = ExternalDataTestImpl.builder().metadata(metadata).modelData(modelInputData).build();
        var outputData = ExternalDataTestImpl.builder().metadata(metadata).modelData(modelOutputData).build();

        when(ambassadorService.handleRequest(any())).thenReturn(outputData);
        mockMvc.perform(
                        post("/v1/model-ambassador/process-with-model")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(outputData)));
    }

    @Test
    @DisplayName("Should return unprocessable entity on ambassador errors")
    void unhappyPath() throws Exception {
        Map<String, Object> metadata = Map.of("run-id", "123");
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");

        var inputData = ExternalDataTestImpl.builder().metadata(metadata).modelData(modelInputData).build();

        doThrow(new ProcessingFailedException("Kaboom!"))
                .when(ambassadorService).handleRequest(any());

        mockMvc.perform(
                        post("/v1/model-ambassador/process-with-model")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputData)))
                .andExpect(status().isUnprocessableEntity());
    }
}
