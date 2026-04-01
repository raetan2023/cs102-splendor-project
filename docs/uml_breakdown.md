## Package: `com.splendor.core`

> Contains the core game loop and the board state.

IMPORTANT 
Standard indexing for all token arrays: [0] white [1] blue [2] green [3] red [4] black

### `GameEngine`

`GameEngine` also contains a nested public interface:
- `NobleSelectionStrategy` with method `Noble chooseNoble(List<Noble>, Player, Board)`

|Visibility|Member|Type|
|:-:|:--|:--|
|`-`|`players`|`List<Player>`|
|`-`|`currentPlayerIndex`|`int`|
|`-`|`gameBoard`|`Board`|
|`-`|`nobleSelectionStrategy`|`GameEngine.NobleSelectionStrategy`|
|`+`|`GameEngine(List<Player>, Board)`|Constructor|
|`+`|`GameEngine(List<Player>, Board, NobleSelectionStrategy)`|Constructor|
|`+`|`startGame()`|`void`|
|`+`|`nextTurn(Action)`|`void`|
|`+`|`checkWin()`|`boolean`|
|`+`|`determineWinner()`|`Player`|
|`+`|`getCurrentPlayer()`|`Player`|
|`+`|`getPlayers()`|`List<Player>`|
|`+`|`getGameBoard()`|`Board`|

### `GemPile`

| Visibility | Member                       | Type      |
| :--------: | :--------------------------- | :-------- |
|    `-`     | `supply`                     | `int`     |
|    `+`     | `canTakeTwo()`               | `boolean` |
|    `+`     | `takeGems(count: int)`       | `void`    |
|    `+`     | `returnGems(count: int)`     | `void`    |
|    `+`     | `getSupply()`                | `int`     |

no need setSupply() because the supply is handled through `takeGems(count)` and `returnGems(count)`

### `Board`

| Visibility | Member                       | Type                                  |
| :--------: | :--------------------------- | :------------------------------------ |
|    `-`     | `gemBank`                    | `GemPile[]`                           |
|    `-`     | `goldSupply`                 | `int` (starts at 5)                   |
|    `-`     | `allCards`                   | `Map<Integer, Deck<DevelopmentCard>>` |
|    `-`     | `allNobles`                  | `List<Noble>`                         |
|    `-`     | `visibleNobles`              | `List<Noble>`                         |
|    `-`     | `visibleCards`               | `Map<Integer, List<DevelopmentCard>>` |
|    `+`     | `revealCard(tier: int)`      | `void`                                |
|    `+`     | `getGemBank()`               | `GemPile[]`                           |
|    `+`     | `getGoldSupply()`            | `int`                                 |
|    `+`     | `takeGold(amount: int)`      | `void`                                |
|    `+`     | `returnGold(amount: int)`    | `void`                                |
|    `+`     | `getAllNobles()`             | `List<Noble>`                         |
|    `+`     | `getVisibleNobles()`         | `List<Noble>`                         |
|    `+`     | `getVisibleCards()`          | `Map<Integer, List<DevelopmentCard>>` |
|    `+`     | `getAllCards()`              | `Map<Integer, Deck<DevelopmentCard>>` |

### `Action` _(Abstract)_

|Visibility|Method|Returns|
|:-:|:--|:--|
|`+`|`isValid(player: Player, board: Board)`|`boolean`|
|`+`|`takeAction(player: Player, board: Board)`|`void`|

### Action Implementations

Concrete classes: **`TakeGems`**, **`PurchaseCard`**, **`ReserveCard`**

Each class `extends Action` and provides full implementations of:

- `isValid(player: Player, board: Board): boolean`
- `takeAction(player: Player, board: Board): void`

```
Action  (abstract)
├── TakeGems
├── PurchaseCard
└── ReserveCard
```

---

## Package: `com.splendor.model`

> Contains physical game components and entities. All classes in this package are **immutable** — no setters.

### `GemColor` _(enum)_

```java
enum GemColor {
    WHITE,  // index 0
    BLUE,   // index 1
    GREEN,  // index 2
    RED,    // index 3
    BLACK,  // index 4
    GOLD    // separate — not in token arrays
}
```

### `DevelopmentCard`

|Visibility|Member|Type|
|:-:|:--|:--|
|`-`|`tier`|`int`|
|`-`|`points`|`int`|
|`-`|`bonus`|`GemColor`|
|`-`|`cost`|`int[]`|
|`+`|`getTier()`|`int`|
|`+`|`getBonusColor()`|`GemColor`|
|`+`|`getPrestigePoints()`|`int`|
|`+`|`getCost()`|`int[]`|
|`-`|`formatCost()`|`String`|
|`+`|`toString()`|`String`|

### `Noble`

