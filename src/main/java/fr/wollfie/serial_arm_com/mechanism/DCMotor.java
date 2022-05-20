package fr.wollfie.serial_arm_com.mechanism;

public final class DCMotor {

    private int speed;

    public DCMotor() {
        this.speed = 0;
    }

    public void setSpeed(int speed) {
        if (0 <= speed && speed <= 255) {
            this.speed = speed;
        }
    }

    public int getSpeed() {
        return speed;
    }
}
