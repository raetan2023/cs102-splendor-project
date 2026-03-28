package com.splendor.core;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.Deck;
import com.splendor.model.Noble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private GemPile[] gemBank;
    private int goldSupply;
    private Map<Integer, Deck<DevelopmentCard>> allCards;
    private List<Noble> allNobles;
    private List<Noble> visibleNobles;
    private Map<Integer, List<DevelopmentCard>> visibleCards;

    public Board() {
        gemBank = new GemPile[5];
        // Initialize 5 gem piles (White, Blue, Green, Red, Black) to 4 tokens each
        for (int i = 0; i < 5; i++) {
            gemBank[i] = new GemPile(4);
        }
        goldSupply = 5;
        
        allCards = new HashMap<>();
        allNobles = new ArrayList<>();
        visibleNobles = new ArrayList<>();
        visibleCards = new HashMap<>();
        visibleCards.put(1, new ArrayList<>());
        visibleCards.put(2, new ArrayList<>());
        visibleCards.put(3, new ArrayList<>());
    }

    public void revealCard(int tier) {
        Deck<DevelopmentCard> deck = allCards.get(tier);
        if (deck != null) {
            DevelopmentCard card = deck.draw();
            if (card != null) {
                visibleCards.get(tier).add(card);
            }
        }
    }

    public GemPile[] getGemBank() {
        return gemBank;
    }

    public int getGoldSupply() {
        return goldSupply;
    }

    public void takeGold(int amount) {
        if (goldSupply >= amount) {
            goldSupply -= amount;
        }
    }

    public void returnGold(int amount) {
        goldSupply += amount;
    }

    public List<Noble> getAllNobles() {
        return allNobles;
    }

    public List<Noble> getVisibleNobles() {
        return visibleNobles;
    }

    public Map<Integer, List<DevelopmentCard>> getVisibleCards() {
        return visibleCards;
    }

    public Map<Integer, Deck<DevelopmentCard>> getAllCards() {
        return allCards;
    }
}
