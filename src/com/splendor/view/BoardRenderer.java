package com.splendor.view;

import com.splendor.core.*;
import com.splendor.player.*;
import com.splendor.model.*;

import java.util.*;


public class BoardRenderer {

    public void renderBoard(Board board){
        System.out.println("***** Current Board *****");

        // Print tokens
        renderTokens(board);
        System.out.println();
        System.out.println();

        //print legend
        printLegend();
        System.out.println();

        // Print nobles
        renderNobles(board); 
        System.out.println();

        // Print cards by tier
        renderCards(board);
        
        

        for(int i = 0; i <= 100; i++){
            System.out.print("*");
        }
        System.out.println();
    }

    public void renderCards(Board board){
        Map<Integer, List<DevelopmentCard>> allVisibleCards = board.getVisibleCards();

        for (int tier = 1; tier <= 3; tier++) {

            // Choose tier color
            String tierColor;
            switch (tier) {
                case 3: 
                    tierColor = ConsoleColors.TIER3; 
                    break;
                case 2: 
                    tierColor = ConsoleColors.TIER2; 
                    break;
                case 1: 
                    tierColor = ConsoleColors.TIER1; 
                    break;
                
                default: 
                    tierColor = ConsoleColors.RESET;
            }

            // Print tier header
            System.out.println(tierColor + "* Tier " + tier + " *" + ConsoleColors.RESET);

            List<DevelopmentCard> visibleCards = allVisibleCards.get(tier);

            if (visibleCards != null && !visibleCards.isEmpty()) {

                List<String[]> cardBoxes = new ArrayList<>();

                // Convert each card into a box (array of lines)
                for (DevelopmentCard card : visibleCards) {
                    cardBoxes.add(formatCardBox(card));
                }

                // Each card box has 4 lines → print row by row
                for (int line = 0; line < 4; line++) {
                    for (String[] box : cardBoxes) {
                        System.out.print(tierColor + box[line] + ConsoleColors.RESET + "  ");
                    }
                    System.out.println();
                }
            }

            System.out.println(); // spacing between tiers
        }
    }

    public void renderTokens(Board board) {
        System.out.println("* Tokens *");

        GemPile[] gemBank = board.getGemBank();

        GemColor[] colors = {
            GemColor.WHITE,
            GemColor.BLUE,
            GemColor.GREEN,
            GemColor.RED,
            GemColor.BLACK
        };

        // Render 5 normal gem colors
        for (int i = 0; i < gemBank.length; i++) {
            int count = gemBank[i].getSupply(); // from GemPile
            System.out.print(colors[i] + ": " + count + " | ");
        }

        // Render gold separately
        System.out.print(GemColor.GOLD + ": " + board.getGoldSupply());

        System.out.println();
    }


    public void renderNobles(Board board){
        List<Noble> nobles = board.getVisibleNobles();

        System.out.println(ConsoleColors.NOBLE + "* Nobles *" + ConsoleColors.RESET);

        if (nobles != null && !nobles.isEmpty()) {

            List<String[]> nobleBoxes = new ArrayList<>();

            for (Noble noble : nobles) {
                nobleBoxes.add(formatNobleBox(noble));
            }

            // Print side by side (same as cards)
            for (int line = 0; line < 4; line++) {
                for (String[] box : nobleBoxes) {
                    System.out.print(ConsoleColors.NOBLE + box[line] + ConsoleColors.RESET + "  ");
                }
                System.out.println();
            }
        }

        System.out.println();
    }

    public String[] formatCardBox(DevelopmentCard card) {
        String line1 = String.format(" T%d   %dVP   +%s ",
                card.getTier(),
                card.getPrestigePoints(),
                card.getBonusColor());

        String line2 = formatCost(card);

            return new String[] {
                "+------------------+",
                "|" + pad(line1) + "|",
                "|" + pad(line2) + "|",
                "+------------------+"
            };
    }

    private String formatCost(DevelopmentCard card) {
    int[] cost = card.getCost();

    String[] symbols = { "W", "U", "G", "R", "K" }; // no conflicts

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < cost.length; i++) {
        if (cost[i] > 0) {
            sb.append(symbols[i])
              .append(":")
              .append(cost[i])
              .append(" ");
        }
    }

    return sb.toString().trim();
}

    private String pad(String text) {
        int width = 18; // match box width
        if (text.length() > width) {
            return text.substring(0, width);
        }

        return String.format("%-" + width + "s", text);
    }

    private String[] formatNobleBox(Noble noble) {
        String name = noble.getName();
        String line1 = String.format(" %s %dVP ", name, noble.getPoints());

        String line2 = formatNobleCost(noble);

        return new String[] {
            "+------------------+",
            "|" + pad(line1) + "|",
            "|" + pad(line2) + "|",
            "+------------------+"
        };
    }

    private String formatNobleCost(Noble noble) {
        List<Integer> req = noble.getRequirementQty();

        String[] symbols = { "W", "U", "G", "R", "K" };

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < req.size(); i++) {
            if (req.get(i) > 0) {
                sb.append(symbols[i])
                .append(":")
                .append(req.get(i))
                .append(" ");
            }
        }

        return sb.toString().trim();
    }

    private void printLegend(){
        System.out.println("// LEGEND (for Noble & Cards): ");
        System.out.println("// WHITE = W | BLUE = U | GREEN = G | RED = R | BLACK = K");
    }
}
