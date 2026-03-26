public class GameView {

    private BoardRenderer boardRenderer;
    private PlayerStatusRenderer playerRenderer;

    // Constructor
    public GameView() {
        this.boardRenderer = new BoardRenderer();
        this.playerRenderer = new PlayerStatusRenderer();
    }

    // Displays the FULL game state
    public void displayGame(Board board, List<Player> players) {
        System.out.println("===== SPLENDOR GAME =====");

        displayBoard(board);
        displayDivider();
        displayPlayers(players);

        System.out.println("=========================");
    }

    // Displays everything for a single turn
    public void displayTurn(Board board, List<Player> players, Player currentPlayer) {
        System.out.println("===== CURRENT TURN =====");

        displayCurrentPlayer(currentPlayer);
        displayDivider();

        displayBoard(board);
        displayDivider();

        displayPlayers(players);

        System.out.println("========================");
    }

    // Displays only the board
    public void displayBoard(Board board) {
        boardRenderer.renderBoard(board);
    }

    // Displays all players
    public void displayPlayers(List<Player> players) {
        playerRenderer.renderAllPlayers(players);
    }

    // Displays current player's turn
    public void displayCurrentPlayer(Player player) {
        System.out.println("Current Player: " + player.getName());
    }

    // Displays a general message
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Displays a divider line
    public void displayDivider() {
        System.out.println("----------------------------------");
    }
}