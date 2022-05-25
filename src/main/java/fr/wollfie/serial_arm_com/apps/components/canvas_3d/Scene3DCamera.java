package fr.wollfie.serial_arm_com.apps.components.canvas_3d;

import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.transform.Rotate;

public class Scene3DCamera {

    private static final double CAMERA_INITIAL_DISTANCE = -500;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    public static PerspectiveCamera buildCamera(double dimension) {
        PerspectiveCamera camera = new PerspectiveCamera();
        Point3D cameraPosition = new Point3D(dimension, dimension, dimension);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);

        camera.setTranslateX(cameraPosition.getX());
        camera.setTranslateY(cameraPosition.getY());
        camera.setTranslateZ(cameraPosition.getZ());

        Scene3D.matrixRotateNode(camera, -45, 45, 0, cameraPosition);

        return camera;
    }
}
