package fr.wollfie.serial_arm_com.apps.components;

import javafx.geometry.Point3D;

import java.util.Arrays;
import java.util.function.Function;

public class BezierAnchorConfigurator {

    public static Point3D[] interpolate(Point3D origin, Point3D anchor, Point3D dest, int nbPoints, boolean retainSecondHalf) {
        Function<Double, Point3D> curveEquation = t ->
                anchor.add(origin.subtract(anchor).multiply(Math.pow(1 - t, 2)))
                        .add(dest.subtract(anchor).multiply(Math.pow(t, 2)));

        double step = 1.0 / nbPoints;
        // Start past origin
        double currentStep = 0.0;

        Point3D[] points = new Point3D[nbPoints];
        for (int i = 0; i < nbPoints; i++) {
            points[i] = curveEquation.apply(currentStep);
            currentStep += step;
        }

        Point3D[] half = new Point3D[nbPoints/2];
        System.arraycopy(points, (retainSecondHalf ? nbPoints/2 : 0), half, 0, nbPoints/2);

        return half;
    }

}
