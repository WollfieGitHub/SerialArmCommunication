package fr.wollfie.serial_arm_com.graphics;

import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import javafx.animation.AnimationTimer;

public class Renderer extends AnimationTimer {

    private final InverseKinApp inverseKinApp;

    public Renderer(InverseKinApp inverseKinApp) {
        this.inverseKinApp = inverseKinApp;
    }

    @Override
    public void handle(long now) {
        inverseKinApp.updateArm();
    }
}
