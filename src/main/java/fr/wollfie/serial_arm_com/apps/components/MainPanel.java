package fr.wollfie.serial_arm_com.apps.components;

import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import fr.wollfie.serial_arm_com.sim.ArmSimulation;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;

public class MainPanel {

    private VBox root;
    public VBox getRoot() { return root; }

    private final ArmSimulation armSimulation;

    public MainPanel(Canvas armCanvas, ArmSimulation armSimulation) {
        initMainBox(armCanvas);
        this.armSimulation = armSimulation;
    }

    private void initMainBox(Canvas canvas) {
        this.root = new VBox();

        VBox invKinVBox = initInvKinVBox(canvas);
        VBox rotVBox = initRotVBox();
        VBox phiEVBox = initPhiEVBox();
        VBox elbowUpVBox = initElbowUpVBox();
        VBox gripOpeningVBox = initGripOpeningVBox();
        //VBox wristConfigVBox = initGripConfigVBox();

        root.setSpacing(10.0);
        root.getChildren().addAll(invKinVBox, rotVBox, gripOpeningVBox, phiEVBox, elbowUpVBox);
    }

    private VBox initInvKinVBox(Canvas invKinCanvas) {
        VBox invKinVBox = new VBox();
        Label invKinLbl = new Label("Robot Position");

        invKinVBox.setPadding(new Insets(10.0));
        invKinVBox.getChildren().addAll(invKinLbl, invKinCanvas);
        invKinVBox.setAlignment(Pos.CENTER);
        invKinVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        return invKinVBox;
    }

    private VBox initRotVBox() {
        VBox rotVBox = new VBox();
        Label rotLbl = new Label("Robot Rotation");
        Slider rotSlider = new Slider();

        rotSlider.setOrientation(Orientation.HORIZONTAL);
        rotSlider.setMin(0);
        rotSlider.setMax(180);
        rotSlider.setValue(0);
        rotSlider.setMajorTickUnit(30);
        rotSlider.setMinorTickCount(5);
        rotSlider.setShowTickLabels(true);
        rotSlider.setShowTickMarks(true);
        rotSlider.setSnapToTicks(true);
        rotSlider.valueProperty().addListener(o -> {
            armSimulation.baseRotationDeg = rotSlider.getValue();
        });

        rotVBox.setPadding(new Insets(10.0));
        rotVBox.getChildren().addAll(rotLbl, rotSlider);
        rotVBox.setAlignment(Pos.CENTER);
        rotVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        return rotVBox;
    }

    private VBox initGripOpeningVBox() {
        VBox gripOpeningVBox = new VBox();
        Label gripLbl = new Label("Grip Opening");
        Slider gripSlider = new Slider();

        gripSlider.setOrientation(Orientation.HORIZONTAL);
        gripSlider.setMin(0);
        gripSlider.setMax(45);
        gripSlider.setValue(0);
        gripSlider.setMajorTickUnit(30);
        gripSlider.setMinorTickCount(5);
        gripSlider.setShowTickLabels(true);
        gripSlider.setShowTickMarks(true);
        gripSlider.setSnapToTicks(true);
        gripSlider.valueProperty().addListener(o -> {
            armSimulation.gripOpeningDeg = gripSlider.getValue();
        });

        gripOpeningVBox.setPadding(new Insets(10.0));
        gripOpeningVBox.getChildren().addAll(gripLbl, gripSlider);
        gripOpeningVBox.setAlignment(Pos.CENTER);
        gripOpeningVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        return gripOpeningVBox;
    }

    private VBox initElbowUpVBox() {
        VBox elbowUpVBox = new VBox();
        CheckBox elbowUpCheckbox = new CheckBox("Elbow Up");

        elbowUpCheckbox.setSelected(true);
        elbowUpCheckbox.selectedProperty().addListener(o -> {
            armSimulation.elbowUp = elbowUpCheckbox.isSelected();
        });

        elbowUpVBox.setPadding(new Insets(10.0));
        elbowUpVBox.getChildren().addAll(elbowUpCheckbox);
        elbowUpVBox.setAlignment(Pos.CENTER);
        elbowUpVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        return elbowUpVBox;
    }

    private VBox initPhiEVBox() {
        VBox phiEVBox = new VBox();
        Label phiELbl = new Label("PhiE");
        Slider phiESlider = new Slider();

        phiESlider.setOrientation(Orientation.HORIZONTAL);
        phiESlider.setMax(Math.PI);
        phiESlider.setValue(0);
        phiESlider.setMin(-Math.PI);
        phiESlider.setMajorTickUnit(Math.PI/4.0);
        phiESlider.setMinorTickCount(4);
        phiESlider.setShowTickMarks(true);
        phiESlider.setSnapToTicks(true);
        phiESlider.setShowTickLabels(true);

        phiESlider.valueProperty().addListener(o -> {
            armSimulation.phiE = phiESlider.getValue();
        });

        phiEVBox.setPadding(new Insets(10.0));
        phiEVBox.getChildren().addAll(phiELbl, phiESlider);
        phiEVBox.setAlignment(Pos.CENTER);
        phiEVBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        return phiEVBox;
    }
}
