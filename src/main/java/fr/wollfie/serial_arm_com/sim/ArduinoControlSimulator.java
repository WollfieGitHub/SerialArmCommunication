package fr.wollfie.serial_arm_com.sim;

import fr.wollfie.serial_arm_com.graphics.ArmPartDrawer;
import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class ArduinoControlSimulator {

    private final ArduinoServo BaseServo = new ArduinoServo(0);
    private final ArduinoServo ShoulderServo = new ArduinoServo(0);
    private final ArduinoServo ElbowServo = new ArduinoServo(0);
    private final ArduinoServo WristServo = new ArduinoServo(0);
    private final ArduinoServo GripServo = new ArduinoServo(0);

    public static double STEP_SIZE = 0.05;
    public static long UPDATE_DELAY_MS = 30;

    private static final double ARM_DRAWING_TRANSPARENCY = 1.0;
    private long lastUpdateMs = System.currentTimeMillis();

    private final ArmPartDrawer bicepsDrawer;
    private final ArmPartDrawer forearmDrawer;
    private final ArmPartDrawer handDrawer;


    public ArduinoControlSimulator(double l1, double l2, double l3) {
        Thread thread = new Thread(this::run);

        bicepsDrawer = ArmPartDrawer.with(
                new Color(0, 0.5, 0.5, ARM_DRAWING_TRANSPARENCY),
                ArmPart.buildNew(l1, null));

        forearmDrawer = ArmPartDrawer.with(
                new Color(0.3, 0.3, 0.7, ARM_DRAWING_TRANSPARENCY),
                ArmPart.buildNew(l2, bicepsDrawer.getArmPart()));

        handDrawer = ArmPartDrawer.with(
                new Color(0.6, 0.1, 0.9, ARM_DRAWING_TRANSPARENCY),
                ArmPart.buildNew(l3, forearmDrawer.getArmPart()));

        thread.start();
    }

    private void run() {
        while (true) {
            if (System.currentTimeMillis() - lastUpdateMs >= UPDATE_DELAY_MS) {
                update();
                lastUpdateMs = System.currentTimeMillis();
            }
        }
    }

    public void write(double baseDeg, double shoulderDeg, double elbowDeg, double wristDeg, double gripDeg) {
        BaseServo.turnTo(baseDeg);
        ShoulderServo.turnTo(shoulderDeg);
        ElbowServo.turnTo(elbowDeg);
        WristServo.turnTo(wristDeg);
        GripServo.turnTo(gripDeg);
    }

    private void update() {
        BaseServo.update();
        ShoulderServo.update();
        ElbowServo.update();
        WristServo.update();
        GripServo.update();

        bicepsDrawer.getArmPart().rotateToAngleRad(ShoulderServo.getCurrentAngleRad());
        forearmDrawer.getArmPart().rotateToAngleRad(ElbowServo.getCurrentAngleRad());
        handDrawer.getArmPart().rotateToAngleRad(WristServo.getCurrentAngleRad());
    }

    public void drawOn(GraphicsContext ctx) {
        bicepsDrawer.drawOn(ctx);
        forearmDrawer.drawOn(ctx);
        handDrawer.drawOn(ctx);
    }

    public void rotDrawOn(GraphicsContext ctx, double radius) {
        Point2D end = new Point2D(0, radius);

        Rotate rotate = Transform.rotate(270-Math.toDegrees(BaseServo.getCurrentAngleRad()), 0, 0);
        end = rotate.transform(end);

        ctx.strokeLine(0, 0, end.getX(), end.getY());
    }

    public void gripDrawOn(GraphicsContext ctx, double radius) {
        Point2D end = new Point2D(0, radius);

        Rotate rotateRight = Transform.rotate(180+ (Math.toDegrees(GripServo.getCurrentAngleRad())-60)
                * InverseKinApp.ANGLE_TO_GRIP_RATIO, 0, 0);

        Rotate rotateLeft = Transform.rotate(180- (Math.toDegrees(GripServo.getCurrentAngleRad())-60)
                * InverseKinApp.ANGLE_TO_GRIP_RATIO, 0, 0);

        Point2D endRight = rotateRight.transform(end);
        Point2D endLeft = rotateLeft.transform(end);

        ctx.strokeLine(0, 0, endRight.getX(), endRight.getY());
        ctx.strokeLine(0, 0, endLeft.getX(), endLeft.getY());
    }
}
