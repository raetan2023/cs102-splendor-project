package com.splendor.player;

/**
 * The player's wallet — tracks their tokens, gold, and card bonuses.
 *
 * Everything here uses raw int arrays indexed 0-4 (WHITE=0, BLUE=1, GREEN=2, RED=3, BLACK=4).
 * Gold is kept separate because it behaves differently: it's a wild token you
 * spend automatically to cover gaps, not a color you collect normally.
 *
 * This class does pure math and validation — it has no knowledge of GemColor
 * or game rules beyond the 10-token limit. Player.java handles the translation.
 */
public class PlayerAssets {

    private static final int TOKEN_LIMIT = 10; // Game rule: can't hold more than 10 tokens total

    private int[] tokens;     // Colored gem tokens [0..4]
    private int goldTokens;   // Wild tokens (stored separately)
    private int[] bonuses;    // Permanent discounts from owned cards [0..4]

    public PlayerAssets() {
        this.tokens = new int[5];
        this.goldTokens = 0;
        this.bonuses = new int[5];
    }

    // --- Tokens ---

    /**
     * Adds (or removes, if qty is negative) colored tokens at the given index.
     * Going below zero is rejected — you can't spend what you don't have.
     */
    public void addToken(int colorIndex, int qty) {
        validateColorIndex(colorIndex);
        int updated = tokens[colorIndex] + qty;
        if (updated < 0) {
            throw new IllegalArgumentException("Token count cannot be negative.");
        }
        tokens[colorIndex] = updated;
    }

    public void addGoldToken() {
        goldTokens++;
    }

    /** Removes one gold token. Throws if there are none left — caller should check first. */
    public void useGoldToken() {
        if (goldTokens <= 0) {
            throw new IllegalStateException("No gold tokens available.");
        }
        goldTokens--;
    }

    public int getTokens(int colorIndex) {
        validateColorIndex(colorIndex);
        return tokens[colorIndex];
    }

    public int getGoldTokens() {
        return goldTokens;
    }

    /** Returns a copy of the token array — safe to modify without affecting internal state. */
    public int[] getTokens() {
        return tokens.clone();
    }

    /**
     * Replaces the entire token array at once.
     * Mainly useful for testing or loading saved state.
     * Input is cloned so the caller can't hold a reference to our internals.
     */
    public void setTokens(int[] tokens) {
        if (tokens == null || tokens.length != 5) {
            throw new IllegalArgumentException("Tokens must have exactly 5 entries.");
        }
        this.tokens = tokens.clone();
    }

    /** Total tokens held including gold — used to check the 10-token rule. */
    public int getNumTokens() {
        int total = goldTokens;
        for (int token : tokens) {
            total += token;
        }
        return total;
    }

    public boolean aboveTenTokens() {
        return getNumTokens() > TOKEN_LIMIT;
    }

    /** How many tokens the player needs to discard to get back to 10. */
    public int getExcessCount() {
        return Math.max(0, getNumTokens() - TOKEN_LIMIT);
    }

    // --- Bonuses (permanent discounts from owned cards) ---

    /** Called when a card is purchased — bumps the bonus for that card's color by 1. */
    public void addBonus(int colorIndex) {
        validateColorIndex(colorIndex);
        bonuses[colorIndex]++;
    }

    public int getBonuses(int colorIndex) {
        validateColorIndex(colorIndex);
        return bonuses[colorIndex];
    }

    /** Returns a copy of the bonus array — safe to read without exposing internals. */
    public int[] getBonuses() {
        return bonuses.clone();
    }

    // --- Affordability ---

    /**
     * Given a card's cost array, calculates how many gold tokens would be needed
     * to cover what colored tokens + bonuses can't pay for.
     *
     * The logic per color:
     *   1. Subtract bonuses from the cost (card discounts apply first)
     *   2. Subtract tokens you have from what's left
     *   3. Anything still unpaid needs to come from gold
     *
     * The caller then compares this number against goldTokens to decide affordability.
     */
    public int goldNeeded(int[] cost) {
        if (cost == null || cost.length != 5) {
            throw new IllegalArgumentException("Cost must have exactly 5 entries.");
        }

        int needed = 0;
        for (int i = 0; i < cost.length; i++) {
            int payable = Math.max(0, cost[i] - bonuses[i]); // After discount, what do we still owe?
            if (tokens[i] < payable) {
                needed += payable - tokens[i]; // Tokens cover what they can; gold covers the rest
            }
        }
        return needed;
    }

    // --- Internal ---

    /** Guards all array accesses — catches out-of-range indices before they cause silent bugs. */
    private void validateColorIndex(int colorIndex) {
        if (colorIndex < 0 || colorIndex >= 5) {
            throw new IllegalArgumentException("Color index must be between 0 and 4.");
        }
    }
}
