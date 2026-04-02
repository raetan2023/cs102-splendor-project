package com.splendor.ai;

import java.util.List;
import java.util.Map;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

/**
 * The contract every AI strategy must follow.
 *
 * Separating the "what to do" (Strategy) from the "how to do it" (AIPlayer)
 * means you can swap in different playstyles — greedy, defensive, point-rush 
 * without touching any of the game engine code. Just implement this interface.
 *
 */
public interface Strategy {

    /**
     * Look at the current game state and decide what to do this turn.
     *
     * @param player         The AI player making the decision (their gems, bonuses, reserved cards)
     * @param availableCards All face-up cards currently on the board
     * @param availableGems  How many of each gem color the bank still has
     * @return A Decision describing the chosen action (never return null — use Decision.pass() instead)
     */
    Decision chooseAction(Player player, List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems);
}
