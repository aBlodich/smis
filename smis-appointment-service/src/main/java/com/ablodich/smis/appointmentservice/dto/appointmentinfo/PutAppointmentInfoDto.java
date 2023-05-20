package com.ablodich.smis.appointmentservice.dto.appointmentinfo;

import java.io.Serializable;
import java.util.UUID;

public record PutAppointmentInfoDto(UUID id,
                                    String inspection,
                                    String diagnose,
                                    String recommendations) implements Serializable {

}
