package fr.wollfie.serial_arm_com.graphics.canvases;

import fr.wollfie.serial_arm_com.sim.ArmSimulation;
import fr.wollfie.serial_arm_com.maths.RobotArmController;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public class ArmSimulationCanvas {
    public static final int MAIN_CANVAS_DIM = 300;

    public final double ScaleFactor;

    private GraphicsContext armCtx;
    private Affine canvasTransform;

    private final RobotArmController armController;
    private final fr.wollfie.serial_arm_com.sim.ArmSimulation ArmSimulation;
    private final Canvas canvas;

    public ArmSimulationCanvas(RobotArmController armController, ArmSimulation armSimulation) {
        this.ArmSimulation = armSimulation;
        this.armController = armController;
        this.ScaleFactor = MAIN_CANVAS_DIM / (armController.getMaxRange());
        this.canvas = initCanvas();
        this.armCtx = canvas.getGraphicsContext2D();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    private Canvas initCanvas() {
        Canvas initCanvas = new Canvas();

        armCtx = initCanvas.getGraphicsContext2D();
        initCanvas.setWidth(2* MAIN_CANVAS_DIM);
        initCanvas.setHeight(MAIN_CANVAS_DIM);

        this.canvasTransform = Transform.affine(
                ScaleFactor, 0,
                0, -ScaleFactor,
                MAIN_CANVAS_DIM, MAIN_CANVAS_DIM);
        armCtx.setTransform(canvasTransform);

        EventHandler<MouseEvent> armOrder = mouseEvent -> {
            Point2D xy = null;
            try {
                Point2D mouse = new Point2D(mouseEvent.getX(), mouseEvent.getY());
                xy = this.canvasTransform.inverseTransform(mouse);
                xy = new Point2D(xy.getX(), Math.max(xy.getY(), 0));

                if (Math.sqrt((xy.getX()*xy.getX()) + (xy.getY()*xy.getY())) > armController.getMaxRange()) {
                    xy = xy.normalize().multiply(armController.getMaxRange());
                }

                ArmSimulation.x = xy.getX();
                ArmSimulation.y = xy.getY();

            } catch (NonInvertibleTransformException e) {
                System.err.println("Cannot invert transform");
                System.err.println(xy);
                System.err.println(this.canvasTransform);
            }
        };

        initCanvas.setOnMouseClicked(armOrder);
        initCanvas.setOnMouseDragged(armOrder);
        return initCanvas;
    }


    public GraphicsContext getGraphicsContext2D() {
        return armCtx;
    }
}