|Visibility|Member|Type|
|:-:|:--|:--|
|`-`|`name`|`String`|
|`-`|`requirementColors`|`List<GemColor>`|
|`-`|`requirementQty`|`List<Integer>`|
|`-`|`points`|`int`|
|`+`|`getName()`|`String`|
|`+`|`needs(p: Player)`|`boolean`|
|`+`|`getRequirementColors()`|`List<GemColor>`|
|`+`|`getRequirementQty()`|`List<Integer>`|
|`+`|`getPoints()`|`int`|
|`+`|`toString()`|`String`|

### `Deck<T>`

> No getters/setters — all interaction is through `shuffle()`, `draw()`, `add()`. Exposing the raw `cards` list directly would break encapsulation.

|Visibility|Member|Type|
|:-:|:--|:--|
|`-`|`cards`|`List<T>`|
|`+`|`shuffle()`|`void`|
|`+`|`draw()`|`T`|
|`+`|`add(card: T)`|`void`|

---

## Package: `com.splendor.player`

> Manages player state, assets, and inventory.

### `PlayerAssets`

| Visibility | Member                                | Type      |
| :--------: | :------------------------------------ | :-------- |
|    `-`     | `tokens`                              | `int[]`   |
|    `-`     | `goldTokens`                          | `int`     |
|    `-`     | `bonuses`                             | `int[]`   |
|    `+`     | `addToken(colorIndex: int, qty: int)` | `void`    |
|    `+`     | `addBonus(colorIndex: int)`           | `void`    |
|    `+`     | `addGoldToken()`                      | `void`    |
|    `+`     | `useGoldToken()`                      | `void`    |
|    `+`     | `getNumTokens()`                      | `int`     |
|    `+`     | `getTokens()`                         | `int[]`   |
|    `+`     | `getTokens(colorIndex: int)`          | `int`     |
|    `+`     | `getGoldTokens()`                     | `int`     |
|    `+`     | `getBonuses()`                        | `int[]`   |
|    `+`     | `getBonuses(colorIndex: int)`         | `int`     |
|    `+`     | `goldNeeded(cost: int[])`             | `int`     |
|    `+`     | `aboveTenTokens()`                    | `boolean` |
|    `+`     | `getExcessCount()`                    | `int`     |

> `getNumTokens()` returns total across all 5 colours **plus** `goldTokens` (gold counts toward the 10-token limit). Added `useGoldToken()` alongside `addGoldToken()` — needed for when gold is spent during `PurchaseCard`.

### `Player`

| Visibility | Member                           | Type                    |
| :--------: | :------------------------------- | :---------------------- |
|    `-`     | `name`                           | `String`                |
|    `-`     | `prestigePoints`                 | `int`                   |
|    `-`     | `wallet`                         | `PlayerAssets`          |
|    `-`     | `ownedCards`                     | `List<DevelopmentCard>` |
|    `-`     | `reservedCards`                  | `List<DevelopmentCard>` |
|    `-`     | `visitedBy`                      | `List<Noble>`           |
|    `+`     | `addPoints(points: int)`         | `void`                  |
|    `+`     | `reserve(card: DevelopmentCard)` | `void`                  |
|    `+`     | `reserveCard(card: DevelopmentCard)` | `boolean`           |
|    `+`     | `removeReservedCard(card: DevelopmentCard)` | `boolean`     |
|    `+`     | `canAfford(noble: Noble)`        | `boolean`               |
|    `+`     | `addNoble(noble: Noble)`         | `void`                  |
|    `+`     | `addOwnedCard(card: DevelopmentCard)` | `void`            |
|    `+`     | `addPrestigePoints(points: int)`  | `void`                  |
|    `+`     | `addToken(gem: GemColor, amount: int)` | `void`           |
|    `+`     | `spendToken(gem: GemColor, amount: int)` | `void`         |
|    `+`     | `removeToken(gem: GemColor, amount: int)` | `void`        |
|    `+`     | `getTokenCount(gem: GemColor)`    | `int`                   |
|    `+`     | `getTotalTokens()`                | `int`                   |
|    `+`     | `getTokens()`                    | `Map<GemColor, Integer>`|
|    `+`     | `getBonuses()`                   | `Map<GemColor, Integer>`|
|    `+`     | `getNobles()`                    | `List<Noble>`           |
|    `+`     | `getName()`                      | `String`                |
|    `+`     | `getPrestigePoints()`            | `int`                   |
|    `+`     | `getWallet()`                    | `PlayerAssets`          |
|    `+`     | `getOwnedCards()`                | `List<DevelopmentCard>` |
|    `+`     | `getReservedCards()`             | `List<DevelopmentCard>` |
|    `+`     | `getVisitedBy()`                 | `List<Noble>`           |

> No setters for list fields — mutations go through controlled methods (`addPoints`, `reserve`, `addNoble`, etc.).

---

## Package: `com.splendor.ai`

