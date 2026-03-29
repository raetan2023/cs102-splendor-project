package ai;

import model.*;
import player.Player;

import java.util.*;

public class GreedyStrategy implements Strategy {

    @Override
    public Action chooseAction(Player player, Board board) {

        // 1. Try to buy the BEST card (highest points first)
        DevelopmentCard bestCard = findBestAffordableCard(player, board);

        if (bestCard != null) {
            return new PurchaseCard(bestCard);
        }

        // 2. If cannot buy, reserve a good card (highest points)
        DevelopmentCard reserveCard = findBestCard(board);

        if (reserveCard != null && player.getReservedCards().size() < 3) {
            return new ReserveCard(reserveCard);
        }

        // 3. Otherwise take gems
        return new TakeGems(); // adjust constructor later if needed
    }

    // =========================
    // Helper Methods
    // =========================

    // Find best card player can afford
    private DevelopmentCard findBestAffordableCard(Player player, Board board) {

        DevelopmentCard best = null;

        for (List<DevelopmentCard> tier : board.getVisibleCards().values()) {
            for (DevelopmentCard card : tier) {

                if (canAfford(player, card)) {
                    if (best == null || card.getPrestigePoints() > best.getPrestigePoints()) {
                        best = card;
                    }
                }
            }
        }

        return best;
    }

    // Find best card overall (for reserving)
    private DevelopmentCard findBestCard(Board board) {

        DevelopmentCard best = null;

        for (List<DevelopmentCard> tier : board.getVisibleCards().values()) {
            for (DevelopmentCard card : tier) {

                if (best == null || card.getPrestigePoints() > best.getPrestigePoints()) {
                    best = card;
                }
            }
        }

        return best;
    }

    // Check if player can afford card
    private boolean canAfford(Player player, DevelopmentCard card) {

        Map<GemColor, Integer> tokens = player.getTokens();
        Map<GemColor, Integer> bonuses = player.getBonuses();

        int[] cost = card.getCost(); // make sure your card has this

        int goldNeeded = 0;

        for (int i = 0; i < 5; i++) {

            GemColor color = GemColor.values()[i];

            int required = cost[i];
            int discount = bonuses.get(color);
            int available = tokens.get(color);

            int remaining = Math.max(0, required - discount);

            if (available < remaining) {
                goldNeeded += (remaining - available);
            }
        }

        return tokens.get(GemColor.GOLD) >= goldNeeded;
    }
}
