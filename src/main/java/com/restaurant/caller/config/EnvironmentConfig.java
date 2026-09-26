package com.restaurant.caller.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentConfig {

    private static final Dotenv dotenv =
            Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

    public static String get(String name) {

        String value = System.getenv(name);

        if (value != null && !value.isBlank()) {
            return value;
        }

        value = dotenv.get(name);

        if (value != null && !value.isBlank()) {
            return value;
        }

        return null;
    }
}