package fr.wollfie.serial_arm_com.sim;

public class VirtualServoControl {

    static final int SERVO_MG996R_MIN = 0;
    static final int SERVO_MG996R_MAX = 300;

    static final int SERVO_BIG_MAX = 440;
    static final int SERVO_BIG_MIN = 0;

    static final double INITIAL_BASE_RAD = Math.toRadians(0);
    static final double INITIAL_SHOULDER_RAD = Math.toRadians(-1);
    static final double INITIAL_ELBOW_RAD = Math.toRadians(30);
    static final double INITIAL_WRIST_RAD = Math.toRadians(85);
    static final double INITIAL_GRIP_RAD = Math.toRadians(0);

    private final VirtualServo baseServo = new VirtualServo(SERVO_MG996R_MIN, SERVO_MG996R_MAX, INITIAL_BASE_RAD, false);
    private final VirtualServo shoulderServo = new VirtualServo(SERVO_BIG_MIN, SERVO_BIG_MAX, INITIAL_SHOULDER_RAD, true);
    private final VirtualServo elbowServo = new VirtualServo(SERVO_BIG_MIN, SERVO_BIG_MAX, INITIAL_ELBOW_RAD, true);
    private final VirtualServo wristServo = new VirtualServo(160, 410, INITIAL_WRIST_RAD, true);
    private final VirtualServo gripServo = new VirtualServo(SERVO_BIG_MIN, SERVO_BIG_MAX, INITIAL_GRIP_RAD, false);


    public void writeAnglesRad(double baseAngleRad, double shoulderAngleRad, double elbowAngleRad,
                               double wristAngleRad, double gripAngleRad) {
        baseServo.writeAngleRad(baseAngleRad);
        shoulderServo.writeAngleRad(shoulderAngleRad);
        elbowServo.writeAngleRad(elbowAngleRad);
        wristServo.writeAngleRad(wristAngleRad);
        gripServo.writeAngleRad(gripAngleRad);
    }

    public String getArduinoReadyAngles() {
        return "{" +
                baseServo.getCurrentPulse() + ", " +
                shoulderServo.getCurrentPulse() + ", " +
                elbowServo.getCurrentPulse() + ", " +
                wristServo.getCurrentPulse() + ", " +
                gripServo.getCurrentPulse() +
                "}";
    }
}
