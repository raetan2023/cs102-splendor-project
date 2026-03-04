# Splendor Card Game — Product Requirements Document

> **CS102 Programming Fundamentals II**

| Field | Details |
|-------|---------|
| **Course** | CS102 Programming Fundamentals II |
| **Project** | Splendor Card Game |
| **Due Date** | 2 April 2026, Thursday, 11:59 PM |
| **Weightage** | 20% of Final Grade |
| **Team** | [Your Team Name – GX-TY] |
| **Version** | 1.0 |
| **Last Updated** | [Date] |

---

## 1. Project Overview

This document defines the product requirements for a console-based implementation of the Splendor board game, developed as part of CS102 Programming Fundamentals II. The application will be built in Java and must faithfully implement the official Splendor rules, support multiple players (human and computer), and demonstrate strong object-oriented design principles.

### 1.1 Objectives

- Implement a fully playable console version of the Splendor card game in Java.
- Demonstrate clean object-oriented design with proper separation of concerns.
- Externalize all configuration parameters for flexibility.
- Deliver an intuitive, usable console-based user experience.
- Gain experience working collaboratively in a software development team.

### 1.2 Success Criteria

- The game compiles and runs without errors via `compile.sh` and `run.sh`.
- All standard Splendor rules are correctly enforced, including win conditions and tie-breakers.
- Configuration values (win threshold, gem counts, card data paths) are loaded from `config.properties`.
- The code follows Java coding conventions and is well-documented.
- The application is intuitive and enjoyable to play.

---

## 2. Game Rules Summary

The game follows the official Splendor rulebook. The following is a summary of the core mechanics relevant to the implementation.

### 2.1 Components

- **Gem Tokens:** Six types – Diamond (white), Sapphire (blue), Emerald (green), Ruby (red), Onyx (black), and Gold (yellow/wild).
- **Development Cards:** Three levels (Level 1, 2, 3) with gem costs, gem bonuses, and prestige points.
- **Noble Tiles:** Worth prestige points; automatically visit a player who meets their bonus requirements.

### 2.2 Player Actions (One Per Turn)

| Action | Description |
|--------|-------------|
| **Take 3 Gems** | Take 3 gem tokens of different colours (if available). A player cannot have more than 10 tokens at the end of their turn. |
| **Take 2 Gems** | Take 2 gem tokens of the same colour, only if there are 4 or more tokens of that colour available. |
| **Reserve a Card** | Reserve 1 development card and take 1 gold (wild) token. A player may hold a maximum of 3 reserved cards. |
| **Purchase a Card** | Buy 1 development card (from the table or reserved) by spending the required gem tokens. Bonuses from owned cards reduce the cost. Gold tokens can substitute any gem. |

### 2.3 Noble Visits

At the end of a turn, if a player meets the bonus requirements of one or more Noble tiles, that Noble automatically visits the player and grants prestige points. If multiple Nobles qualify, the player chooses one.

### 2.4 Winning Condition

The game ends at the conclusion of the round in which any player reaches the prestige point threshold (default: 15). The player with the most prestige points wins. In the event of a tie, the player with fewer purchased development cards wins.

---

## 3. Functional Requirements

### 3.1 Core Game Engine

| ID | Requirement | Priority | Status |
|----|-------------|----------|--------|
| FR-01 | Implement gem token management (take 3 different, take 2 same, return excess over 10). | Must Have | To Do |
| FR-02 | Implement development card purchasing with gem cost calculation including bonuses and gold substitution. | Must Have | To Do |
| FR-03 | Implement card reservation (max 3 per player) with gold token acquisition. | Must Have | To Do |
| FR-04 | Implement Noble tile visits: auto-check eligibility at end of turn, allow player choice if multiple qualify. | Must Have | To Do |
| FR-05 | Implement win detection at the end of each round (not mid-round) when prestige threshold is reached. | Must Have | To Do |
| FR-06 | Implement tie-breaker logic: fewest development cards wins. | Must Have | To Do |
| FR-07 | Prevent all illegal moves (e.g., taking unavailable gems, exceeding token limit, purchasing without sufficient gems). | Must Have | To Do |
| FR-08 | Support 2–4 players with correct gem token setup per player count (per Splendor rules). | Must Have | To Do |

