package com.ablodich.smis.brainmriclassificationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "ml_models")
public class MlModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ml_models_id_seq")
    @SequenceGenerator(name = "ml_models_id_seq", sequenceName = "ml_models_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "options")
    private String options;

    @Column(name = "active")
    private Boolean active;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        MlModel mlModel = (MlModel) o;
        return getId() != null && Objects.equals(getId(), mlModel.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
