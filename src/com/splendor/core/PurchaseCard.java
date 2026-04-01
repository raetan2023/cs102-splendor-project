package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.player.PlayerAssets;
import com.splendor.model.DevelopmentCard;

import java.util.List;

public class PurchaseCard extends Action {
    private DevelopmentCard card;
    private boolean isReserved;

    public PurchaseCard(DevelopmentCard card, boolean isReserved) {
        this.card = card;
        this.isReserved = isReserved;
    }

    @Override
    // checks if player has enough tokens to make purchase 
    public boolean isValid(Player player, Board board) {
        int[] cost = card.getCost();
        int goldRequired = player.getWallet().goldNeeded(cost);
        int playerGold = player.getWallet().getGoldTokens(); 
        return playerGold >= goldRequired;
    }

    @Override
    public void takeAction(Player player, Board board) {
        PlayerAssets assets = player.getWallet();

        // removes the card from either the reserved deck or the board, depending on where it is
        if (isReserved) {
            player.removeReservedCard(card);
        } else {
            List<DevelopmentCard> tierCards = board.getVisibleCards().get(card.getTier());
            if (tierCards != null) {
                tierCards.remove(card);
                board.revealCard(card.getTier()); // Reveal a new card to automatically replace it
            }
        }

        // adds card to owned cards
        player.addOwnedCard(card);

        // adds to player's prestigePoints
        player.addPoints(card.getPrestigePoints()); 
        
        // adds bonus to player wallet
        assets.addBonus(card.getBonusColor().ordinal()); // getBonusColor returns only GemColor, so we have to get the ordinal

        // removes tokens required for purchase from player wallet and return to board's gem supply
        int[] cost = card.getCost();
        int[] playerTokens = assets.getTokens();
        int[] bonuses = assets.getBonuses();

        GemPile[] gemBank = board.getGemBank();

        for (int i = 0; i < 5; i++) {
            int remainder = cost[i] - bonuses[i];
            if (remainder > 0) {
                if (playerTokens[i] >= remainder) {
                    playerTokens[i] -= remainder;
                    gemBank[i].returnGems(remainder);
                } else {
                    int goldNeeded = remainder - playerTokens[i];
                    if (playerTokens[i] > 0) {
                        gemBank[i].returnGems(playerTokens[i]);
                        playerTokens[i] = 0;
                    }
                    
                    // removes gold required for purchase from player wallet
                    for (int g = 0; g < goldNeeded; g++) {
                        assets.useGoldToken();
                        board.returnGold(1);
                    }
                }
            }
        }

        // Apply deducted regular tokens back
        assets.setTokens(playerTokens);
    }
}
