package com.splendor.core;

public class GemPile {
    private int supply;

    public GemPile(int initialSupply) {
        this.supply = initialSupply;
    }

    public boolean canTakeTwo() {
        return supply >= 4;
    }

    public void takeGems(int count) {
        if (supply >= count) {
            supply -= count;
        }
    }

    public void returnGems(int count) {
        supply += count;
    }

    public int getSupply() {
        return supply;
    }
}
