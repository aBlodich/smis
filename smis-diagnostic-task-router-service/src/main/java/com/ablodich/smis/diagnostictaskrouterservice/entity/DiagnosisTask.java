package com.ablodich.smis.diagnostictaskrouterservice.entity;

import com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "diagnosis_tasks")
public class DiagnosisTask implements Serializable {
    @Id
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "error_description")
    private String errorDescription;

    @Column(name = "wait_for_appointment_validation")
    private Boolean waitForAppointmentValidation;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "original_file_id")
    private String originalFileId;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private DiagnosisTaskState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_type_code", referencedColumnName = "code")
    private DiagnosisType diagnosisType;

    @ManyToMany
    @JoinTable(name = "diagnosis_tasks_checking_services",
               joinColumns = @JoinColumn(name = "diagnosis_task_id"),
               inverseJoinColumns = @JoinColumn(name = "checking_service_id"))
    private Set<CheckingService> checkingServices;

    @OneToMany(mappedBy = "diagnosisTask", cascade = CascadeType.ALL)
    private Set<DiagnosisTaskResult> results;

    @PrePersist
    private void fillDefaults() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        this.createdAt = OffsetDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DiagnosisTask that = (DiagnosisTask) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
