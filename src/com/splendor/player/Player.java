package com.splendor.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.model.Noble;

/**
 * Represents a single player in the game — human or AI.
 * Tracks everything they own: points, tokens, cards, and noble visits.
 *
 * The actual gem/bonus math lives in PlayerAssets (the wallet).
 * This class is the public face that the rest of the game talks to.
 */
public class Player {

    private static final int RESERVED_CARD_LIMIT = 3; // Game rule: hand size cap

    private String name;
    private int prestigePoints;
    private PlayerAssets wallet;          // Holds tokens and card bonuses
    private List<DevelopmentCard> ownedCards;    // Cards permanently bought
    private List<DevelopmentCard> reservedCards; // Cards held aside (max 3)
    private List<Noble> visitedBy;               // Nobles that have visited this player

    public Player(String name) {
        this.name = name;
        this.prestigePoints = 0;
        this.wallet = new PlayerAssets();
        this.ownedCards = new ArrayList<>();
        this.reservedCards = new ArrayList<>();
        this.visitedBy = new ArrayList<>();
    }

    // --- Points ---

    public void addPoints(int points) {
        prestigePoints += points;
    }

    /** Alias for addPoints — exists so call sites can be more expressive about intent. */
    public void addPrestigePoints(int points) {
        addPoints(points);
    }

    // --- Cards ---

    /**
     * Adds a card to the reserved hand. Throws if the hand is already full.
     * Internal version — callers outside this class should use reserveCard() instead,
     * which returns false rather than throwing.
     */
    public void reserve(DevelopmentCard card) {
        if (reservedCards.size() >= RESERVED_CARD_LIMIT) {
            throw new IllegalStateException("A player cannot reserve more than 3 cards.");
        }
        reservedCards.add(card);
    }

    /**
     * Safer public version of reserve() — returns false instead of throwing
     * if the hand is full. Use this from game logic.
     */
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

    /** Moves a card out of the reserved hand (e.g. when buying it). */
    public boolean removeReservedCard(DevelopmentCard card) {
        return reservedCards.remove(card);
    }

    // --- Nobles ---

    /**
     * Checks if this player has enough card bonuses to satisfy a noble's requirements.
     * Nobles care about bonuses (permanent card discounts), NOT raw tokens.
     */
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

    /** Noble visits the player: add them to the list and grant their prestige points. */
    public void addNoble(Noble noble) {
        visitedBy.add(noble);
        addPoints(noble.getPoints());
    }

    // --- Tokens ---

    /**
     * Adds tokens to the player's wallet.
     * Gold is handled separately because it's stored differently in PlayerAssets.
     */
    public void addToken(GemColor gem, int amount) {
        if (gem == GemColor.GOLD) {
            for (int i = 0; i < amount; i++) {
                wallet.addGoldToken();
            }
            return;
        }
        wallet.addToken(toColorIndex(gem), amount);
    }

    /**
     * Spends tokens from the wallet. Uses negative amounts internally on the wallet,
     * which is why gold needs its own path (wallet.useGoldToken handles it properly).
     */
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

    /** Alias for spendToken — same thing, different name for readability at call sites. */
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

    /**
     * Returns a snapshot of all token counts as a color → amount map.
     * Unmodifiable so callers can't accidentally mutate wallet state.
     */
    public Map<GemColor, Integer> getTokens() {
        Map<GemColor, Integer> tokenView = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            tokenView.put(color, getTokenCount(color));
        }
        return Collections.unmodifiableMap(tokenView);
    }

    /**
     * Returns card bonuses per color (i.e. permanent discounts from owned cards).
     * Gold bonus is always 0 — there's no such thing as a gold card bonus in Splendor.
     * Unmodifiable for the same reason as getTokens().
     */
    public Map<GemColor, Integer> getBonuses() {
        Map<GemColor, Integer> bonusView = new EnumMap<>(GemColor.class);
        for (int i = 0; i < 5; i++) {
            bonusView.put(GemColor.values()[i], wallet.getBonuses(i));
        }
        bonusView.put(GemColor.GOLD, 0);
        return Collections.unmodifiableMap(bonusView);
    }

    // --- Getters ---

    public String getName() { return name; }
    public int getPrestigePoints() { return prestigePoints; }
    public PlayerAssets getWallet() { return wallet; }
    public List<DevelopmentCard> getOwnedCards() { return ownedCards; }
    public List<Noble> getVisitedBy() { return visitedBy; }
    public List<Noble> getNobles() { return getVisitedBy(); } // Alias — same list, friendlier name

    /** Returns reserved cards as an unmodifiable view — use reserveCard/removeReservedCard to mutate. */
    public List<DevelopmentCard> getReservedCards() {
        return Collections.unmodifiableList(reservedCards);
    }

    // --- Internal helpers ---

    /**
     * Converts a GemColor to the 0-4 index used by PlayerAssets' internal arrays.
     * Gold is intentionally excluded — it's stored separately and passing it here is a bug.
     */
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
