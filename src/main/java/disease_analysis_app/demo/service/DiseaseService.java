package disease_analysis_app.demo.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import disease_analysis_app.demo.dto.DiseaseRecordDto;
import disease_analysis_app.demo.entity.DiseaseRecord;
import disease_analysis_app.demo.external_data.ExternalDataClient;
import disease_analysis_app.demo.repository.DiseaseRecordRepository;
import disease_analysis_app.demo.view_model.AnalysisResult;
import lombok.RequiredArgsConstructor;

@Service
@EnableAsync
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRecordRepository repository;
    @SuppressWarnings("unused")
    private final ExternalDataClient externalClient; // Mock external integration

    @Async
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<Integer> ingestAsync(List<DiseaseRecordDto> dtos) {
        List<DiseaseRecord> entities = dtos.stream()
                .map(this::toEntity)
                .toList();
        return CompletableFuture.completedFuture(repository.saveAll(entities).size());
    }

    // Propagation explanation in comments below
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public void performCriticalAnalysis() { /* ... */ }

    public List<DiseaseRecord> getAllWithAnalysis() {
        return repository.findAllWithAnalysis(); // JOIN FETCH avoids N+1
    }

    public AnalysisResult analyzeWithStreams() {
        List<DiseaseRecord> records = repository.findAll();
        double avgSeverity = records.stream()
                .mapToInt(DiseaseRecord::getSeverity)
                .average()
                .orElse(0.0);

        Map<String, Long> byDisease = records.stream()
                .collect(Collectors.groupingBy(DiseaseRecord::getDiseaseName, Collectors.counting()));

        return new AnalysisResult(avgSeverity, byDisease);
    }

    private DiseaseRecord toEntity(DiseaseRecordDto dto) {
        DiseaseRecord entity = new DiseaseRecord();
        return entity;
    }    
}
