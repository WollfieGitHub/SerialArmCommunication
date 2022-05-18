package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.maths.InverseKinematicModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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

import java.util.Objects;

public class InverseKinApp extends Application {

    private ServoControl servoControl = new ServoControl();
    private InverseKinematicModel invKinModel;

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

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        var scene = new Scene(root, 640, 600);

        invKinModel = InverseKinematicModel.of(30, 15, 10);
        y = invKinModel.getMaxRange() * 2 / 3.0;
        x = invKinModel.getMaxRange() * 2 / 3.0;

        stage.getIcons().add(new Image(Objects.requireNonNull(InverseKinApp.class.getResourceAsStream("/Logo.png"))));

        init(root);

        stage.setScene(scene);
        stage.show();
    }


    private void init(VBox root) {


        VBox invKinVBox = new VBox();
        Label invKinLbl = new Label("Robot Position");
        Canvas invKinCanvas = new Canvas();
        initCanvas(invKinCanvas);

        invKinVBox.setPadding(new Insets(10.0));
        invKinVBox.getChildren().addAll(invKinLbl, invKinCanvas);
        invKinVBox.setAlignment(Pos.CENTER);
        invKinVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));

        VBox rotVBox = new VBox();
        Label rotLbl = new Label("Robot Rotation");
        Slider rotSlider = new Slider();

        rotSlider.setOrientation(Orientation.HORIZONTAL);

        rotVBox.setPadding(new Insets(10.0));
        rotVBox.getChildren().addAll(rotLbl, rotSlider);
        rotVBox.setAlignment(Pos.CENTER);
        rotVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));

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
            drawArm();
        });

        phiEVBox.setPadding(new Insets(10.0));
        phiEVBox.getChildren().addAll(phiELbl, phiESlider);
        phiEVBox.setAlignment(Pos.CENTER);
        phiEVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));

        VBox elbowUpVBox = new VBox();
        CheckBox elbowUpCheckbox = new CheckBox("Elbow Up");

        elbowUpCheckbox.setSelected(true);
        elbowUpCheckbox.selectedProperty().addListener(o -> {
            elbowUp = elbowUpCheckbox.isSelected();
            drawArm();
        });

        elbowUpVBox.setPadding(new Insets(10.0));
        elbowUpVBox.getChildren().addAll(elbowUpCheckbox);
        elbowUpVBox.setAlignment(Pos.CENTER);
        elbowUpVBox.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, null, null)));

        root.setBackground(new Background(new BackgroundFill(rgb255To1(64, 64, 64, 255), null, null)));
        root.setSpacing(10.0);
        root.getChildren().addAll(invKinVBox, rotVBox, phiEVBox, elbowUpVBox);
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

                drawArm();

            } catch (NonInvertibleTransformException e) {
                System.err.println("Cannot invert transform");
            }
        });

        drawArm();
    }

    private void drawArm() {
        invKinModel.apply(x, y, Math.toDegrees(phiE), elbowUp);

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

    public static void main(String[] args) { launch(); }
}
