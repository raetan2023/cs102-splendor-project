package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.model.Noble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    public interface NobleSelectionStrategy { // will be implemented in Main. i did not want to implement it here
                                              // because of separation of concern
        Noble chooseNoble(List<Noble> qualifyingNobles, Player currentPlayer, Board gameBoard);
    }

    private List<Player> players;
    private Board gameBoard;
    private int currentPlayerIndex;
    private NobleSelectionStrategy nobleSelectionStrategy;

    public GameEngine(List<Player> players, Board gameBoard, NobleSelectionStrategy nobleSelectionStrategy) {
        this.players = players;
        this.gameBoard = gameBoard;
        this.currentPlayerIndex = 0;
        this.nobleSelectionStrategy = nobleSelectionStrategy;
    }

    public void startGame() {
        // set up initial board state cards
        for (int tier = 1; tier <= 3; tier++) {
            if (gameBoard.getAllCards().containsKey(tier)) {
                gameBoard.getAllCards().get(tier).shuffle();
                for (int i = 0; i < 4; i++) {
                    gameBoard.revealCard(tier);
                }
            }
        }

        // set up Nobles (draw 3 for a 2-player game)
        // nobles do not replenish when taken
        List<Noble> allNobles = gameBoard.getAllNobles();
        Collections.shuffle(allNobles); // Assuming we can shuffle the list directly
        int noblesToDeal = Math.min(3, allNobles.size());
        for (int i = 0; i < noblesToDeal; i++) {
            gameBoard.getVisibleNobles().add(allNobles.get(i));
        }
    }

    public void nextTurn(Action action) throws IllegalArgumentException {
        Player currentPlayer = players.get(currentPlayerIndex);

        // 1. validate and execute action
        if (!action.isValid(currentPlayer, gameBoard)) {
            throw new IllegalArgumentException("Invalid action for the current state.");
        }
        action.takeAction(currentPlayer, gameBoard);

        // 2. automatic noble check
        List<Noble> qualifyingNobles = new ArrayList<>();
        for (Noble noble : gameBoard.getVisibleNobles()) {
            if (noble.needs(currentPlayer)) {
                qualifyingNobles.add(noble);
            }
        }

        if (!qualifyingNobles.isEmpty()) {
            Noble visitingNoble;
            if (qualifyingNobles.size() == 1) { // if they qualify for 1 noble, they take it
                visitingNoble = qualifyingNobles.get(0);
            } else { // if they qualify for more than 1 noble, they choose which one to take. the
                     // implementation is in Main.java. this is just to trigger the strategy and
                     // handle exception
                visitingNoble = nobleSelectionStrategy.chooseNoble(qualifyingNobles, currentPlayer, gameBoard);
                if (visitingNoble == null || !qualifyingNobles.contains(visitingNoble)) {
                    throw new IllegalStateException(
                            "Noble selection strategy must return one of the qualifying nobles.");
                }
            }
            currentPlayer.addNoble(visitingNoble);
            gameBoard.getVisibleNobles().remove(visitingNoble);
        }

        // 3. flip current player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean checkWin() {
        // A round ends when every player has taken an equal amount of turns.
        // Because nextTurn() increments currentPlayerIndex to 0 after the last player's turn,
        // we just need to check if we're back to player 1 (index 0).
        if (currentPlayerIndex != 0) {
            return false;
        }

        for (Player player : players) {
            if (player.getPrestigePoints() >= 15) {
                return true;
            }
        }
        return false;
    }

    public Player determineWinner() {
        Player winner = players.get(0);
        for (Player p : players) {
            if (p.getPrestigePoints() > winner.getPrestigePoints()
                    || (p.getPrestigePoints() == winner.getPrestigePoints()
                            && p.getOwnedCards().size() < winner.getOwnedCards().size())) {
                winner = p;
            }
        }
        return winner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}
