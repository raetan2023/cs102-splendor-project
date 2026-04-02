package com.splendor.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

/**
 * The "just grab the best thing available right now" AI.
 * No lookahead, no planning — it always tries to buy first,
 * then reserve, then grab gems, and only passes if it's truly stuck.
 */
public class GreedyStrategy implements Strategy {

    private static final int MAX_RESERVED_CARDS = 3; // Game rule: can't hold more than 3 reserved cards
    private static final int MAX_GEM_PICK = 3;       // Game rule: can take at most 3 gems per turn

    @Override
    public Decision chooseAction(Player player, List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems) {

        // 1. Can we buy something off the board right now? Great, take the best one.
        DevelopmentCard affordableVisible = findBestAffordableCard(player, availableCards);
        if (affordableVisible != null) {
            return Decision.purchase(affordableVisible);
        }

        // 2. Can we buy something we already reserved? Check the hand next.
        DevelopmentCard affordableReserved = findBestAffordableCard(player, player.getReservedCards());
        if (affordableReserved != null) {
            return Decision.purchase(affordableReserved);
        }

        // 3. Nothing affordable — lock down the best visible card before someone else grabs it.
        DevelopmentCard bestCard = findBestCard(availableCards);
        if (bestCard != null && player.getReservedCards().size() < MAX_RESERVED_CARDS) {
            return Decision.reserve(bestCard);
        }

        // 4. Can't buy or reserve — at least grab some gems to work toward something.
        List<GemColor> gemChoice = chooseGemColors(player, availableCards, availableGems);
        if (!gemChoice.isEmpty()) {
            return Decision.takeGems(gemChoice);
        }

        // 5. Completely stuck (hand full, no gems available). Just pass.
        return Decision.pass();
    }

    /**
     * From a list of cards, returns the one we can afford that scores highest
     * by isBetterCard's ranking. Returns null if nothing is affordable.
     */
    private DevelopmentCard findBestAffordableCard(Player player, List<DevelopmentCard> cards) {
        DevelopmentCard best = null;

        for (DevelopmentCard card : cards) {
            if (canAfford(player, card) && isBetterCard(card, best)) {
                best = card;
            }
        }

        return best;
    }

    /**
     * Same as above but without the affordability filter —
     * used when we want to reserve the single best card on the board.
     */
    private DevelopmentCard findBestCard(List<DevelopmentCard> cards) {
        DevelopmentCard best = null;

        for (DevelopmentCard card : cards) {
            if (isBetterCard(card, best)) {
                best = card;
            }
        }

        return best;
    }

    /**
     * "Can afford" means: after spending all our colored gems and bonuses,
     * the remaining gap can be covered by our gold tokens.
     */
    private boolean canAfford(Player player, DevelopmentCard card) {
        return player.getWallet().goldNeeded(card.getCost()) <= player.getTokenCount(GemColor.GOLD);
    }

    /**
     * Ranks two cards against each other. Priority order:
     *   1. More prestige points
     *   2. Higher tier (tier 3 > tier 2 > tier 1)
     *   3. Cheaper total cost (less gems needed)
     */
    private boolean isBetterCard(DevelopmentCard candidate, DevelopmentCard currentBest) {
        if (candidate == null) return false;
        if (currentBest == null) return true;

        if (candidate.getPrestigePoints() != currentBest.getPrestigePoints()) {
            return candidate.getPrestigePoints() > currentBest.getPrestigePoints();
        }

        if (candidate.getTier() != currentBest.getTier()) {
            return candidate.getTier() > currentBest.getTier();
        }

        return totalCost(candidate) < totalCost(currentBest);
    }

    /**
     * Decides which gems to take this turn.
     * Tries to take the colors we're shortest on for our target card.
     * Falls back to just taking whatever's available if no clear target exists.
     */
    private List<GemColor> chooseGemColors(Player player, List<DevelopmentCard> availableCards,
            Map<GemColor, Integer> availableGems) {

        int currentTotalTokens = 0;
        for (int tokens : player.getWallet().getTokens()) {
            currentTotalTokens += tokens;
        }
        int maxGemsWeCanTake = Math.min(MAX_GEM_PICK, 10 - currentTotalTokens);

        if (maxGemsWeCanTake <= 0) {
            return new ArrayList<>();
        }

        // Find the card we're closest to being able to afford
        DevelopmentCard target = findClosestCard(player, availableCards);
        if (target == null) {
            return fallbackGemChoice(availableGems, maxGemsWeCanTake);
        }

        // Sort colors by how badly we need them for that card, then take the top available ones
        List<GemColor> priority = buildGemPriority(player, target);
        List<GemColor> chosen = new ArrayList<>();

        for (GemColor color : priority) {
            if (color == GemColor.GOLD) continue; // Can't just take gold — you only get it via reserving

            Integer supply = availableGems.get(color);
            if (supply != null && supply > 0 && !chosen.contains(color)) {
                chosen.add(color);
            }

            if (chosen.size() == maxGemsWeCanTake) break;
        }

        if (chosen.isEmpty()) {
            return fallbackGemChoice(availableGems, maxGemsWeCanTake);
        }

        return chosen;
    }

    /**
     * Finds the card we need the fewest extra gems to afford.
     * Ties are broken by isBetterCard so we lean toward higher-value cards.
     */
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

    /**
     * Sorts gem colors by how much we still need of each one for the target card.
     * Most-needed color ends up first so we prioritize wisely.
     */
    private List<GemColor> buildGemPriority(Player player, DevelopmentCard target) {
        List<GemColor> colors = new ArrayList<>();

        for (GemColor color : GemColor.values()) {
            if (color != GemColor.GOLD) colors.add(color);
        }

        Collections.sort(colors,
            Comparator.comparingInt((GemColor color) -> gemNeed(player, target, color)).reversed());

        return colors;
    }

    /**
     * No target card in sight — just take the first 3 available colors we find.
     * Better than passing even if it's not optimal.
     */
    private List<GemColor> fallbackGemChoice(Map<GemColor, Integer> availableGems, int maxGemsWeCanTake) {
        List<GemColor> chosen = new ArrayList<>();

        for (GemColor color : GemColor.values()) {
            if (color == GemColor.GOLD) continue;

            Integer supply = availableGems.get(color);
            if (supply != null && supply > 0) {
                chosen.add(color);
            }

            if (chosen.size() == maxGemsWeCanTake) break;
        }

        return chosen;
    }

    /**
     * How many more gems do we need total to afford this card?
     * Counts tokens + card bonuses we already have toward the cost.
     */
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

    /**
     * How short are we on one specific color for a target card?
     * Returns 0 if we already have enough of that color.
     */
    private int gemNeed(Player player, DevelopmentCard card, GemColor color) {
        int index = color.ordinal();
        int required = card.getCost()[index];
        int owned = player.getTokenCount(color) + player.getBonuses().get(color);
        return Math.max(0, required - owned);
    }

    /** Sums up all gem costs on a card — used as a tiebreaker (cheaper = easier to get). */
    private int totalCost(DevelopmentCard card) {
        int total = 0;
        for (int cost : card.getCost()) {
            total += cost;
        }
        return total;
    }
}
