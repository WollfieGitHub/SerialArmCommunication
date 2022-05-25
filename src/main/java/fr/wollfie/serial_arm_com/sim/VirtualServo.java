package fr.wollfie.serial_arm_com.sim;

import fr.wollfie.serial_arm_com.mechanism.Servo;

public class VirtualServo extends Servo {

    public VirtualServo(int minPulse, int maxPulse, double mountedAngleRad, boolean changeDirection) {
        super(minPulse, maxPulse, mountedAngleRad, changeDirection);
    }


}
