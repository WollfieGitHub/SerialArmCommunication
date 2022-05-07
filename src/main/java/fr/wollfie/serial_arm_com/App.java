package fr.wollfie.serial_arm_com;

import fr.wollfie.serial_arm_com.io.ArduinoSerial;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

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

    private int grip = 90;
    private int wrist = 90;
    private int shoulder = 90;
    private int elbow = 90;

    private void init(VBox root) {
        root.setAlignment(Pos.CENTER);

        root.setOnKeyPressed(key -> {
            final int servoStep = 5;

            switch (key.getCode()) {
                case A: grip += servoStep;      break;
                case Z: wrist += servoStep;     break;
                case E: elbow += servoStep;     break;
                case R: shoulder += servoStep;  break;

                case Q: grip -= servoStep;      break;
                case S: wrist -= servoStep;     break;
                case D: elbow -= servoStep;     break;
                case F: shoulder -= servoStep;  break;
                default: break;
            }

            sendData();
        });

        Slider sliderShoulder = new Slider();
        Slider sliderElbow = new Slider();
        Slider sliderWrist = new Slider();
        Slider sliderGrip = new Slider();

        InvalidationListener dataUpdater = o -> {
            grip = (int) (sliderGrip.getValue());
            wrist = (int) (sliderWrist.getValue());
            shoulder = (int)( sliderShoulder.getValue());
            elbow = (int)( sliderElbow.getValue());
            sendData();
        };

        Text gripText = new Text("Grip");
        sliderGrip.setMax(180);
        sliderGrip.setMin(0);
        sliderGrip.setValue(90);
        sliderGrip.valueProperty().addListener(dataUpdater);
        sliderGrip.setOrientation(Orientation.HORIZONTAL);

        Text wristText = new Text("Wrist");
        sliderWrist.setMax(180);
        sliderWrist.setMin(0);
        sliderWrist.setValue(90);
        sliderWrist.valueProperty().addListener(dataUpdater);
        sliderWrist.setOrientation(Orientation.HORIZONTAL);

        Text elbowText = new Text("Elbow");
        sliderElbow.setMax(180);
        sliderElbow.setMin(0);
        sliderElbow.setValue(90);
        sliderElbow.valueProperty().addListener(dataUpdater);
        sliderElbow.setOrientation(Orientation.HORIZONTAL);

        Text shoulderText = new Text("Shoulder");
        sliderShoulder.setMax(180);
        sliderShoulder.setMin(0);
        sliderShoulder.setValue(90);
        sliderShoulder.valueProperty().addListener(dataUpdater);
        sliderShoulder.setOrientation(Orientation.HORIZONTAL);

        root.getChildren().addAll(gripText, sliderGrip,
                wristText, sliderWrist,
                elbowText, sliderElbow, shoulderText,
                sliderShoulder);
    }

    private void sendData() {
        String msg = String.format("G%03dW%03dE%04dS%04d%n", grip, wrist, elbow, shoulder);
        arduinoSerial.write(msg);
        System.out.println(msg);
    }

    public static void main(String[] args) {
        launch();
    }

}