### 3.2 Player Modes

| ID | Requirement | Priority | Status |
|----|-------------|----------|--------|
| FR-09 | Support Human vs Human gameplay. | Must Have | To Do |
| FR-10 | Support Human vs Computer (AI) gameplay. | Must Have | To Do |
| FR-11 | AI opponent makes valid moves with reasonable strategy (not purely random). | Should Have | To Do |

### 3.3 Configuration & Data

| ID | Requirement | Priority | Status |
|----|-------------|----------|--------|
| FR-12 | Load winning prestige point threshold from `config.properties`. | Must Have | To Do |
| FR-13 | Load initial gem token counts (per player count) from `config.properties`. | Must Have | To Do |
| FR-14 | Load development card data from external data files; file paths specified in `config.properties`. | Must Have | To Do |
| FR-15 | Load Noble tile data from external data files. | Must Have | To Do |

### 3.4 Console UI

| ID | Requirement | Priority | Status |
|----|-------------|----------|--------|
| FR-16 | Display the game board clearly: available gems, face-up cards per level, Noble tiles. | Must Have | To Do |
| FR-17 | Display each player's status: owned gems, bonuses, prestige points, reserved cards. | Must Have | To Do |
| FR-18 | Provide clear prompts for player actions and display validation errors for illegal moves. | Must Have | To Do |
| FR-19 | Announce game results: winner, final scores, and tie-breaker outcome if applicable. | Must Have | To Do |

---

## 4. Non-Functional Requirements

### 4.1 Code Quality

- Modularize the code with multiple well-defined classes and methods following the Single Responsibility Principle.
- Adhere to Java coding conventions (Oracle standard): meaningful names, consistent indentation, proper Javadoc.
- Organize classes logically using Java packages (e.g., `model`, `view`, `controller`, `util`, `config`).
- Separate game logic from UI/display code to maintain a clean architecture.
- Avoid god classes – no single class should handle game logic, display, and configuration.

### 4.2 Build & Deployment

- The project must compile via `compile.sh` and run via `run.sh` without manual configuration.
- Alternatively, a Maven project structure (`src/main/java`) is acceptable if it compiles and runs correctly.
- All external libraries must be free/open-source and placed in the `lib/` directory (or managed via Maven).
- Class files must be output to the `classes/` directory (left empty at submission).

### 4.3 Usability (Nielsen's Heuristics)

- **Visibility of system status:** always show the current game state and whose turn it is.
- **Error prevention:** validate inputs and prevent illegal moves before execution.
- **User control and freedom:** allow players to cancel or undo input before confirming an action.
- **Help and documentation:** provide a help command or instructions accessible during gameplay.
- **Consistency:** use consistent formatting, terminology, and interaction patterns throughout.

---

## 5. Bonus Features (Optional)

Each bonus feature is worth up to 1% of the final grade (max 1% total).

| ID | Feature | Complexity | Status |
|----|---------|------------|--------|
| BN-01 | Client-server implementation for 2 players over a local network, with clean separation between game logic and network/UI layer. | High | To Do |
| BN-02 | Additional useful game features beyond minimum requirements (e.g., undo, save/load, replay, difficulty levels for AI). | Medium | To Do |

---

## 6. Technical Architecture

### 6.1 Suggested Package Structure

The following package organization is recommended to maintain separation of concerns:

| Package               | Responsibility                                                                         |
| --------------------- | -------------------------------------------------------------------------------------- |
| `com.splendor.model`  | Domain entities: `Card`, `Noble`, `Gem` (enum), `Player`, `Board`, `Deck`, `TokenBank` |
| `com.splendor.engine` | Game flow and rules: `GameEngine`, `ActionValidator`, `WinChecker`, `InputParser`      |
| `com.splendor.ai`     | AI strategy: `AIPlayer`, `Strategy` interface, `GreedyStrategy`                        |
| `com.splendor.view`   | Console display: `GameView`, `BoardRenderer`, `PlayerStatusRenderer`                   |
| `com.splendor.config` | All file loading: `ConfigLoader`, `CardLoader`, `NobleLoader`                          |

