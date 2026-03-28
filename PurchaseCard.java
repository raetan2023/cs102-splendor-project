/**
 * assumptions made outside of UML:
 * DevelopmentCard has getCost(): int[]
 * Player has getWallet(): PlayerAssets
 * Player has addOwnedCard(DevelopmentCard)
 * PlayerAssets has getTokens(): int[]
 * PlayerAssets has getBonuses(): int[]
 * PlayerAssets has setTokens(int[])
 */

package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.player.PlayerAssets;
import com.splendor.model.DevelopmentCard;

public class PurchaseCard extends Action {
    private DevelopmentCard card;

    public PurchaseCard(DevelopmentCard card) {
        this.card = card;
    }

    @Override
    public boolean isValid(Player player, Board board) {
        int[] cost = card.getCost();
        int goldRequired = player.getWallet().goldNeeded(cost);
        int playerGold = player.getWallet().getTokens()[5];
        return playerGold >= goldRequired;
    }

    @Override
    public void takeAction(Player player, Board board) {
        PlayerAssets assets = player.getWallet();

        // Add card to owned cards
        player.addOwnedCard(card);

        // Add prestige points
        player.addPoints(card.getPrestigePoints()); 
        
        // Add permanent bonus
        assets.addBonus(card.getBonusColor()); 

        // Return tokens to the board supply
        int[] cost = card.getCost();
        int[] playerTokens = assets.getTokens();
        int[] bonuses = assets.getBonuses();

        GemPile[] gemBank = board.getGemBank();

        for (int i = 0; i < 5; i++) {
            int remainder = cost[i] - bonuses[i];
            if (remainder > 0) {
                if (playerTokens[i] >= remainder) {
                    playerTokens[i] -= remainder;
                    gemBank[i].setSupply(gemBank[i].getSupply() + remainder);
                } else {
                    int goldNeeded = remainder - playerTokens[i];
                    gemBank[i].setSupply(gemBank[i].getSupply() + playerTokens[i]);
                    playerTokens[i] = 0;
                    
                    playerTokens[5] -= goldNeeded;
                    board.setGoldSupply(board.getGoldSupply() + goldNeeded);
                }
            }
        }

        // Apply deducted tokens back
        assets.setTokens(playerTokens);
    }
}
