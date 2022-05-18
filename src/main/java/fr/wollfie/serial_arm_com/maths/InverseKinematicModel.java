package fr.wollfie.serial_arm_com.maths;

import fr.wollfie.serial_arm_com.apps.ArmPart;
import fr.wollfie.serial_arm_com.apps.ArmPartDrawer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public final class InverseKinematicModel {

    private final double l1;
    private final double l2;
    private final double l3;

    private final ArmPartDrawer bicepsDrawer;
    private final ArmPartDrawer forearmDrawer;
    private final ArmPartDrawer handDrawer;

    private InverseKinematicModel(double l1, double l2, double l3) {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;

        bicepsDrawer = ArmPartDrawer.with(
                new Color(0, 0.5, 0.5, 1.0),
                ArmPart.buildNew(l1, null));

        forearmDrawer = ArmPartDrawer.with(
                new Color(0.3, 0.3, 0.7, 1.0),
                ArmPart.buildNew(l2, bicepsDrawer.getArmPart()));

        handDrawer = ArmPartDrawer.with(
                new Color(0.6, 0.1, 0.9, 1.0),
                ArmPart.buildNew(l3, forearmDrawer.getArmPart()));
    }

    public static InverseKinematicModel of(double l1, double l2, double l3) {
        return new InverseKinematicModel(l1, l2, l3);
    }

    private double cosCalc(double a, double b, double c) {
        return Math.acos((a*a + b*b - c*c)/(2*a*b));
    }

    public void apply(double x, double y, double angleToGroundDeg) {
        apply(x, y, angleToGroundDeg, true);
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

    public void drawOn(GraphicsContext ctx) {
        bicepsDrawer.drawOn(ctx);
        forearmDrawer.drawOn(ctx);
        handDrawer.drawOn(ctx);
    }

}