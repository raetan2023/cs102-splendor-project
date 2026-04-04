# Project Documentation

## Package: `com.splendor`
Contains the application entry points, main game loop logic, and various test suites/scenarios to run the Splendor game.

### Class: `AIPlayerTestCases`
Test suite to ensure that the `AIPlayer` correctly initializes, loads strategies, and calculates intelligent decisions.
**Attributes:**
- `int failed`: Counter for failed test assertions.
- `int passed`: Counter for passed test assertions.

**Methods:**
- `void assertNotNull(String testName, Object obj, String failReason)`: Asserts an object is not null, incrementing counters.
- `void assertTrue(String testName, boolean condition, String failReason)`: Asserts a boolean condition evaluates to true.
- `void main(String[] args)`: Entry point for running the test suite.
- `void printHeader()`: Prints the test suite header.
- `void printSummary()`: Prints the final pass/fail test tally.
- `void runDecisionGenerationTests()`: Runs tests regarding the AI's ability to generate valid game decisions.
- `void runEdgeCaseTests()`: Runs AI tests under unusual or boundary conditions.
- `void runInitializationTests()`: Runs AI instantiation and setup tests.
- `void runIntegrationTests()`: Tests the AI's integration with the broader game logic and engine.
- `void runStrategySelectionTests()`: Tests the dynamic strategy changing mechanisms of the AI.
- `void testAIPlayerCanGetReservedCards()`: Asserts that an AI player correctly retrieves its reserved cards.
- `void testAIPlayerCanGetWallet()`: Asserts that the AI player correctly integrates its `PlayerAssets` wallet.
- `void testAIPlayerCanReserveCards()`: Asserts that the AI limits and successfully processes reservations.
- `void testAIPlayerExtendsPlayer()`: Asserts that the AIPlayer accurately subclasses `Player`.
- `void testAIPlayerHasName()`: Checks for proper agent nomenclature initialization.
- `void testChooseActionNeverNull()`: Asserts the AI always returns an action or a PASS action, never crashing on a null.
- `void testChooseActionReturnsBoardAction()`: Asserts the AI outputs valid game board actions.
- `void testConstructorWithCustomStrategy()`: Tests the AI initialization when injected with a custom `Strategy`.
- `void testDefaultConstructorUsesGreedyStrategy()`: Tests if AI defaults to the `GreedyStrategy` when none is specified.
- `void testGetStrategyReturnsCurrentStrategy()`: Asserts that the getter correctly tracks the active strategy.
- `void testMultipleActionsGenerated()`: Asserts the AI successfully branches and evaluates multiple theoretical actions.
- `void testMultiplePlayersIndependent()`: Asserts that multiple AI instances do not leak state or overlap logic.
- `void testStrategiesAreInjectable()`: Asserts dependency injection works appropriately for AI behaviors.
- `void testStrategyCanBeChanged()`: Asserts the setter safely updates the AI's running strategy.
- `void testStrategyChangesTakesEffect()`: Asserts that a new strategy actually changes the AI's output.
- `void testStrategySetterWorks()`: Verifies basic strategy reassignment operations.

### Class: `DemoScenario` and `DemoScenario2`
Provides pre-configured game states intended to test explicit rule scenarios (e.g., edge-case board states).
**Methods:**
- `void announceWinner(List<Player> players, GameView view)`: Triggers the endgame conditions and announces the winner.
- `void main(String[] args)`: Runs the specific demo script.
- `Action promptHumanAction(Scanner sc, Player player, Board board, GameView view, GameEngine engine)`: Overrides or mimics CLI interactions for prompting moves.
- `Action promptPurchaseCard(Scanner sc, Player player, Board board, GameView view, GameEngine engine)`: Tests UI purchase options.
- `Action promptReserveCard(Scanner sc, Player player, Board board, GameView view)`: Tests UI reservation options.
- `Action promptTakeGems(Scanner sc, Board board, GameView view)`: Tests UI gem drawing.

### Class: `Main`
The primary entry point that boots the game, handles the CLI interactive loop, and processes all human input.
**Methods:**
- `void announceWinner(List<Player> players, GameView view)`: Calculates final scores + tie-breakers to declare a winner.
- `void main(String[] args)`: Program runtime entry point. Sets up the config, players, board, and game loop.
- `Action promptHumanAction(Scanner sc, Player player, Board board, GameView view, GameEngine engine)`: Top-level switch statement asking the user what action they wish to take.
- `Action promptPurchaseCard(Scanner sc, Player player, Board board, GameView view, GameEngine engine)`: The interface dialogue loop for purchasing cards.
- `Action promptReserveCard(Scanner sc, Player player, Board board, GameView view)`: The interface dialogue loop for reserving cards.
- `Action promptTakeGems(Scanner sc, Board board, GameView view)`: The interface dialogue loop for drawing tokens safely.

