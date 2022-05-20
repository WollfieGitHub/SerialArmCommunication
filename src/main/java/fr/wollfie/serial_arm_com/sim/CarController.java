package fr.wollfie.serial_arm_com.sim;

import fr.wollfie.serial_arm_com.mechanism.ServoControl;

public class CarController {

    private final ServoControl servoControl;

    public CarController(ServoControl servoControl) {
        this.servoControl = servoControl;
    }


    public void update(int motorASpeed, int motorBSpeed) {
        servoControl.writeSpeed(motorASpeed, motorBSpeed);
    }
}
