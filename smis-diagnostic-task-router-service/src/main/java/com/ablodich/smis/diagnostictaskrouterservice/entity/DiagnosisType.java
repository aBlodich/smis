package com.ablodich.smis.diagnostictaskrouterservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "diagnosis_types")
public class DiagnosisType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diagnosis_types_id_seq")
    @SequenceGenerator(name = "diagnosis_types_id_seq", sequenceName = "diagnosis_types_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @OneToMany(mappedBy = "diagnosisType")
    private Set<DiagnosisTask> diagnosisTasks;

    @OneToMany(mappedBy = "diagnosisType")
    private Set<CheckingService> checkingServices;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DiagnosisType that = (DiagnosisType) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
