package com.splendor.model;

import java.util.List;

import com.splendor.player.Player;

public class Noble {
    private String name;
    private List<GemColor> requirementColors;
    private List<Integer> requirementQty;
    private int points;

    public Noble(String name, List<GemColor> requirementColors, List<Integer> requirementQty, int points) {
        this.name = name;
        this.requirementColors = requirementColors;
        this.requirementQty = requirementQty;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public List<GemColor> getRequirementColors() {
        return requirementColors;
    }

    public List<Integer> getRequirementQty() {
        return requirementQty;
    }

    public int getPoints() {
        return points;
    }

    public boolean needs(Player p) {
        return p.canAfford(this);
    }

    @Override
    public String toString() {
        return "Noble [name=" + name + ", points=" + points + ", requirements=" + formatRequirements() + "]";
    }

    private String formatRequirements() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < requirementColors.size(); i++) {
            if (i > 0)
                result.append(", ");
            result.append(requirementColors.get(i)).append("=").append(requirementQty.get(i));
        }
        return result.toString();
    }
}