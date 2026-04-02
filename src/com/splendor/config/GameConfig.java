package com.splendor.config;

public class GameConfig {
    // these are all attributes that GameConfig stores for future reference
    private int targetPrestige;
    private int maxTokensPerPlayer;

    private int gemCount2Players;
    private int gemCount3Players;
    private int gemCount4Players;
    private int goldTokenCount;

    private String developmentCardsPath;
    private String noblesPath;

    // constuctor
    public GameConfig(
            int targetPrestige,
            int maxTokensPerPlayer,
            int gemCount2Players,
            int gemCount3Players,
            int gemCount4Players,
            int goldTokenCount,
            String developmentCardsPath,
            String noblesPath) {

        this.targetPrestige = targetPrestige;
        this.maxTokensPerPlayer = maxTokensPerPlayer;
        this.gemCount2Players = gemCount2Players;
        this.gemCount3Players = gemCount3Players;
        this.gemCount4Players = gemCount4Players;
        this.goldTokenCount = goldTokenCount;
        this.developmentCardsPath = developmentCardsPath;
        this.noblesPath = noblesPath;
    }

    // getters
    public int getTargetPrestige() {
        return targetPrestige;
    }

    public int getMaxTokensPerPlayer() {
        return maxTokensPerPlayer;
    }

    public int getGemCount2Players() {
        return gemCount2Players;
    }

    public int getGemCount3Players() {
        return gemCount3Players;
    }

    public int getGemCount4Players() {
        return gemCount4Players;
    }

    public int getGoldTokenCount() {
        return goldTokenCount;
    }

    public String getDevelopmentCardsPath() {
        return developmentCardsPath;
    }

    public String getNoblesPath() {
        return noblesPath;
    }

    public int getGemCountForPlayers(int numPlayers) {
        switch (numPlayers) {
            case 2:
                return gemCount2Players;
            case 3:
                return gemCount3Players;
            case 4:
                return gemCount4Players;
            default:
                throw new IllegalArgumentException("Unsupported number of players: " + numPlayers);
        }
    }
}
