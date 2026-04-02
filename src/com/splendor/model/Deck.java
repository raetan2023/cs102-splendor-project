package com.splendor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Generic deck class so that it can be used for development cards or other game elements
public class Deck<T> {
    private List<T> cards; // List to store the cards in deck

    // Default constructor that initializes the deck with an empty ArrayList
    public Deck() {
        this.cards = new ArrayList<>();
    }

    // Initialize and copy list to instance
    public Deck(List<T> cards) {
        this.cards = new ArrayList<>(cards);
    }

    // Shuffle the deck randomly using Collections.shuffle
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // Draws and removes the top card from deck
    public T draw() {
        // Check if deck is empty, if yes, return null
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    // Adds a new card into the BOTTOM of the deck
    public void add(T card) {
        cards.add(card);
    }

    // Check if deck is empty
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // Get number of cards in deck currently
    public int size() {
        return cards.size();
    }
}
