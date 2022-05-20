package fr.wollfie.serial_arm_com.graphics.canvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class GripSimulationCanvas extends LittleCanvas {

    private final Canvas canvas;
    public Canvas getCanvas() { return canvas; }

    private final GraphicsContext GripCtx;

    public GripSimulationCanvas() {
        this.canvas = new Canvas();
        this.canvas.setWidth(LITTLE_CANVAS_DIM);
        this.canvas.setHeight(LITTLE_CANVAS_DIM);
        this.GripCtx = this.canvas.getGraphicsContext2D();

        Affine affine = Transform.affine(1, 0, 0, 1, LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM/2.0);
        canvas.getGraphicsContext2D().setTransform(affine);
    }

    public void resetCanvas() {
        GripCtx.clearRect(-LITTLE_CANVAS_DIM/2.0, -LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);
        GripCtx.setStroke(new Color(1, 0, 0, 1));
        GripCtx.setLineWidth(1);

    }
}
