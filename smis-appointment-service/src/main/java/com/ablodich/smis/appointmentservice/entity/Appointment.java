package com.ablodich.smis.appointmentservice.entity;

import com.ablodich.smis.appointmentservice.entity.enumerate.AppointmentFormat;
import com.ablodich.smis.appointmentservice.entity.enumerate.AppointmentType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment implements Serializable {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name ="doctor_id")
    private UUID doctorId;

    @Column(name = "doctor_specialization_id")
    private Integer doctorSpecializationId;

    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AppointmentType type;

    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private AppointmentFormat format;

    @OneToOne(mappedBy = "appointment")
    private AppointmentInfo appointmentInfo;

    @PrePersist
    void prePersist() {
        this.id = UUID.randomUUID();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Appointment that = (Appointment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
