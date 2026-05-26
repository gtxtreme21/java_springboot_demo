package disease_analysis_app.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import disease_analysis_app.demo.entity.DiseaseRecord;

public interface DiseaseRecordRepository extends JpaRepository<DiseaseRecord, Long> {

    // 1. JOIN FETCH solution
    @Query("SELECT d FROM DiseaseRecord d JOIN FETCH d.analysisReport")
    List<DiseaseRecord> findAllWithAnalysis();

    // 2. EntityGraph solution
    @EntityGraph(attributePaths = {"analysisReport"})
    List<DiseaseRecord> findAllWithGraph();
}
