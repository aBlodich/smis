package com.ablodich.smis.appointmentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "appointments_infos_attachments")
public class AppointmentInfoAttachment implements Serializable {

    @EmbeddedId
    private AppointmentInfoAttachmentId id;

    @Column(name = "original_file_id")
    private String originalFileId;

    @Column(name = "segmented_file_id")
    private String segmentedFileId;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        AppointmentInfoAttachment that = (AppointmentInfoAttachment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Getter
    @Setter
    @Embeddable
    public static class AppointmentInfoAttachmentId implements Serializable {

        @ManyToOne
        @JoinColumn(name = "appointment_info_id")
        private AppointmentInfo appointmentInfo;

        @Column(name = "diagnosis_task_id")
        private UUID diagnosisTaskId;

        public static AppointmentInfoAttachmentId of(final AppointmentInfo appointmentInfo, final UUID taskId) {
            AppointmentInfoAttachmentId id = new AppointmentInfoAttachmentId();
            id.setAppointmentInfo(appointmentInfo);
            id.setDiagnosisTaskId(taskId);
            return id;
        }


        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
                return false;
            }
            AppointmentInfoAttachmentId that = (AppointmentInfoAttachmentId) o;
            return getAppointmentInfo() != null && Objects.equals(getAppointmentInfo(), that.getAppointmentInfo()) && getDiagnosisTaskId() != null &&
                    Objects.equals(getDiagnosisTaskId(), that.getDiagnosisTaskId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(appointmentInfo, diagnosisTaskId);
        }
    }
}