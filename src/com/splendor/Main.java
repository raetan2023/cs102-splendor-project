package com.splendor;

import com.splendor.config.CardLoader;
import com.splendor.config.NobleLoader;
import com.splendor.core.Action;
import com.splendor.core.Board;
import com.splendor.core.GameEngine;
import com.splendor.core.PurchaseCard;
import com.splendor.core.ReserveCard;
import com.splendor.core.TakeGems;
import com.splendor.model.Deck;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.Noble;
import com.splendor.player.Player;
import com.splendor.view.GameView;
import com.splendor.view.PlayerStatusRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Splendor game data...");

        // 1. Load Data
        CardLoader cardLoader = new CardLoader();
        List<DevelopmentCard> allCards = cardLoader.loadCards("data/development_cards.csv");

        // Split cards by tier
        List<DevelopmentCard> tier1 = new ArrayList<>();
        List<DevelopmentCard> tier2 = new ArrayList<>();
        List<DevelopmentCard> tier3 = new ArrayList<>();

        for (DevelopmentCard card : allCards) {
            if (card.getTier() == 1) tier1.add(card);
            else if (card.getTier() == 2) tier2.add(card);
            else if (card.getTier() == 3) tier3.add(card);
        }

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
        view.displayGame(engine.getGameBoard(), engine.getPlayers(), engine);

        System.out.println("\nSplendor setup complete! Starting the game.\n");

        // 6. Main game loop
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;

        while (!gameOver) {
            Player current = engine.getCurrentPlayer();
            view.displayTurn(engine.getGameBoard(), engine.getPlayers(), engine);

            boolean validTurn = false;
            while (!validTurn) {
                Action action = promptHumanAction(scanner, current, engine.getGameBoard(), view);
                try {
                    engine.nextTurn(action);
                    validTurn = true;
                } catch (IllegalArgumentException e) {
                    view.displayMessage("Invalid move: " + e.getMessage() + " Try again.");
                }
            }

            if (engine.checkWin()) {
                gameOver = true;
            }
        }

        view.displayGame(engine.getGameBoard(), engine.getPlayers(), engine);
        announceWinner(engine.getPlayers(), view);
        scanner.close();
    }

    private static Action promptHumanAction(Scanner sc, Player player, Board board, GameView view) {
        while (true) {
            // view.displayMessage(player.getName() + " — choose: (1) Take Gems  (2) Buy Card  (3) Reserve Card");
            PlayerStatusRenderer renderer = new PlayerStatusRenderer();
            renderer.renderPlayer(player); // show current player's status

            view.displayMessage(player.getName() + " — choose: (1) Take Gems  (2) Buy Card  (3) Reserve Card");
            String input = sc.nextLine().trim();
            switch (input) {
                case "1": return promptTakeGems(sc, board, view);
                case "2": return promptPurchaseCard(sc, player, board, view);
                case "3": return promptReserveCard(sc, player, board, view);
                default:  view.displayMessage("Enter 1, 2, or 3.");
            }
        }
    }

    private static Action promptTakeGems(Scanner sc, Board board, GameView view) {
        int[] gemBank = new int[5];
        for (int i = 0; i < 5; i++) {
            gemBank[i] = board.getGemBank()[i].getSupply();
        }

        view.displayMessage("Bank: WHITE=" + gemBank[0] + " BLUE=" + gemBank[1]
                + " GREEN=" + gemBank[2] + " RED=" + gemBank[3] + " BLACK=" + gemBank[4]);
        view.displayMessage("Enter gems to take (e.g. 1 0 1 1 0 for WHITE GREEN RED):");

        int[] gems = new int[5];
        while (true) {
            try {
                String[] parts = sc.nextLine().trim().split("\\s+");
                if (parts.length != 5) {
                    view.displayError("Enter exactly 5 numbers (one per color).");
                    continue;
                }
                for (int i = 0; i < 5; i++) {
                    gems[i] = Integer.parseInt(parts[i]);
                }
                return new TakeGems(gems);
            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Enter 5 numbers separated by spaces.");
            }
        }
    }

    private static Action promptPurchaseCard(Scanner sc, Player player, Board board, GameView view) {
        // Build a numbered list of all purchasable cards
        List<DevelopmentCard> options = new ArrayList<>();
        List<Boolean> isReservedFlags = new ArrayList<>();

        view.displayMessage("--- Board cards ---");
        Map<Integer, List<DevelopmentCard>> visibleCards = board.getVisibleCards();
        for (int tier = 3; tier >= 1; tier--) {
            List<DevelopmentCard> tierCards = visibleCards.get(tier);
            if (tierCards != null) {
                for (DevelopmentCard card : tierCards) {
                    options.add(card);
                    isReservedFlags.add(false);
                    view.displayMessage("[" + options.size() + "] " + card);
                }
            }
        }

        List<DevelopmentCard> reserved = player.getReservedCards();
        if (!reserved.isEmpty()) {
            view.displayMessage("--- Your reserved cards ---");
            for (DevelopmentCard card : reserved) {
                options.add(card);
                isReservedFlags.add(true);
                view.displayMessage("[" + options.size() + "] " + card);
            }
        }

        if (options.isEmpty()) {
            view.displayError("No cards available to buy.");
            return null;
        }

        while (true) {
            view.displayMessage("Enter card number to buy:");
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 1 || choice > options.size()) {
                    view.displayMessage("Enter a number between 1 and " + options.size() + ".");
                    continue;
                }
                DevelopmentCard selected = options.get(choice - 1);
                boolean isReserved = isReservedFlags.get(choice - 1);
                return new PurchaseCard(selected, isReserved);
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input. Enter a number.");
            }
        }
    }

    private static Action promptReserveCard(Scanner sc, Player player, Board board, GameView view) {
        List<DevelopmentCard> options = new ArrayList<>();

        view.displayMessage("--- Board cards ---");
        Map<Integer, List<DevelopmentCard>> visibleCards = board.getVisibleCards();
        for (int tier = 3; tier >= 1; tier--) {
            List<DevelopmentCard> tierCards = visibleCards.get(tier);
            if (tierCards != null) {
                for (DevelopmentCard card : tierCards) {
                    options.add(card);
                    view.displayMessage("[" + options.size() + "] " + card);
                }
            }
        }

        if (options.isEmpty()) {
            view.displayError("No cards available to reserve.");
            return null;
        }

        while (true) {
            view.displayMessage("Enter card number to reserve:");
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 1 || choice > options.size()) {
                    view.displayError("Enter a number between 1 and " + options.size() + ".");
                    continue;
                }
                return new ReserveCard(options.get(choice - 1));
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input. Enter a number.");
            }
        }
    }

    private static void announceWinner(List<Player> players, GameView view) {
        Player winner = players.get(0);
        for (Player p : players) {
            if (p.getPrestigePoints() > winner.getPrestigePoints()
                    || (p.getPrestigePoints() == winner.getPrestigePoints()
                            && p.getOwnedCards().size() < winner.getOwnedCards().size())) {
                winner = p;
            }
        }
        view.displayMessage("Game over! " + winner.getName()
                + " wins with " + winner.getPrestigePoints() + " prestige points!");
    }

    
}
