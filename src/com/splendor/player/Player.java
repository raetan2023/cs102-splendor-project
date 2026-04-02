package com.splendor.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.model.Noble;


//represents a single player (can be human or AI)
//tracks the player's points, tokens, cards and noble visits
public class Player {

    private static final int RESERVED_CARD_LIMIT = 3; // Game rule: hand size cap

    private String name;
    private int prestigePoints;
    private PlayerAssets wallet;          //  refers to PlayerAssets class (with tokens and card bonuses)
    private List<DevelopmentCard> ownedCards;    //player's owned cards
    private List<DevelopmentCard> reservedCards; // player's reserved cards
    private List<Noble> visitedBy;               // Nobles that have visited this player

    public Player(String name) {
        this.name = name;
        this.prestigePoints = 0;
        this.wallet = new PlayerAssets();
        this.ownedCards = new ArrayList<>();
        this.reservedCards = new ArrayList<>();
        this.visitedBy = new ArrayList<>();
    }

    public void addPoints(int points) {
        prestigePoints += points;
    }

    public void addPrestigePoints(int points) {
        addPoints(points);
    }

    //adds card to the reserved hand
    //throws exception if reserved card limit is reached 
    public void reserve(DevelopmentCard card) {
        if (reservedCards.size() >= RESERVED_CARD_LIMIT) {
            throw new IllegalStateException("A player cannot reserve more than 3 cards.");
        }
        reservedCards.add(card);
    }

    //alternative safer reserving method 
    //returns false if limit is reached instead of throwing exception
    public boolean reserveCard(DevelopmentCard card) {
        if (reservedCards.size() >= RESERVED_CARD_LIMIT) {
            return false;
        }
        reserve(card);
        return true;
    }

    public void addOwnedCard(DevelopmentCard card) {
        ownedCards.add(card);
    }

    // moves card out of reserved hand to purchase
    public boolean removeReservedCard(DevelopmentCard card) {
        return reservedCards.remove(card);
    }
    
    //checks player bonuses to see if they can afford nobles
    public boolean canAfford(Noble noble) {
        List<GemColor> colors = noble.getRequirementColors();
        List<Integer> qtys = noble.getRequirementQty();

        for (int i = 0; i < colors.size(); i++) {
            int requiredQty = qtys.get(i);
            int colorIndex = toColorIndex(colors.get(i));
            if (wallet.getBonuses(colorIndex) < requiredQty) {
                return false;
            }
        }

        return true;
    }

    // when noble visits player, adds them to the list and gives player the nobel's prestige points
    public void addNoble(Noble noble) {
        visitedBy.add(noble);
        addPoints(noble.getPoints());
    }

    // adds tokens to the player's wallet (their assets) 
    public void addToken(GemColor gem, int amount) {
        if (gem == GemColor.GOLD) {
            for (int i = 0; i < amount; i++) {
                wallet.addGoldToken();
            }
            return;
        }
        wallet.addToken(toColorIndex(gem), amount);
    }

    // spends tokens from wallet, throws excpetion if the amount to spend is negative (invalid input)
    public void spendToken(GemColor gem, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        if (gem == GemColor.GOLD) {
            for (int i = 0; i < amount; i++) {
                wallet.useGoldToken();
            }
            return;
        }

        wallet.addToken(toColorIndex(gem), -amount); // Negative = remove
    }

    // alternative to spendToken with different name to make easy to call
    public void removeToken(GemColor gem, int amount) {
        spendToken(gem, amount);
    }

    public int getTokenCount(GemColor gem) {
        if (gem == GemColor.GOLD) {
            return wallet.getGoldTokens();
        }
        return wallet.getTokens(toColorIndex(gem));
    }

    public int getTotalTokens() {
        return wallet.getNumTokens();
    }

    // gives unmodifiable summary of all token colors 
    public Map<GemColor, Integer> getTokens() {
        Map<GemColor, Integer> tokenView = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            tokenView.put(color, getTokenCount(color));
        }
        return Collections.unmodifiableMap(tokenView);
    }

    //returns how many card bonuses the player has for each color (always 0 for gold tokens)
    public Map<GemColor, Integer> getBonuses() {
        Map<GemColor, Integer> bonusView = new EnumMap<>(GemColor.class);
        for (int i = 0; i < 5; i++) {
            bonusView.put(GemColor.values()[i], wallet.getBonuses(i));
        }
        bonusView.put(GemColor.GOLD, 0);
        return Collections.unmodifiableMap(bonusView);
    }

    // -getters-

    public String getName() { return name; }
    public int getPrestigePoints() { return prestigePoints; }
    public PlayerAssets getWallet() { return wallet; }
    public List<DevelopmentCard> getOwnedCards() { return ownedCards; }
    public List<Noble> getVisitedBy() { return visitedBy; }
    public List<Noble> getNobles() { return getVisitedBy(); } // Alias — same list, friendlier name

    // shows view of player's reserved card (unmodifiable - only for the player to see)
    public List<DevelopmentCard> getReservedCards() {
        return Collections.unmodifiableList(reservedCards);
    }

    // helper methods

    //converts gem colors to index so they are easier to work with (gold not included, dealt with separately)
    private int toColorIndex(GemColor color) {
        switch (color) {
            case WHITE: return 0;
            case BLUE:  return 1;
            case GREEN: return 2;
            case RED:   return 3;
            case BLACK: return 4;
            default:
                throw new IllegalArgumentException("Gold is not part of the standard token array.");
        }
    }
}
