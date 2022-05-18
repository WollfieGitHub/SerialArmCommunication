package fr.wollfie.serial_arm_com.sim;

public class ArduinoServo {

    private double currentAngleDeg;
    private double targetAngleDeg;

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
        this.currentAngleDeg = ArduinoControlSimulator.STEP_SIZE * targetAngleDeg + (1 - ArduinoControlSimulator.STEP_SIZE) * currentAngleDeg;
    }
}
