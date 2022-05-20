package fr.wollfie.serial_arm_com.graphics.canvases;

import fr.wollfie.serial_arm_com.sim.ArmSimulation;
import fr.wollfie.serial_arm_com.maths.RobotArmController;
import fr.wollfie.serial_arm_com.mechanism.ServoControl;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static fr.wollfie.serial_arm_com.graphics.canvases.LittleCanvas.LITTLE_CANVAS_DIM;

public class GraphicHandler {

    private final GraphicsContext ArmCtx;
    private final GraphicsContext RotationCtx;
    private final GraphicsContext GripCtx;

    private final RobotArmController ArmController;
    private final ServoControl ServoControl;

    private final ArmSimulationCanvas ArmCanvas;
    private final RotationSimulationCanvas RotationCanvas;
    private final GripSimulationCanvas GripCanvas;
    private final CarCanvas CarCanvas;
    private final GraphicsContext CarCtx;

    public GraphicHandler(RobotArmController armController, ServoControl servoControl, ArmSimulation armSimulation) {
        this.ArmController = armController;
        this.ServoControl = servoControl;

        this.ArmCanvas = new ArmSimulationCanvas(armController, armSimulation);
        this.ArmCtx = ArmCanvas.getGraphicsContext2D();

        this.RotationCanvas = new RotationSimulationCanvas();
        this.RotationCtx = RotationCanvas.getCanvas().getGraphicsContext2D();

        this.GripCanvas = new GripSimulationCanvas();
        this.GripCtx = GripCanvas.getCanvas().getGraphicsContext2D();

        this.CarCanvas = new CarCanvas(armSimulation);
        this.CarCtx = this.CarCanvas.getCanvas().getGraphicsContext2D();
    }

    public void updateArm() {

        final double DrawingDim = ArmController.getMaxRange();

        ArmCtx.clearRect(-DrawingDim, -DrawingDim, 2*DrawingDim, 2*DrawingDim);

        ArmCtx.setStroke(new Color(1, 0, 0, 1));
        ArmCtx.setLineWidth(1/ ArmCanvas.ScaleFactor);

        ArmCtx.strokeOval(
                -ArmController.getMaxRange(),
                -ArmController.getMaxRange(),
                ArmController.getMaxRange()*2,
                ArmController.getMaxRange()*2);

        ArmCtx.setLineWidth(2/ ArmCanvas.ScaleFactor);

        ArmController.drawArmOn(ArmCtx);
        ServoControl.simDrawArmOn(ArmCtx);

        // ROTATION
        RotationCanvas.resetCanvas();
        ServoControl.simDrawRotOn(RotationCtx, LITTLE_CANVAS_DIM/2.0);

        // GRIP
        GripCanvas.resetCanvas();
        ServoControl.simDrawGripOn(GripCtx, LITTLE_CANVAS_DIM/2.0);

        // CAR
        CarCanvas.drawCanvas();

    }



    public Canvas getArmCanvas() {
        return ArmCanvas.getCanvas();
    }

    public Canvas getRotCanvas() {
        return RotationCanvas.getCanvas();
    }

    public Canvas getGripCanvas() {
        return GripCanvas.getCanvas();
    }

    public Canvas getCarCanvas() {
        return CarCanvas.getCanvas();
    }
}
