package com.splendor.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;

/**
 * Shows a single action an AI player can take on their turn.
 * Think of this like a "move slip" which means it records what the player decided to do
 * and any relevant details (which card, which gems, etc).
 */
public class Decision {

    // The four things a player can do on their turn
    public enum Type {
        PURCHASE,   // Buy a development card using gems
        RESERVE,    // Hold a card for later (locks it so others cannot take it)
        TAKE_GEMS,  // Pick up gem tokens from the board
        PASS        // Do nothing this turn
    }

    private final Type type;           // What kind of action this is
    private final DevelopmentCard card; // The card involved (only for PURCHASE / RESERVE)
    private final List<GemColor> gemColors; // The gems involved (only for TAKE_GEMS)

    // Private so nobody can create a Decision directly — use the helper methods below instead
    private Decision(Type type, DevelopmentCard card, List<GemColor> gemColors) {
        this.type = type;
        this.card = card;

        // Defensive copy so nobody can mess with the gem list from outside this class
        this.gemColors = gemColors == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(gemColors));
    }


    /** Player is buying this card right now. No gems needed in the decision itself
     *  - the engine handles deducting them separately. */
    public static Decision purchase(DevelopmentCard card) {
        return new Decision(Type.PURCHASE, card, Collections.emptyList());
    }

    /** Player is setting this card aside for later use. They get a gold (wild) gem too,
     *  but again the engine handles that part. */
    public static Decision reserve(DevelopmentCard card) {
        return new Decision(Type.RESERVE, card, Collections.emptyList());
    }

    /** Player is picking up gems. The list says which colors (up to 3 different,
     *  or 2 of the same if taking a double). */
    public static Decision takeGems(List<GemColor> gemColors) {
        return new Decision(Type.TAKE_GEMS, null, gemColors);
    }

    /** Player has nothing useful to do this turn, so they skip. */
    public static Decision pass() {
        return new Decision(Type.PASS, null, Collections.emptyList());
    }

    //  Getters only have read-only access to what's inside the decision 

    public Type getType() {
        return type;
    }

    public DevelopmentCard getCard() {
        return card; // Will be null if this isn't a PURCHASE or RESERVE decision
    }

    public List<GemColor> getGemColors() {
        return gemColors; // Will be empty if this isn't a TAKE_GEMS decision
    }
}
