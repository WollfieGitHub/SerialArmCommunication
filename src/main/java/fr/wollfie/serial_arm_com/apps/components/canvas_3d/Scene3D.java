package fr.wollfie.serial_arm_com.apps.components.canvas_3d;

import fr.wollfie.serial_arm_com.maths.RobotArmController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class Scene3D {

    private final PerspectiveCamera camera;
    private final Group rootGroup;
    private final SimpleObjectProperty<Transform> canvasTransform = new SimpleObjectProperty<>();

    private final SubScene rootScene;
    private final RobotArmController robotController;
    private final int dimension;

    private int rootSizeBase;

    public Scene3D(int dimension, RobotArmController robotController) {
        this.dimension = dimension;
        double spaceDimension = robotController.getMaxRange()*2;
        this.robotController = robotController;
        this.camera = Scene3DCamera.buildCamera(spaceDimension/2.0);
        this.rootGroup = new Group();

        this.rootScene = new SubScene(rootGroup, dimension, dimension);
        rootScene.setCamera(camera);
        rootScene.setFill(new Color(200/255.0, 200/255.0, 200/255.0, 1));

        canvasTransform.setValue(Transform.affine(
                -dimension/spaceDimension, 0, 0, dimension/2.0,
                0, dimension/spaceDimension, 0, dimension/1.5,
                0, 0, dimension/spaceDimension, -dimension/4.0
        ));

        Sphere origin = new Sphere(1);

        Box xAxis = new Box(spaceDimension, 0.2, 0.2);
        xAxis.setMaterial(new PhongMaterial(new Color(1, 0, 0, 1)));

        Box yAxis = new Box(0.2, spaceDimension, 0.2);
        yAxis.setMaterial(new PhongMaterial(new Color(0, 0, 1, 1)));

        Box zAxis = new Box(0.2, 0.2, spaceDimension);
        zAxis.setMaterial(new PhongMaterial(new Color(0, 1, 0, 1)));

        rootGroup.getTransforms().add(canvasTransform.getValue());
        rootGroup.getChildren().addAll(origin, xAxis, yAxis, zAxis, robotController.get3DRepresentations());
        this.rootSizeBase = rootGroup.getChildren().size();
    }

    public enum Axis {
        NONE, X, Y, Z
    }

    public SubScene getRootScene() {
        return rootScene;
    }

    public static void matrixRotateNode(Node n, double yawDeg, double pitchDeg, double rollDeg, Point3D pivot){
        double yaw = Math.toRadians(yawDeg);
        double pitch = Math.toRadians(pitchDeg);
        double roll = Math.toRadians(rollDeg);
        double A11=Math.cos(roll)*Math.cos(yaw);
        double A12=Math.cos(pitch)*Math.sin(roll)+Math.cos(roll)*Math.sin(pitch)*Math.sin(yaw);
        double A13=Math.sin(roll)*Math.sin(pitch)-Math.cos(roll)*Math.cos(pitch)*Math.sin(yaw);
        double A21=-Math.cos(yaw)*Math.sin(roll);
        double A22=Math.cos(roll)*Math.cos(pitch)-Math.sin(roll)*Math.sin(pitch)*Math.sin(yaw);
        double A23=Math.cos(roll)*Math.sin(pitch)+Math.cos(pitch)*Math.sin(roll)*Math.sin(yaw);
        double A31=Math.sin(yaw);
        double A32=-Math.cos(yaw)*Math.sin(pitch);
        double A33=Math.cos(pitch)*Math.cos(yaw);

        double d = Math.acos((A11+A22+A33-1d)/2d);
        if(d!=0d){
            double den=2d*Math.sin(d);
            Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);

            n.setTranslateX(-pivot.getX());
            n.setTranslateY(-pivot.getY());
            n.setTranslateZ(-pivot.getZ());

            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));

            n.setTranslateX(pivot.getX());
            n.setTranslateY(pivot.getY());
            n.setTranslateZ(pivot.getZ());
        }
    }

    public void addTrajectory(Group trajectory) {
        int newSize = this.rootGroup.getChildren().size();
        for (int i = this.rootSizeBase; i < newSize; i++) {
            this.rootGroup.getChildren().remove(i);
        }
        this.rootGroup.getChildren().add(trajectory);
        trajectory.setRotationAxis(Rotate.X_AXIS);
        trajectory.setRotate(-90);

        trajectory.setTranslateY(0);
        trajectory.setTranslateX(-robotController.getMaxRange() * (2.0/3.0));
        trajectory.setTranslateZ(0);
    }
}
