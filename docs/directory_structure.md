cs102-splendor-project/
│
├── compile.sh
├── run.sh
├── config.properties
├── README.md
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
│           │
│           ├── view/                        
│           │   ├── GameView.java
│           │   ├── BoardRenderer.java
│           │   └── PlayerStatusRenderer.java
│           │
│           └── config/                      ← PRD only (not in UML yet)
│               ├── ConfigLoader.java
│               ├── CardLoader.java
│               └── NobleLoader.java
│
├── data/
│   ├── cards_level1.csv
│   ├── cards_level2.csv
│   ├── cards_level3.csv
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
    └── workflow.txt
