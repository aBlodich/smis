package com.ablodich.smis.appointmentservice.entity.enumerate;

public enum AppointmentType {
    PRIMARY("Первичный"),
    SECONDARY("Вторичный");

    private final String ruName;

    AppointmentType(final String ruName) {
        this.ruName = ruName;
    }
}
