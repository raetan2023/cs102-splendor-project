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

import java.util.List;

public class PurchaseCard extends Action {
    private DevelopmentCard card;
    private boolean isReserved;

    public PurchaseCard(DevelopmentCard card, boolean isReserved) {
        this.card = card;
        this.isReserved = isReserved;
    }

    @Override
    public boolean isValid(Player player, Board board) {
        int[] cost = card.getCost();
        int goldRequired = player.getWallet().goldNeeded(cost);
        int playerGold = player.getWallet().getGoldTokens(); // getTokens()[5] is no longer guaranteed, let's use proper method
        return playerGold >= goldRequired;
    }

    @Override
    public void takeAction(Player player, Board board) {
        PlayerAssets assets = player.getWallet();

        // 1. Remove card from the board or reserved hand
        if (isReserved) {
            player.getReservedCards().remove(card);
        } else {
            List<DevelopmentCard> tierCards = board.getVisibleCards().get(card.getTier());
            if (tierCards != null) {
                tierCards.remove(card);
                board.revealCard(card.getTier()); // Reveal a new card to automatically replace it
            }
        }

        // 2. Add card to owned cards
        player.addOwnedCard(card);

        // 3. Add prestige points
        player.addPoints(card.getPrestigePoints()); 
        
        // 4. Add permanent bonus
        assets.addBonus(card.getBonusColor().ordinal()); // getBonusColor returns GemColor. need ordinal

        // 5. Deduct tokens and return to the board supply
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
                    
                    // Deduct gold
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
