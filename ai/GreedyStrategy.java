package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.DevelopmentCard;
import model.GemColor;
import player.Player;

public class GreedyStrategy implements Strategy {

    private static final int MAX_RESERVED_CARDS = 3;
    private static final int MAX_GEM_PICK = 3;

    @Override
    public Decision chooseAction(Player player, List<DevelopmentCard> visibleCards, Map<GemColor, Integer> availableGems) {
        DevelopmentCard bestAffordable = findBestAffordableCard(player, visibleCards);
        if (bestAffordable != null) {
            return Decision.purchase(bestAffordable);
        }

        DevelopmentCard bestReservedAffordable = findBestAffordableCard(player, player.getReservedCards());
        if (bestReservedAffordable != null) {
            return Decision.purchase(bestReservedAffordable);
        }

        DevelopmentCard bestCard = findBestCard(visibleCards);
        if (bestCard != null && player.getReservedCards().size() < MAX_RESERVED_CARDS) {
            return Decision.reserve(bestCard);
        }

        List<GemColor> gemsToTake = chooseGemColors(player, visibleCards, availableGems);
        if (!gemsToTake.isEmpty()) {
            return Decision.takeGems(gemsToTake);
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

    private List<GemColor> chooseGemColors(Player player, List<DevelopmentCard> visibleCards, Map<GemColor, Integer> availableGems) {
        DevelopmentCard targetCard = findClosestCard(player, visibleCards);
        if (targetCard == null) {
            return fallbackGemChoice(availableGems);
        }

        List<GemColor> priorities = buildGemPriority(player, targetCard);
        List<GemColor> selected = new ArrayList<>();

        for (GemColor color : priorities) {
            if (selected.size() == MAX_GEM_PICK) {
                break;
            }

            Integer supply = availableGems.get(color);
            if (supply != null && supply > 0 && color != GemColor.GOLD && !selected.contains(color)) {
                selected.add(color);
            }
        }

        if (!selected.isEmpty()) {
            return selected;
        }

        return fallbackGemChoice(availableGems);
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

    private List<GemColor> fallbackGemChoice(Map<GemColor, Integer> availableGems) {
        List<GemColor> selected = new ArrayList<>();

        for (GemColor color : GemColor.values()) {
            if (selected.size() == MAX_GEM_PICK) {
                break;
            }

            Integer supply = availableGems.get(color);
            if (color != GemColor.GOLD && supply != null && supply > 0) {
                selected.add(color);
            }
        }

        return selected;
    }
}
