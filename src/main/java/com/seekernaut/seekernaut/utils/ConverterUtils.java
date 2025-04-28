package com.seekernaut.seekernaut.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekernaut.seekernaut.exception.exceptions.ValidationException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Slf4j
public class ConverterUtils {

    /**
     * Converter objeto em json string
     */
    public static String objectToJson(Object object) {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            return MessageFormat.format("erro ao serializar objeto para formato json. {0}", e.getMessage());
        }
    }

    /**
     * Minimizar a string em json removendo os espaçamentos
     */
    public static String minimizeJson(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(jsonObject); // Compacta o JSON
        } catch (Exception e) {
            log.error("Failed to minimize JSON: {}", e.getMessage());
            return json;
        }
    }

    public static <T> T jsonToObject(String json, Class<T> tClass) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(json, tClass);
    }

    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> resultMap = new HashMap<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                // Verifica se o valor é uma instância de LocalDate
                if (value instanceof LocalDate) {
                    resultMap.put(field.getName(), value.toString()); // ou value para JSON
                } else {
                    resultMap.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new ValidationException("erro convert object to map: " + e.getMessage());
            }
        }
        return resultMap;
    }

}
