package fr.wollfie.serial_arm_com.sim;

import fr.wollfie.serial_arm_com.maths.RobotArmController;

public class ArmSimulation {

    public double phiE = 0;
    public double x;
    public double y;
    public boolean elbowUp = true;

    public double baseRotationDeg;
    public double gripOpeningDeg;

    public int motorASpeed;
    public int motorBSpeed;

    private final RobotArmController ArmController;
    private final CarController CarController;

    public ArmSimulation(RobotArmController armController, CarController carController) {
        this.ArmController = armController;
        this.CarController = carController;

        y = ArmController.getMaxRange() * 2 / 3.0;
        x = ArmController.getMaxRange() * 2 / 3.0;
    }

    public void update() {
        this.ArmController.update(x, y, Math.toDegrees(phiE), elbowUp, gripOpeningDeg, baseRotationDeg);
        this.CarController.update(motorASpeed, motorBSpeed);
    }
}
