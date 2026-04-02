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
 * An AI-controlled player. It works just like a human Player under the hood,
 * but instead of waiting for user input it asks a Strategy object what to do
 * and then converts that decision into a real game Action.
 *
 * The Strategy can be swapped out at runtime (see setStrategy), so you can
 * pit different AI personalities against each other without changing this class.
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
     * The main entry point called by the game engine each turn.
     * Gathers what the AI can see, asks the strategy what to do,
     * then converts that into a concrete Action the engine can execute.
     */
    public Action chooseAction(Board board) {
        List<DevelopmentCard> availableCards = getAllVisibleCards(board);
        Map<GemColor, Integer> availableGems = getAvailableGems(board);

        Decision decision = strategy.chooseAction(this, availableCards, availableGems);
        return decisionToAction(board, decision);
    }

    /**
     * Flattens the board's visible cards into one simple list.
     * The board organises cards by tier (1/2/3), but the strategy
     * doesn't care about that structure so we collapse it here.
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
     * Only looks at the first 5 colors (skips gold/wild — you can't just take those).
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
     * The engine wants an int array indexed by gem color ordinal.
     * This just counts how many of each color appear in the list and fills the array.
     */
    private Action gemsListToAction(List<GemColor> gemColors) {
        int[] gemsToTake = new int[5];
        for (GemColor color : gemColors) {
            gemsToTake[color.ordinal()]++;
        }
        return new TakeGems(gemsToTake);
    }

    // Lets you inspect or hot-swap the AI's strategy (useful for testing different behaviours)
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
