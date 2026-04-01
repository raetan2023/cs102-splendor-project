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
    // checks if limit for total number of reserved cards is exceeded, returns true if not 
    public boolean isValid(Player player, Board board) {
        return player.getReservedCards().size() < 3;
    }

    @Override
    public void takeAction(Player player, Board board) {
        // removes the card to reserve from the board if it's there
        List<DevelopmentCard> tierCards = board.getVisibleCards().get(card.getTier());
        if (tierCards != null && tierCards.contains(card)) {
            tierCards.remove(card);
            board.revealCard(card.getTier()); // reveals replacement of reserved card on board (makes new card face up)
        }

        //adds card to player's reserved cards
        player.reserve(card);

        //checks if the board has any gold tokens left and adds one to player's wallet if yes, removes it from the board
        if (board.getGoldSupply() > 0) { 
            player.getWallet().addGoldToken(); 
            board.takeGold(1);
        }
    }
}
