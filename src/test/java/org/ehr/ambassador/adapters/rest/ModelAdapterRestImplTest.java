package org.ehr.ambassador.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.ehr.ambassador.domain.adapters.ModelAdapter;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.ehr.ambassador.config.ModelAdapterConfig.URL_PORT_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ModelAdapterRestImplTest {

    public static MockWebServer mockBackEnd;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ModelAdapter modelAdapter;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format(URL_PORT_TEMPLATE, "http://localhost", mockBackEnd.getPort());
        WebClient testWebClient = WebClient.builder().baseUrl(baseUrl).build();
        modelAdapter = new ModelAdapterRestImpl(testWebClient);
        ReflectionTestUtils.setField(modelAdapter, "modelTimeout", 1000);
    }

    @Test
    @DisplayName("Should post body to the model and get the json body response")
    void callOk() throws Exception {
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        Map<String, Object> modelOutputData = Map.of("score", 0.873);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(modelOutputData))
                .addHeader("Content-Type", "application/json"));

        var modelActualOutputData = modelAdapter.processWithModel(modelInputData);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/predict", recordedRequest.getPath());
        assertEquals(modelOutputData, modelActualOutputData);
    }

    @Test
    @DisplayName("Should throw exception if JSON header not present")
    void noJsonBody() throws Exception {
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        Map<String, Object> modelOutputData = Map.of("score", 0.873);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(modelOutputData)));

        assertThrows(ProcessingFailedException.class, () -> modelAdapter.processWithModel(modelInputData));
    }

    @Test
    @DisplayName("Should throw exception if there is no response")
    void shouldTimeout() {
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        assertThrows(ProcessingFailedException.class, () -> modelAdapter.processWithModel(modelInputData));
    }

    @Test
    @DisplayName("Should throw exception if response is an error")
    void errorModelResponse() throws Exception {
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        Map<String, Object> modelOutputData = Map.of("score", 0.873);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(modelOutputData))
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .addHeader("Content-Type", "application/json"));

        assertThrows(ProcessingFailedException.class, () -> modelAdapter.processWithModel(modelInputData));
    }
}
