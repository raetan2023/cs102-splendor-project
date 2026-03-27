package com.splendor.config;

import java.io.*;
import java.util.*;

public class PropertiesConfigLoader implements ConfigLoader{

    @Override
    public GameConfig load(String path) {
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(path)) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + path);
        }

        int targetPrestige = getRequiredInt(props, "win.prestige.points");
        int maxTokensPerPlayer = getRequiredInt(props, "max.tokens.per.player");

        int gemCount2Players = getRequiredInt(props, "gems.2players");
        int gemCount3Players = getRequiredInt(props, "gems.3players");
        int gemCount4Players = getRequiredInt(props, "gems.4players");
        int goldTokenCount = getRequiredInt(props, "gems.gold");

        String developmentCardsPath = getRequiredProperty(props, "data.development_cards");
        String noblesPath = getRequiredProperty(props, "data.nobles");

        return new GameConfig(
            targetPrestige,
            maxTokensPerPlayer,
            gemCount2Players,
            gemCount3Players,
            gemCount4Players,
            goldTokenCount,
            developmentCardsPath,
            noblesPath
        );
    }

    private String getRequiredProperty(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required config key: " + key);
        }
        return value.trim();
    }

    private int getRequiredInt(Properties props, String key) {
        String value = getRequiredProperty(props, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for config key: " + key + " = " + value);
        }
    }
}
