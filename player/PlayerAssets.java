package player;

public class PlayerAssets {

    private static final int TOKEN_LIMIT = 10;

    private int[] tokens;
    private int goldTokens;
    private int[] bonuses;

    public PlayerAssets() {
        this.tokens = new int[5];
        this.goldTokens = 0;
        this.bonuses = new int[5];
    }

    public void addToken(int colorIndex, int qty) {
        validateColorIndex(colorIndex);

        int updated = tokens[colorIndex] + qty;
        if (updated < 0) {
            throw new IllegalArgumentException("Token count cannot be negative.");
        }

        tokens[colorIndex] = updated;
    }

    public void addBonus(int colorIndex) {
        validateColorIndex(colorIndex);
        bonuses[colorIndex]++;
    }

    public void addGoldToken() {
        goldTokens++;
    }

    public void useGoldToken() {
        if (goldTokens <= 0) {
            throw new IllegalStateException("No gold tokens available.");
        }
        goldTokens--;
    }

    public int getNumTokens() {
        int total = goldTokens;

        for (int token : tokens) {
            total += token;
        }

        return total;
    }

    public int[] getTokens() {
        return tokens.clone();
    }

    public int getTokens(int colorIndex) {
        validateColorIndex(colorIndex);
        return tokens[colorIndex];
    }

    public int getGoldTokens() {
        return goldTokens;
    }

    public int[] getBonuses() {
        return bonuses.clone();
    }

    public int getBonuses(int colorIndex) {
        validateColorIndex(colorIndex);
        return bonuses[colorIndex];
    }

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

    public boolean aboveTenTokens() {
        return getNumTokens() > TOKEN_LIMIT;
    }

    public int getExcessCount() {
        return Math.max(0, getNumTokens() - TOKEN_LIMIT);
    }

    private void validateColorIndex(int colorIndex) {
        if (colorIndex < 0 || colorIndex >= 5) {
            throw new IllegalArgumentException("Color index must be between 0 and 4.");
        }
    }
}
