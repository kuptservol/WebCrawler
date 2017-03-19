package ru.skuptsov.robot.util.properties;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Properties;

import static java.util.stream.Collectors.toMap;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class PropUtils {
    public static Map<String, String> asMap(@NotNull Properties properties) {
        return properties.entrySet().stream()
                .collect(toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
    }
}
