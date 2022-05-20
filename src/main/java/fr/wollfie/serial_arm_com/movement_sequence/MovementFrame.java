package fr.wollfie.serial_arm_com.movement_sequence;

import fr.wollfie.serial_arm_com.maths.RobotArmController;
import javafx.geometry.Point3D;

public final class MovementFrame {

    // Using grip position instead of overall servo positions because we can
    // use a custom curve to interpolate
    private final Point3D PolarGripPosition;
    // Range(0, 1)
    private final double GripOpeningDegree;
    private final double AngleToGroundDegree;

    private MovementFrame(Point3D polarGripPosition, double gripOpeningDegree, double angleToGroundDegree) {
        PolarGripPosition = polarGripPosition;
        GripOpeningDegree = gripOpeningDegree;
        AngleToGroundDegree = angleToGroundDegree;
    }

    public static MovementFrame capture(Point3D polarGripPosition, double gripOpeningDegree, double angleToGroundDegree) {
        return new MovementFrame(polarGripPosition, gripOpeningDegree, angleToGroundDegree);
    }

    public static MovementFrame capture(RobotArmController armController) {
        return new MovementFrame(
                armController.getPolarGripPosition(),
                armController.getGripOpeningDeg(),
                armController.getAngleToGroundDeg());
    }

    public Point3D getPolarGripPosition() {
        return PolarGripPosition;
    }

    public double getGripOpeningDegree() {
        return GripOpeningDegree;
    }

    public double getAngleToGroundDegree() {
        return AngleToGroundDegree;
    }
}
