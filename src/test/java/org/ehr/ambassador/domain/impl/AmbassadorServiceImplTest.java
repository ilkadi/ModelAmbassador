package org.ehr.ambassador.domain.impl;

import org.ehr.ambassador.domain.AmbassadorService;
import org.ehr.ambassador.domain.adapters.DecayMonitoringAdapter;
import org.ehr.ambassador.domain.adapters.ModelAdapter;
import org.ehr.ambassador.domain.entities.ExternalData;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmbassadorServiceImplTest {
    @Mock
    private ModelAdapter modelAdapter;
    @Mock
    private DecayMonitoringAdapter decayMonitoringAdapter;

    private AmbassadorService ambassadorService;

    @BeforeEach
    public void setup() {
        reset(modelAdapter, decayMonitoringAdapter);
        ambassadorService = new AmbassadorServiceImpl(modelAdapter, decayMonitoringAdapter);
    }

    @Test
    @DisplayName("Should relay data in and out from the model, export both")
    public void happyPath() throws ProcessingFailedException {
        Map<String, Object> metadata = Map.of("run-id", "123");
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        Map<String, Object> modelOutputData = Map.of("score", 0.873);
        var inputData = mock(ExternalData.class);

        when(inputData.getMetadata()).thenReturn(metadata);
        when(inputData.getModelData()).thenReturn(modelInputData);
        when(modelAdapter.processWithModel(modelInputData)).thenReturn(modelOutputData);

        var outputData = ambassadorService.handleRequest(inputData);

        assertEquals(metadata, outputData.getMetadata());
        assertEquals(modelOutputData, outputData.getModelData());
        verify(decayMonitoringAdapter, times(1))
                .exportInputData(inputData);
        verify(decayMonitoringAdapter, times(1))
                .exportOutputData(outputData);
    }

    @Test
    @DisplayName("Should throw exception when input's data export fails")
    public void unhappyPathCannotExportInputData() throws ProcessingFailedException {
        var inputData = mock(ExternalData.class);

        doThrow(new ProcessingFailedException("Kaboom!")).when(decayMonitoringAdapter)
                .exportInputData(inputData);

        assertThrows(ProcessingFailedException.class, () -> ambassadorService.handleRequest(inputData));
    }

    @Test
    @DisplayName("Should throw exception when model adapter call fails")
    public void unhappyPathCannotProcessWithModel() throws ProcessingFailedException {
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        var inputData = mock(ExternalData.class);
        when(inputData.getModelData()).thenReturn(modelInputData);

        doThrow(new ProcessingFailedException("Kaboom!")).when(modelAdapter)
                .processWithModel(modelInputData);

        assertThrows(ProcessingFailedException.class, () -> ambassadorService.handleRequest(inputData));
    }

    @Test
    @DisplayName("Should throw exception when output's data export fails")
    public void unhappyPathCannotExportOutputData() throws ProcessingFailedException {
        Map<String, Object> metadata = Map.of("run-id", "123");
        Map<String, Object> modelInputData = Map.of("key-1", 100, "key-2", "hmm");
        Map<String, Object> modelOutputData = Map.of("score", 0.873);
        var inputData = mock(ExternalData.class);

        when(inputData.getMetadata()).thenReturn(metadata);
        when(inputData.getModelData()).thenReturn(modelInputData);
        when(modelAdapter.processWithModel(modelInputData)).thenReturn(modelOutputData);

        doThrow(new ProcessingFailedException("Kaboom!")).when(decayMonitoringAdapter)
                .exportOutputData(any());

        assertThrows(ProcessingFailedException.class, () -> ambassadorService.handleRequest(inputData));
    }
}
