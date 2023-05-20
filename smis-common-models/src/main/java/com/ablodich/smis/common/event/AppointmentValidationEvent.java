package com.ablodich.smis.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentValidationEvent {
    private UUID taskId;
    private UUID appointmentId;
}
