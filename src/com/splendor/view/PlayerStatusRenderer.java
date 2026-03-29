package com.splendor.view;

import java.util.List;
import java.util.Map;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

public class PlayerStatusRenderer {
    public void renderPlayer(Player player) {
        // status of 1 specific player
        System.out.println("-----------------------------");
        System.out.println("Player: " + player.getName());
        System.out.println("Points: " + player.getPrestigePoints());
        System.out.println("Tokens: " + formatTokens(player));
        System.out.println("Cards: " + formatCards(player.getOwnedCards()));
        System.out.println("Reserved Cards: " + formatCards(player.getReservedCards()));
        System.out.println("-----------------------------");
    }

    public void renderAllPlayers(List<Player> players) {
        // Display status of ALL players
        System.out.println("=== Players Status ===");

        for (Player player : players) {
            renderPlayer(player);
        }

        System.out.println("======================");
    }

    public void renderScore(Player player) {
        // Display the score for 1 specific player
        System.out.println(player.getName() + " has "
                + player.getPrestigePoints() + " points");
    }

    private String formatTokens(Player player) {
        Map<GemColor, Integer> tokens = player.getTokens();
        String result = "";

        for (GemColor color : GemColor.values()) {
            result += color + ": " + tokens.get(color) + " | ";
        }

        // Remove trailing " | "
        if (result.length() >= 3) {
            result = result.substring(0, result.length() - 3);
        }

        return result;
    }

    private String formatCards(List<DevelopmentCard> cards) {
        String result = "";

        for (DevelopmentCard card : cards) {
            result += "[" + card.getTier() + ": +" + card.getBonusColor() + " | " + card.getPrestigePoints() + " VP] ";
        }

        // Remove trailing space if needed
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }

        if(result.length() == 0){
            result += "[0]";
        }

        return result;
    }
}