> AI layer that chooses actions programmatically via pluggable strategies.

### `AIPlayer`

- extends `Player`
- `- strategy: Strategy`
- `+ AIPlayer(String)`
- `+ AIPlayer(String, Strategy)`
- `+ chooseAction(Board): Action`
- `+ getStrategy(): Strategy`
- `+ setStrategy(Strategy): void`

### `Strategy` (interface)

- `+ chooseAction(Player, List<DevelopmentCard>, Map<GemColor, Integer>): Decision`

### `Decision`

- nested enum `Type { PURCHASE, RESERVE, TAKE_GEMS, PASS }`
- `- type: Type`
- `- card: DevelopmentCard`
- `- gemColors: List<GemColor>`
- static factory methods: `purchase`, `reserve`, `takeGems`, `pass`
- getters: `getType`, `getCard`, `getGemColors`

### `GreedyStrategy`

- implements `Strategy`
- chooses a purchase/reserve/take_gems/pass decision according to greedy heuristics.

---

## Package: `com.splendor.config`

> Configuration and data loading from CSV/properties files.

### `GameConfig`

- `- targetPrestige: int`
- `- maxTokensPerPlayer: int`
- `- gemCount2Players: int`
- `- gemCount3Players: int`
- `- gemCount4Players: int`
- `- goldTokenCount: int`
- `- developmentCardsPath: String`
- `- noblesPath: String`
- constructor with all fields
- getters for all fields
- `+ getGemCountForPlayers(int): int`

### `ConfigLoader` (interface)

- `+ load(String): GameConfig`

### `PropertiesConfigLoader`

- implements `ConfigLoader`
- loads properties and returns a `GameConfig`

### `CardLoader`

- `+ loadCards(String): List<DevelopmentCard>`

### `NobleLoader`

- `+ loadNobles(String): List<Noble>`

---

## Package: `com.splendor.view`

> Console rendering helpers for board and player status.

### `BoardRenderer`

- `+ renderBoard(Board)`
- `+ renderCards(Board)`
- `+ renderTokens(Board)`
- `+ renderNobles(Board)`
- formatting helpers and legend output

### `ConsoleColors`

- static named color constants used by renderers (`RESET`, `TIER1`, `NOBLE`, etc.)

### `GameView`

- fields: `BoardRenderer boardRenderer`, `PlayerStatusRenderer playerRenderer`, `GameEngine gameEngine`
- `+ displayGame(Board, List<Player>, GameEngine)`
- `+ displayTurn(Board, List<Player>, GameEngine)`
- `+ displayBoard(Board)`
- `+ displayPlayers(List<Player>, GameEngine)`
- `+ displayCurrentPlayer(Player)`
- `+ displayMessage(String)`
- `+ static promptDiscard(Player, int): int[]`

### `PlayerStatusRenderer`

- `+ renderPlayer(Player, String, boolean)`
- `+ renderAllPlayers(List<Player>, Player)`
- `+ renderScore(Player)`
- helpers for formatting tokens, cards and bonuses

---

## Structural Relationships

|Relationship|Owner|Target|Notes|
|:--|:--|:--|:--|
|**Composition**|`GameEngine`|`Board`|`Board` exists only within an active game|
|**Composition**|`Board`|`GemPile`|5 `GemPile` instances (one per non-gold colour)|
|**Aggregation**|`Board`|`Deck`|`Deck` exists independently; `Board` holds it during play|
|**Implementation**|`TakeGems`, `PurchaseCard`, `ReserveCard`|`Action`|Concrete classes providing logic for the abstract base|

---

## Functional Dependencies

> _"Uses-a" relationships where one class requires knowledge of another._

|Class|Depends On|Reason|
|:--|:--|:--|
|`Player`|`PlayerAssets`, `Noble`, `DevelopmentCard`|Wallet and bonus tracking, noble assignment and card inventory|
|`PlayerAssets`|`GemColor`|Gem color indexing and calculations|
|`DevelopmentCard`|`GemColor`|Bonus gem type|
|`Noble`|`GemColor`|Cost requirement colors|
|`Board`|`GemPile`, `Deck<DevelopmentCard>`, `Noble`|Game token supply, card tiers, visible nobles|
|`Action`|`Board`, `Player`|Validation and game-state mutation context|
|`GameEngine`|`Action`, `Board`, `Player`, `NobleSelectionStrategy`|Game loop, turn processing, noble choice behavior|
|`AIPlayer`|`Strategy`, `Decision`, `Board`, `Player`|Automated move decision-making based on state|
|`ConfigLoader` / `CardLoader` / `NobleLoader`|`GameConfig`, `DevelopmentCard`, `Noble`|Data loading for game configuration and components|
|`GameView` / `BoardRenderer` / `PlayerStatusRenderer`|`Board`, `Player`, `GameEngine`|Rendering current game state and player status|


