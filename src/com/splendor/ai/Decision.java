package com.splendor.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.splendor.model.DevelopmentCard;
import com.splendor.model.GemColor;

public class Decision {
    public enum Type {
        PURCHASE,
        RESERVE,
        TAKE_GEMS,
        PASS
    }

    private final Type type;
    private final DevelopmentCard card;
    private final List<GemColor> gemColors;

    private Decision(Type type, DevelopmentCard card, List<GemColor> gemColors) {
        this.type = type;
        this.card = card;
        this.gemColors = gemColors == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(gemColors));
    }

    public static Decision purchase(DevelopmentCard card) {
        return new Decision(Type.PURCHASE, card, Collections.emptyList());
    }

    public static Decision reserve(DevelopmentCard card) {
        return new Decision(Type.RESERVE, card, Collections.emptyList());
    }

    public static Decision takeGems(List<GemColor> gemColors) {
        return new Decision(Type.TAKE_GEMS, null, gemColors);
    }

    public static Decision pass() {
        return new Decision(Type.PASS, null, Collections.emptyList());
    }

    public Type getType() {
        return type;
    }

    public DevelopmentCard getCard() {
        return card;
    }

    public List<GemColor> getGemColors() {
        return gemColors;
    }
}
