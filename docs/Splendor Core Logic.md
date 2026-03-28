# **Splendor Core Logic**

## **Setup**

GameEngine.startGame()

* 3 decks, 4 face-up cards per level → Board holds 3 Deck\<DevelopmentCard\> \+ a visibleCards map. Tier 1 has 40 cards, tier 2 has 30 cards and tier 1 has 20 cards.

Board  
├── deck1: Deck\<DevelopmentCard\>   (36 remaining tier 1 cards)  
├── deck2: Deck\<DevelopmentCard\>   (26 remaining tier 2 cards)    
├── deck3: Deck\<DevelopmentCard\>   (16 remaining tier 3 cards)  
└── visibleCards: Map\<Integer, List\<DevelopmentCard\>\>

      1 → \[card, card, card, card\]  
      2 → \[card, card, card, card\]  
      3 → \[card, card, card, card\]

* 2 players in GameEngine.players → 1 AI and 1 human  
* 4 gems per colour → stored in Board.gemBank (a GemPile). GemPile.supply initialised to 4 at the start. The supply ranges between 0 and 4 throughout the game.  
* 5 gold → stored in goldSupply under Bank  
* 3 nobles drawn randomly → stored in Board.visibleNobles

## **Per Turn**

GameEngine.nextTurn()

Each turn is one of 3 possible Action subclasses:

### **TakeGems.isValid() / takeAction()**

* **Option A:** Take 2 of the same colour → only valid if GemPile.canTakeTwo() returns true (i.e. \>=4 of that colour in Board.gemBank)  
* **Option B:** Take up to 3 gems of 3 different colours → no minimum bank requirement per colour, but you can only take 1 of each. A player can also take fewer than 3 (e.g. if fewer than 3 colours are available, or by choice)  
* After taking: if PlayerAssets.aboveTenTokens() is true, player must discard down to 10\. This discard logic lives inside TakeGems.takeAction() — go through addToken() and removeToken(). Can prompt the player to choose which to discard via GameView.

### **PurchaseCard.isValid() / takeAction()**

* Player can buy a face-up card from the board in visibleCards, or a card from their own reservedCards  
* To check if PurchaseCard.isValid(): Cost \= card's gem cost minus PlayerAssets.getBonuses() (permanent gems from owned cards), remainder paid from PlayerAssets.getTokens(). Gold fills any remaining gap, use PlayerAssets.goldNeeded(cost\[\]) for this.  
* On purchase:   
1. add DevelopmentCard to player's owned cards (note: Player needs an ownedCards: List\<DevelopmentCard\> field — currently missing from UML). If the card has bonus, call PlayerAssets.addBonus(colorIndex). If card has points, call Player.addPoints().  
2. Return tokens to respective GemPile.supply  
* **Nobles are not purchased\!** — they visit automatically (see below)

* **Buying a reserved card (`PurchaseCard.takeAction()`)**  
1. Player selects a card from `Player.getReservedCards()`  
2. Calculate cost: `card.getCost()` minus `PlayerAssets.getBonuses()` (permanent gems reduce cost, floor 0 per colour)  
3. Pay remaining cost from `PlayerAssets.tokens[]`  
4. `PlayerAssets.goldNeeded(cost[])` determines how many gold to spend to cover any shortfall  
5. If gold is used: call `PlayerAssets.useGoldToken()` (once per gold spent) and return that gold to `Board.setGoldSupply(goldSupply + 1)`  
6. Remove card from `Player.reservedCards`, add to `Player.ownedCards`  
7. `PlayerAssets.addBonus(card.getBonusColor().ordinal())`  
8. `Player.addPoints(card.getPrestigePoints())`

**Gold always goes back to `Board.goldSupply` when spent**, not discarded. It circulates just like normal tokens.

**Reserving a card (`ReserveCard.takeAction()`)**

1. Check `ReserveCard.isValid()` — `reservedCards.size() < 3`  
2. Card is removed from `Board.visibleCards` (or drawn from top of deck if reserving face-down)  
3. Card added to `Player.reservedCards`  
4. Check if `Board.goldSupply > 0` before giving gold — if no gold left, player reserves the card but gets nothing  
5. If gold available: `Board.setGoldSupply(goldSupply - 1)` and `PlayerAssets.addGoldToken()`  
6. Check if `PlayerAssets.aboveTenTokens()` — gold counts toward the 10-token limit, so player may need to discard

## **After Every Turn (automatic, inside GameEngine.nextTurn())**

Noble check — not an action, not a player choice (unless multiple qualify):

* For each noble in Board.visibleNobles, call noble.needs(player) → checks if player's PlayerAssets.getBonuses() meets the noble's requirementColors \+ requirementQty  
* If a noble qualifies: call Player.addNoble(noble) (this should be implemented inside Player.addNoble(): when noble visits, add to Player.visitedBy and call Player.addPoints(3)), remove noble from Board.visibleNobles  
* If multiple qualify: player chooses one (handled via GameView prompt)

## **Win Check (end of round, inside GameEngine)**

* After both players complete their turn, check if either has prestigePoints \>= 15  
* If yes, game ends — player with higher prestigePoints wins  
* Tie-breaker: fewest ownedCards.size() wins

## **Key Gaps to Flag for UML Update**

Before Antigravity generates the classes:

| Gap | Fix needed |
| :---- | :---- |
| \`Board\` has no \`visibleCards\` | Add \`Map\<Integer, List\<DevelopmentCard\>\> visibleCards\` |
| \`Player\` has no \`ownedCards\` | Add \`List\<DevelopmentCard\> ownedCards\` |
| \`getBonusColor()\` returns \`int\` not \`GemColor\` | Fix return type |
| \`GameEngine\` has no win check method | Add \`checkWin(): boolean\` |
| \`GemPile\` only has \`canTakeTwo()\` | Also needs \`takeGems()\`, \`returnGems()\` methods |

Rae to do:  
GemPile.takeGems(), returnGems() and Board.visibleCards  
Add checkWin() in GameEngine  
Add takeGems() and returnGems() in GemPile  
Add goldSupply (int) in Board. Starts at 5  
Add visibleCards Map\<Integer, List\<DevelopmentCard\>\> in Board