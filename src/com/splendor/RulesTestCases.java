package com.splendor;

import com.splendor.core.Action;
import com.splendor.core.Board;
import com.splendor.core.GameEngine;
import com.splendor.core.ReserveCard;
import com.splendor.core.TakeGems;
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
        System.out.println("========================================");
        System.out.println(" SPLENDOR RULE TEST CASES (PASS / FAIL) ");
        System.out.println("========================================");

        // 1. Token limits
        testRuleOf4_AllowsTwoSameWhenSupplyIs4();
        testRuleOf4_BlocksTwoSameWhenSupplyIs3();
        testTokenCap_EndOfTurnCannotStayAbove10();
        testTokenCap_CanTemporarilyGoAbove10AndDiscardBack();
        testTokenCap_CanDiscardOldTokensNotJustNewOnes();

        // 2. Reserving cards & gold
        testBlindReserve_FromTopOfDeck();
        testReserveLimit_Max3Cards();
        testReservedCardCannotBeDiscarded_CurrentDesignCheck();
        testReserveStillWorksWhenGoldSupplyEmpty();
        testGoldCannotBeDraftedNormally();

        // 3. Nobles & bonuses
        testNobleIsAutomaticAtEndOfTurn();
        testTokensDoNotAttractNobles();
        testOnlyOneNobleClaimedPerTurnWhenMultipleQualify();

        // 4. Game end & tie-breakers
        testEndGameShouldFinishCurrentRound_NotImplementedCheck();
        testTieBreaker_FewestDevelopmentCards();
        testTieBreaker_SharedVictoryIfStillTied();

        System.out.println();
        System.out.println("========================================");
        System.out.println("RESULT: Passed = " + passed + ", Failed = " + failed);
        System.out.println("========================================");
    }

    // =========================================================
    // Helpers
    // =========================================================

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

    private static void assertEquals(String testName, int expected, int actual) {
        if (expected == actual) {
            pass(testName);
        } else {
            fail(testName, "expected " + expected + " but got " + actual);
        }
    }

    private static void assertPlayerTokenCount(String testName, Player player, GemColor color, int expected) {
        int actual = player.getTokenCount(color);
        if (actual == expected) {
            pass(testName);
        } else {
            fail(testName, "expected " + color + "=" + expected + " but got " + actual);
        }
    }

    private static DevelopmentCard makeCard(int tier, int points, GemColor bonus, int w, int b, int g, int r, int bl) {
        return new DevelopmentCard(tier, points, bonus, new int[]{w, b, g, r, bl});
    }

    private static Noble makeNoble(String name, int points, GemColor color, int qty) {
        return new Noble(name, Arrays.asList(color), Arrays.asList(qty), points);
    }

    /**
     * Helper for blind reserve because your current codebase does not yet
     * have a dedicated Action class for reserving from the top of a deck.
     */
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

    private static class NoOpAction extends Action {
        @Override
        public boolean isValid(Player player, Board board) {
            return true;
        }

        @Override
        public void takeAction(Player player, Board board) {
            // intentionally does nothing
        }
    }

    /**
     * Rule-based winner helper for your requested tie-break tests.
     * This is NOT wired into your current GameEngine yet.
     */
    private static List<Player> determineWinnersByOfficialTieBreak(List<Player> players) {
        List<Player> winners = new ArrayList<>();
        int maxPoints = Integer.MIN_VALUE;

        for (Player p : players) {
            if (p.getPrestigePoints() > maxPoints) {
                maxPoints = p.getPrestigePoints();
            }
        }

        List<Player> tiedOnPoints = new ArrayList<>();
        for (Player p : players) {
            if (p.getPrestigePoints() == maxPoints) {
                tiedOnPoints.add(p);
            }
        }

        if (tiedOnPoints.size() == 1) {
            winners.add(tiedOnPoints.get(0));
            return winners;
        }

        int fewestCards = Integer.MAX_VALUE;
        for (Player p : tiedOnPoints) {
            fewestCards = Math.min(fewestCards, p.getOwnedCards().size());
        }

        for (Player p : tiedOnPoints) {
            if (p.getOwnedCards().size() == fewestCards) {
                winners.add(p);
            }
        }

        return winners;
    }

    // =========================================================
    // 1. Token Limits
    // =========================================================

    private static void testRuleOf4_AllowsTwoSameWhenSupplyIs4() {
        String testName = "Rule of 4: can take 2 same color when stack has 4";
        Board board = new Board();
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{2, 0, 0, 0, 0});
        boolean valid = action.isValid(player, board);

        assertTrue(testName, valid, "action should be valid when supply is 4");
    }

    private static void testRuleOf4_BlocksTwoSameWhenSupplyIs3() {
        String testName = "Rule of 4: cannot take 2 same color when stack has only 3";
        Board board = new Board();
        Player player = new Player("P1");

        board.getGemBank()[0].takeGems(1); // white goes from 4 -> 3

        TakeGems action = new TakeGems(new int[]{2, 0, 0, 0, 0});
        boolean valid = action.isValid(player, board);

        assertTrue(testName, !valid, "action should be invalid when supply is 3");
    }

    private static void testTokenCap_EndOfTurnCannotStayAbove10() {
        String testName = "10-token cap: wallet detects over-limit at end of turn";
        Player player = new Player("P1");

        player.addToken(GemColor.WHITE, 5);
        player.addToken(GemColor.BLUE, 5);
        player.addToken(GemColor.GREEN, 1);

        boolean overLimit = player.getWallet().aboveTenTokens();

        assertTrue(testName, overLimit, "player should be detected as above 10 tokens");
    }

    private static void testTokenCap_CanTemporarilyGoAbove10AndDiscardBack() {
        String testName = "10-token cap: can go above 10 during turn, then discard back to 10";
        Player player = new Player("P1");

        player.addToken(GemColor.WHITE, 4);
        player.addToken(GemColor.BLUE, 4);
        player.addToken(GemColor.GREEN, 2); // total = 10

        // Temporarily go above 10
        player.addToken(GemColor.RED, 2);   // total = 12

        boolean aboveBeforeDiscard = player.getWallet().aboveTenTokens();

        // Simulate discarding 2 tokens back down to 10
        int[] adjusted = player.getWallet().getTokens();
        adjusted[3] -= 2; // discard the 2 red
        player.getWallet().setTokens(adjusted);

        boolean aboveAfterDiscard = player.getWallet().aboveTenTokens();
        int total = player.getTotalTokens();

        assertTrue(testName,
                aboveBeforeDiscard && !aboveAfterDiscard && total == 10,
                "player should be allowed to exceed 10 temporarily, then end on exactly 10");
    }

    private static void testTokenCap_CanDiscardOldTokensNotJustNewOnes() {
        String testName = "10-token cap: can discard old tokens, not only newly picked tokens";
        Player player = new Player("P1");

        // Old pool
        player.addToken(GemColor.WHITE, 4);
        player.addToken(GemColor.BLUE, 4);
        player.addToken(GemColor.GREEN, 1); // total = 9

        // Newly picked this turn
        player.addToken(GemColor.RED, 2);   // total = 11

        // Discard an OLD token (blue), not one of the new red tokens
        int[] adjusted = player.getWallet().getTokens();
        adjusted[1] -= 1; // discard 1 BLUE
        player.getWallet().setTokens(adjusted);

        boolean correct =
                player.getTotalTokens() == 10 &&
                player.getTokenCount(GemColor.RED) == 2 &&
                player.getTokenCount(GemColor.BLUE) == 3;

        assertTrue(testName, correct, "should be able to keep new tokens and discard an older token instead");
    }

    // =========================================================
    // 2. Reserving Cards & Gold Tokens
    // =========================================================

    private static void testBlindReserve_FromTopOfDeck() {
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

        assertTrue(testName, correct, "blind reserve should draw top card from deck and give gold if available");
    }

    private static void testReserveLimit_Max3Cards() {
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
        DevelopmentCard c1 = makeCard(1, 0, GemColor.WHITE, 0, 0, 0, 0, 0);
        DevelopmentCard c2 = makeCard(1, 0, GemColor.BLUE, 0, 0, 0, 0, 0);
        DevelopmentCard c3 = makeCard(1, 0, GemColor.GREEN, 0, 0, 0, 0, 0);

        player.reserveCard(c1);
        player.reserveCard(c2);
        player.reserveCard(c3);

        // Current API exposes the mutable reserved list directly.
        // That means external code can remove a reserved card, which breaks the rule.
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

        // Empty the gold supply
        board.takeGold(board.getGoldSupply());

        DevelopmentCard card = makeCard(1, 0, GemColor.WHITE, 1, 0, 0, 0, 0);
        board.getVisibleCards().get(1).add(card);

        ReserveCard action = new ReserveCard(card);
        boolean valid = action.isValid(player, board);

        if (!valid) {
            fail(testName, "reserve action should still be valid even when gold is empty");
            return;
        }

        action.takeAction(player, board);

        boolean correct =
                player.getReservedCards().contains(card) &&
                player.getWallet().getGoldTokens() == 0 &&
                board.getGoldSupply() == 0;

        assertTrue(testName, correct, "card should be reserved even though no gold is gained");
    }

    private static void testGoldCannotBeDraftedNormally() {
        String testName = "Gold tokens cannot be drafted through normal gem-taking action";
        Board board = new Board();
        Player player = new Player("P1");

        TakeGems action = new TakeGems(new int[]{0, 0, 0, 0, 0, 1});
        boolean valid = action.isValid(player, board);

        assertTrue(testName, !valid, "gold should not be draftable via TakeGems");
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

        // Give permanent bonus, not token
        p1.getWallet().addBonus(0); // WHITE bonus

        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);
        engine.nextTurn(new NoOpAction());

        boolean correct =
                p1.getVisitedBy().size() == 1 &&
                p1.getVisitedBy().contains(noble) &&
                p1.getPrestigePoints() == 3 &&
                board.getVisibleNobles().isEmpty();

        assertTrue(testName, correct, "noble should be claimed automatically after the turn");
    }

    private static void testTokensDoNotAttractNobles() {
        String testName = "Tokens do not attract nobles; only permanent bonuses matter";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Board board = new Board();

        Noble noble = makeNoble("White Noble", 3, GemColor.WHITE, 1);
        board.getVisibleNobles().add(noble);

        // Give token only, no permanent bonus
        p1.addToken(GemColor.WHITE, 5);

        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);
        engine.nextTurn(new NoOpAction());

        boolean correct =
                p1.getVisitedBy().isEmpty() &&
                board.getVisibleNobles().size() == 1;

        assertTrue(testName, correct, "noble should NOT be claimed using tokens alone");
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

        p1.getWallet().addBonus(0); // WHITE bonus

        GameEngine engine = new GameEngine(Arrays.asList(p1, p2), board);
        engine.nextTurn(new NoOpAction());

        boolean correct =
                p1.getVisitedBy().size() == 1 &&
                board.getVisibleNobles().size() == 1;

        assertTrue(testName, correct, "engine should grant only one noble when multiple qualify");
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

        // Current engine only has checkWin(), which returns immediately.
        // It does not track "final round" or ensure equal turns.
        boolean currentCheck = engine.checkWin();

        if (currentCheck) {
            fail(testName, "current GameEngine detects win immediately but does not model finishing the round");
        } else {
            pass(testName);
        }
    }

    private static void testTieBreaker_FewestDevelopmentCards() {
        String testName = "Tie-breaker: tied points -> player with fewer development cards wins";

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        p1.addPrestigePoints(15);
        p2.addPrestigePoints(15);

        // P1 has 6 dev cards
        for (int i = 0; i < 6; i++) {
            p1.addOwnedCard(makeCard(1, 0, GemColor.WHITE, 0, 0, 0, 0, 0));
        }

        // P2 has 4 dev cards
        for (int i = 0; i < 4; i++) {
            p2.addOwnedCard(makeCard(3, 0, GemColor.BLUE, 0, 0, 0, 0, 0));
        }

        List<Player> winners = determineWinnersByOfficialTieBreak(Arrays.asList(p1, p2));

        boolean correct =
                winners.size() == 1 &&
                winners.get(0) == p2;

        assertTrue(testName, correct, "player with fewer development cards should win the tie");
    }

    private static void testTieBreaker_SharedVictoryIfStillTied() {
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

        assertTrue(testName, correct, "victory should be shared if points and dev-card count are both tied");
    }
}
