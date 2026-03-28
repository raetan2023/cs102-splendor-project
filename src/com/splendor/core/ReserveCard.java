/**
 * assumptions made outside of UML:
 * Player has getWallet(): PlayerAssets
 * Player has getReservedCards(): List<DevelopmentCard>
 */

package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.model.DevelopmentCard;

import java.util.List;

public class ReserveCard extends Action {
    private DevelopmentCard card;

    public ReserveCard(DevelopmentCard card) {
        this.card = card;
    }

    @Override
    public boolean isValid(Player player, Board board) {
        return player.getReservedCards().size() < 3;
    }

    @Override
    public void takeAction(Player player, Board board) {
        // Remove from board if it's there
        List<DevelopmentCard> tierCards = board.getVisibleCards().get(card.getTier());
        if (tierCards != null && tierCards.contains(card)) {
            tierCards.remove(card);
            board.revealCard(card.getTier()); // Reveal replacement
        }

        player.reserve(card);
        
        if (board.getGoldSupply() > 0) { 
            player.getWallet().addGoldToken(); 
            board.takeGold(1);
        }
    }
}
