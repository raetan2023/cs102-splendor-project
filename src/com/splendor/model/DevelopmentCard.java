package com.splendor.model;

public class DevelopmentCard {
    private int tier;
    private int points;
    private GemColor bonus;
    private int[] cost;

    public DevelopmentCard(int tier, int points, GemColor bonus, int[] cost) {
        this.tier = tier;
        this.points = points;
        this.bonus = bonus;
        this.cost = cost.clone();
    }

    public int getTier() {
        return tier;
    }

    public int getPrestigePoints() {
        return points;
    }

    public GemColor getBonusColor() {
        return bonus;
    }

    public int[] getCost() {
        return cost.clone();
    }

    private String formatCost() {
        String result = "";

        for (int i = 0; i < 5; i++) {
            if (cost[i] > 0) {
                if (!result.equals("")) {
                    result += ", ";
                }
                result += GemColor.values()[i] + "=" + cost[i];
            }
        }

        if (result.equals("")) {
            return "free";
        }

        return result;
    }

    @Override
    public String toString() {
        return "Tier " + tier
                + " Card [points=" + points
                + ", bonus=" + bonus
                + ", cost=" + formatCost() + "]";
    }
}