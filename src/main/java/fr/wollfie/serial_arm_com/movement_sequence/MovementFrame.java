package fr.wollfie.serial_arm_com.movement_sequence;

import fr.wollfie.serial_arm_com.maths.RobotArmController;
import javafx.geometry.Point3D;
import org.json.JSONObject;

import java.io.Serializable;

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

    public static MovementFrame createFrom(MovementFrame frame, Point3D gripPosition) {
        return new MovementFrame(
                gripPosition,
                frame.GripOpeningDegree,
                frame.AngleToGroundDegree
        );
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

    public static MovementFrame deserialize(JSONObject json) {
        return new MovementFrame(
                deserializePoint(json.getJSONObject("polarGripPos")),
                json.getDouble("gripOpening"),
                json.getDouble("angleToGround")
        );
    }

    public JSONObject serialize() {
        return new JSONObject()
                .put("gripOpening", this.GripOpeningDegree)
                .put("angleToGround", this.AngleToGroundDegree)
                .put("polarGripPos", this.serializePoint(this.PolarGripPosition));
    }

    private static Point3D deserializePoint(JSONObject jsonObject) {
        return new Point3D(
                jsonObject.getDouble("x"),
                jsonObject.getDouble("y"),
                jsonObject.getDouble("z")
        );
    }

    private JSONObject serializePoint(Point3D point) {
        return new JSONObject()
                .put("x", point.getX())
                .put("y", point.getY())
                .put("z", point.getZ());
    }
}
