package fr.wollfie.serial_arm_com;

import fr.wollfie.serial_arm_com.io.ArduinoSerial;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    private static final int SERVO_TRIM_MIN = 110;
    private static final int SERVO_TRIM_MAX = 590;

    private static final int SERVO_MG996R_MIN = 0;
    private static final int SERVO_MG996R_MAX = 300;
    private static final int SERVO_MG996R_MID = (SERVO_MG996R_MAX-SERVO_MG996R_MIN)/2;

    private static final int SHOULDER_TRIM_MAX = 440;
    private static final int SHOULDER_TRIM_MIN = 0;

    private static final String ARDUINO_PORT = "COM3";

    private ArduinoSerial arduinoSerial;

    @Override
    public void start(Stage stage) {

        VBox root = new VBox();
        var scene = new Scene(root, 640, 480);

        init(root);

        arduinoSerial = new ArduinoSerial();
        arduinoSerial.showAllPort();
        arduinoSerial.start(ARDUINO_PORT);

        stage.setScene(scene);
        stage.show();

    }

    private int base = SERVO_MG996R_MIN;
    private int grip = SERVO_MG996R_MID;
    private int wrist = SERVO_MG996R_MID;
    private int shoulder = 90;
    private int elbow = 90;

    private int motor1 = 0;
    private int motor2 = 0;

    private Slider sliderBase = new Slider();
    private Slider sliderShoulder = new Slider();
    private Slider sliderElbow = new Slider();
    private Slider sliderWrist = new Slider();
    private Slider sliderGrip = new Slider();

    private static final float MOVING_SCALE = 0.05f;

    private void init(VBox root) {
        root.setAlignment(Pos.CENTER);

        Slider motor1Slider = new Slider();
        Slider motor2Slider = new Slider();

        InvalidationListener dataUpdater = o -> {
            base = (int) (sliderBase.getValue());
            grip = (int) (sliderGrip.getValue());
            wrist = (int) (sliderWrist.getValue());
            shoulder = (int)( sliderShoulder.getValue());
            elbow = (int)( sliderElbow.getValue());
            motor1 = (int)(motor1Slider.getValue());
            motor2 = (int)(motor2Slider.getValue());
            sendData();
        };

        Text baseText = new Text("Base");
        sliderBase.setMax(SERVO_MG996R_MAX);
        sliderBase.setMin(SERVO_MG996R_MIN);
        sliderBase.setValue(SERVO_MG996R_MIN);
        sliderBase.valueProperty().addListener(dataUpdater);
        sliderBase.setOrientation(Orientation.HORIZONTAL);

        Text gripText = new Text("Grip");
        sliderGrip.setMax(SHOULDER_TRIM_MAX);
        sliderGrip.setMin(SHOULDER_TRIM_MIN);
        sliderGrip.setValue((SHOULDER_TRIM_MAX + SHOULDER_TRIM_MIN) / 2.0f);
        sliderGrip.valueProperty().addListener(dataUpdater);
        sliderGrip.setOrientation(Orientation.HORIZONTAL);

        Text wristText = new Text("Wrist");
        sliderWrist.setMax(SERVO_MG996R_MAX);
        sliderWrist.setMin(SERVO_MG996R_MIN);
        sliderWrist.setValue((SERVO_MG996R_MAX + SERVO_MG996R_MIN) / 2.0f);
        sliderWrist.valueProperty().addListener(dataUpdater);
        sliderWrist.setOrientation(Orientation.HORIZONTAL);

        Text elbowText = new Text("Elbow");
        sliderElbow.setMax(SHOULDER_TRIM_MAX);
        sliderElbow.setMin(SHOULDER_TRIM_MIN);
        sliderElbow.setValue((SHOULDER_TRIM_MAX + SHOULDER_TRIM_MIN) / 2.0f);
        sliderElbow.valueProperty().addListener(dataUpdater);
        sliderElbow.setOrientation(Orientation.HORIZONTAL);

        Text shoulderText = new Text("Shoulder");
        sliderShoulder.setMax(SHOULDER_TRIM_MAX);
        sliderShoulder.setMin(SHOULDER_TRIM_MIN);
        sliderShoulder.setValue((SHOULDER_TRIM_MAX + SHOULDER_TRIM_MIN) / 2.0f);
        sliderShoulder.valueProperty().addListener(dataUpdater);
        sliderShoulder.setOrientation(Orientation.HORIZONTAL);

        HBox motorHBox = new HBox();

        motor1Slider.setMax(255);
        motor1Slider.setMin(5);
        motor1Slider.setValue(0);
        motor1Slider.valueProperty().addListener(dataUpdater);
        motor1Slider.setOrientation(Orientation.VERTICAL);

        motor2Slider.setMax(255);
        motor2Slider.setMin(0);
        motor2Slider.setValue(0);
        motor2Slider.valueProperty().addListener(dataUpdater);
        motor2Slider.setOrientation(Orientation.VERTICAL);

        motorHBox.getChildren().addAll(motor1Slider, motor2Slider);

        HBox moveStraightHbox = new HBox();
        Button moveStraightPlus = new Button("+");
        Button moveStraightMinus = new Button("-");


        moveStraightPlus.setOnAction(o -> {
            double propMove = (SHOULDER_TRIM_MAX - SHOULDER_TRIM_MIN) * MOVING_SCALE;
            elbow += propMove;
            shoulder += propMove;
            updateSliders();
            sendData();
        });

        moveStraightMinus.setOnAction(o -> {
            double propMove = (SHOULDER_TRIM_MAX - SHOULDER_TRIM_MIN) * MOVING_SCALE;
            elbow -= propMove;
            shoulder -= propMove;
            updateSliders();
            sendData();
        });

        moveStraightHbox.getChildren().addAll(moveStraightMinus, moveStraightPlus);

        root.getChildren().addAll(baseText, sliderBase, gripText, sliderGrip,
                wristText, sliderWrist,
                elbowText, sliderElbow, shoulderText,
                sliderShoulder, motorHBox,
                moveStraightHbox);
    }

    private void updateSliders() {
        sliderBase.setValue(base);
        sliderShoulder.setValue(shoulder);
        sliderElbow.setValue(elbow);
        sliderWrist.setValue(wrist);
        sliderGrip.setValue(grip);
    }

    private void sendData() {
        String msg = String.format("B%04dG%04dW%04dE%04dS%04d%n", base, grip, wrist, elbow, shoulder);
        arduinoSerial.write(msg);
        System.out.println(msg);
    }

    public static void main(String[] args) {
        launch();
    }

}