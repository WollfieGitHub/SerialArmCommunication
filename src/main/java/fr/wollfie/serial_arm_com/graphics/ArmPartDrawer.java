package fr.wollfie.serial_arm_com.graphics;

import fr.wollfie.serial_arm_com.sim.ArmPart;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ArmPartDrawer {

    private final ArmPart armPart;
    private final Color color;

    private ArmPartDrawer(Color color, ArmPart armPart) {
        this.armPart = armPart;
        this.color = color;
    }

    public static ArmPartDrawer with(Color color, ArmPart part) {
        return new ArmPartDrawer(color, part);
    }

    public void drawOn(GraphicsContext ctx) {
        ctx.setStroke(color);

        Point2D origin = armPart.getOriginPoint();
        Point2D end = armPart.getEndPoint();

        ctx.strokeLine(origin.getX(), origin.getY(), end.getX(), end.getY());
    }

    public ArmPart getArmPart() {
        return armPart;
    }
}
