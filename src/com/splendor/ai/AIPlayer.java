package com.splendor.ai;

import java.util.ArrayList;
import java.util.HashMap;
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

/**
 *This represents an AI-controlled player that behaves similarly to a human player.
 Instead of waiting for user input, it relies on a Strategy object to decide its next move it takes and then
 translating that decision into an in-game action.
 */
public class AIPlayer extends Player {

    private Strategy strategy;

    // Defaults to GreedyStrategy if nobody specifies one
    public AIPlayer(String name) {
        this(name, new GreedyStrategy());
    }

    public AIPlayer(String name, Strategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    /**
     * The main entry point is called by the game engine each turn.
        This then gather what the AI can see, asks the strategy what to do,
     * after that converts that into a concrete Action the engine can execute.
     */
    public Action chooseAction(Board board) {
        List<DevelopmentCard> availableCards = getAllVisibleCards(board);
        Map<GemColor, Integer> availableGems = getAvailableGems(board);

        Decision decision = strategy.chooseAction(this, availableCards, availableGems);
        return decisionToAction(board, decision);
    }

    /**
     * Flattens the board's visible cards into one simple list.
     * The board organises cards by different tiers (1/2/3), but the strategy
     * doesn't care about that structure hence we collapse it here.
     */
    private List<DevelopmentCard> getAllVisibleCards(Board board) {
        List<DevelopmentCard> visibleCards = new ArrayList<>();
        for (List<DevelopmentCard> tierCards : board.getVisibleCards().values()) {
            visibleCards.addAll(tierCards);
        }
        return visibleCards;
    }

    /**
     * Reads the gem bank and returns a color → count map.
     * Only looks at the first 5 colors (skips gold/wild — you cannot just take those).
     */
    private Map<GemColor, Integer> getAvailableGems(Board board) {
        Map<GemColor, Integer> availableGems = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            GemColor color = GemColor.values()[i];
            availableGems.put(color, board.getGemBank()[i].getSupply());
        }
        return availableGems;
    }

    /**
     * Translates a high-level Decision (what the strategy *wants* to do)
     * into a concrete Action object (what the game engine actually runs).
     *
     * Returns null for PASS or anything unexpected — the engine treats
     * a null action as skipping the turn.
     */
    private Action decisionToAction(Board board, Decision decision) {
        if (decision == null) {
            return null;
        }

        switch (decision.getType()) {
            case PURCHASE:
                DevelopmentCard card = decision.getCard();
                // Buying from your hand (reserved) works differently than buying from the board
                boolean isReserved = this.getReservedCards().contains(card);
                return new PurchaseCard(card, isReserved);

            case RESERVE:
                return new ReserveCard(decision.getCard());

            case TAKE_GEMS:
                return gemsListToAction(decision.getGemColors());

            case PASS:
            default:
                return null;
        }
    }

    /**
     * The strategy gives us a list like [RED, RED, BLUE].
     * The engine needs an int array where each position corresponds to a gem color’s index.
     * This just simply counts how many of each color appear in the list and fills the array.
     */
    private Action gemsListToAction(List<GemColor> gemColors) {
        int[] gemsToTake = new int[5];
        for (GemColor color : gemColors) {
            gemsToTake[color.ordinal()]++;
        }
        return new TakeGems(gemsToTake);
    }

    // allow us to inspect or hot-swap the AI's strategy which is useful for testing different behaviours
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
