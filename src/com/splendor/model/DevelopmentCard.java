package com.splendor.model;

public class DevelopmentCard {
    private int tier; // Store tier of card (tier 1,2,3)
    private int points; // number of prestige points the card is worth
    private GemColor bonus; // stores the gemcolor bonus the card gives when purchased
    private int[] cost; // array to store the cost of the card (which color gems)

    // Constructor to initialize a new card. 
    public DevelopmentCard(int tier, int points, GemColor bonus, int[] cost) {
        this.tier = tier;
        this.points = points;
        this.bonus = bonus;
        this.cost = cost.clone(); // used clone to that cost cannot be modified outside of this class
    }

    // Getter for tier of card
    public int getTier() {
        return tier;
    }

    // Getter for prestige points the card is worth
    public int getPrestigePoints() {
        return points;
    }

    // Getter for bonus Gem Color the card gives
    public GemColor getBonusColor() {
        return bonus;
    }

    // Getter for cost of card
    public int[] getCost() {
        return cost.clone();
    }

    // convert the int[] to a String format to display cost later
    private String formatCost() {
        String result = "";
        // Loop through the cost which corresponds to the 5 gem colors
        for (int i = 0; i < 5; i++) {
            // Check if the card requires this gem color
            if (cost[i] > 0) {
                // if result string alr has a gem color in it, add a comma
                if (!result.equals("")) {
                    result += ", ";
                }
                // String will be in the format [GemColor] = [Number]
                result += GemColor.values()[i] + " = " + cost[i];
            }
        }
        // If all cost 0, returns "free"
        if (result.equals("")) {
            return "free";
        }

        return result;
    }

    // overriden toString method to display cost
    @Override
    public String toString() {
        return "Tier " + tier
                + " Card [Points: " + points
                + " | Bonus: " + bonus
                + " | Cost: " + formatCost() + "]";
    }
}
