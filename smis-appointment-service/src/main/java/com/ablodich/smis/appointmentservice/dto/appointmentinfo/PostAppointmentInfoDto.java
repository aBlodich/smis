package com.ablodich.smis.appointmentservice.dto.appointmentinfo;

import java.io.Serializable;

public record PostAppointmentInfoDto(String inspection,
                                     String diagnose,
                                     String recommendations) implements Serializable {

}
