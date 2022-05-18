package fr.wollfie.serial_arm_com.apps;

public class Servo {

    private static final double RAD_MIN = 0;
    private static final double RAD_MAX = Math.PI;
    private static final double RAD_RANGE = RAD_MAX - RAD_MIN;

    private final int MinPulse;
    private final int MaxPulse;
    private final int PulseRange;
    private final double MountedAngleRad;

    private int currentPulse;
    private double currentAngle;
    private double displayAngleRad;

    private Servo(int minPulse, int maxPulse, double mountedAngleRad) {
        MinPulse = minPulse;
        MaxPulse = maxPulse;
        PulseRange = MaxPulse - MinPulse;
        currentPulse = 0;
        MountedAngleRad = mountedAngleRad;
    }

    public static Servo of(int minPulse, int maxPulse, double mountedAngleRad) {
        return new Servo(minPulse, maxPulse, mountedAngleRad);
    }

    public void writeAngleRad(double angleRad) {
        this.displayAngleRad = angleRad;
        double clampedAngle = modRange(angleRad-MountedAngleRad);
        double factor = clampedAngle/RAD_RANGE;
        this.currentAngle = clampedAngle;
        this.currentPulse = (int)(MinPulse + factor * PulseRange);
    }

    public double getDisplayAngleDeg() {
        return Math.toDegrees(displayAngleRad);
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
