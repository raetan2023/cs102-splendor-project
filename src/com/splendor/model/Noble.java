package com.splendor.model;

import java.util.List;

import com.splendor.player.Player;

public class Noble {
    private String name; // Name of nobel
    private List<GemColor> requirementColors; // List of specific gems required to get noble
    private List<Integer> requirementQty; // List of number of each gem required to get noble
    private int points; // prestige points the noble awards

    // Constructor to initialize the noble
    public Noble(String name, List<GemColor> requirementColors, List<Integer> requirementQty, int points) {
        this.name = name;
        this.requirementColors = requirementColors;
        this.requirementQty = requirementQty;
        this.points = points;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for required colors
    public List<GemColor> getRequirementColors() {
        return requirementColors;
    }

    // Getter for required number of each colored gem
    public List<Integer> getRequirementQty() {
        return requirementQty;
    }

    // Getter for number of prestige points the noble gives
    public int getPoints() {
        return points;
    }

    // Checks if the Player meets the requirements for this noble
    public boolean needs(Player p) {
        return p.canAfford(this);
    }

    // Override toString method to print out noble in the format "Noble [Name: xxx | Points: x | Requirements: x] "
    @Override
    public String toString() {
        return "Noble [Name:" + name + 
        " | Points: " + points + 
        " | Requirements: " + formatRequirements() + "]";
    }

    // Helper method for toString to print out the requirements in the format "[GemColor = x]"
    private String formatRequirements() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < requirementColors.size(); i++) {
            if (i > 0)
                result.append(", ");
            result.append(requirementColors.get(i)).append(" = ").append(requirementQty.get(i));
        }
        return result.toString();
    }
}
