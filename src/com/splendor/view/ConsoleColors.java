package com.splendor.view;

public class ConsoleColors {
    public static final String RESET = "\033[0m";

    // Reserved for errors
    public static final String RED_BOLD = "\033[1;31m";

    // Players colours
    public static final String GREEN = "\033[38;5;46m";  // neon green
    public static final String CYAN = "\033[38;5;51m";  // cyan-blue
    public static final String MAGENTA = "\033[38;5;201m"; // hot pink
    public static final String BLUE = "\033[38;5;51m";  // neon cyan
    public static final String ORANGE = "\033[38;5;208m"; // neon orange

    //Board
    public static final String TIER1 = "\033[38;5;214m"; // bright orange
    public static final String TIER2 = "\033[38;5;45m";  // neon green-cyan
    public static final String TIER3 = "\033[38;5;201m"; // neon pink
    public static final String NOBLE = "\033[38;5;226m"; // bright yellow

    // Highlight current player
    public static final String BOLD = "\033[1m";
}