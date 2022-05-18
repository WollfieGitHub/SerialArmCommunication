package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.maths.RobotArmController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InverseKinApp extends Application {

    private final TextArea logger = new TextArea();
    private final ServoControl servoControl = new ServoControl(logger);
    private RobotArmController invKinModel;

    private GraphicsContext ctx;
    private Affine canvasTransform;

    private static final int CANVAS_DIM = 300;
    private static final Color BACKGROUND_COLOR = rgb255To1(131, 168, 146, 255);

    private static Color rgb255To1(int r, int g, int b, int a) {
        return new Color(r/255.0, g/255.0, b/255.0, a/255.0);
    }

    private double scaleFactor;

    private double phiE = 0;
    private double x;
    private double y;
    private boolean elbowUp = true;

    private double baseRotationDeg;
    private double gripOpeningDeg;

    @Override
    public void start(Stage stage) {
        HBox root = new HBox();

        var scene = new Scene(root, 900, 700);

        invKinModel = RobotArmController.of(30, 15, 10, servoControl);
        y = invKinModel.getMaxRange() * 2 / 3.0;
        x = invKinModel.getMaxRange() * 2 / 3.0;

        stage.getIcons().add(new Image(Objects.requireNonNull(InverseKinApp.class.getResourceAsStream("/Logo.png"))));

        VBox mainBox = new VBox();
        VBox loggerBox = new VBox();

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(loggerBox, mainBox);
        root.setSpacing(10.0);
        root.setBackground(new Background(new BackgroundFill(rgb255To1(64, 64, 64, 255), null, null)));

        initMainBox(mainBox);
        initLoggerBox(loggerBox);

        stage.setScene(scene);
        stage.show();
    }

    private void initLoggerBox(VBox loggerBox) {
        loggerBox.getChildren().addAll(logger);
        logger.setDisable(true);
    }


    private void initMainBox(VBox mainBox) {


        VBox invKinVBox = initInvKinVBox();
        VBox rotVBox = initRotVBox();
        VBox phiEVBox = initPhiEVBox();
        VBox elbowUpVBox = initElbowUpVBox();
        VBox gripOpeningVBox = initGripOpeningVBox();

        mainBox.setSpacing(10.0);
        mainBox.getChildren().addAll(invKinVBox, rotVBox, gripOpeningVBox, phiEVBox, elbowUpVBox);
    }

    private void initCanvas(Canvas invKinCanvas) {
        scaleFactor = InverseKinApp.CANVAS_DIM / (invKinModel.getMaxRange());
        ctx = invKinCanvas.getGraphicsContext2D();
        invKinCanvas.setWidth(2*CANVAS_DIM);
        invKinCanvas.setHeight(CANVAS_DIM);

        canvasTransform = Transform.affine(
                scaleFactor, 0,
                0, -scaleFactor,
                CANVAS_DIM, CANVAS_DIM);
        ctx.setTransform(canvasTransform);

        invKinCanvas.setOnMouseDragged(mouseEvent -> {
            try {
                Point2D mouse = new Point2D(mouseEvent.getX(), mouseEvent.getY());
                Point2D xy = canvasTransform.inverseTransform(mouse);

                x = xy.getX();
                y = xy.getY();

                updateArm();

            } catch (NonInvertibleTransformException e) {
                System.err.println("Cannot invert transform");
            }
        });

        updateArm();
    }

    private void updateArm() {
        invKinModel.update(x, y, Math.toDegrees(phiE), elbowUp, gripOpeningDeg, baseRotationDeg);

        final double DrawingDim = invKinModel.getMaxRange();

        ctx.clearRect(-DrawingDim, -DrawingDim, 2*DrawingDim, 2*DrawingDim);

        ctx.setStroke(new Color(1, 0, 0, 1));
        ctx.setLineWidth(1/ scaleFactor);

        ctx.strokeOval(
                -invKinModel.getMaxRange(),
                -invKinModel.getMaxRange(),
                invKinModel.getMaxRange()*2,
                invKinModel.getMaxRange()*2);

        ctx.setLineWidth(2/ scaleFactor);

        invKinModel.drawOn(ctx);
    }

    @NotNull
    private VBox initInvKinVBox() {
        VBox invKinVBox = new VBox();
        Label invKinLbl = new Label("Robot Position");
        Canvas invKinCanvas = new Canvas();
        initCanvas(invKinCanvas);

        invKinVBox.setPadding(new Insets(10.0));
        invKinVBox.getChildren().addAll(invKinLbl, invKinCanvas);
        invKinVBox.setAlignment(Pos.CENTER);
        invKinVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return invKinVBox;
    }

    @NotNull
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
            baseRotationDeg = rotSlider.getValue();
            updateArm();
        });

        rotVBox.setPadding(new Insets(10.0));
        rotVBox.getChildren().addAll(rotLbl, rotSlider);
        rotVBox.setAlignment(Pos.CENTER);
        rotVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return rotVBox;
    }

    @NotNull
    private VBox initGripOpeningVBox() {
        VBox gripOpeningVBox = new VBox();
        Label gripLbl = new Label("Grip Opening");
        Slider gripSlider = new Slider();

        gripSlider.setOrientation(Orientation.HORIZONTAL);
        gripSlider.setMin(60);
        gripSlider.setMax(120);
        gripSlider.setValue(90);
        gripSlider.setMajorTickUnit(30);
        gripSlider.setMinorTickCount(5);
        gripSlider.setShowTickLabels(true);
        gripSlider.setShowTickMarks(true);
        gripSlider.setSnapToTicks(true);
        gripSlider.valueProperty().addListener(o -> {
            gripOpeningDeg = gripSlider.getValue();
            updateArm();
        });

        gripOpeningVBox.setPadding(new Insets(10.0));
        gripOpeningVBox.getChildren().addAll(gripLbl, gripSlider);
        gripOpeningVBox.setAlignment(Pos.CENTER);
        gripOpeningVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return gripOpeningVBox;
    }

    @NotNull
    private VBox initElbowUpVBox() {
        VBox elbowUpVBox = new VBox();
        CheckBox elbowUpCheckbox = new CheckBox("Elbow Up");

        elbowUpCheckbox.setSelected(true);
        elbowUpCheckbox.selectedProperty().addListener(o -> {
            elbowUp = elbowUpCheckbox.isSelected();
            updateArm();
        });

        elbowUpVBox.setPadding(new Insets(10.0));
        elbowUpVBox.getChildren().addAll(elbowUpCheckbox);
        elbowUpVBox.setAlignment(Pos.CENTER);
        elbowUpVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return elbowUpVBox;
    }

    @NotNull
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
            phiE = phiESlider.getValue();
            updateArm();
        });

        phiEVBox.setPadding(new Insets(10.0));
        phiEVBox.getChildren().addAll(phiELbl, phiESlider);
        phiEVBox.setAlignment(Pos.CENTER);
        phiEVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return phiEVBox;
    }

    public static void main(String[] args) { launch(); }
}
