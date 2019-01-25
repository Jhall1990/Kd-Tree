import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> rbBst;

    public PointSET() {
        // construct an empty set of points
        rbBst = new TreeSet<>();
    }

    public boolean isEmpty() {
        // is the set empty?
        return rbBst.isEmpty();
    }

    public int size() {
        // number of points in the set
        return rbBst.size();
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        if (p == null)
            throw new IllegalArgumentException("p cannot be null");

        rbBst.add(p);
    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        if (p == null)
            throw new IllegalArgumentException("p cannot be null");

        if (isEmpty())
            return false;

        return rbBst.contains(p);
    }

    public void draw() {
        // draw all points to standard draw
        for (Point2D p : rbBst)
            p.draw();
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary)
        if (rect == null)
            throw new IllegalArgumentException("rect cannot be null");

        ArrayList<Point2D> pointsInRect = new ArrayList<>();

        for (Point2D p : rbBst) {
            if (p.x() >= rect.xmin() && p.x() <= rect.xmax())
                if (p.y() >= rect.ymin() && p.y() <= rect.ymax())
                    pointsInRect.add(p);
        }

        return pointsInRect;
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new IllegalArgumentException("p cannot be null");
        
        if (isEmpty())
            return null;

        double distance = p.distanceSquaredTo(rbBst.first());
        Point2D closest = rbBst.first();

        for (Point2D point : rbBst) {
            double tmpDis = point.distanceSquaredTo(p);
            if (tmpDis < distance) {
                closest = point;
                distance = tmpDis;
            }
        }

        return closest;
    }

    public static void main(String[] args) {
        // unit testing of the methods (optional)
    }
}
