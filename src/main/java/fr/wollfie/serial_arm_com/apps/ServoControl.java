package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.io.ArduinoSerial;

public class ServoControl {
    static final int SERVO_MG996R_MIN = 0;
    static final int SERVO_MG996R_MAX = 300;
    static final int SERVO_MG996R_MID = (SERVO_MG996R_MAX - SERVO_MG996R_MIN) / 2;
    static final int SHOULDER_TRIM_MAX = 440;
    static final int SHOULDER_TRIM_MIN = 0;
    static final String ARDUINO_PORT = "COM3";
    ArduinoSerial arduinoSerial;
    int base = SERVO_MG996R_MIN;
    int grip = SERVO_MG996R_MID;
    int wrist = SERVO_MG996R_MID;
    int shoulder = 90;
    int elbow = 90;

    public ServoControl() {
        arduinoSerial = new ArduinoSerial();
        arduinoSerial.showAllPort();
        arduinoSerial.start(ARDUINO_PORT);
    }

    void sendData() {
        String msg = String.format("B%04dG%04dW%04dE%04dS%04d%n", base, grip, wrist, elbow, shoulder);
        arduinoSerial.write(msg);
        System.out.println(msg);
    }
}