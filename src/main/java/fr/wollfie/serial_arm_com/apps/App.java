package fr.wollfie.serial_arm_com.apps;

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

    private final ServoControl servoControl = new ServoControl();

    @Override
    public void start(Stage stage) {

        VBox root = new VBox();
        var scene = new Scene(root, 640, 480);

        init(root);

        stage.setScene(scene);
        stage.show();

    }

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
            servoControl.base = (int) (sliderBase.getValue());
            servoControl.grip = (int) (sliderGrip.getValue());
            servoControl.wrist = (int) (sliderWrist.getValue());
            servoControl.shoulder = (int) (sliderShoulder.getValue());
            servoControl.elbow = (int) (sliderElbow.getValue());
            motor1 = (int)(motor1Slider.getValue());
            motor2 = (int)(motor2Slider.getValue());
            servoControl.sendData();
        };

        Text baseText = new Text("Base");
        sliderBase.setMax(ServoControl.SERVO_MG996R_MAX);
        sliderBase.setMin(ServoControl.SERVO_MG996R_MIN);
        sliderBase.setValue(ServoControl.SERVO_MG996R_MIN);
        sliderBase.valueProperty().addListener(dataUpdater);
        sliderBase.setOrientation(Orientation.HORIZONTAL);

        Text gripText = new Text("Grip");
        sliderGrip.setMax(ServoControl.SHOULDER_TRIM_MAX);
        sliderGrip.setMin(ServoControl.SHOULDER_TRIM_MIN);
        sliderGrip.setValue((ServoControl.SHOULDER_TRIM_MAX + ServoControl.SHOULDER_TRIM_MIN) / 2.0f);
        sliderGrip.valueProperty().addListener(dataUpdater);
        sliderGrip.setOrientation(Orientation.HORIZONTAL);

        Text wristText = new Text("Wrist");
        sliderWrist.setMax(ServoControl.SERVO_MG996R_MAX);
        sliderWrist.setMin(ServoControl.SERVO_MG996R_MIN);
        sliderWrist.setValue((ServoControl.SERVO_MG996R_MAX + ServoControl.SERVO_MG996R_MIN) / 2.0f);
        sliderWrist.valueProperty().addListener(dataUpdater);
        sliderWrist.setOrientation(Orientation.HORIZONTAL);

        Text elbowText = new Text("Elbow");
        sliderElbow.setMax(ServoControl.SHOULDER_TRIM_MAX);
        sliderElbow.setMin(ServoControl.SHOULDER_TRIM_MIN);
        sliderElbow.setValue((ServoControl.SHOULDER_TRIM_MAX + ServoControl.SHOULDER_TRIM_MIN) / 2.0f);
        sliderElbow.valueProperty().addListener(dataUpdater);
        sliderElbow.setOrientation(Orientation.HORIZONTAL);

        Text shoulderText = new Text("Shoulder");
        sliderShoulder.setMax(ServoControl.SHOULDER_TRIM_MAX);
        sliderShoulder.setMin(ServoControl.SHOULDER_TRIM_MIN);
        sliderShoulder.setValue((ServoControl.SHOULDER_TRIM_MAX + ServoControl.SHOULDER_TRIM_MIN) / 2.0f);
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


        root.getChildren().addAll(baseText, sliderBase, gripText, sliderGrip,
                wristText, sliderWrist,
                elbowText, sliderElbow, shoulderText,
                sliderShoulder, motorHBox);
    }

    public static void main(String[] args) {
        launch();
    }

}