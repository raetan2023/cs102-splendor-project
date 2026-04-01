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

public class AIPlayer extends Player {

    private Strategy strategy;

    public AIPlayer(String name) {
        this(name, new GreedyStrategy());
    }

    public AIPlayer(String name, Strategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    public Action chooseAction(Board board) {
        List<DevelopmentCard> availableCards = getAllVisibleCards(board);
        Map<GemColor, Integer> availableGems = getAvailableGems(board);
        
        Decision decision = strategy.chooseAction(this, availableCards, availableGems);
        return decisionToAction(board, decision);
    }

    private List<DevelopmentCard> getAllVisibleCards(Board board) {
        List<DevelopmentCard> visibleCards = new ArrayList<>();
        for (List<DevelopmentCard> tierCards : board.getVisibleCards().values()) {
            visibleCards.addAll(tierCards);
        }
        return visibleCards;
    }

    private Map<GemColor, Integer> getAvailableGems(Board board) {
        Map<GemColor, Integer> availableGems = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            GemColor color = GemColor.values()[i];
            availableGems.put(color, board.getGemBank()[i].getSupply());
        }
        return availableGems;
    }

    private Action decisionToAction(Board board, Decision decision) {
        if (decision == null) {
            return null;
        }

        switch (decision.getType()) {
            case PURCHASE:
                DevelopmentCard card = decision.getCard();
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

    private Action gemsListToAction(List<GemColor> gemColors) {
        int[] gemsToTake = new int[5];
        for (GemColor color : gemColors) {
            gemsToTake[color.ordinal()]++;
        }
        return new TakeGems(gemsToTake);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
