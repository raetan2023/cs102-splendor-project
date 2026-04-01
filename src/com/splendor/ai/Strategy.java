package com.splendor.ai;

import java.util.List;
import java.util.Map;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

public interface Strategy {
    Decision chooseAction(Player player, List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems);
}
