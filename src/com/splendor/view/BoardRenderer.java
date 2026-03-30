package com.splendor.view;

import com.splendor.core.*;
import com.splendor.player.*;
import com.splendor.model.*;

import java.util.*;


public class BoardRenderer {

    public void renderBoard(Board board){
        System.out.println("***** Current Board *****");

        // Print cards by tier
        renderCards(board);

        // Print tokens
        renderTokens(board);
        System.out.println();

        // Print nobles
        renderNobles(board); 

        for(int i = 0; i <= 100; i++){
            System.out.print("*");
        }
        System.out.println();
    }

    // public void renderCards(Board board){
    //     // from tiers 1 to 3
    //     Map<Integer, List<DevelopmentCard>> allVisibleCards = board.getVisibleCards(); // <-- board keeps track of revealed cards
        
    //     for (int tier = 1; tier <= 3; tier++) {
    //         System.out.print("* Tier " + tier + " *");
    //         System.out.println();

    //         // Get revealed cards for this tier
    //         List<DevelopmentCard> visibleCards = allVisibleCards.get(tier);

    //         // Print each card nicely
    //         if (visibleCards != null) {
    //             for (DevelopmentCard card : visibleCards) {
    //                 System.out.print(formatCard(card) + " ");
    //                 System.out.println();
    //             }
    //         }

    //         System.out.println(); // %n per tier
    //         System.out.println();
    //     }
    // }

    public void renderCards(Board board){
        Map<Integer, List<DevelopmentCard>> allVisibleCards = board.getVisibleCards();
        
        for (int tier = 1; tier <= 3; tier++) {
            // Pick color based on tier
            String tierColor;
            switch (tier) {
                case 1: tierColor = ConsoleColors.TIER1; break;
                case 2: tierColor = ConsoleColors.TIER2; break;
                case 3: tierColor = ConsoleColors.TIER3; break;
                default: tierColor = ConsoleColors.RESET;
            }

            System.out.println(tierColor + "* Tier " + tier + " *" + ConsoleColors.RESET);

            List<DevelopmentCard> visibleCards = allVisibleCards.get(tier);
            if (visibleCards != null) {
                for (DevelopmentCard card : visibleCards) {
                    System.out.println(tierColor + formatCard(card) + ConsoleColors.RESET);
                }
            }
            System.out.println();
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

    // public void renderNobles(Board board){
    //     List<Noble> nobles = board.getVisibleNobles(); 

    //     System.out.print("* Nobles *");
    //     System.out.println();
    //     for (Noble noble : nobles) {
    //         System.out.print(formatNoble(noble) + " ");
    //         System.out.println();
    //     }
    //     System.out.println();
    // }

    public void renderNobles(Board board){
        List<Noble> nobles = board.getVisibleNobles(); 

        System.out.println(ConsoleColors.NOBLE + "* Nobles *" + ConsoleColors.RESET);
        for (Noble noble : nobles) {
            System.out.println(ConsoleColors.NOBLE + formatNoble(noble) + ConsoleColors.RESET);
        }
        System.out.println();
    }

    private String formatCard(DevelopmentCard card){
        return card.toString();
    }

    private String formatNoble(Noble noble) {
        return noble.toString();
    }
}
