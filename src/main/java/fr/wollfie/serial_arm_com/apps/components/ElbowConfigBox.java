package fr.wollfie.serial_arm_com.apps.components;

import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import fr.wollfie.serial_arm_com.maths.RobotArmController;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;

public class ElbowConfigBox {
    private static VBox initGripConfigVBox(RobotArmController invKinModel) {
        VBox gripOpeningVBox = new VBox();
        Label gripLbl = new Label("Wrist Config");
        Slider gripSlider = new Slider();

        gripSlider.setOrientation(Orientation.HORIZONTAL);
        gripSlider.setMin(0);
        gripSlider.setMax(180);
        gripSlider.setValue(0);
        gripSlider.setMajorTickUnit(30);
        gripSlider.setMinorTickCount(5);
        gripSlider.setShowTickLabels(true);
        gripSlider.setShowTickMarks(true);
        gripSlider.setSnapToTicks(true);
        gripSlider.valueProperty().addListener(o -> {
            invKinModel.configWrist(gripSlider.getValue());
        });

        gripOpeningVBox.setPadding(new Insets(10.0));
        gripOpeningVBox.getChildren().addAll(gripLbl, gripSlider);
        gripOpeningVBox.setAlignment(Pos.CENTER);
        gripOpeningVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        return gripOpeningVBox;
    }
}
