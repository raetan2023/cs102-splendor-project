package model;

import player.Player;

public class Noble {
    private int[] requirements; // only WHITE, BLUE, GREEN, RED, BLACK used
    private int points;

    public Noble(int[] requirements, int points) {
        this.requirements = requirements.clone();
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public int[] getRequirements() {
        return requirements.clone();
    }

    public boolean needs(Player p) {
        return p.canAfford(this);
    }

    @Override
    public String toString() {
        return "Noble [points=" + points + ", requirements=" + formatRequirements() + "]";
    }

    private String formatRequirements() {
        String result = "";

        for (int i = 0; i < 5; i++) {
            if (requirements[i] > 0) {
                if (!result.equals("")) {
                    result += ", ";
                }
                result += GemColor.values()[i] + "=" + requirements[i];
            }
        }

        return result;
    }
}
