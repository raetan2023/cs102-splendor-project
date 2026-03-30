package com.splendor.view;

public class ConsoleColors {
    public static final String RESET = "\033[0m";

    // Reserved for errors
    public static final String RED_BOLD = "\033[1;31m";

    // Player-safe colors (visible on dark + light)
    public static final String GREEN = "\033[0;32m";
    public static final String BLUE = "\033[0;34m";
    public static final String MAGENTA = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String GRAY = "\033[0;90m"; // bright black

    // Highlight current player
    public static final String BOLD = "\033[1m";
}