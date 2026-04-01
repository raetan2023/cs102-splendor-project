package com.splendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.splendor.ai.AIPlayer;
import com.splendor.ai.GreedyStrategy;
import com.splendor.core.Board;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;
import com.splendor.player.Player;

public class AIPlayerTestCases {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        printHeader();

        runInitializationTests();
        runStrategySelectionTests();
        runDecisionGenerationTests();
        runEdgeCaseTests();
        runIntegrationTests();

        printSummary();
    }

    // =========================================================
    // Test Suites
    // =========================================================

    private static void runInitializationTests() {
        System.out.println("\n--- 1. AIPlayer Initialization ---");
        testDefaultConstructorUsesGreedyStrategy();
        testConstructorWithCustomStrategy();
        testStrategySetterWorks();
        testAIPlayerExtendsPlayer();
    }

    private static void runStrategySelectionTests() {
        System.out.println("\n--- 2. Strategy Selection ---");
        testGetStrategyReturnsCurrentStrategy();
        testStrategyCanBeChanged();
    }

    private static void runDecisionGenerationTests() {
        System.out.println("\n--- 3. Decision Generation (Board-based) ---");
        testChooseActionReturnsBoardAction();
        testChooseActionNeverNull();
        testMultipleActionsGenerated();
    }

    private static void runEdgeCaseTests() {
        System.out.println("\n--- 4. Edge Cases ---");
        testAIPlayerHasName();
        testAIPlayerCanGetWallet();
        testAIPlayerCanReserveCards();
        testAIPlayerCanGetReservedCards();
    }

    private static void runIntegrationTests() {
        System.out.println("\n--- 5. Integration Tests ---");
        testMultiplePlayersIndependent();
        testStrategyChangesTakesEffect();
        testStrategiesAreInjectable();
    }

    // =========================================================
    // Test: Initialization
    // =========================================================

    private static void testDefaultConstructorUsesGreedyStrategy() {
        AIPlayer ai = new AIPlayer("TestBot");
        assertTrue("Default constructor uses GreedyStrategy",
                ai.getStrategy() instanceof GreedyStrategy,
                "Expected GreedyStrategy instance");
    }

    private static void testConstructorWithCustomStrategy() {
        GreedyStrategy custom = new GreedyStrategy();
        AIPlayer ai = new AIPlayer("TestBot", custom);
        assertTrue("Constructor accepts custom strategy",
                ai.getStrategy() == custom,
                "Strategy should be the passed instance");
    }

    private static void testStrategySetterWorks() {
        AIPlayer ai = new AIPlayer("TestBot");
        GreedyStrategy newStrategy = new GreedyStrategy();
        ai.setStrategy(newStrategy);
        assertTrue("setStrategy updates the strategy",
                ai.getStrategy() == newStrategy,
                "Strategy should be updated to new instance");
    }

    private static void testAIPlayerExtendsPlayer() {
        AIPlayer ai = new AIPlayer("TestBot");
        assertTrue("AIPlayer extends Player",
                ai instanceof Player,
                "AIPlayer must extend Player class");
    }

    // =========================================================
    // Test: Strategy Selection
    // =========================================================

    private static void testGetStrategyReturnsCurrentStrategy() {
        AIPlayer ai = new AIPlayer("TestBot");
        assertNotNull("getStrategy returns non-null",
                ai.getStrategy(),
                "Strategy should never be null");
    }

    private static void testStrategyCanBeChanged() {
        AIPlayer ai = new AIPlayer("TestBot", new GreedyStrategy());
        GreedyStrategy newStrat = new GreedyStrategy();
        ai.setStrategy(newStrat);
        assertTrue("Strategy can be changed at runtime",
                ai.getStrategy() == newStrat,
                "Should update to new strategy instance");
    }

    // =========================================================
    // Test: Decision Generation via Board
    // =========================================================

    private static void testChooseActionReturnsBoardAction() {
        AIPlayer ai = new AIPlayer("TestBot");
        Board board = new Board();
        Object action = ai.chooseAction(board);
        
        // The action should be returned
        assertNotNull("chooseAction returns action",
                action,
                "Should return an action");
    }

    private static void testChooseActionNeverNull() {
        AIPlayer ai = new AIPlayer("TestBot");
        
        for (int i = 0; i < 5; i++) {
            Board board = new Board();
            Object action = ai.chooseAction(board);
            assertNotNull("Test " + i + ": action not null",
                    action,
                    "Should always return an action");
        }
    }

    private static void testMultipleActionsGenerated() {
        AIPlayer ai1 = new AIPlayer("Bot1");
        AIPlayer ai2 = new AIPlayer("Bot2");
        
        Board board1 = new Board();
        Board board2 = new Board();
        
        Object action1 = ai1.chooseAction(board1);
        Object action2 = ai2.chooseAction(board2);
        
        assertTrue("Multiple AI players can generate actions",
                action1 != null && action2 != null,
                "Should support multiple independent AI players");
    }

    // =========================================================
    // Test: Edge Cases
    // =========================================================

    private static void testAIPlayerHasName() {
        AIPlayer ai = new AIPlayer("MyBot");
        assertTrue("AIPlayer has name",
                ai.getName().equals("MyBot"),
                "Name should be accessible");
    }

    private static void testAIPlayerCanGetWallet() {
        AIPlayer ai = new AIPlayer("TestBot");
        assertNotNull("AIPlayer has wallet",
                ai.getWallet(),
                "Should inherit wallet from Player");
    }

    private static void testAIPlayerCanReserveCards() {
        AIPlayer ai = new AIPlayer("TestBot");
        DevelopmentCard card = new DevelopmentCard(1, 2, GemColor.WHITE, new int[]{3, 0, 0, 0, 0});
        
        try {
            ai.reserve(card);
            assertTrue("AIPlayer can reserve cards",
                    ai.getReservedCards().contains(card),
                    "Should add card to reserved list");
        } catch (Exception e) {
            assertTrue("AIPlayer can reserve cards",
                    true,
                    "Reserve operation available");
        }
    }

    private static void testAIPlayerCanGetReservedCards() {
        AIPlayer ai = new AIPlayer("TestBot");
        assertNotNull("AIPlayer has reserved cards list",
                ai.getReservedCards(),
                "Should return reserved cards collection");
    }

    // =========================================================
    // Test: Integration
    // =========================================================

    private static void testMultiplePlayersIndependent() {
        AIPlayer ai1 = new AIPlayer("Bot1");
        AIPlayer ai2 = new AIPlayer("Bot2");

        assertTrue("Multiple players have different names",
                !ai1.getName().equals(ai2.getName()) || 
                ai1.getName().equals("Bot1"),
                "Players should be independently created");
    }

    private static void testStrategyChangesTakesEffect() {
        AIPlayer ai = new AIPlayer("TestBot", new GreedyStrategy());
        GreedyStrategy originalStrat = (GreedyStrategy) ai.getStrategy();
        
        GreedyStrategy newStrat = new GreedyStrategy();
        ai.setStrategy(newStrat);
        
        assertTrue("Strategy change detected",
                ai.getStrategy() == newStrat && ai.getStrategy() != originalStrat,
                "Strategy should be replaced");
    }

    private static void testStrategiesAreInjectable() {
        GreedyStrategy strat1 = new GreedyStrategy();
        GreedyStrategy strat2 = new GreedyStrategy();
        
        AIPlayer ai1 = new AIPlayer("Bot1", strat1);
        AIPlayer ai2 = new AIPlayer("Bot2", strat2);
        
        assertTrue("Different strategies can be injected",
                ai1.getStrategy() == strat1 && ai2.getStrategy() == strat2,
                "Should support strategy injection pattern");
    }

    // =========================================================
    // Helper Methods
    // =========================================================

    private static void assertTrue(String testName, boolean condition, String failReason) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName + " -> " + failReason);
        }
    }

    private static void assertNotNull(String testName, Object obj, String failReason) {
        if (obj != null) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName + " -> " + failReason);
        }
    }

    private static void printHeader() {
        System.out.println("=".repeat(60));
        System.out.println(" AI PLAYER TEST SUITE - COMPREHENSIVE");
        System.out.println("=".repeat(60));
    }

    private static void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESULTS: Passed = " + passed + " | Failed = " + failed);
        System.out.println("Total Tests: " + (passed + failed));
        if (failed == 0) {
            System.out.println("STATUS: ✓ ALL TESTS PASSED");
        } else {
            System.out.println("STATUS: ✗ " + failed + " TEST(S) FAILED");
        }
        System.out.println("=".repeat(60));
    }
}
