package com.splendor.config;

import java.io.*;
import java.util.*;

public class PropertiesConfigLoader implements ConfigLoader{

    @Override
    public GameConfig load(String path) {
        // using Properties to load
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(path)) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + path);
        }
        // int values
        int targetPrestige = getRequiredInt(props, "win.prestige.points");
        int maxTokensPerPlayer = getRequiredInt(props, "max.tokens.per.player");

        int gemCount2Players = getRequiredInt(props, "gems.2players");
        int gemCount3Players = getRequiredInt(props, "gems.3players");
        int gemCount4Players = getRequiredInt(props, "gems.4players");
        int goldTokenCount = getRequiredInt(props, "gems.gold");
        // String values (filepaths)
        String developmentCardsPath = getRequiredProperty(props, "data.development_cards");
        String noblesPath = getRequiredProperty(props, "data.nobles");

        // initialize and return GameConfig
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
        // getProperty is a method in Property to get the key value defined in config.properties file
        String value = props.getProperty(key);
        // parse String and return
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required config key: " + key);
        }
        return value.trim();
    }

    private int getRequiredInt(Properties props, String key) {
        // get the String expression for the integer required
        String value = getRequiredProperty(props, key);
        // parse value and return int value
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for config key: " + key + " = " + value);
        }
    }
}
