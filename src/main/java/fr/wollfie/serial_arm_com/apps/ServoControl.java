package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.io.ArduinoSerial;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.Nullable;

public class ServoControl {
    static final int SERVO_MG996R_MIN = 0;
    static final int SERVO_MG996R_MAX = 300;

    static final int SERVO_BIG_MAX = 440;
    static final int SERVO_BIG_MIN = 0;

    static final double INITIAL_BASE_RAD = Math.toRadians(0);
    static final double INITIAL_SHOULDER_RAD = Math.toRadians(0);
    static final double INITIAL_ELBOW_RAD = Math.toRadians(30);
    static final double INITIAL_WRIST_RAD = Math.toRadians(90);
    static final double INITIAL_GRIP_RAD = Math.toRadians(0);

    static final String ARDUINO_PORT = "COM3";
    ArduinoSerial arduinoSerial;

    private final Servo Base = Servo.of(SERVO_MG996R_MIN, SERVO_MG996R_MAX);
    private final Servo Shoulder = Servo.of(SERVO_BIG_MIN, SERVO_BIG_MAX);
    private final Servo Elbow = Servo.of(SERVO_BIG_MIN, SERVO_BIG_MAX);
    private final Servo Wrist = Servo.of(SERVO_MG996R_MIN, SERVO_MG996R_MAX);
    private final Servo Grip = Servo.of(SERVO_BIG_MIN, SERVO_BIG_MAX);

    private final boolean LogPoses;
    private final TextArea Logger;

    public ServoControl(TextArea logger) {
        arduinoSerial = new ArduinoSerial();
        arduinoSerial.showAllPort();
        arduinoSerial.start(ARDUINO_PORT);
        LogPoses = logger != null;
        Logger = logger;
    }

    public ServoControl() {
        this(null);
    }

    public void writeAnglesRad(double baseAngleRad, double shoulderAngleRad, double elbowAngleRad,
                               double wristAngleRad, double gripAngleRad) {
        Base.writeAngleRad(baseAngleRad-INITIAL_BASE_RAD);
        Shoulder.writeAngleRad(shoulderAngleRad-INITIAL_SHOULDER_RAD);
        Elbow.writeAngleRad(elbowAngleRad-INITIAL_ELBOW_RAD);
        Wrist.writeAngleRad(wristAngleRad-INITIAL_WRIST_RAD);
        Grip.writeAngleRad(gripAngleRad-INITIAL_GRIP_RAD);
        sendData();
    }

    public void sendData() {
        String msg = String.format(
                "B%04dG%04dW%04dE%04dS%04d%n",
                Base.getCurrentPulse(),
                Grip.getCurrentPulse(),
                Wrist.getCurrentPulse(),
                Elbow.getCurrentPulse(),
                Shoulder.getCurrentPulse());

        arduinoSerial.write(msg);

        if (LogPoses) {
            Logger.setText(logAngles() + "\n\nCommand : \n" + msg);
        }
    }

    private String logAngles() {
        return String.format("" +
                "Angles : " +
                "\n\t- Base : %3.2f" +
                "\n\t- Shoulder : %3.2f" +
                "\n\t- Elbow : %3.2f" +
                "\n\t- Wrist : %3.2f" +
                "\n\t- Grip : %3.2f",
                Base.getCurrentAngleDeg(), Shoulder.getCurrentAngleDeg(), Elbow.getCurrentAngleDeg(),
                Wrist.getCurrentAngleDeg(), Grip.getCurrentAngleDeg()
        );
    }
}