package com.ablodich.smis.diagnostictaskrouterservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Constants {
    public static String DIAGNOSIS_TYPE_NOT_FOUND = "Не найден тип диагностики с id = ";
    public static String DIAGNOSIS_TYPE_NOT_FOUND_BY_CODE = "Не найден тип диагностики с code = ";
    public static String CHECKING_SERVICE_NOT_FOUND = "Не найден сервис проверки с id = ";
    public static String CHECKING_SERVICE_NOT_FOUND_BY_CODE = "Не найден сервис проверки с code = ";

    public static String DIAGNOSIS_TASK_NOT_EXISTS = "Отсувствует задача на проверку с id = ";
}
