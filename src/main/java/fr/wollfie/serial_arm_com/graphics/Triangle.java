package fr.wollfie.serial_arm_com.graphics;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.function.Function;

public class Triangle extends Shape {

    private Point2D a;
    private Point2D b;
    private Point2D c;

    public Triangle(Point2D a, Point2D b, Point2D c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    private double sign (Point2D p1, Point2D p2, Point2D p3)
    {
        return (p1.getX() - p3.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p3.getY());
    }

    public boolean contains(Point2D pt)
    {
        double d1, d2, d3;
        boolean hasNeg, hasPos;

        d1 = sign(pt, a, b);
        d2 = sign(pt, b, c);
        d3 = sign(pt, c, a);

        hasNeg = (d1 <= 0) || (d2 <= 0) || (d3 <= 0);
        hasPos = (d1 >= 0) || (d2 >= 0) || (d3 >= 0);

        return !(hasNeg && hasPos);
    }

    public double[] getXs() {
        return new double[]{a.getX(), b.getX(), c.getX()};
    }

    public double[] getYs() {
        return new double[]{a.getY(), b.getY(), c.getY()};
    }

    public Point2D project(Point2D p) {

        Point2D[] trianglePoints = new Point2D[]{a, b, c};

        Point2D minPoint = null;
        double minDist = Double.MAX_VALUE;

        for (int i = 0; i < trianglePoints.length; i++) {
            Point2D projection = projectOnLine(trianglePoints[i], trianglePoints[(i+1) % trianglePoints.length], p);
            double distance = projection.distance(p);

            if (distance < minDist) {
                minDist = distance;
                minPoint = projection;
            }
        }

        return minPoint;
    }

    private Point2D projectOnLine(Point2D a, Point2D b, Point2D p) {
        Point2D ap = p.subtract(a);
        Point2D ab = b.subtract(a);
        return a.add(ab.multiply(ap.dotProduct(ab) / ab.dotProduct(ab)));
    }
}
