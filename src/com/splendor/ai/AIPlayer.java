package com.splendor.ai;

import com.splendor.core.Action;
import com.splendor.core.Board;
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
        return strategy.chooseAction(this, board);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}