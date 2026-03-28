package com.splendor.core;
public abstract class Action {
    
    public abstract boolean isValid(Player player, Board board);
    
    public abstract void takeAction(Player player, Board board);
    
}
