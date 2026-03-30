package com.splendor.view;

// public class ConsoleColors {
//     public static final String RESET = "\033[0m";

//     // Reserved for errors
//     public static final String RED_BOLD = "\033[1;31m";

//     // Players colours
//     public static final String GREEN = "\033[38;5;46m";  // neon green
//     public static final String CYAN = "\033[38;5;51m";  // cyan-blue
//     public static final String MAGENTA = "\033[38;5;201m"; // hot pink
//     public static final String BLUE = "\033[38;5;51m";  // neon cyan
//     public static final String ORANGE = "\033[38;5;208m"; // neon orange

//     //Board
//     public static final String TIER1 = "\033[38;5;214m"; // bright orange
//     public static final String TIER2 = "\033[38;5;45m";  // neon green-cyan
//     public static final String TIER3 = "\033[38;5;201m"; // neon pink
//     public static final String NOBLE = "\033[38;5;226m"; // bright yellow

//     // Highlight current player
//     public static final String BOLD = "\033[1m";
// }

public class ConsoleColors {
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