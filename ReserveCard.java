/**
 * assumptions made outside of UML:
 * Player has getWallet(): PlayerAssets
 * Player has getReservedCards(): List<DevelopmentCard>
 */

package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.model.DevelopmentCard;

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
        player.reserve(card);
        
        if (board.getGoldSupply() > 0) { 
            player.getWallet().addToken(5, 1);
            board.setGoldSupply(board.getGoldSupply() - 1);
        }
    }
}