### Class: `RulesTestCases`
The primary test suite validating the physical rules, edge cases, and constraints of Splendor logic (Rule of 4, Token Limits, etc).
**Attributes:**
- `int failed`: Counter for failed rules assertions.
- `int passed`: Counter for passed rules assertions.
**Methods:**
- `void assertTrue(String testName, boolean condition, String failReason)`: Standard assertion function.
- `List<Player> determineWinnersByOfficialTieBreak(List<Player> players)`: Evaluates edge case win conditions for accurate tie-breaking.
- `void fail(String testName, String reason)`: Marks a test as failed.
- `void main(String[] args)`: Entry point for the unit tester.
- `DevelopmentCard makeCard(...)`: Factory method for instantiating custom cards on-the-fly for testing scopes.
- `Noble makeNoble(...)`: Factory method for instantiating custom nobles.
- `void pass(String testName)`: Marks a test as passed.
- `void printHeader()`, `void printSummary()`: Output formatters for the test tally.
- Various `run...Tests()` methods that execute batches of related isolated game rule edge cases (reserve, draft, purchase, tie breakers).

---

## Package: `com.splendor.ai`
Handles autonomous player decisions, behavior patterns, and environment evaluations.

### Class: `AIPlayer` (Extends `Player`)
Represents an automated machine agent participating in the game. It uses injected strategies to define its turns.
**Attributes:**
- `Strategy strategy`: The core brain algorithms loaded into the AI to determine logic patterns.
**Methods:**
- `Action chooseAction(Board board)`: Analyzes the board state and requests an Action from its Strategy brain.
- `Action decisionToAction(Board board, Decision decision)`: Converts the abstract `Decision` object into an executable `Action` subclass (Purchase/Reserve/Take).
- `Action gemsListToAction(List<GemColor> gemColors)`: Helper to serialize gem choices into a `TakeGems` action.
- `List<DevelopmentCard> getAllVisibleCards(Board board)`: Parses and flattens the board for evaluation.
- `Strategy getStrategy()`, `void setStrategy(Strategy strategy)`: Strategy mutation mechanisms.

### Class: `Decision`
A lightweight wrapper representing a conceptual AI decision before it is materialized into an engine-enforced `Action`.
**Attributes:**
- `DevelopmentCard card`: The card targeted by the decision (if reserving/purchasing).
- `List<GemColor> gemColors`: The gems to take (if prioritizing tokens).
- `Type type`: An enumerator identifying the type of choice (PASS, PURCHASE, RESERVE, TAKE).
**Methods:**
- Various lightweight getters `getCard()`, `getGemColors()`, `getType()`.
- Static Factory Methods: `pass()`, `purchase(card)`, `reserve(card)`, `takeGems(list)` for easy scoping.

### Class: `GreedyStrategy` (Implements `Strategy`)
A core algorithm targeting optimal quick-wins. Evaluates short-term point advantages, prioritises purchasing cards, and falls back to optimal gem hunting based on what the AI can afford soon.
**Attributes:**
- `int MAX_GEM_PICK`, `int MAX_RESERVED_CARDS`: Constraining factors mimicking human limits.
**Methods:**
- `List<GemColor> buildGemPriority(Player player, DevelopmentCard target)`: Analyzes token shortfall and targets the highest priority gems.
- `boolean canAfford(Player player, DevelopmentCard card)`: Evaluates purchasing power.
- `Decision chooseAction(Player player, List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems)`: Main strategy entry point. Calculates optimal move prioritizing Purchase > Target Reserve > Take Needs.
- `List<GemColor> chooseGemColors(Player player, List<DevelopmentCard> availableCards, Map<GemColor, Integer> availableGems)`: Heuristic algorithm for selecting tokens.
- `List<GemColor> fallbackGemChoice(Map<GemColor, Integer> availableGems, int maxGemsWeCanTake)`: Emergency grab when priority logic yields no results.
- `DevelopmentCard findBestAffordableCard(Player player, List<DevelopmentCard> cards)`: Sorts currently affordable options by prestige ratio.
- `DevelopmentCard findBestCard(...)`, `DevelopmentCard findClosestCard(...)`: Pathfinding algorithms aiming at optimal endgame goals.
- `int totalCost(DevelopmentCard card)`, `int gemNeed(...)`, `int tokenShortfall(...)`: Core mathematical analysis measuring exactly how far away the AI is from a purchase.

### Interface: `Strategy`
A strict abstraction designed to let the `AIPlayer` switch out behavioral brains on the fly.