### 6.2 Key Design Principles

- **Single Responsibility Principle:** each class has one clear purpose.
- **Externalized configuration:** all tuneable parameters in `config.properties`, card/noble data in separate data files.
- **Model–View separation:** game logic must not depend on console I/O classes.
- **Strategy pattern for AI:** define a `Strategy` interface so different AI behaviours can be swapped easily.
- **No magic numbers:** use named constants or configuration values throughout.

### 6.3 Configuration File (`config.properties`)

Example structure:

```properties
# Game settings
win.prestige.points=15

# Gem counts per player count
gems.2players=4
gems.3players=5
gems.4players=7
gems.gold=5

# Data file paths
data.cards.level1=data/cards_level1.csv
data.cards.level2=data/cards_level2.csv
data.cards.level3=data/cards_level3.csv
data.nobles=data/nobles.csv
```

---

## 7. Deliverables & Submission

### 7.1 Submission Contents

- `compile.sh` — compiles all Java source files into the `classes/` directory.
- `run.sh` — runs the application.
- `src/` — all Java source files.
- `classes/` — left empty (populated after `compile.sh` runs).
- `media/` — image and audio files (if any; empty for console app).
- `lib/` — external JAR dependencies (if not using Maven).
- `config.properties` — externalized game configuration.
- `data/` — card and noble data files (CSV or similar).
- `GX-TY.pptx` (or `.pdf`) — presentation slides including AI Use appendix.

### 7.2 Submission Details

- Submit via eLearn > Assignments as a ZIP file. Email submissions will be ignored.
- Ensure the project compiles and runs on a machine with `javac` and `java` on the PATH.
- Maven may be used as an alternative to `compile.sh`/`run.sh` with standard directory structure.

---

## 8. Presentation Requirements

**Time:** 10 minutes presentation + demo, 10 minutes Q&A.

### 8.1 Required Content

- Object-oriented design overview with UML class diagrams.
- Open-source libraries used and rationale for selection.
- Design considerations and architectural decisions.
- If client-server: describe how packages maintain separation between game logic and network/UI.
- Live demonstration of all game features.
- AI Use appendix: list of AI tools and prompts used for major decisions.

---

## 9. Grading Breakdown

| Component | Marks | Weight |
|-----------|-------|--------|
| Code Quality (modularization, design, convention, config) | 10 | 50% |
| Application (usability, fun, correctness) | 5 | 25% |
| Presentation (content, deck, pacing, demo) | 5 | 25% |
| **Total** | **20** | **100%** |

> **Penalty:** Up to 50% deduction if any Java source file does not compile or run.
>
> **Bonus:** Up to 1% of final grade for additional features (e.g., client-server, extra game features).

---

## 10. Suggested Timeline

| Week | Milestone | Owner |
|------|-----------|-------|
| Week 1 | Finalize PRD, set up project structure, define `config.properties`, create data files. | All |
| Week 2 | Implement model classes (`Card`, `Gem`, `Noble`, `Player`, `Board`) and data loaders. | [Assign] |
| Week 3 | Implement game engine: turn logic, action validation, win detection, tie-breaker. | [Assign] |
| Week 4 | Implement console UI (view layer) and AI player. | [Assign] |
| Week 5 | Integration testing, bug fixes, usability improvements, bonus features. | All |
| Week 6 | Prepare presentation, rehearse demo, final submission. | All |

---

## 11. Assumptions & Open Questions

### 11.1 Assumptions

1. The game will be played via a single console unless the bonus client-server feature is implemented.
2. Card and Noble data will be provided in CSV format with clearly defined columns.
3. AI difficulty does not need to be configurable unless pursued as a bonus feature.
4. The gold (wild) token count is fixed at 5 regardless of player count, per standard Splendor rules.

### 11.2 Open Questions

> *Add any unresolved questions here as the team identifies them during development. Examples:*

1. Should the game support saving and loading a game session?
2. Should we implement a tutorial or guided first game for new players?
3. What AI strategy should be the default: greedy, balanced, or aggressive?
