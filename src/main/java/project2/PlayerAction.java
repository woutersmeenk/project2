package project2;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;

public enum PlayerAction {
    ACTION(new KeyTrigger(KeyInput.KEY_RETURN)), REVERT(new KeyTrigger(
            KeyInput.KEY_LSHIFT)), LEFT(new KeyTrigger(KeyInput.KEY_LEFT)), RIGHT(
            new KeyTrigger(KeyInput.KEY_RIGHT)), UP(new KeyTrigger(
            KeyInput.KEY_UP)), DOWN(new KeyTrigger(KeyInput.KEY_DOWN)), RESET(
            new KeyTrigger(KeyInput.KEY_R));

    public final Trigger trigger;

    private PlayerAction(final Trigger trigger) {
        this.trigger = trigger;
    }
}
