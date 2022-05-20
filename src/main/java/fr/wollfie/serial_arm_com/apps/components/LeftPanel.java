package fr.wollfie.serial_arm_com.apps.components;

import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import fr.wollfie.serial_arm_com.sim.ArduinoControlSimulator;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class LeftPanel {

    private final VBox root;

    public LeftPanel(TextArea logger, Canvas rotCanvas) {
        this.root = new VBox();
        this.root.setSpacing(10.0);

        HBox fineTuningHBox = initFineTuningHBox();

        VBox rotVBox = initRotBox(rotCanvas);


        this.root.getChildren().addAll(logger, fineTuningHBox, rotVBox);
        logger.setDisable(true);
    }


    private VBox initRotBox(Canvas rotCanvas) {
        VBox rotVBox = new VBox();

        rotVBox.setAlignment(Pos.CENTER);
        rotVBox.setPadding(new Insets(10));
        rotVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        rotVBox.getChildren().addAll(rotCanvas);
        return rotVBox;
    }

    private HBox initFineTuningHBox() {
        HBox fineTuningHBox = new HBox();

        VBox stepSizeVBox = new VBox();

        Label stepSizeLbl = new Label("Step size");
        Slider stepSizeSlider = new Slider();
        stepSizeSlider.setOrientation(Orientation.VERTICAL);
        stepSizeSlider.setMajorTickUnit(0.05);
        stepSizeSlider.setMinorTickCount(4);
        stepSizeSlider.setShowTickMarks(true);
        stepSizeSlider.setShowTickLabels(true);
        stepSizeSlider.setSnapToTicks(true);
        stepSizeSlider.setMax(0.2);
        stepSizeSlider.setValue(ArduinoControlSimulator.STEP_SIZE);
        stepSizeSlider.setMin(0);
        stepSizeSlider.valueProperty().addListener(o -> {
            ArduinoControlSimulator.STEP_SIZE = stepSizeSlider.getValue();
            stepSizeLbl.setText("Step size : " + String.format("%2.4f", ArduinoControlSimulator.STEP_SIZE));
        });

        stepSizeVBox.getChildren().addAll(stepSizeLbl, stepSizeSlider);

        VBox updateDelayVBox = new VBox();

        Label updateDelayLbl = new Label("Update Delay");
        Slider updateDelaySlider = new Slider();
        updateDelaySlider.setOrientation(Orientation.VERTICAL);
        updateDelaySlider.setMajorTickUnit(10);
        updateDelaySlider.setMinorTickCount(10);
        updateDelaySlider.setShowTickMarks(true);
        updateDelaySlider.setShowTickLabels(true);
        updateDelaySlider.setSnapToTicks(true);
        updateDelaySlider.setMax(100);
        updateDelaySlider.setValue(ArduinoControlSimulator.UPDATE_DELAY_MS);
        updateDelaySlider.setMin(0);
        updateDelaySlider.valueProperty().addListener(o -> {
            ArduinoControlSimulator.UPDATE_DELAY_MS = (long)updateDelaySlider.getValue();
            updateDelayLbl.setText("Update Delay : " + ArduinoControlSimulator.UPDATE_DELAY_MS + "ms");
        });

        updateDelayVBox.getChildren().addAll(updateDelayLbl, updateDelaySlider);

        fineTuningHBox.getChildren().addAll(stepSizeVBox, updateDelayVBox);
        fineTuningHBox.setAlignment(Pos.CENTER);
        fineTuningHBox.setSpacing(30);
        fineTuningHBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        fineTuningHBox.setPadding(new Insets(10));
        return fineTuningHBox;
    }

    public Node getRoot() {
        return this.root;
    }
}