---

## Package: `com.splendor.config`
Data access layer parsing external configuration files, cards, and environment properties into game models.

### Class: `CardLoader`
**Methods:**
- `List<DevelopmentCard> loadCards(String path)`: Parses target CSV resources into a list of usable `DevelopmentCard` models securely.

### Interface: `ConfigLoader`
Generic boundary stating how properties loaders should define configurations.

### Class: `GameConfig`
Model schema containing static environment values required to initialize the Splendor game accurately per rules (especially player-count variants).
**Attributes:**
- `String developmentCardsPath`, `String noblesPath`: Disk references for loading data files.
- `int gemCount2Players`, `int gemCount3Players`, `int gemCount4Players`: Rule variations for supply size per player count.
- `int goldTokenCount`, `int maxTokensPerPlayer`, `int targetPrestige`: General game rule limits.
**Methods:**
- Getters for all internal configuration boundaries to supply the runtime.

### Class: `NobleLoader`
**Methods:**
- `List<Noble> loadNobles(String path)`: Parses target CSV resources into a list of usable `Noble` tiles.

### Class: `PropertiesConfigLoader` (Implements `ConfigLoader`)
**Methods:**
- `GameConfig load(String path)`: Primary parser converting a Java `.properties` file into an active `GameConfig` object dynamically mapping schema values.
- `int getRequiredInt(...)`, `String getRequiredProperty(...)`: Helper methods executing strict validation checks on loader data.

---

## Package: `com.splendor.core`
The primary operational logic containing the core loop, state management, and actions logic enforcement.

### Interface: `Action`
A standardized interface pattern acting as a command to be executed against the game board state.
**Methods:**
- `boolean isValid(Player player, Board board)`: Validates the action under the engine's strict parameters before execution.
- `void takeAction(Player player, Board board)`: Executes board/player mutation logic directly.

### Class: `Board`
Core state container holding all tokens, decks, and visible interactive items present on the table.
**Attributes:**
- `GemPile[] gemBank`: Array representing White, Blue, Green, Red, Black piles explicitly.
- `int goldSupply`: Gold is stored separately as a wildcard.
- `List<Noble> allNobles`, `visibleNobles`: Nobles available directly to players.
**Methods:**
- `void revealCard(int tier)`: Pops a card from the deck layer onto the visible row.
- Getter logic distributing active references for mutations.

### Class: `GameEngine`
The orchestrator of the whole framework. Determines turns, validations, auto-noble claiming, and winning constraints.
**Attributes:**
- `int currentPlayerIndex`: Pointer mapping to the active turn-taker.
- `Board gameBoard`: Active table state context.
- `NobleSelectionStrategy nobleSelectionStrategy`: Callback allowing the frontend CLI to handle ambiguous logic manually (prompting the user).
- `List<Player> players`: References to the competing models.
**Methods:**
- `boolean checkWin()`: Evaluates turn rotations to detect if a round has finished, and checks prestige totals.
- `Player determineWinner()`: Triggers cascade tie-break logic upon game-end.
- `void nextTurn(Action action)`: Resolves an `Action`, enforces the token limits, triggers noble attraction checks automatically, and flips the index to the adjacent player.
- `void startGame()`: Scuffles cards and pulls out starting nobles, shifting models from loading phase to runtime phase.

### Class: `GemPile`
Represents an individual stack of colored tokens physically.
**Attributes:**
- `int supply`: Total chips inside the pile.
**Methods:**
- `boolean canTakeTwo()`: Checks the 'Rule of 4' preventing aggressive double grabs.
- `void returnGems(int count)`, `void takeGems(int count)`: Additive and subtractive calculations on the stack size.

### Class: `PurchaseCard` (Implements `Action`)
**Attributes:** 
- `DevelopmentCard card`: Associated target for execution.
- `boolean isReserved`: Logic checking if pulling from private reserves or public rows.
**Methods:**
- `isValid(Player player, Board board)`, `takeAction(...)`: Logic ensuring funds meet the requirement (including wildcards) and processing token subtraction against card bonuses.

### Class: `ReserveCard` (Implements `Action`)
**Attributes:**
- `DevelopmentCard card`: Associated target for execution.
**Methods:**
- `isValid(...)`, `takeAction(...)`: Logic validating reserve limits (max 3), appending the card to private lists, and fetching 1 gold if the bank allows.

### Class: `TakeGems` (Implements `Action`)
**Attributes:**
- `int[] gemsToTake`: Requested quantity map to extract from the `GemBank`.
**Methods:**
- `isValid(...)`, `takeAction(...)`: Validates whether taking 2 of the same (requires ≥ 4 in bank), or 3 unique, processes the wallet appending accordingly and manages rulebreaking restrictions.

