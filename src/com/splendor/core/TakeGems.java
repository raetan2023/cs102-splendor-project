/**
 * assumptions made outside of UML:
 * Player has getWallet(): PlayerAssets
 * PlayerAssets has getTokens(): int[]
 * PlayerAssets has setTokens(int[])
 * GameView has promptDiscard(Player, int): int[]
 */

package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.player.PlayerAssets;
import com.splendor.view.GameView;

public class TakeGems extends Action {
    private int[] gemsToTake; 
    
    public TakeGems(int[] gemsToTake) {
        this.gemsToTake = gemsToTake;
    }

    @Override
    public boolean isValid(Player player, Board board) {
        int totalTaken = 0;
        int colorsTaken = 0;
        boolean takingTwoSame = false;
        GemPile[] gemBank = board.getGemBank();

        for (int i = 0; i < gemsToTake.length; i++) {
            int count = gemsToTake[i];
            if (count > 0) {
                if (i == 5) {
                    return false;
                }
                totalTaken += count;
                colorsTaken++;

                if (count == 2) {
                    takingTwoSame = true;
                    if (!gemBank[i].canTakeTwo()) {
                        return false;
                    }
                } else if (count > 2) {
                    return false; 
                } else if (count == 1) {
                    if (gemBank[i].getSupply() < 1) {
                        return false;
                    }
                }
            }
        }

        if (takingTwoSame) {
            return totalTaken == 2 && colorsTaken == 1;
        } else {
            return totalTaken > 0 && totalTaken <= 3 && colorsTaken == totalTaken;
        }
    }

    @Override
    public void takeAction(Player player, Board board) {
        PlayerAssets wallet = player.getWallet();
        GemPile[] gemBank = board.getGemBank();

        for (int i = 0; i < gemsToTake.length; i++) {
            if (gemsToTake[i] > 0) {
                wallet.addToken(i, gemsToTake[i]);
                gemBank[i].takeGems(gemsToTake[i]);
            }
        }

        // Discard logic via GameView prompt for CLI
        if (wallet.aboveTenTokens()) {
            int overLimit = 0;
            int[] currentTokens = wallet.getTokens();
            for (int count : currentTokens) {
                overLimit += count;
            }
            overLimit -= 10;

            // Using GameView to block execution and get CLI input from the player
            int[] discarded = GameView.promptDiscard(player, overLimit);

            for (int i = 0; i < currentTokens.length; i++) {
                currentTokens[i] -= discarded[i];
                if (i == 5) {
                    board.returnGold(discarded[i]);
                } else {
                    gemBank[i].returnGems(discarded[i]);
                }
            }
            wallet.setTokens(currentTokens);
        }
    }
}
