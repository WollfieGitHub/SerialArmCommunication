package fr.wollfie.serial_arm_com.movement_sequence;

import fr.wollfie.serial_arm_com.maths.RobotArmController;
import fr.wollfie.serial_arm_com.sim.VirtualServoControl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MovementSequencer {

    private final RobotArmController robotController;

    public BooleanProperty isPlaying = new SimpleBooleanProperty(false);

    private MovementAnimation animation;
    private int currentFrame;
    private long millisFrameStarted;

    public MovementSequencer(RobotArmController robotController) {
        this.robotController = robotController;
    }

    public ReadOnlyBooleanProperty playingProperty() {
        return this.isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    public void play(MovementAnimation animation, VirtualServoControl virtualServoControl) {
        this.animation = InterpolatedAnimation.of(animation, virtualServoControl, this.robotController);
        this.isPlaying.setValue(true);
        this.currentFrame = 0;
        this.millisFrameStarted = System.currentTimeMillis();

        this.updateFrame();
    }

    public void update() {
        if (!isPlaying.getValue()) return;
        if (System.currentTimeMillis() - millisFrameStarted < 1000f / (animation.getFrameRate())) return;

        this.currentFrame += 1;
        this.millisFrameStarted = System.currentTimeMillis();

        if (currentFrame > this.animation.getFrameCount() -1) {
            this.isPlaying.setValue(false);
            this.animation = null;
        } else {
            updateFrame();
        }
    }

    private void updateFrame() {
        this.robotController.update(this.animation.getFrame(this.currentFrame), true);
        this.robotController.update3DRepresentations();
    }

    public void stop() {
        this.animation = null;
        this.isPlaying.setValue(false);
    }
}
