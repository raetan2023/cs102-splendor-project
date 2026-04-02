package com.splendor.view;

import com.splendor.core.Board;
import com.splendor.core.GameEngine;
import com.splendor.model.GemColor;
import com.splendor.player.Player;
import java.util.List;
import java.util.Scanner;

public class GameView {

    private BoardRenderer boardRenderer;
    private PlayerStatusRenderer playerRenderer;
    private GameEngine gameEngine;

    // Constructor
    public GameView() {
        this.boardRenderer = new BoardRenderer();
        this.playerRenderer = new PlayerStatusRenderer();
    }

    // Displays the FULL game state --> using all the other classes in view
    public void displayGame(Board board, List<Player> players, GameEngine gameEngine) {
        System.out.println("===== SPLENDOR GAME =====");

        displayDivider();

        displayBoard(board);
        displayDivider();

        displayPlayers(players, gameEngine);
        displayCurrentPlayer(gameEngine.getCurrentPlayer());

        displayFinish();
    }

    // Displays everything for a single turn
    public void displayTurn(Board board, List<Player> players, GameEngine gameEngine) {
        System.out.println("===== CURRENT TURN =====");

        displayCurrentPlayer(gameEngine.getCurrentPlayer());
        displayDivider();

        displayBoard(board);
        displayDivider();

        displayPlayers(players, gameEngine);

        System.out.println("========================");
    }

    // Displays only the board
    public void displayBoard(Board board) {
        boardRenderer.renderBoard(board);
    }

    // Displays all players and highlights the current player
    public void displayPlayers(List<Player> players, GameEngine gameEngine) {
        Player currentPlayer = gameEngine.getCurrentPlayer();
        playerRenderer.renderAllPlayers(players, currentPlayer);
    }

    // Displays current player's turn
    public void displayCurrentPlayer(Player player) {
        System.out.println("Current Turn: " + player.getName());
    }

    // Displays a general message
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // prompt message if users exceed the limit of 10 gems
    public static int[] promptDiscard(Player player, int amountToDiscard) {
        Scanner scanner = new Scanner(System.in);

        GemColor[] colors = {
            GemColor.WHITE,
            GemColor.BLUE,
            GemColor.GREEN,
            GemColor.RED,
            GemColor.BLACK
        };

        int[] discard = new int[colors.length];

        System.out.println("Player: " + player.getName());
        System.out.println("You must discard " + amountToDiscard + " gems.");

        // keep asking the player to discard till the amount entered is correct
        while (true) {
            int total = 0;

            // ask the players for each gem color how much gem player wish to discard
            for (int i = 0; i < colors.length; i++) {

                // keeps prompting till a valid input is received
                while (true) {
                    try {
                        GemColor color = colors[i];

                        int owned = player.getTokenCount(color);

                        System.out.print("Enter amount for " + color + ": ");
                        int input = scanner.nextInt();

                        if (input < 0) {
                            System.out.println("Cannot be negative.");
                            continue;
                        }

                        if (input > owned) {
                            System.out.println("You only have " + owned + " " + color);
                            continue;
                        }

                        discard[i] = input;
                        break;

                        // catch when input is not int
                        // InputMismatchException
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                        scanner.nextLine();
                    }
                }

                total += discard[i];
            }

            if (total == amountToDiscard) {
                return discard;
            }

            System.out.println("You must discard exactly " + amountToDiscard + " gems.");
            System.out.println("You entered: " + total);
            System.out.println("Try again.\n");
        }
    }

    // Displays a divider line (*)
    public void displayDivider() {
        for (int i = 0; i <= 100; i++) {
            System.out.print("*");
        }
        System.out.println();
    }

    // display finish line (=)
    public void displayFinish() {
        for (int i = 0; i <= 100; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    // prints the error msg in red
    public void displayError(String message) {
        System.out.println(ConsoleColors.RED_BOLD + message + ConsoleColors.RESET);
    }
}
