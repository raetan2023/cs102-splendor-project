package com.splendor.ai;

import com.splendor.core.Action;
import com.splendor.core.Board;
import com.splendor.player.Player;

public interface Strategy {
    Action chooseAction(Player player, Board board);
}
