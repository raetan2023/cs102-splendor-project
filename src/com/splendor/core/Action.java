//abstract interface to create structure for all player moves
package com.splendor.core;

import com.splendor.player.Player;

public abstract class Action {
    //checks if selected action is valid for given player
    public abstract boolean isValid(Player player, Board board);
    //executes action
    public abstract void takeAction(Player player, Board board);
}
