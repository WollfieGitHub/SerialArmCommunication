package fr.wollfie.serial_arm_com.graphics.canvases;

import fr.wollfie.serial_arm_com.graphics.Triangle;
import fr.wollfie.serial_arm_com.sim.ArmSimulation;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public class CarCanvas extends LittleCanvas {

    private static final int INDICATOR_DIM = 20;

    private GraphicsContext carCtx;

    private Affine transform;

    private Canvas canvas;
    public Canvas getCanvas() { return canvas; }

    private Triangle canvasTriangle;

    private final ArmSimulation ArmSimulation;

    private double lastX;
    private double lastY;

    public CarCanvas(ArmSimulation armSimulation) {
        this.ArmSimulation = armSimulation;
        this.canvasTriangle = new Triangle(
                new Point2D(-LITTLE_CANVAS_DIM/4.0, LITTLE_CANVAS_DIM),
                new Point2D(LITTLE_CANVAS_DIM/4.0, LITTLE_CANVAS_DIM),
                new Point2D(0, 0)
        );
        this.transform = Transform.affine(
                1, 0, 0, -1, LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM
        );

        initCarBox();
    }

    private void initCarBox() {
        canvas = new Canvas();
        canvas.setWidth(LITTLE_CANVAS_DIM);
        canvas.setHeight(LITTLE_CANVAS_DIM);
        carCtx = canvas.getGraphicsContext2D();
        carCtx.setTransform(transform);

        EventHandler<MouseEvent> onMouseInteract = e -> {
            Point2D xy = new Point2D(e.getX(), e.getY());
            try {
                xy = this.transform.inverseTransform(xy);
            } catch (NonInvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }

            if (!canvasTriangle.contains(xy)) {
                xy = canvasTriangle.project(xy);
            }

            Point2D leftCorner = new Point2D(-LITTLE_CANVAS_DIM/4.0, LITTLE_CANVAS_DIM);
            Point2D rightCorner = new Point2D(LITTLE_CANVAS_DIM/4.0, LITTLE_CANVAS_DIM);
            Point2D origin = new Point2D(0, 0);

            lastX = xy.getX();
            lastY = xy.getY();

            double height = origin.distance(xy);
            double diffLeft = xy.getX() - leftCorner.getX();
            double diffRight = rightCorner.getX() - xy.getX();

            double maxDiff = rightCorner.getX() - leftCorner.getX();
            double maxHeight = rightCorner.getY();

            double diffSpeedA = Math.min(1, Math.max(0, Math.max(0.5, diffRight / maxDiff) * 2 - 1));
            double diffSpeedB = Math.min(1, Math.max(0, Math.max(0.5, diffLeft / maxDiff) * 2 - 1));

            double heightSpeed = Math.min(Math.max(height / maxHeight, 0), 1);

            int speedA = (int)( (heightSpeed - diffSpeedA)*255 );
            int speedB = (int)( (heightSpeed - diffSpeedB)*255 );

            this.ArmSimulation.motorASpeed = speedA;
            this.ArmSimulation.motorBSpeed = speedB;
        };

        canvas.setOnMouseDragged(onMouseInteract);
        canvas.setOnMouseDragExited(e -> {
            this.ArmSimulation.motorASpeed = 0;
            this.ArmSimulation.motorBSpeed = 0;
            lastY = 0;
            lastX = 0;
        });
    }

    public void drawCanvas() {
        carCtx.clearRect(-LITTLE_CANVAS_DIM/2.0, 0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);

        carCtx.setFill(new Color(0.2, 0.2, 0.2, 1));

        carCtx.fillPolygon(canvasTriangle.getXs(), canvasTriangle.getYs(), 3);

        carCtx.setFill(new Color(1, 0, 0, 1));
        carCtx.fillOval(
                lastX - INDICATOR_DIM/2.0,
                lastY - INDICATOR_DIM/2.0,
                INDICATOR_DIM,
                INDICATOR_DIM
        );
    }
}
