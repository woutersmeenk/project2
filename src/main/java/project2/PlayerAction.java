package project2;

import com.jme.input.KeyInput;


public enum PlayerAction {
    ACTION(new KeyTrigger(KeyInput.KEY_RETURN)), REVERT(new KeyTrigger(
            KeyInput.KEY_LSHIFT)), LEFT(new KeyTrigger(KeyInput.KEY_LEFT)), RIGHT(
            new KeyTrigger(KeyInput.KEY_RIGHT)), UP(new KeyTrigger(
            KeyInput.KEY_UP)), DOWN(new KeyTrigger(KeyInput.KEY_DOWN));

    public final Trigger trigger;

    private PlayerAction(final Trigger trigger) {
        this.trigger = trigger;
    }
}
