package com.ablodich.smis.appointmentservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class StringConstants {
    public static final String APPOINTMENT_NOT_FOUND = "Не найдена запись к врачу с ID = ";
    public static final String APPOINTMENT_INFO_NOT_FOUND = "Не найдена информация о записи к врачу с ID = ";
}
