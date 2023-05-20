package com.ablodich.smis.diagnostictaskrouterservice.entity;

import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventType;
import com.ablodich.smis.common.event.enumerate.PredictionResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "diagnosis_task_results")
public class DiagnosisTaskResult {
    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction")
    private PredictionResult prediction;

    @Column(name = "segmented_file_id")
    private String segmentedFileId;

    @Column(name = "active")
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private DiagnosisTask diagnosisTask;

    @PrePersist
    private void fillDefaults() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DiagnosisTaskResult that = (DiagnosisTaskResult) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
