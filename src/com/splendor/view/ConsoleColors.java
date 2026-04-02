package com.splendor.view;

public class ConsoleColors {
    // resets the format / colour to normal text
    public static final String RESET = "\033[0m";

    // Reserved for errors (still red to stand out)
    public static final String RED_BOLD = "\033[1;31m";

    // For current turn
    public static final String ORANGE = "\033[38;5;208m"; // neon orange

    // Players colors (soft pastel palette)
    public static final String PLAYER1 = "\033[38;5;219m"; // pastel pink
    public static final String PLAYER2 = "\033[38;5;159m"; // pastel blue
    public static final String PLAYER3 = "\033[38;5;183m"; // pastel lavender
    public static final String PLAYER4 = "\033[38;5;187m"; // pastel mint
    public static final String PLAYER5 = "\033[38;5;230m"; // cream / soft yellow

    // Board tiers (soft pastels)
    public static final String TIER1 = "\033[38;5;224m"; // soft peach
    public static final String TIER2 = "\033[38;5;189m"; // light lavender
    public static final String TIER3 = "\033[38;5;195m"; // pastel pink
    public static final String NOBLE = "\033[38;5;230m";; // soft butter yellow

    // Highlight current player
    public static final String BOLD = "\033[1m";
}
