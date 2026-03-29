package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.DevelopmentCard;
import model.GemColor;

public class Decision {

    private DecisionType type;
    private DevelopmentCard targetCard;
    private List<GemColor> gemColors;

    private Decision(DecisionType type, DevelopmentCard targetCard, List<GemColor> gemColors) {
        this.type = type;
        this.targetCard = targetCard;
        this.gemColors = new ArrayList<>(gemColors);
    }

    public static Decision purchase(DevelopmentCard card) {
        return new Decision(DecisionType.PURCHASE, card, Collections.<GemColor>emptyList());
    }

    public static Decision reserve(DevelopmentCard card) {
        return new Decision(DecisionType.RESERVE, card, Collections.<GemColor>emptyList());
    }

    public static Decision takeGems(List<GemColor> colors) {
        return new Decision(DecisionType.TAKE_GEMS, null, colors);
    }

    public static Decision pass() {
        return new Decision(DecisionType.PASS, null, Collections.<GemColor>emptyList());
    }

    public DecisionType getType() {
        return type;
    }

    public DevelopmentCard getTargetCard() {
        return targetCard;
    }

    public List<GemColor> getGemColors() {
        return Collections.unmodifiableList(gemColors);
    }
}
