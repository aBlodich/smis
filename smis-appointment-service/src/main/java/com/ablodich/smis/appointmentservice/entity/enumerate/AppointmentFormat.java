package com.ablodich.smis.appointmentservice.entity.enumerate;

public enum AppointmentFormat {
    PAID("Платно"),
    FREE("Беслатно");

    private final String ruName;

    AppointmentFormat(final String ruName) {
        this.ruName = ruName;
    }
}
