package ai;

import java.util.List;
import java.util.Map;

import model.DevelopmentCard;
import model.GemColor;
import player.Player;

public interface Strategy {
    Decision chooseAction(Player player, List<DevelopmentCard> visibleCards, Map<GemColor, Integer> availableGems);
}
