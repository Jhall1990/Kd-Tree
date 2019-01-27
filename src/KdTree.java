import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;


public class KdTree {
    private int count;
    private KdTreeNode root = null;

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
        root = insert(root, p, true);
    }

    private KdTreeNode insert(KdTreeNode node, Point2D p, boolean vert) {
        if (node == null) {
            count++;
            return new KdTreeNode(p, vert);
        }

        // The node is already in the tree.
        if (Double.compare(p.x(), node.point.x()) == 0 && Double.compare(p.y(), node.point.y()) == 0)
            return node;

        if ((vert && p.x() < node.point.x()) || !vert && p.y() < node.point.y())
            node.left = insert(node.left, p, !node.vertical);
        else
            node.right = insert(node.right, p, !node.vertical);

        return node;
    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        return contains(root, p) != null;
    }

    private KdTreeNode contains(KdTreeNode node, Point2D p) {
        // Check if we reached the end of the tree.
        if (node == null) {
            return null;
        }

        // Compare the current node's point to the search point.
        int comp = node.point.compareTo(p);

        if (comp == 0)
            return node;
        else if (comp < 0)
            return contains(node.left, p);
        else
            return contains(node.right, p);
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
        private KdTreeNode left;
        private KdTreeNode right;
        private final boolean vertical;

        KdTreeNode(Point2D point, boolean vertical) {
            this.point = point;
            this.left = null;
            this.right = null;
            this.vertical = vertical;
        }
    }
}
