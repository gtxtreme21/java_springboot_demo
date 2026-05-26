package disease_analysis_app.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class AnalysisReport {
    @Id @GeneratedValue
    private Long id;
    private String summary;

    @OneToMany(mappedBy = "analysisReport", fetch = FetchType.LAZY)
    private List<DiseaseRecord> records = new ArrayList<>();
}
