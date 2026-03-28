package com.splendor.core;

import com.splendor.player.Player;

public abstract class Action {
    public abstract boolean isValid(Player player, Board board);
    public abstract void takeAction(Player player, Board board);
}
