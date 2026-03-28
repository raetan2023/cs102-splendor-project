package com.splendor.view;

public class BoardRenderer{
    public void renderBoard(Board board){
        System.out.println("=== Current Board ===");

        // Print cards by tier
        renderCards(board);

        // Print tokens
        renderTokens(board);

        // Print nobles
        renderNobles(board); 

        System.out.println("====================");
    }

    public void renderCards(Board board){
        // from tiers 1 to 3
        for (int tier = 1; tier <= 3; tier++) {
            System.out.print("Tier " + tier + ": ");

            // Get revealed cards for this tier
            List<Card> visibleCards = board.getVisibleCards(tier); // <-- board keeps track of revealed cards

            // Print each card nicely
            for (Card card : visibleCards) {
                System.out.print(formatCard(card) + " ");
            }

            System.out.println(); // %n per tier
        }
    }

// //may need to make changes to name TokenType after this func is made available in the board
//     public void renderTokens(Board board){
//         System.out.println("Tokens:");

//         Map<TokenType, Integer> tokens = board.getTokens();

//         for (TokenType type : TokenType.values()) {
//             System.out.print(type + ": " + tokens.get(type) + " | ");
//         }

//         System.out.println(); // %n after all tokens
//     }

    public void renderTokens(Board board) {
        System.out.println("Tokens:");

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

        System.out.print("Nobles: ");
        for (Noble noble : nobles) {
            System.out.print(formatNoble(noble) + " ");
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