package fr.wollfie.serial_arm_com.apps;

public class Servo {

    private static final double RAD_MIN = 0;
    private static final double RAD_MAX = Math.PI;
    private static final double RAD_RANGE = RAD_MAX - RAD_MIN;

    private final int MinPulse;
    private final int MaxPulse;
    private final int PulseRange;

    private int currentPulse;
    private double currentAngle;

    private Servo(int minPulse, int maxPulse) {
        MinPulse = minPulse;
        MaxPulse = maxPulse;
        PulseRange = MaxPulse - MinPulse;
        currentPulse = 0;
    }

    public static Servo of(int minPulse, int maxPulse) {
        return new Servo(minPulse, maxPulse);
    }

    public void writeAngleRad(double angleRad) {
        double clampedAngle = modRange(angleRad);
        double factor = clampedAngle/RAD_RANGE;
        this.currentAngle = clampedAngle;
        this.currentPulse = (int)(MinPulse + factor * PulseRange);
    }

    public int getCurrentPulse() {
        return currentPulse;
    }

    public double getCurrentAngleDeg() {
        return Math.toDegrees(this.currentAngle);
    }


    private static double modRange(double val) {
        return (((val % Servo.RAD_RANGE) + Servo.RAD_RANGE) % Servo.RAD_RANGE);
    }
}
