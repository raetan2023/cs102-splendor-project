package ai;

import java.util.List;
import java.util.Map;

import model.DevelopmentCard;
import model.GemColor;
import player.Player;

public class AIPlayer extends Player {
    private Strategy strategy;

    public AIPlayer(String name) {
        this(name, new GreedyStrategy());
    }

    public AIPlayer(String name, Strategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    public Decision chooseAction(List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems) {
        return strategy.chooseAction(this, availableCards, availableGems);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
