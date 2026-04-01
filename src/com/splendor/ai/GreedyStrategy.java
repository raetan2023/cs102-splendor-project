package com.splendor.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

public class GreedyStrategy implements Strategy {
    private static final int MAX_RESERVED_CARDS = 3;
    private static final int MAX_GEM_PICK = 3;

    @Override
    public Decision chooseAction(Player player, List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems) {
        DevelopmentCard affordableVisible = findBestAffordableCard(player, availableCards);
        if (affordableVisible != null) {
            return Decision.purchase(affordableVisible);
        }

        DevelopmentCard affordableReserved = findBestAffordableCard(player, player.getReservedCards());
        if (affordableReserved != null) {
            return Decision.purchase(affordableReserved);
        }

        DevelopmentCard bestCard = findBestCard(availableCards);
        if (bestCard != null && player.getReservedCards().size() < MAX_RESERVED_CARDS) {
            return Decision.reserve(bestCard);
        }

        List<GemColor> gemChoice = chooseGemColors(player, availableCards, availableGems);
        if (!gemChoice.isEmpty()) {
            return Decision.takeGems(gemChoice);
        }

        return Decision.pass();
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

    private boolean canAfford(Player player, DevelopmentCard card) {
        return player.getWallet().goldNeeded(card.getCost()) <= player.getTokenCount(GemColor.GOLD);
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

        if (candidate.getTier() != currentBest.getTier()) {
            return candidate.getTier() > currentBest.getTier();
        }

        return totalCost(candidate) < totalCost(currentBest);
    }

    private List<GemColor> chooseGemColors(Player player, List<DevelopmentCard> availableCards,
            Map<GemColor, Integer> availableGems) {
        DevelopmentCard target = findClosestCard(player, availableCards);
        if (target == null) {
            return fallbackGemChoice(availableGems);
        }

        List<GemColor> priority = buildGemPriority(player, target);
        List<GemColor> chosen = new ArrayList<>();

        for (GemColor color : priority) {
            if (color == GemColor.GOLD) {
                continue;
            }

            Integer supply = availableGems.get(color);
            if (supply != null && supply > 0 && !chosen.contains(color)) {
                chosen.add(color);
            }

            if (chosen.size() == MAX_GEM_PICK) {
                break;
            }
        }

        if (chosen.isEmpty()) {
            return fallbackGemChoice(availableGems);
        }

        return chosen;
    }

    private DevelopmentCard findClosestCard(Player player, List<DevelopmentCard> cards) {
        DevelopmentCard best = null;
        int bestShortfall = Integer.MAX_VALUE;

        for (DevelopmentCard card : cards) {
            int shortfall = tokenShortfall(player, card);
            if (shortfall < bestShortfall || (shortfall == bestShortfall && isBetterCard(card, best))) {
                best = card;
                bestShortfall = shortfall;
            }
        }

        return best;
    }

    private List<GemColor> buildGemPriority(Player player, DevelopmentCard target) {
        List<GemColor> colors = new ArrayList<>();

        for (GemColor color : GemColor.values()) {
            if (color != GemColor.GOLD) {
                colors.add(color);
            }
        }

        Collections.sort(colors, Comparator.comparingInt((GemColor color) -> gemNeed(player, target, color)).reversed());
        return colors;
    }

    private List<GemColor> fallbackGemChoice(Map<GemColor, Integer> availableGems) {
        List<GemColor> chosen = new ArrayList<>();

        for (GemColor color : GemColor.values()) {
            if (color == GemColor.GOLD) {
                continue;
            }

            Integer supply = availableGems.get(color);
            if (supply != null && supply > 0) {
                chosen.add(color);
            }

            if (chosen.size() == MAX_GEM_PICK) {
                break;
            }
        }

        return chosen;
    }

    private int tokenShortfall(Player player, DevelopmentCard card) {
        int[] cost = card.getCost();
        int shortfall = 0;

        for (int i = 0; i < cost.length; i++) {
            GemColor color = GemColor.values()[i];
            int owned = player.getTokenCount(color) + player.getBonuses().get(color);
            shortfall += Math.max(0, cost[i] - owned);
        }

        return shortfall;
    }

    private int gemNeed(Player player, DevelopmentCard card, GemColor color) {
        int index = color.ordinal();
        int required = card.getCost()[index];
        int owned = player.getTokenCount(color) + player.getBonuses().get(color);
        return Math.max(0, required - owned);
    }

    private int totalCost(DevelopmentCard card) {
        int total = 0;
        for (int cost : card.getCost()) {
            total += cost;
        }
        return total;
    }
}
