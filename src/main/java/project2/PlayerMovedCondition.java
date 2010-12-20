package project2;

import project2.model.level.Box;
import project2.triggers.Condition;

public class PlayerMovedCondition implements Condition {
    private final Box player;

    public PlayerMovedCondition(Box player) {
        super();
        this.player = player;
    }

    @Override
    public boolean isTrue() {
        return player.isChanged();
    }

}
