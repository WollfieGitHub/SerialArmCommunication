package fr.wollfie.serial_arm_com.sim;

public class ArduinoServo {

    private double currentAngleDeg;
    private double targetAngleDeg;

    private static final double STEP_SIZE = 0.05;

    public ArduinoServo(double initAngleDeg) {
        this.currentAngleDeg = initAngleDeg;
        this.targetAngleDeg = initAngleDeg;
    }

    public void turnTo(double angleDeg) {
        this.targetAngleDeg = angleDeg;
    }

    public double getCurrentAngleRad() {
        return Math.toRadians(currentAngleDeg);
    }

    public void update() {
        this.currentAngleDeg = STEP_SIZE * targetAngleDeg + (1 - STEP_SIZE) * currentAngleDeg;
    }
}
