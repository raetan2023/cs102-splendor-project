package com.splendor.view;

import com.splendor.core.*;
import com.splendor.model.*;
import java.util.*;

public class BoardRenderer {

    // prints the whole board
    public void renderBoard(Board board) {
        System.out.println("***** Current Board *****");

        // Print tokens
        renderTokens(board);
        System.out.println();
        System.out.println();

        // print legend
        printLegend();
        System.out.println();

        // Print nobles
        renderNobles(board);
        System.out.println();

        // Print cards by tier
        renderCards(board);

        // prints the ending line
        for (int i = 0; i <= 100; i++) {
            System.out.print("*");
        }

        System.out.println();
    }

    // prints the cards (tier 1 to 3)
    public void renderCards(Board board) {
        Map<Integer, List<DevelopmentCard>> allVisibleCards = board.getVisibleCards();

        // choose colour for each tier
        for (int tier = 1; tier <= 3; tier++) {

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
            // after printing reset the format back to normal text
            System.out.println(tierColor + "* Tier " + tier + " *" + ConsoleColors.RESET);

            // get all the cards on the board as a list
            List<DevelopmentCard> visibleCards = allVisibleCards.get(tier);

            if (visibleCards != null && !visibleCards.isEmpty()) {

                List<String[]> cardBoxes = new ArrayList<>();

                // Convert each card into a box (array of lines)    [formatting into boxes]
                for (DevelopmentCard card : visibleCards) {
                    cardBoxes.add(formatCardBox(card));
                }

                // Each card box has 4 lines → print row by row     [printing the boxes with cards]
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

    // prints the count for each gem that the player can take from
    // format is taken as int[] --> order is as per colors (follows strictly)
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

        // Render gold separately --> since gold cant be "taken" by the players
        // only given per reserved card
        System.out.print(GemColor.GOLD + ": " + board.getGoldSupply());

        System.out.println();
    }

    // prints the nobles on the board
    // also in the format of boxes 
    public void renderNobles(Board board) {

        //get the nobles to be displayed
        List<Noble> nobles = board.getVisibleNobles();

        // ConsoleColors.NOBLE acts as the "paintbrush"
        // chooses the color then print the text
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

    // responsible to format cards and nobles into boxes
    public String[] formatCardBox(DevelopmentCard card) {
        String line1 = String.format(" T%d   %dVP   +%s ",
                card.getTier(), //printed with "T" at the front
                card.getPrestigePoints(), // hv units of VP
                card.getBonusColor());      //printed with "+" at the front

        // prints the value of the card
        String line2 = formatCost(card);

        return new String[]{
            "+------------------+", // id 0
            "|" + pad(line1) + "|", // id 1
            "|" + pad(line2) + "|", // id 2
            "+------------------+" // id 3
        };
    }

    // reposible to format the information of the cards
    private String formatCost(DevelopmentCard card) {
        int[] cost = card.getCost();

        String[] symbols = {"W", "U", "G", "R", "K"}; // no conflicts

        // StringBuilder is mutable
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cost.length; i++) {
            if (cost[i] > 0) {

                // Stringbuilder does not return a new string object when appended
                sb.append(symbols[i])
                        .append(":")
                        .append(cost[i])
                        .append(" ");
            }
        }

        // convert Stringbuilder back to String 
        return sb.toString().trim();
    }

    // checks if text exceeds the width limit
    private String pad(String text) {
        int width = 18;     // match box width
        // unlike to exceed 18 since there is no cards 
        // that needs at least 1 gem for each color

        if (text.length() > width) {
            return text.substring(0, width);    // prevents the cards info to overload the box
        }

        return String.format("%-" + width + "s", text);
    }

    // box for noble --> diff input
    private String[] formatNobleBox(Noble noble) {
        String name = noble.getName();
        String line1 = String.format(" %s %dVP ", name, noble.getPoints());

        String line2 = formatNobleCost(noble);

        return new String[]{
            "+------------------+",
            "|" + pad(line1) + "|",
            "|" + pad(line2) + "|",
            "+------------------+"
        };
    }

    // like formatCost for cards but this is for nobles
    private String formatNobleCost(Noble noble) {
        List<Integer> req = noble.getRequirementQty();

        String[] symbols = {"W", "U", "G", "R", "K"};

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

    // legend needed as both blue and black starts with b
    // legend used to achieve a clean look and 
    // not to exceed the width limit
    private void printLegend() {
        System.out.println("// LEGEND (for Noble & Cards): ");
        System.out.println("// WHITE = W | BLUE = U | GREEN = G | RED = R | BLACK = K");
    }
}
