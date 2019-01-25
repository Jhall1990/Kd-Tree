import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;


public class KdTree {
    private int count;
    private KdTreeNode twoDTree = null;

    public KdTree() {
        // construct an empty set of points
        count = 0;

    }

    public boolean isEmpty() {
        // is the set empty?
        return count == 0;
    }

    public int size() {
        // number of points in the set
        return count;
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        if (twoDTree == null) {
            twoDTree = new KdTreeNode(p);
            count++;
        } else {
            KdTreeNode curNode = twoDTree;

            while (curNode != null) {
                if (curNode.point.compareTo(p) > 0)
                    curNode = curNode.right;
                else
                    curNode = curNode.left;
            }

            curNode = new KdTreeNode(p);
        }

    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        return false;
    }

    public void draw() {
        // draw all points to standard draw
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary)
        return new ArrayList<Point2D>();
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty
        return new Point2D(1, 1);
    }

    public static void main(String[] args) {
        // unit testing of the methods (optional)
    }

    private class KdTreeNode {
        private final Point2D point;
        private final KdTreeNode left;
        private final KdTreeNode right;

        KdTreeNode(Point2D point) {
            this.point = point;
            this.left = null;
            this.right = null;
        }
    }
}
