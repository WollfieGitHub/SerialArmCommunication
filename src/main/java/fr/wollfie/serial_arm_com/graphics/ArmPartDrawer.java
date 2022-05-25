package fr.wollfie.serial_arm_com.graphics;

import fr.wollfie.serial_arm_com.sim.ArmPart;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Transform;

public class ArmPartDrawer {

    private final ArmPart armPart;
    private final Cylinder shape;
    private final Color color;

    private ArmPartDrawer(Color color, ArmPart armPart) {
        this.armPart = armPart;
        this.color = color;
        this.shape = new Cylinder(1, armPart.getLength());
        this.shape.setMaterial(new PhongMaterial(color));
        this.shape.getTransforms().add(Transform.affine(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0
        ));
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

    public Cylinder get3DRepresentation() {
        return this.shape;
    }

    public void updateRepresentation3D(double angleOffset) {
        Point2D midPoint = armPart.getEndPoint()
                .subtract(armPart.getOriginPoint())
                .multiply(0.5)
                .add(armPart.getOriginPoint());

        this.shape.setTranslateX(midPoint.getX());
        this.shape.setTranslateY(midPoint.getY());
        this.shape.setRotate(angleOffset+this.armPart.getGlobalAngleDeg());
    }

    public ArmPart getArmPart() {
        return armPart;
    }
}
