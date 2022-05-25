package fr.wollfie.serial_arm_com.movement_sequence;

import fr.wollfie.serial_arm_com.apps.components.BezierAnchorConfigurator;
import fr.wollfie.serial_arm_com.maths.RobotArmController;
import fr.wollfie.serial_arm_com.sim.VirtualServoControl;
import fr.wollfie.serial_arm_com.utils.Utils3D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.io.*;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterpolatedAnimation extends MovementAnimation{

    private static final int INTERPOLATION_POINTS = 10;
    private static final Color CURVE_COLOR = new Color(255/255.0, 4/255.0, 222/255.0, 1);
    private VirtualServoControl virtualServoControl;
    private RobotArmController controller;

    private InterpolatedAnimation(MovementAnimation animation, VirtualServoControl virtualServoControl,
                                  RobotArmController controller) {
        super(animation.getName() + "_interpolated", interpolate(animation),
                animation.getFrameRate() * INTERPOLATION_POINTS);
        this.controller = controller;
        this.virtualServoControl = virtualServoControl;
    }

    @Override
    public void save() {
        try {
            throw new IllegalAccessException("An interpolated animation should not be saved");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MovementFrame[] interpolate(MovementAnimation animation) {
        List<MovementFrame> frames = new ArrayList<>();
        int nbFrames = animation.getFrameCount();

        for (int i = 0; i < animation.getFrameCount()-2; i++) {
            Point3D[] interpolation = BezierAnchorConfigurator.interpolate(
                    animation.getFrame(i).getPolarGripPosition(),
                    animation.getFrame(i+1).getPolarGripPosition(),
                    animation.getFrame(i+2).getPolarGripPosition()
                    , INTERPOLATION_POINTS, false);

            for (int j = 0; j < interpolation.length; j++) {
                frames.add(MovementFrame.createFrom(animation.getFrame(i), interpolation[j]));
            }
        }

        Point3D[] interpolation = BezierAnchorConfigurator.interpolate(
                animation.getFrame(nbFrames-3).getPolarGripPosition(),
                animation.getFrame(nbFrames-2).getPolarGripPosition(),
                animation.getFrame(nbFrames-1).getPolarGripPosition()
                , INTERPOLATION_POINTS, true);

        for (Point3D point3D : interpolation) {
            frames.add(MovementFrame.createFrom(animation.getFrame(nbFrames - 2), point3D));
        }



        return frames.toArray(new MovementFrame[0]);
    }

    public static InterpolatedAnimation of(MovementAnimation animation, VirtualServoControl virtualServoControl,
                                           RobotArmController controller) {
        return new InterpolatedAnimation(animation, virtualServoControl, controller);
    }

    public Cylinder[] draw() {
        Cylinder[] cylinders = new Cylinder[this.getFrameCount()-1];

        for (int i = 0; i < this.getFrameCount()-1; i++) {
            Point3D src = this.getFrame(i).getPolarGripPosition();
            Point3D dest = this.getFrame(i+1).getPolarGripPosition();


            cylinders[i] = Utils3D.createConnection(
                    Utils3D.toCartesian(src),
                    Utils3D.toCartesian(dest));
            cylinders[i].setMaterial(new PhongMaterial(CURVE_COLOR));
        }
        System.out.println(Arrays.toString(cylinders));

        return cylinders;
    }

    public void export() {
        String path = MovementAnimation.path;
        File animDir = new File(path);

        if (!(animDir.exists() || animDir.mkdirs())) {
            try {
                throw new IOException("Path could not be created and doesn't exist : " + String.format("%s", animDir.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String fileName = "arduinoready_" + this.getName() + ".txt";
        animDir = new File(animDir.getPath() + File.separator + fileName);
        try(PrintWriter fileIn = new PrintWriter(new BufferedOutputStream(new FileOutputStream(animDir)))) {
            fileIn.write(this.getArduinoReadyInstructions());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getArduinoReadyInstructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("double frames[").append(this.getFrameCount()).append("][5] = ");
        sb.append("{\n");
        for (int i = 0; i < this.getFrameCount(); i++) {
            this.controller.update(this.getFrame(i), false);
            String arduinoReadyFrame = this.virtualServoControl.getArduinoReadyAngles();
            sb.append("\t").append(arduinoReadyFrame);
            if (i != this.getFrameCount()-1) {
                sb.append(",\n");
            }
        }
        sb.append("};");
        return sb.toString();
    }
}