---

## Package: `com.splendor.model`
Data-only entities modeling raw Splendor objects stripped of core transactional behaviors.

### Class: `Deck<T>`
**Attributes:** 
- `List<T> cards`: Generalized array holding entities.
**Methods:**
- `T draw()`, `void shuffle()`, `void add()`, `boolean isEmpty()`, `int size()`: Generic collection manipulations.

### Class: `DevelopmentCard`
**Attributes:**
- `GemColor bonus`: Permanent discount value when placed in player's tableau.
- `int[] cost`: Price map required from the user's wallet.
- `int points`: Prestige point value associated directly to score.
- `int tier`: Denotes level scope (1, 2, or 3).
**Methods:**
- Getters, formatting properties.

### Enum: `GemColor`
Strict schema mapping definitions containing constants `WHITE`, `BLUE`, `GREEN`, `RED`, `BLACK`, `GOLD`.

### Class: `Noble`
**Attributes:**
- `String name`, `int points`: Identifying scopes.
- `List<GemColor> requirementColors`, `List<Integer> requirementQty`: Cost logic strictly measuring tableau *bonuses*, not physical tokens.
**Methods:**
- `boolean needs(Player p)`: Checks whether a given player's tableau has reached the physical thresholds necessary to trigger this tile automatically.

---

## Package: `com.splendor.player`
Contains logical states specific to independent participants holding unique environments.

### Class: `Player`
**Attributes:**
- `int RESERVED_CARD_LIMIT`: Softcap set to 3.
- `String name`, `int prestigePoints`: Identifiers.
- `List<DevelopmentCard> ownedCards`, `reservedCards`: Private tableau logic.
- `List<Noble> visitedBy`: Trackers for won bonus tiles.
- `PlayerAssets wallet`: Complex object handling individual physical chips.
**Methods:**
- `boolean reserveCard(...)`, `boolean removeReservedCard(...)`
- `boolean canAfford(Noble noble)`: Used by GameEngine checking.
- Token tracking, prestige manipulation helpers, list management.

### Class: `PlayerAssets`
**Attributes:**
- `int TOKEN_LIMIT`, `int[] bonuses`, `int goldTokens`, `int[] tokens`: Explicit separation of permanent bonuses, physical wildcard gold, and standard physical tokens.
**Methods:**
- `boolean aboveTenTokens()`, `int getExcessCount()`: Hard limit validation preventing invalid turns without discards.
- `int goldNeeded(int[] cost)`: Dynamically calculates precisely how many wildcards are necessary to bridge the gap in missing token funding for a transaction.
- Extensive additive/subtractive management ensuring internal integrity without disrupting external bounds.

---

## Package: `com.splendor.view`
Decoupled UI rendering formatting logic isolating the command-line display from internal mechanics.

### Class: `BoardRenderer`
**Methods:**
- `String[] formatCardBox(...)`, `String[] formatNobleBox(...)`: Creates robust ASCII grid rectangles enclosing variable width strings correctly.
- `String formatCost(...)`, `String formatNobleCost(...)`: Maps strings neatly within box spaces.
- `void printLegend()`
- `void renderBoard(...)`, `void renderCards(...)`, `void renderNobles(...)`, `renderTokens(...)`: Top-level sequence pushing entire board scopes gracefully onto the terminal space.

### Interface: `ConsoleColors`
Collection of ANSI escape code string constants defining colors like `RED_BOLD` and mapping to unique players `PLAYER1`, `PLAYER2` etc, natively creating vibrant syntax highlighting on compatible terminals.

### Class: `GameView`
A facade bundling render tools directly usable by the CLI implementation loop.
**Attributes:**
- `BoardRenderer boardRenderer`, `PlayerStatusRenderer playerRenderer`: Delegate classes.
**Methods:**
- `void displayGame(...)`, `void displayTurn(...)`: Flushes current engine data comprehensively out to the CLI block.
- `void displayMessage(...)`, `void displayError(...)`: Output hooks standardizing textual formatting (and color) logic.
- `int[] promptDiscard(Player player, int amountToDiscard)`: Interactive element used primarily when tokens exceed 10.

### Class: `PlayerStatusRenderer`
**Methods:**
- `String formatBonuses(Player player)`, `String formatTokens(Player player)`: Turns abstract token maps into colored CLI strings.
- `String getPlayerColor(int index)`: Consistently links a user session to a definitive terminal color (e.g., Player 1 is Cyan).
- `void renderPlayer(...)`, `void renderScore(...)`, `void renderCardBoxes(...)`: Formats a UI widget representing the player's private tableaux, keeping the game visual and tidy.
