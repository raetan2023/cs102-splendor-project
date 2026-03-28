package com.splendor.core;

import com.splendor.player.Player;
import com.splendor.model.Noble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    private List<Player> players;
    private Board gameBoard;
    private int currentPlayerIndex;

    public GameEngine(List<Player> players, Board gameBoard) {
        this.players = players;
        this.gameBoard = gameBoard;
        this.currentPlayerIndex = 0;
    }

    public void startGame() {
        // Setup initial board state cards
        for (int tier = 1; tier <= 3; tier++) {
            if (gameBoard.getAllCards().containsKey(tier)) {
                gameBoard.getAllCards().get(tier).shuffle();
                for (int i = 0; i < 4; i++) {
                    gameBoard.revealCard(tier);
                }
            }
        }

        // Setup Nobles (draw 3 for a 2-player game)
        // Note: Nobles do not replenish when taken.
        List<Noble> allNobles = gameBoard.getAllNobles();
        Collections.shuffle(allNobles); // Assuming we can shuffle the list directly
        int noblesToDeal = Math.min(3, allNobles.size());
        for (int i = 0; i < noblesToDeal; i++) {
            gameBoard.getVisibleNobles().add(allNobles.get(i));
        }
    }

    public void nextTurn(Action action) throws IllegalArgumentException {
        Player currentPlayer = players.get(currentPlayerIndex);

        // 1. Validate and Execute Action
        if (!action.isValid(currentPlayer, gameBoard)) {
            throw new IllegalArgumentException("Invalid action for the current state.");
        }
        action.takeAction(currentPlayer, gameBoard);

        // 2. Automatic Noble Check
        List<Noble> qualifyingNobles = new ArrayList<>();
        for (Noble noble : gameBoard.getVisibleNobles()) {
            if (noble.needs(currentPlayer)) {
                qualifyingNobles.add(noble);
            }
        }

        if (!qualifyingNobles.isEmpty()) {
            // If multiple qualify, GameView prompt should handle player choice.
            // Assuming auto-pick first one for now as a fallback.
            Noble visitingNoble = qualifyingNobles.get(0); 
            currentPlayer.addNoble(visitingNoble);
            gameBoard.getVisibleNobles().remove(visitingNoble);
        }

        // 3. Win Condition Update/Check (Just triggering it to verify state)
        boolean hasWinner = checkWin();

        // 4. Flip current player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean checkWin() {
        // A player wins if they have 15 or more prestige points.
        for (Player player : players) {
            if (player.getPrestigePoints() >= 15) {
                return true;
            }
        }
        return false;
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
