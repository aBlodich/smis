package com.ablodich.smis.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentValidationEventResult {
    private UUID taskId;
    private Boolean isValid;
}
