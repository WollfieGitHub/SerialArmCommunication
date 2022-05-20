module org.example {
    requires javafx.controls;
    requires com.fazecast.jSerialComm;
    requires org.jetbrains.annotations;
    exports fr.wollfie.serial_arm_com;
    exports fr.wollfie.serial_arm_com.maths;
    exports fr.wollfie.serial_arm_com.apps;
    exports fr.wollfie.serial_arm_com.sim;
    exports fr.wollfie.serial_arm_com.movement_sequence;
    exports fr.wollfie.serial_arm_com.mechanism;
    exports fr.wollfie.serial_arm_com.graphics;
}