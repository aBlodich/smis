package com.ablodich.smis.appointmentservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "appointments_infos")
public class AppointmentInfo implements Serializable {

    @Id
    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "inspection")
    private String inspection;

    @Column(name = "diagnose")
    private String diagnose;

    @Column(name = "recommendations")
    private String recommendations;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", referencedColumnName = "id")
    private Appointment appointment;

    @OneToMany(mappedBy = "id.appointmentInfo", cascade = {CascadeType.ALL})
    private Set<AppointmentInfoAttachment> attachments;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        AppointmentInfo that = (AppointmentInfo) o;
        return getAppointmentId() != null && Objects.equals(getAppointmentId(), that.getAppointmentId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
