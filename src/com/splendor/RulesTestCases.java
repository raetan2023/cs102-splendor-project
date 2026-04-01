package com.splendor;

import com.splendor.core.Action;
import com.splendor.core.Board;
import com.splendor.core.GameEngine;
import com.splendor.core.ReserveCard;
import com.splendor.core.TakeGems;
import com.splendor.core.PurchaseCard;
import com.splendor.model.Deck;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.model.Noble;
import com.splendor.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RulesTestCases {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        printHeader();

        runTokenLimitTests();
        runTokenDraftingTests();
        runReserveTests();
        runPurchaseTests();
        runNobleTests();
        runEndGameTests();

        printSummary();
    }

    // =========================================================
    // Test Suites
    // =========================================================

    private static void runTokenLimitTests() {
        System.out.println("\n--- 1. Token Limits ---");
        testRuleOf4AllowsTwoSameWhenSupplyIs4();
        testRuleOf4BlocksTwoSameWhenSupplyIs3();
        testTokenCapDetectedAbove10();
        testCanTemporarilyGoAbove10ThenDiscardBack();
        testCanDiscardOldTokensNotJustNewOnes();
    }

    private static void runReserveTests() {
        System.out.println("\n--- 2. Reserving Cards & Gold Tokens ---");
        testBlindReserveFromTopOfDeck();
        testReserveLimitMax3Cards();
        testReservedCardCannotBeDiscarded_CurrentDesignCheck();
        testReserveStillWorksWhenGoldSupplyEmpty();
        testGoldCannotBeDraftedNormally();
    }

    private static void runNobleTests() {
        System.out.println("\n--- 3. Nobles & Bonuses ---");
        testNobleIsAutomaticAtEndOfTurn();
        testTokensDoNotAttractNobles();
        testOnlyOneNobleClaimedPerTurnWhenMultipleQualify();
    }

    private static void runEndGameTests() {
        System.out.println("\n--- 4. Game End & Tie-Breakers ---");
        testEndGameShouldFinishCurrentRound_NotImplementedCheck();
        testTieBreakerFewestDevelopmentCards();
        testTieBreakerSharedVictoryIfStillTied();
    }

    // =========================================================
    // Output Helpers
    // =========================================================

    private static void printHeader() {
        System.out.println("========================================");
        System.out.println(" SPLENDOR RULE TEST CASES (PASS / FAIL) ");
        System.out.println("========================================");
    }

    private static void printSummary() {
        System.out.println("\n========================================");
        System.out.println("RESULT: Passed = " + passed + ", Failed = " + failed);
        System.out.println("========================================");
    }

    private static void pass(String testName) {
        passed++;
        System.out.println("[PASS] " + testName);
    }

    private static void fail(String testName, String reason) {
        failed++;
        System.out.println("[FAIL] " + testName + " -> " + reason);
    }

    private static void assertTrue(String testName, boolean condition, String failReason) {
        if (condition) {
            pass(testName);
        } else {
            fail(testName, failReason);
        }
    }

    // =========================================================
    // Object Builders / Helpers
    // =========================================================

    private static DevelopmentCard makeCard(int tier, int points, GemColor bonus,
                                            int white, int blue, int green, int red, int black) {
        return new DevelopmentCard(tier, points, bonus,
                new int[]{white, blue, green, red, black});
    }

    private static Noble makeNoble(String name, int points, GemColor color, int qty) {
        return new Noble(name, Arrays.asList(color), Arrays.asList(qty), points);
    }

    private static boolean reserveBlind(Board board, Player player, int tier) {
        if (player.getReservedCards().size() >= 3) {
            return false;
        }

        Deck<DevelopmentCard> deck = board.getAllCards().get(tier);
        if (deck == null || deck.isEmpty()) {
            return false;
        }

        DevelopmentCard drawn = deck.draw();
        if (drawn == null) {
            return false;
        }

        player.reserve(drawn);

        if (board.getGoldSupply() > 0) {
            player.getWallet().addGoldToken();
            board.takeGold(1);
        }

        return true;
    }

    private static List<Player> determineWinnersByOfficialTieBreak(List<Player> players) {
        List<Player> tiedOnPoints = new ArrayList<>();
        List<Player> winners = new ArrayList<>();

        int maxPoints = Integer.MIN_VALUE;
        for (Player player : players) {
            maxPoints = Math.max(maxPoints, player.getPrestigePoints());
        }

        for (Player player : players) {
            if (player.getPrestigePoints() == maxPoints) {
                tiedOnPoints.add(player);
            }
        }

        if (tiedOnPoints.size() == 1) {
            winners.add(tiedOnPoints.get(0));
            return winners;
        }

        int fewestCards = Integer.MAX_VALUE;
        for (Player player : tiedOnPoints) {
            fewestCards = Math.min(fewestCards, player.getOwnedCards().size());
        }

        for (Player player : tiedOnPoints) {
            if (player.getOwnedCards().size() == fewestCards) {
                winners.add(player);
            }
        }

        return winners;
    }

    // =========================================================
    // Test Support Classes
    // =========================================================

    private static class NoOpAction extends Action {
        @Override
        public boolean isValid(Player player, Board board) {
            return true;
        }

        @Override
        public void takeAction(Player player, Board board) {
            // do nothing
        }
    }

    // =========================================================
    // 1. Token Limits
    // =========================================================

    private static void testRuleOf4AllowsTwoSameWhenSupplyIs4() {
        String testName = "Rule of 4: can take 2 same color when stack has 4";
        Board board = new Board();
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{2, 0, 0, 0, 0});
        assertTrue(testName, action.isValid(player, board),
                "action should be valid when supply is 4");
    }

    private static void testRuleOf4BlocksTwoSameWhenSupplyIs3() {
        String testName = "Rule of 4: cannot take 2 same color when stack has only 3";
        Board board = new Board();
        Player player = new Player("P1");

        board.getGemBank()[0].takeGems(1);

        TakeGems action = new TakeGems(new int[]{2, 0, 0, 0, 0});
        assertTrue(testName, !action.isValid(player, board),
                "action should be invalid when supply is 3");
    }

    private static void testTokenCapDetectedAbove10() {
        String testName = "10-token cap: wallet detects over-limit at end of turn";
        Player player = new Player("P1");

        player.addToken(GemColor.WHITE, 5);
        player.addToken(GemColor.BLUE, 5);
        player.addToken(GemColor.GREEN, 1);

        assertTrue(testName, player.getWallet().aboveTenTokens(),
                "player should be detected as above 10 tokens");
    }

    private static void testCanTemporarilyGoAbove10ThenDiscardBack() {
        String testName = "10-token cap: can go above 10 during turn, then discard back to 10";
        Player player = new Player("P1");

        player.addToken(GemColor.WHITE, 4);
        player.addToken(GemColor.BLUE, 4);
        player.addToken(GemColor.GREEN, 2);
        player.addToken(GemColor.RED, 2);

        boolean aboveBeforeDiscard = player.getWallet().aboveTenTokens();

        int[] adjusted = player.getWallet().getTokens();
        adjusted[3] -= 2;
        player.getWallet().setTokens(adjusted);

        boolean correct =
                aboveBeforeDiscard &&
                !player.getWallet().aboveTenTokens() &&
                player.getTotalTokens() == 10;

        assertTrue(testName, correct,
                "player should be allowed to exceed 10 temporarily, then end on exactly 10");
    }

    private static void testCanDiscardOldTokensNotJustNewOnes() {
        String testName = "10-token cap: can discard old tokens, not only newly picked tokens";
        Player player = new Player("P1");

        player.addToken(GemColor.WHITE, 4);
        player.addToken(GemColor.BLUE, 4);
        player.addToken(GemColor.GREEN, 1);
        player.addToken(GemColor.RED, 2);

        int[] adjusted = player.getWallet().getTokens();
        adjusted[1] -= 1; // discard 1 BLUE
        player.getWallet().setTokens(adjusted);

        boolean correct =
                player.getTotalTokens() == 10 &&
                player.getTokenCount(GemColor.RED) == 2 &&
                player.getTokenCount(GemColor.BLUE) == 3;

        assertTrue(testName, correct,
                "should be able to keep new tokens and discard an older token instead");
    }

    // =========================================================
    // 2. Reserving Cards & Gold Tokens
    // =========================================================

    private static void testBlindReserveFromTopOfDeck() {
        String testName = "Blind reserve: can reserve from top of a tier deck";
        Board board = new Board();
        Player player = new Player("P1");

        DevelopmentCard hidden = makeCard(2, 1, GemColor.GREEN, 0, 2, 2, 0, 0);
        Deck<DevelopmentCard> tier2 = new Deck<>();
        tier2.add(hidden);
        board.getAllCards().put(2, tier2);

        int goldBefore = board.getGoldSupply();
        boolean result = reserveBlind(board, player, 2);

        boolean correct =
                result &&
                player.getReservedCards().size() == 1 &&
                player.getReservedCards().get(0) == hidden &&
                board.getAllCards().get(2).size() == 0 &&
                player.getWallet().getGoldTokens() == 1 &&
                board.getGoldSupply() == goldBefore - 1;

        assertTrue(testName, correct,
                "blind reserve should draw top card from deck and give gold if available");
    }

    private static void testReserveLimitMax3Cards() {
        String testName = "Reserve limit: cannot have more than 3 reserved cards";
        Player player = new Player("P1");

        player.reserveCard(makeCard(1, 0, GemColor.WHITE, 0, 0, 0, 0, 0));
        player.reserveCard(makeCard(1, 0, GemColor.BLUE, 0, 0, 0, 0, 0));
        player.reserveCard(makeCard(1, 0, GemColor.GREEN, 0, 0, 0, 0, 0));

        boolean fourthResult = player.reserveCard(makeCard(1, 0, GemColor.RED, 0, 0, 0, 0, 0));

        assertTrue(testName, !fourthResult, "4th reserve should be rejected");
    }

    private static void testReservedCardCannotBeDiscarded_CurrentDesignCheck() {
        String testName = "Reserve edge case: reserved card cannot be discarded to make room";

        Player player = new Player("P1");
        player.reserveCard(makeCard(1, 0, GemColor.WHITE, 0, 0, 0, 0, 0));
        player.reserveCard(makeCard(1, 0, GemColor.BLUE, 0, 0, 0, 0, 0));
        player.reserveCard(makeCard(1, 0, GemColor.GREEN, 0, 0, 0, 0, 0));

        boolean wasAbleToCheat;
        try {
            player.getReservedCards().remove(0);
            wasAbleToCheat = true;
        } catch (Exception e) {
            wasAbleToCheat = false;
        }

        if (wasAbleToCheat) {
            fail(testName, "current design allows external code to remove reserved cards directly");
        } else {
            pass(testName);
        }
    }

    private static void testReserveStillWorksWhenGoldSupplyEmpty() {
        String testName = "Reserve edge case: can still reserve when gold supply is empty";
        Board board = new Board();
        Player player = new Player("P1");

        board.takeGold(board.getGoldSupply());

        DevelopmentCard card = makeCard(1, 0, GemColor.WHITE, 1, 0, 0, 0, 0);
        board.getVisibleCards().get(1).add(card);

        ReserveCard action = new ReserveCard(card);
        if (!action.isValid(player, board)) {
            fail(testName, "reserve action should still be valid even when gold is empty");
            return;
        }

        action.takeAction(player, board);

        boolean correct =
                player.getReservedCards().contains(card) &&
                player.getWallet().getGoldTokens() == 0 &&
                board.getGoldSupply() == 0;

        assertTrue(testName, correct,
                "card should be reserved even though no gold is gained");
    }

    private static void testGoldCannotBeDraftedNormally() {
        String testName = "Gold tokens cannot be drafted through normal gem-taking action";
        Board board = new Board();
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{0, 0, 0, 0, 0, 1});
        assertTrue(testName, !action.isValid(player, board),
                "gold should not be draftable via TakeGems");
    }

    // =========================================================
    // 3. Nobles & Bonuses
    // =========================================================

    private static void testNobleIsAutomaticAtEndOfTurn() {
        String testName = "Nobles are automatic at end of turn, not a separate action";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Board board = new Board();

        Noble noble = makeNoble("White Noble", 3, GemColor.WHITE, 1);
        board.getVisibleNobles().add(noble);

        p1.getWallet().addBonus(0);

        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);
        engine.nextTurn(new NoOpAction());

        boolean correct =
                p1.getVisitedBy().size() == 1 &&
                p1.getVisitedBy().contains(noble) &&
                p1.getPrestigePoints() == 3 &&
                board.getVisibleNobles().isEmpty();

        assertTrue(testName, correct,
                "noble should be claimed automatically after the turn");
    }

    private static void testTokensDoNotAttractNobles() {
        String testName = "Tokens do not attract nobles; only permanent bonuses matter";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Board board = new Board();

        Noble noble = makeNoble("White Noble", 3, GemColor.WHITE, 1);
        board.getVisibleNobles().add(noble);

        p1.addToken(GemColor.WHITE, 5);

        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);
        engine.nextTurn(new NoOpAction());

        boolean correct =
                p1.getVisitedBy().isEmpty() &&
                board.getVisibleNobles().size() == 1;

        assertTrue(testName, correct,
                "noble should NOT be claimed using tokens alone");
    }

    private static void testOnlyOneNobleClaimedPerTurnWhenMultipleQualify() {
        String testName = "Multiple nobles edge case: only one noble can be claimed per turn";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Board board = new Board();

        Noble noble1 = makeNoble("Noble A", 3, GemColor.WHITE, 1);
        Noble noble2 = makeNoble("Noble B", 3, GemColor.WHITE, 1);

        board.getVisibleNobles().add(noble1);
        board.getVisibleNobles().add(noble2);

        p1.getWallet().addBonus(0);

        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);
        engine.nextTurn(new NoOpAction());

        boolean correct =
                p1.getVisitedBy().size() == 1 &&
                board.getVisibleNobles().size() == 1;

        assertTrue(testName, correct,
                "engine should grant only one noble when multiple qualify");
    }

    // =========================================================
    // 4. Game End & Tie-Breakers
    // =========================================================

    private static void testEndGameShouldFinishCurrentRound_NotImplementedCheck() {
        String testName = "End game: current round should finish after someone reaches 15";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Board board = new Board();
        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);

        p1.addPrestigePoints(15);

        boolean currentCheck = engine.checkWin();

        if (currentCheck) {
            fail(testName, "current GameEngine detects win immediately but does not model finishing the round");
        } else {
            pass(testName);
        }
    }

    private static void testTieBreakerFewestDevelopmentCards() {
        String testName = "Tie-breaker: tied points -> player with fewer development cards wins";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        p1.addPrestigePoints(15);
        p2.addPrestigePoints(15);

        for (int i = 0; i < 6; i++) {
            p1.addOwnedCard(makeCard(1, 0, GemColor.WHITE, 0, 0, 0, 0, 0));
        }

        for (int i = 0; i < 4; i++) {
            p2.addOwnedCard(makeCard(3, 0, GemColor.BLUE, 0, 0, 0, 0, 0));
        }

        List<Player> winners = determineWinnersByOfficialTieBreak(Arrays.asList(p1, p2));

        boolean correct = winners.size() == 1 && winners.get(0) == p2;

        assertTrue(testName, correct,
                "player with fewer development cards should win the tie");
    }

    private static void testTieBreakerSharedVictoryIfStillTied() {
        String testName = "Tie-breaker: if still tied after card count, victory is shared";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        p1.addPrestigePoints(15);
        p2.addPrestigePoints(15);

        for (int i = 0; i < 5; i++) {
            p1.addOwnedCard(makeCard(2, 0, GemColor.WHITE, 0, 0, 0, 0, 0));
            p2.addOwnedCard(makeCard(2, 0, GemColor.BLUE, 0, 0, 0, 0, 0));
        }

        List<Player> winners = determineWinnersByOfficialTieBreak(Arrays.asList(p1, p2));

        boolean correct =
                winners.size() == 2 &&
                winners.contains(p1) &&
                winners.contains(p2);

        assertTrue(testName, correct,
                "victory should be shared if points and dev-card count are both tied");
    }

    // =========================================================
    // 5. Token Drafting
    // =========================================================

    private static void runTokenDraftingTests() {
        System.out.println("\n--- 5. Token Drafting ---");
        testValidPick3DifferentTokens();
        testInvalidPick3With2SameColoredTokens();
        testCannotPickFromEmptyStack();
    }

    private static void testValidPick3DifferentTokens() {
        String testName = "Drafting: can take 3 different tokens";
        Board board = new Board();
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{1, 1, 1, 0, 0});
        assertTrue(testName, action.isValid(player, board),
                "action should be valid when picking 3 different tokens");
    }

    private static void testInvalidPick3With2SameColoredTokens() {
        String testName = "Drafting: cannot pick 2 same, 1 different in one turn";
        Board board = new Board();
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{2, 1, 0, 0, 0});
        assertTrue(testName, !action.isValid(player, board),
                "action should be invalid; can't mix a double-pick with single picks");
    }

    private static void testCannotPickFromEmptyStack() {
        String testName = "Drafting: cannot pick from an empty token stack";
        Board board = new Board();
        board.getGemBank()[0].takeGems(board.getGemBank()[0].getSupply()); // empty white stack
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{1, 1, 1, 0, 0});
        assertTrue(testName, !action.isValid(player, board),
                "action should be invalid if supply for requested token is 0");
    }

    // =========================================================
    // 6. Purchasing Cards
    // =========================================================

    private static void runPurchaseTests() {
        System.out.println("\n--- 6. Purchasing Cards ---");
        testPurchaseWithGold();
        testPurchaseWithBonusesOnly();
        testCannotAffordCard();
        testPurchasingReservedCard();
        testTokensReturnedToBankButBonusesKept();
    }

    private static void testPurchaseWithGold() {
        String testName = "Purchasing: Gold substitutes for missing tokens";
        Board board = new Board();
        Player player = new Player("P1");

        player.addToken(GemColor.GOLD, 2);
        player.addToken(GemColor.WHITE, 1);
        
        DevelopmentCard card = makeCard(1, 1, GemColor.BLUE, 3, 0, 0, 0, 0); // costs 3 white
        PurchaseCard action = new PurchaseCard(card, false);
        
        assertTrue(testName, action.isValid(player, board),
                "should be valid using 1 white and 2 gold");
    }

    private static void testPurchaseWithBonusesOnly() {
        String testName = "Purchasing: can buy using only bonuses";
        Board board = new Board();
        Player player = new Player("P1");

        for(int i=0; i<3; i++) player.getWallet().addBonus(0); // 3 white bonuses
        
        DevelopmentCard card = makeCard(1, 1, GemColor.BLUE, 3, 0, 0, 0, 0); // costs 3 white
        PurchaseCard action = new PurchaseCard(card, false);
        
        assertTrue(testName, action.isValid(player, board),
                "should be valid using 3 white bonuses");
    }

    private static void testCannotAffordCard() {
        String testName = "Purchasing: invalid if player lacks funds";
        Board board = new Board();
        Player player = new Player("P1");

        DevelopmentCard card = makeCard(1, 1, GemColor.BLUE, 3, 0, 0, 0, 0); // costs 3 white
        PurchaseCard action = new PurchaseCard(card, false);
        
        assertTrue(testName, !action.isValid(player, board),
                "should be invalid if player has nothing");
    }

    private static void testPurchasingReservedCard() {
        String testName = "Purchasing: reserved card is removed from hand";
        Board board = new Board();
        Player player = new Player("P1");

        DevelopmentCard card = makeCard(1, 1, GemColor.BLUE, 0, 0, 0, 0, 0); // costs nothing
        player.reserve(card);
        
        PurchaseCard action = new PurchaseCard(card, true);
        if(!action.isValid(player, board)) {
             fail(testName, "action should be valid");
             return;
        }
        action.takeAction(player, board);
        
        boolean correct = !player.getReservedCards().contains(card) && 
                          player.getOwnedCards().contains(card);
                          
        assertTrue(testName, correct,
                "card should be moved from reserved hand to owned");
    }

    private static void testTokensReturnedToBankButBonusesKept() {
        String testName = "Purchasing: tokens return to bank, bonuses stay";
        Board board = new Board();
        Player player = new Player("P1");

        int initialTokens = board.getGemBank()[0].getSupply(); // white stack

        player.addToken(GemColor.WHITE, 1); // 1 token
        board.getGemBank()[0].takeGems(1);
        
        player.getWallet().addBonus(0); // 1 bonus
        
        DevelopmentCard card = makeCard(1, 1, GemColor.BLUE, 2, 0, 0, 0, 0); // costs 2 white
        PurchaseCard action = new PurchaseCard(card, false);
        action.takeAction(player, board);
        
        boolean correct = player.getTokenCount(GemColor.WHITE) == 0 &&
                          board.getGemBank()[0].getSupply() == initialTokens &&
                          player.getWallet().getBonuses(0) == 1; // bonus remains
                          
        assertTrue(testName, correct,
                "spent tokens should return; bonuses should remain");
    }
}

