package disease_analysis_app.demo.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import disease_analysis_app.demo.dto.DiseaseRecordDto;
import disease_analysis_app.demo.entity.DiseaseRecord;
import disease_analysis_app.demo.service.DiseaseService;
import disease_analysis_app.demo.view_model.AnalysisResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @PostMapping("/ingest")
    @PreAuthorize("hasRole('ADMIN')") // Role-based
    public CompletableFuture<ResponseEntity<String>> ingestJson(@RequestBody List<DiseaseRecordDto> records) {
        return diseaseService.ingestAsync(records)
                .thenApply(saved -> ResponseEntity.ok("Ingested " + saved + " records"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @CircuitBreaker(name = "diseaseService", fallbackMethod = "getAllFallback")
    public List<DiseaseRecord> getAll() {
        return diseaseService.getAllWithAnalysis(); // Demonstrates JOIN FETCH
    }

    public List<DiseaseRecord> getAllFallback(Throwable t) {
        return List.of(new DiseaseRecord(0L, "Fallback Data", "N/A", 0, "N/A", "N/A", null));
    }

    @GetMapping("/analyze")
    public AnalysisResult analyze() {
        return diseaseService.analyzeWithStreams();
    }
}
