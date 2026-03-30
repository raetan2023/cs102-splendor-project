package com.splendor.view;

import java.util.List;
import java.util.Map;
import java.util.*;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;
import com.splendor.core.GameEngine;

public class PlayerStatusRenderer {

    public void renderPlayer(Player player, String color, boolean isCurrent) {
        String prefix = isCurrent ? ConsoleColors.BOLD + color : color;

        System.out.println(prefix + "|-----------------------------");
        System.out.println("Player: " + player.getName());
        System.out.println("Points: " + player.getPrestigePoints());
        System.out.println("Tokens: " + formatTokens(player));
        System.out.println("Bonus: " + formatBonuses(player));
        System.out.println("Cards: " + formatCards(player.getOwnedCards()));
        System.out.println("Reserved Cards: " + formatCards(player.getReservedCards()));
        System.out.println("-----------------------------|" + ConsoleColors.RESET);
    }

    public void renderAllPlayers(List<Player> players, Player currentPlayer) {
        System.out.println("=== Players Status ===");

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String color = getPlayerColor(i);

            // Highlight current player
            if (player.equals(currentPlayer)) {
                System.out.println(ConsoleColors.BOLD + color + ">> CURRENT TURN <<" + ConsoleColors.RESET);
            }

            System.out.print(color);
            renderPlayer(player, color, true);
            System.out.print(ConsoleColors.RESET);
        }

        displayFinish();
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

    private String formatBonuses(Player player) {
        Map<GemColor, Integer> bonusMap = new HashMap<>();

        // Initialize all colors to 0
        for (GemColor color : GemColor.values()) {
            bonusMap.put(color, 0);
        }

        // Count bonuses from owned cards
        for (DevelopmentCard card : player.getOwnedCards()) {
            GemColor bonus = card.getBonusColor();
            bonusMap.put(bonus, bonusMap.get(bonus) + 1);
        }

        // Build string
        String result = "";
        for (GemColor color : GemColor.values()) {
            result += color + "=" + bonusMap.get(color) + " | ";
        }

        // Remove trailing " | "
        if (result.length() >= 3) {
            result = result.substring(0, result.length() - 3);
        }

        return result;
    }

    public String getPlayerColor(int index) {
        String[] colors = {
            ConsoleColors.GREEN,
            ConsoleColors.BLUE,
            ConsoleColors.MAGENTA,
            ConsoleColors.CYAN,
            ConsoleColors.ORANGE
        };
        return colors[index % colors.length];
    }

    public void displayFinish() {
        for(int i = 0; i <= 100; i++){
            System.out.print("=");
        }
        System.out.println();
    }
}