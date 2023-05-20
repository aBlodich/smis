package com.ablodich.smis.diagnostictaskrouterservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "checking_services")
public class CheckingService {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checking_service_id_seq")
    @SequenceGenerator(name = "checking_service_id_seq", sequenceName = "checking_service_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "service_name")
    private String serviceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_type_code", referencedColumnName = "code")
    private DiagnosisType diagnosisType;

    @ManyToMany(mappedBy = "checkingServices")
    private Set<DiagnosisTask> diagnosisTasks;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        CheckingService that = (CheckingService) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
