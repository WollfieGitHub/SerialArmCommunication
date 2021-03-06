package fr.wollfie.serial_arm_com.maths;

import fr.wollfie.serial_arm_com.sim.ArmPart;
import fr.wollfie.serial_arm_com.graphics.ArmPartDrawer;
import fr.wollfie.serial_arm_com.mechanism.ServoControl;
import fr.wollfie.serial_arm_com.movement_sequence.MovementFrame;
import fr.wollfie.serial_arm_com.sim.VirtualServo;
import fr.wollfie.serial_arm_com.sim.VirtualServoControl;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jetbrains.annotations.Nullable;

public final class RobotArmController {

    private final double l1;
    private final double l2;
    private final double l3;

    private final ArmPartDrawer bicepsDrawer;
    private final ArmPartDrawer forearmDrawer;
    private final ArmPartDrawer handDrawer;

    private final ServoControl servoControl;
    private final Rotate rotateTransform = Transform.rotate(0, 0, 0);
    private final VirtualServoControl virtualServoControl;

    private Point3D gripPosition;
    private double gripOpeningDeg;
    private double angleToGroundDeg;

    private Group representation3D;
    private double baseAngleDeg;

    public void configWrist(double angleDeg) {
        this.handDrawer.getArmPart().rotateToAngleRad(Math.toRadians(angleDeg));
    }

    public Point3D getPolarGripPosition() {
        return gripPosition;
    }

    public double getGripOpeningDeg() {
        return gripOpeningDeg;
    }

    public double getAngleToGroundDeg() {
        return angleToGroundDeg;
    }

    private RobotArmController(double l1, double l2, double l3,
                               ServoControl servoControl, VirtualServoControl virtualServoControl) {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.servoControl = servoControl;
        this.virtualServoControl = virtualServoControl;

        bicepsDrawer = ArmPartDrawer.with(
                new Color(0, 0.5, 0.5, 0.3),
                ArmPart.buildNew(l1, null));

        forearmDrawer = ArmPartDrawer.with(
                new Color(0.3, 0.3, 0.7, 0.3),
                ArmPart.buildNew(l2, bicepsDrawer.getArmPart()));

        handDrawer = ArmPartDrawer.with(
                new Color(0.6, 0.1, 0.9, 0.3),
                ArmPart.buildNew(l3, forearmDrawer.getArmPart()));

        this.representation3D = new Group(
                bicepsDrawer.get3DRepresentation(),
                forearmDrawer.get3DRepresentation(),
                handDrawer.get3DRepresentation());

        this.representation3D.getTransforms().add(Transform.affine(
                1, 0, 0, 0,
                0, -1, 0, 0,
                0, 0, 1, 0
        ));

        this.representation3D.getTransforms().add(rotateTransform);
        rotateTransform.setAxis(Rotate.Y_AXIS);
    }

    public static RobotArmController of(double l1, double l2, double l3,
                                        @Nullable ServoControl servoControl, VirtualServoControl virtualServoControl) {
        return new RobotArmController(l1, l2, l3, servoControl, virtualServoControl);
    }

    private double cosCalc(double a, double b, double c) {
        return Math.acos((a*a + b*b - c*c)/(2*a*b));
    }

    public void apply(double x, double y, double angleToGroundDeg) {
        apply(x, y, angleToGroundDeg, true);
    }

    public void update(double x, double y, double angleToGroundDeg, boolean elbowUp,
                       double gripOpeningDeg, double baseRotationDeg, boolean sendToArm) {
        apply(x, y, angleToGroundDeg, elbowUp);

        if (servoControl != null && sendToArm) {
            servoControl.writeAnglesRad(
                    Math.toRadians(baseRotationDeg),
                    bicepsDrawer.getArmPart().getAngleRad(),
                    forearmDrawer.getArmPart().getAngleRad(),
                    handDrawer.getArmPart().getAngleRad(),
                    Math.toRadians(gripOpeningDeg)
            );

            gripPosition = new Point3D(x, Math.toRadians(baseRotationDeg), y);
            this.gripOpeningDeg = gripOpeningDeg;
            this.angleToGroundDeg = angleToGroundDeg;
            this.baseAngleDeg = baseRotationDeg;
        } else {
            this.virtualServoControl.writeAnglesRad(
                    Math.toRadians(baseRotationDeg),
                    bicepsDrawer.getArmPart().getAngleRad(),
                    forearmDrawer.getArmPart().getAngleRad(),
                    handDrawer.getArmPart().getAngleRad(),
                    Math.toRadians(gripOpeningDeg)
            );
        }
    }

    public void update(MovementFrame movementFrame, boolean sendToArm) {
        Point3D polar = movementFrame.getPolarGripPosition();
        double x = polar.getX();
        double rot = Math.toDegrees(polar.getY());
        double y = polar.getZ();
        this.update(x, y, movementFrame.getAngleToGroundDegree(),
                true, movementFrame.getGripOpeningDegree(), rot, sendToArm);
    }

    public void apply(double x, double y, double angleToGroundDeg, boolean elbowUp) {
        double phiE = Math.toRadians(angleToGroundDeg);

        double xw = x - l3 * Math.cos(phiE);
        double yw = y - l3 * Math.sin(phiE);
        double r = Math.sqrt(xw * xw + yw * yw);
        double gamma = cosCalc(r, l1, l2);

        double theta2 = Math.PI - cosCalc(l1, l2, r);
        double theta1 = Math.atan2(yw, xw) - gamma;
        double theta3 = phiE - theta1 - theta2;

        double finalTheta1;
        double finalTheta2;
        double finalTheta3;

        if (elbowUp) {
            finalTheta1 = theta1 + 2 * gamma;
            finalTheta2 = -theta2;
            finalTheta3 = theta3 + 2 * (theta2 - gamma);
        } else {
            finalTheta1 = theta1;
            finalTheta2 = theta2;
            finalTheta3 = theta3;
        }

        if (!Double.isNaN(finalTheta1) && !Double.isNaN(finalTheta2) && !Double.isNaN(finalTheta3)) {
            bicepsDrawer.getArmPart().rotateToAngleRad(finalTheta1);
            forearmDrawer.getArmPart().rotateToAngleRad(finalTheta2);
            handDrawer.getArmPart().rotateToAngleRad(finalTheta3);
        }
    }

    public double getMaxRange() {
        return l1 + l2 + l3;
    }

    public void drawArmOn(GraphicsContext ctx) {
        bicepsDrawer.drawOn(ctx);
        forearmDrawer.drawOn(ctx);
        handDrawer.drawOn(ctx);
    }

    public Group get3DRepresentations() {
        return representation3D;
    }

    public void update3DRepresentations() {
        this.bicepsDrawer.updateRepresentation3D(90);
        this.forearmDrawer.updateRepresentation3D(90);
        this.handDrawer.updateRepresentation3D(90);

        this.representation3D.setTranslateX(0);
        this.representation3D.setTranslateY(0);

        rotateTransform.setAngle(baseAngleDeg);

    }



}
