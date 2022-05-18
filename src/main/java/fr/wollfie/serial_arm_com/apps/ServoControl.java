package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.io.ArduinoSerial;
import fr.wollfie.serial_arm_com.sim.ArduinoControlSimulator;
import javafx.scene.canvas.GraphicsContext;
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

    private final Servo Base = Servo.of(SERVO_MG996R_MIN, SERVO_MG996R_MAX, INITIAL_BASE_RAD);
    private final Servo Shoulder = Servo.of(SERVO_BIG_MIN, SERVO_BIG_MAX, INITIAL_SHOULDER_RAD);
    private final Servo Elbow = Servo.of(SERVO_BIG_MIN, SERVO_BIG_MAX, INITIAL_ELBOW_RAD);
    private final Servo Wrist = Servo.of(SERVO_MG996R_MIN, SERVO_MG996R_MAX, INITIAL_WRIST_RAD);
    private final Servo Grip = Servo.of(SERVO_BIG_MIN, SERVO_BIG_MAX, INITIAL_GRIP_RAD);

    private final boolean LogPoses;
    private final TextArea Logger;

    private final ArduinoControlSimulator arduinoControlSimulator;

    public ServoControl(double l1, double l2, double l3, TextArea logger) {
        arduinoSerial = new ArduinoSerial();
        arduinoSerial.showAllPort();
        arduinoSerial.start(ARDUINO_PORT);
        LogPoses = logger != null;
        Logger = logger;
        this.arduinoControlSimulator = new ArduinoControlSimulator(l1, l2, l3);
    }

    public ServoControl(double l1, double l2, double l3) {
        this(l1, l2, l3, null);
    }

    public void writeAnglesRad(double baseAngleRad, double shoulderAngleRad, double elbowAngleRad,
                               double wristAngleRad, double gripAngleRad) {
        Base.writeAngleRad(baseAngleRad);
        Shoulder.writeAngleRad(shoulderAngleRad);
        Elbow.writeAngleRad(elbowAngleRad);
        Wrist.writeAngleRad(wristAngleRad);
        Grip.writeAngleRad(gripAngleRad);
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
            arduinoControlSimulator.write(
                    Base.getDisplayAngleDeg(),
                    Shoulder.getDisplayAngleDeg(),
                    Elbow.getDisplayAngleDeg(),
                    Wrist.getDisplayAngleDeg(),
                    Grip.getDisplayAngleDeg()
            );
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

    public void simDrawOn(GraphicsContext ctx) {
        this.arduinoControlSimulator.drawOn(ctx);
    }
}