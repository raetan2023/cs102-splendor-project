package com.splendor.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.splendor.core.Action;
import com.splendor.core.Board;
import com.splendor.core.PurchaseCard;
import com.splendor.core.ReserveCard;
import com.splendor.core.TakeGems;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

public class GreedyStrategy implements Strategy {

    private static final int MAX_RESERVED_CARDS = 3;
    private static final int MAX_GEM_PICK = 3;

    @Override
    public Action chooseAction(Player player, Board board) {
        List<DevelopmentCard> visibleCards = getAllVisibleCards(board);
        DevelopmentCard bestAffordable = findBestAffordableCard(player, visibleCards);
        if (bestAffordable != null) {
            return new PurchaseCard(bestAffordable, false);
        }

        DevelopmentCard bestReservedAffordable = findBestAffordableCard(player, player.getReservedCards());
        if (bestReservedAffordable != null) {
            return new PurchaseCard(bestReservedAffordable, true);
        }

        DevelopmentCard bestCard = findBestCard(visibleCards);
        if (bestCard != null && player.getReservedCards().size() < MAX_RESERVED_CARDS) {
            return new ReserveCard(bestCard);
        }

        int[] gemsToTake = chooseGemsToTake(player, visibleCards, board);
        if (hasAnyGemSelection(gemsToTake)) {
            return new TakeGems(gemsToTake);
        }

        return null;
    }

    private DevelopmentCard findBestAffordableCard(Player player, List<DevelopmentCard> cards) {
        DevelopmentCard best = null;

        for (DevelopmentCard card : cards) {
            if (canAfford(player, card) && isBetterCard(card, best)) {
                best = card;
            }
        }

        return best;
    }

    private DevelopmentCard findBestCard(List<DevelopmentCard> cards) {
        DevelopmentCard best = null;

        for (DevelopmentCard card : cards) {
            if (isBetterCard(card, best)) {
                best = card;
            }
        }

        return best;
    }

    private boolean isBetterCard(DevelopmentCard candidate, DevelopmentCard currentBest) {
        if (candidate == null) {
            return false;
        }

        if (currentBest == null) {
            return true;
        }

        if (candidate.getPrestigePoints() != currentBest.getPrestigePoints()) {
            return candidate.getPrestigePoints() > currentBest.getPrestigePoints();
        }

        return candidate.getTier() < currentBest.getTier();
    }

    private boolean canAfford(Player player, DevelopmentCard card) {
        int[] cost = card.getCost();
        int goldNeeded = 0;
        Map<GemColor, Integer> tokens = player.getTokens();
        Map<GemColor, Integer> bonuses = player.getBonuses();

        for (int i = 0; i < 5; i++) {
            GemColor color = GemColor.values()[i];
            int discountedCost = Math.max(0, cost[i] - bonuses.get(color));
            int available = tokens.get(color);

            if (available < discountedCost) {
                goldNeeded += discountedCost - available;
            }
        }

        return tokens.get(GemColor.GOLD) >= goldNeeded;
    }

    private int[] chooseGemsToTake(Player player, List<DevelopmentCard> visibleCards, Board board) {
        DevelopmentCard targetCard = findClosestCard(player, visibleCards);
        if (targetCard == null) {
            return fallbackGemChoice(board);
        }

        List<GemColor> priorities = buildGemPriority(player, targetCard);
        int[] selection = new int[5];
        int selectedCount = 0;

        if (!priorities.isEmpty()) {
            GemColor topPriority = priorities.get(0);
            int topNeed = gemNeed(player, targetCard, topPriority);
            int topIndex = topPriority.ordinal();

            if (topNeed >= 2 && board.getGemBank()[topIndex].canTakeTwo()) {
                selection[topIndex] = 2;
                return selection;
            }
        }

        for (GemColor color : priorities) {
            if (selectedCount == MAX_GEM_PICK) {
                break;
            }

            int colorIndex = color.ordinal();
            if (color != GemColor.GOLD && board.getGemBank()[colorIndex].getSupply() > 0 && selection[colorIndex] == 0) {
                selection[colorIndex] = 1;
                selectedCount++;
            }
        }

        if (selectedCount > 0) {
            return selection;
        }

        return fallbackGemChoice(board);
    }

    private DevelopmentCard findClosestCard(Player player, List<DevelopmentCard> visibleCards) {
        DevelopmentCard best = null;
        int lowestShortfall = Integer.MAX_VALUE;

        for (DevelopmentCard card : visibleCards) {
            int shortfall = tokenShortfall(player, card);

            if (shortfall < lowestShortfall || (shortfall == lowestShortfall && isBetterCard(card, best))) {
                best = card;
                lowestShortfall = shortfall;
            }
        }

        return best;
    }

    private int tokenShortfall(Player player, DevelopmentCard card) {
        int[] cost = card.getCost();
        Map<GemColor, Integer> tokens = player.getTokens();
        Map<GemColor, Integer> bonuses = player.getBonuses();
        int shortfall = 0;

        for (int i = 0; i < 5; i++) {
            GemColor color = GemColor.values()[i];
            int discountedCost = Math.max(0, cost[i] - bonuses.get(color));
            shortfall += Math.max(0, discountedCost - tokens.get(color));
        }

        return shortfall;
    }

    private List<GemColor> buildGemPriority(Player player, DevelopmentCard targetCard) {
        List<GemColor> priorities = new ArrayList<>();
        int[] cost = targetCard.getCost();
        Map<GemColor, Integer> tokens = player.getTokens();
        Map<GemColor, Integer> bonuses = player.getBonuses();

        for (int i = 0; i < 5; i++) {
            GemColor color = GemColor.values()[i];
            int discountedCost = Math.max(0, cost[i] - bonuses.get(color));
            int shortfall = Math.max(0, discountedCost - tokens.get(color));

            if (shortfall > 0) {
                priorities.add(color);
            }
        }

        Collections.sort(priorities, (left, right) -> {
            int leftNeed = gemNeed(player, targetCard, left);
            int rightNeed = gemNeed(player, targetCard, right);
            return rightNeed - leftNeed;
        });

        return priorities;
    }

    private int gemNeed(Player player, DevelopmentCard card, GemColor color) {
        int colorIndex = color.ordinal();
        int[] cost = card.getCost();
        Map<GemColor, Integer> tokens = player.getTokens();
        Map<GemColor, Integer> bonuses = player.getBonuses();
        int discountedCost = Math.max(0, cost[colorIndex] - bonuses.get(color));
        return Math.max(0, discountedCost - tokens.get(color));
    }

    private int[] fallbackGemChoice(Board board) {
        int[] selection = new int[5];
        int selectedCount = 0;

        for (int i = 0; i < 5; i++) {
            if (selectedCount == MAX_GEM_PICK) {
                break;
            }

            if (board.getGemBank()[i].getSupply() > 0) {
                selection[i] = 1;
                selectedCount++;
            }
        }

        return selection;
    }

    private boolean hasAnyGemSelection(int[] gemsToTake) {
        for (int count : gemsToTake) {
            if (count > 0) {
                return true;
            }
        }

        return false;
    }

    private List<DevelopmentCard> getAllVisibleCards(Board board) {
        List<DevelopmentCard> visibleCards = new ArrayList<>();

        for (List<DevelopmentCard> tierCards : board.getVisibleCards().values()) {
            visibleCards.addAll(tierCards);
        }

        return visibleCards;
    }
}