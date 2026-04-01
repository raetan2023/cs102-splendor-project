cs102-splendor-project/
│
├── compile.sh
├── run.sh
|__ run_ai_tests.sh
|__ run_rules_tests.sh
├── config.properties
├── README.md
|__ .gitignore
│
├── src/
│   └── com/
│       └── splendor/
│           │
│           ├── core/                        
│           │   ├── GameEngine.java
│           │   ├── Board.java
│           │   ├── GemPile.java
│           │   ├── Action.java              (abstract)
│           │   ├── TakeGems.java
│           │   ├── PurchaseCard.java
│           │   └── ReserveCard.java
│           │
│           ├── model/                       
│           │   ├── GemColor.java            (enum)
│           │   ├── DevelopmentCard.java
│           │   ├── Noble.java
│           │   └── Deck.java                (generic)
│           │
│           ├── player/                      
│           │   ├── Player.java
│           │   └── PlayerAssets.java
│           │
│           ├── ai/                          
│           │   ├── Strategy.java            (interface)
│           │   ├── AIPlayer.java
│           │   └── GreedyStrategy.java
|           |   |__ Decision.java
│           │
│           ├── view/                        
│           │   ├── GameView.java
│           │   ├── BoardRenderer.java
│           │   └── PlayerStatusRenderer.java
|           |   |__ ConsoleColors.java
│           │
│           └── config/                      ← PRD only (not in UML yet)
│               ├── ConfigLoader.java
│               ├── CardLoader.java
│               └── NobleLoader.java
|               |__ PropertiesConfigLoader.java
|               |__ GameConfig.java
│
├── data/
│   ├── development_cards.csv
│   └── nobles.csv
│
├── classes/                                 ← empty; populated by compile.sh
├── lib/                                     ← external JARs if needed
├── media/                                   ← empty for console app
│
└── docs/
    ├── PRD.md
    ├── uml_breakdown.md
    ├── git_guide.md
    |__ Splendor Core Logic.md
    └── workflow.txt
