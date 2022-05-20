package fr.wollfie.serial_arm_com.graphics.canvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class RotationSimulationCanvas extends LittleCanvas{

    private GraphicsContext rotCtx;

    private Canvas canvas;
    public Canvas getCanvas() { return canvas; }

    public RotationSimulationCanvas() {
        initRotBox();
    }

    private void initRotBox() {
        canvas = new Canvas();
        rotCtx = canvas.getGraphicsContext2D();
        canvas.setWidth(LITTLE_CANVAS_DIM);
        canvas.setHeight(LITTLE_CANVAS_DIM);

        Affine affine = Transform.affine(1, 0, 0, 1,
                LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM/2.0);
        rotCtx.setTransform(affine);
    }

    public void resetCanvas() {
        rotCtx.clearRect(-LITTLE_CANVAS_DIM/2.0, -LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);
        rotCtx.setStroke(new Color(1, 0, 0, 1));
        rotCtx.setLineWidth(1);

        rotCtx.strokeOval(-LITTLE_CANVAS_DIM/2.0, -LITTLE_CANVAS_DIM/2.0, LITTLE_CANVAS_DIM, LITTLE_CANVAS_DIM);
    }
}
