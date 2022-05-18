package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.maths.RobotArmController;
import fr.wollfie.serial_arm_com.sim.ArduinoControlSimulator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
    private ServoControl servoControl;
    private RobotArmController invKinModel;

    private GraphicsContext armCtx;
    private GraphicsContext rotCtx;
    private GraphicsContext gripCtx;

    private Affine canvasTransform;

    private static final int MAIN_CANVAS_DIM = 300;
    private static final Color BACKGROUND_COLOR = rgb255To1(131, 168, 146, 255);
    private static final int LITTLE_CANVAS_DIM = 150;

    public static final double ANGLE_TO_GRIP_RATIO = 0.5;

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

        double l1 = 30, l2 = 15, l3 = 10;

        servoControl  = new ServoControl(l1, l2, l3, logger);
        invKinModel = RobotArmController.of(l1, l2, l3, servoControl);

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
        stage.setOnCloseRequest(e -> System.exit(0));

        Renderer renderer = new Renderer(this);
        renderer.start();

    }

    private void initLoggerBox(VBox loggerBox) {
        loggerBox.setSpacing(10.0);

        HBox fineTuningHBox = initFineTuningHBox();

        VBox rotCanvas = initRotBox();
        VBox gripCanvas = initGripBox();

        loggerBox.getChildren().addAll(logger, fineTuningHBox, rotCanvas, gripCanvas);
        logger.setDisable(true);
    }

    public void updateArm() {
        invKinModel.update(x, y, Math.toDegrees(phiE), elbowUp, gripOpeningDeg, baseRotationDeg);

        final double DrawingDim = invKinModel.getMaxRange();

        armCtx.clearRect(-DrawingDim, -DrawingDim, 2*DrawingDim, 2*DrawingDim);

        armCtx.setStroke(new Color(1, 0, 0, 1));
        armCtx.setLineWidth(1/ scaleFactor);

        armCtx.strokeOval(
                -invKinModel.getMaxRange(),
                -invKinModel.getMaxRange(),
                invKinModel.getMaxRange()*2,
                invKinModel.getMaxRange()*2);

        armCtx.setLineWidth(2/ scaleFactor);

        invKinModel.drawArmOn(armCtx);
        servoControl.simDrawArmOn(armCtx);

        // ROTATION
        rotCtx.clearRect(-LITTLE_CANVAS_DIM/2.0, -LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);
        rotCtx.setStroke(new Color(1, 0, 0, 1));
        rotCtx.setLineWidth(1);

        rotCtx.strokeOval(-LITTLE_CANVAS_DIM/2.0, -LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);
        servoControl.simDrawRotOn(rotCtx, LITTLE_CANVAS_DIM/2.0);

        // GRIP
        gripCtx.clearRect(-LITTLE_CANVAS_DIM/2.0, -LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);
        gripCtx.setStroke(new Color(1, 0, 0, 1));
        gripCtx.setLineWidth(1);

        servoControl.simDrawGripOn(gripCtx, LITTLE_CANVAS_DIM/2.0);
    }

    private VBox initGripBox() {
        VBox vBox = new VBox();

        Canvas canvas = new Canvas();
        gripCtx = canvas.getGraphicsContext2D();
        canvas.setWidth(LITTLE_CANVAS_DIM);
        canvas.setHeight(LITTLE_CANVAS_DIM);

        Affine affine = Transform.affine(1, 0, 0, 1, LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM/2.0);
        gripCtx.setTransform(affine);

        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        vBox.getChildren().addAll(canvas);

        return vBox;
    }

    private VBox initRotBox() {
        VBox vBox = new VBox();

        Canvas canvas = new Canvas();
        rotCtx = canvas.getGraphicsContext2D();
        canvas.setWidth(LITTLE_CANVAS_DIM);
        canvas.setHeight(LITTLE_CANVAS_DIM);

        Affine affine = Transform.affine(1, 0, 0, 1, LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM/2.0);
        rotCtx.setTransform(affine);

        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        vBox.getChildren().addAll(canvas);

        return vBox;
    }


    private void initMainBox(VBox mainBox) {


        VBox invKinVBox = initInvKinVBox();
        VBox rotVBox = initRotVBox();
        VBox phiEVBox = initPhiEVBox();
        VBox elbowUpVBox = initElbowUpVBox();
        VBox gripOpeningVBox = initGripOpeningVBox();
        //VBox wristConfigVBox = initGripConfigVBox();

        mainBox.setSpacing(10.0);
        mainBox.getChildren().addAll(invKinVBox, rotVBox, gripOpeningVBox, phiEVBox, elbowUpVBox);
    }

    private VBox initGripConfigVBox() {
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
        gripOpeningVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return gripOpeningVBox;
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
        fineTuningHBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        fineTuningHBox.setPadding(new Insets(10));
        return fineTuningHBox;
    }

    private void initCanvas(Canvas invKinCanvas) {
        scaleFactor = InverseKinApp.MAIN_CANVAS_DIM / (invKinModel.getMaxRange());
        armCtx = invKinCanvas.getGraphicsContext2D();
        invKinCanvas.setWidth(2* MAIN_CANVAS_DIM);
        invKinCanvas.setHeight(MAIN_CANVAS_DIM);

        canvasTransform = Transform.affine(
                scaleFactor, 0,
                0, -scaleFactor,
                MAIN_CANVAS_DIM, MAIN_CANVAS_DIM);
        armCtx.setTransform(canvasTransform);

        EventHandler<MouseEvent> armOrder = mouseEvent -> {
            try {
                Point2D mouse = new Point2D(mouseEvent.getX(), mouseEvent.getY());
                Point2D xy = canvasTransform.inverseTransform(mouse);
                xy = new Point2D(xy.getX(), Math.max(xy.getY(), 0));

                if (Math.sqrt((xy.getX()*xy.getX()) + (xy.getY()*xy.getY())) > invKinModel.getMaxRange()) {
                    xy = xy.normalize().multiply(invKinModel.getMaxRange());
                }

                x = xy.getX();
                y = xy.getY();

            } catch (NonInvertibleTransformException e) {
                System.err.println("Cannot invert transform");
            }
        };

        invKinCanvas.setOnMouseClicked(armOrder);
        invKinCanvas.setOnMouseDragged(armOrder);
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
        gripSlider.setMin(0);
        gripSlider.setMax(45);
        gripSlider.setValue(0);
        gripSlider.setMajorTickUnit(30);
        gripSlider.setMinorTickCount(5);
        gripSlider.setShowTickLabels(true);
        gripSlider.setShowTickMarks(true);
        gripSlider.setSnapToTicks(true);
        gripSlider.valueProperty().addListener(o -> {
            gripOpeningDeg = gripSlider.getValue();
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
        });

        phiEVBox.setPadding(new Insets(10.0));
        phiEVBox.getChildren().addAll(phiELbl, phiESlider);
        phiEVBox.setAlignment(Pos.CENTER);
        phiEVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));
        return phiEVBox;
    }

    public static void main(String[] args) { launch(); }
}
