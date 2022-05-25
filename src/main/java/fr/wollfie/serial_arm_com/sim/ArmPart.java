package fr.wollfie.serial_arm_com.sim;

import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jetbrains.annotations.Nullable;

public final class ArmPart {

    private final double length;
    private double angleRad = 0.0;
    private final ArmPart parent;

    private ArmPart(double length, ArmPart parent) {
        this.length = length;
        this.parent = parent;
    }

    public double getLength() {
        return length;
    }

    private ArmPart(double length, double angleRad, ArmPart parent) {
        this.length = length;
        this.angleRad = angleRad;
        this.parent = parent;
    }

    public static ArmPart buildNew(double partLength, @Nullable ArmPart parent) {
        return new ArmPart(partLength, parent);
    }

    public void rotateToAngleDeg(double angleDeg) { this.angleRad = Math.toRadians(angleDeg); }
    public void rotateToAngleRad(double angleRad) { this.angleRad = angleRad; }

    public double getAngleRad() { return angleRad; }
    public double getAngleDeg() { return Math.toDegrees(angleRad); }

    public double getGlobalAngleDeg() { return Math.toDegrees(getGlobalAngleRad()); }
    public double getGlobalAngleRad() {
        double globalAngleDeg = angleRad;
        if (parent != null) {
            globalAngleDeg += parent.getGlobalAngleRad();
        }
        return globalAngleDeg;
    }

    public Point2D getOriginPoint() {
        if (parent != null) {
            return parent.getEndPoint();
        } else {
            return new Point2D(0, 0);
        }
    }

    public Point2D getEndPoint() {
        // Part is flattened on the x-axis
        Point2D origin = new Point2D(length, 0);

        // How cringe can it be to ask for degrees without more details ?
        Rotate rotation = Transform.rotate(getAngleDeg(), 0, 0);
        origin = rotation.transform(origin);

        if (parent != null) {
            // Apply rotation of the whole arm
            Rotate globalRotation = Transform.rotate(parent.getGlobalAngleDeg(), 0, 0);
            origin = globalRotation.transform(origin);

            // Move to the end of the parent
            origin = origin.add(parent.getEndPoint());
        }

        return origin;
    }

}
