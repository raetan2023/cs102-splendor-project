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

        //iterates through count of each colour player wants to take
        for (int i = 0; i < gemsToTake.length; i++) {
            int count = gemsToTake[i];
            if (count > 0) {
                if (i == 5) {
                    return false;
                }
                totalTaken += count;
                colorsTaken++;

                //checks if there are enough gems on the board depending on if player wants to take two of the same colour or only one
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

        /*returns if action is valid:
            - if two of the same colors are taken, verifies that two tokens are taken from only one color
            - otherwise, confirms that 1-3 tokens are taken, all of different colours */
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

        //adds chosen gems first before checking if total gems are above limit (10)
        for (int i = 0; i < gemsToTake.length; i++) {
            if (gemsToTake[i] > 0) {
                wallet.addToken(i, gemsToTake[i]);
                gemBank[i].takeGems(gemsToTake[i]);
            }
        }

        // checks if token limit is reached and discards tokens using GameView, promptDiscard()
        if (wallet.aboveTenTokens()) {
            int overLimit = 0;
            //currentTokens is number of gems of each color that player currently has
            int[] currentTokens = wallet.getTokens();

            //finds how many tokens of each color need to be discarded
            for (int count : currentTokens) {
                overLimit += count;
            }
            overLimit -= 10;

            // uses GameView to block execution and get CLI input from the player
            int[] discarded = GameView.promptDiscard(player, overLimit);

            for (int i = 0; i < currentTokens.length; i++) {
                //subtracts number of discarded tokens of each color from player's current tokens
                currentTokens[i] -= discarded[i];

                //returns discarded tokens to the board
                if (i == 5) {
                    board.returnGold(discarded[i]);
                } else {
                    gemBank[i].returnGems(discarded[i]);
                }
            }
            
            //updates player's wallet with to remove discarded tokens 
            wallet.setTokens(currentTokens);
        }
    }
}
