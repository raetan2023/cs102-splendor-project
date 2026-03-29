package com.splendor;

import com.splendor.config.CardLoader;
import com.splendor.config.NobleLoader;
import com.splendor.core.Board;
import com.splendor.core.GameEngine;
import com.splendor.model.Deck;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.Noble;
import com.splendor.player.Player;
import com.splendor.view.GameView;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Splendor game data...");

        // 1. Load Data
        CardLoader cardLoader = new CardLoader();
        List<DevelopmentCard> tier1 = cardLoader.loadCards("data/cards_level1.csv");
        List<DevelopmentCard> tier2 = cardLoader.loadCards("data/cards_level2.csv");
        List<DevelopmentCard> tier3 = cardLoader.loadCards("data/cards_level3.csv");

        NobleLoader nobleLoader = new NobleLoader();
        List<Noble> nobles = nobleLoader.loadNobles("data/nobles.csv");

        // 2. Setup Board and Decks
        Board board = new Board();
        board.getAllNobles().addAll(nobles);
        board.getAllCards().put(1, new Deck<>(tier1));
        board.getAllCards().put(2, new Deck<>(tier2));
        board.getAllCards().put(3, new Deck<>(tier3));

        // 3. Setup Players
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        List<Player> players = Arrays.asList(player1, player2);

        // 4. Initialize GameEngine
        GameEngine engine = new GameEngine(players, board);
        engine.startGame(); // Deals nobles and initial cards

        // 5. Initialize GameView & Display Initial State
        GameView view = new GameView();
        view.displayGame(engine.getGameBoard(), engine.getPlayers());

        System.out.println("\nSplendor setup complete! Ready to start the game loop.");
        
        // TODO: Start your main scanner loop here to take turns
    }
}
