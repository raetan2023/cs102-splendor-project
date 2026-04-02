package com.splendor.player;

//tracks player's tokens, gold and card bonuses
// uses array indexes for colors (white = 0, blue = 1, green = 2, red = 3, black =4)
//gold treated separately 
public class PlayerAssets {

    private static final int TOKEN_LIMIT = 10; // can't hold more than 10 tokens total

    private int[] tokens;     
    private int goldTokens;   
    private int[] bonuses;    

    public PlayerAssets() {
        this.tokens = new int[5];
        this.goldTokens = 0;
        this.bonuses = new int[5];
    }

    // adds tokens to wallet (positive qty)
    //removes tokens from wallet (negative qty) but throws exception if qty > what the player has
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

    //removes 1 gold token, throws exception if no gold tokens are available
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

    //returns copy of tokens
    public int[] getTokens() {
        return tokens.clone();
    }

    // replaces whole token array at once
    public void setTokens(int[] tokens) {
        if (tokens == null || tokens.length != 5) {
            throw new IllegalArgumentException("Tokens must have exactly 5 entries.");
        }
        this.tokens = tokens.clone();
    }

    // calculates total tokens (including gold) to use to validate that player's total tokens are below 10 according to game rules
    public int getNumTokens() {
        int total = goldTokens;
        for (int token : tokens) {
            total += token;
        }
        return total;
    }

    //checks if player is over token limit
    public boolean aboveTenTokens() {
        return getNumTokens() > TOKEN_LIMIT;
    }

    // returns how many excess tokens player needs to discard to get to 10 
    public int getExcessCount() {
        return Math.max(0, getNumTokens() - TOKEN_LIMIT);
    }

    //when card is purchased, increases the bonus for the card's color by 1
    public void addBonus(int colorIndex) {
        validateColorIndex(colorIndex);
        bonuses[colorIndex]++;
    }

    public int getBonuses(int colorIndex) {
        validateColorIndex(colorIndex);
        return bonuses[colorIndex];
    }

    // returns copy of bonus array
    public int[] getBonuses() {
        return bonuses.clone();
    }

    /**
     * according to card's cost array, calculates how many gold tokens would be needed beyond color tokens and bonuses
     *
     * The logic:
     *   1. Subtract bonuses from the cost (card discounts apply first)
     *   2. Subtract tokens you have from what's left
     *   3. Anything still unpaid needs to come from gold
     *
     * compares this number against goldTokens to decide affordability.
     */
    public int goldNeeded(int[] cost) {
        if (cost == null || cost.length != 5) {
            throw new IllegalArgumentException("Cost must have exactly 5 entries.");
        }

        int needed = 0;
        for (int i = 0; i < cost.length; i++) {
            int payable = Math.max(0, cost[i] - bonuses[i]); 
            if (tokens[i] < payable) {
                needed += payable - tokens[i]; 
            }
        }
        return needed;
    }


    // catches if player tries to access array with out-of-bounds index to avoid errors
    private void validateColorIndex(int colorIndex) {
        if (colorIndex < 0 || colorIndex >= 5) {
            throw new IllegalArgumentException("Color index must be between 0 and 4.");
        }
    }
}
