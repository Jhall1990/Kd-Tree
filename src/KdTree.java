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
        if (p == null)
            throw new IllegalArgumentException("p cannot be null");

        // Recursion base case. If the current node is null, add a new node and
        // increment the counter.
        if (node == null) {
            count++;
            return new KdTreeNode(p, vert);
        }

        // The node is already in the tree.
        if (Double.compare(p.x(), node.point.x()) == 0 && Double.compare(p.y(), node.point.y()) == 0)
            return node;

        // If current node is a vertical node compare the node's x val against the new points x.
        // If the point's x is lower recurse on the left node, otherwise use the right. If it's
        // a horizontal node compare the y coords. If the point's y is less than the nodes recurse
        // on the left, otherwise the right.
        if ((vert && p.x() < node.point.x()) || !vert && p.y() < node.point.y())
            node.left = insert(node.left, p, !node.vertical);
        else
            node.right = insert(node.right, p, !node.vertical);

        return node;
    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        if (p == null)
            throw new IllegalArgumentException("p cannot be null");

        return contains(root, p) != null;
    }

    private KdTreeNode contains(KdTreeNode node, Point2D p) {
        // Check if we reached the end of the tree.
        if (node == null) {
            return null;
        }

        // Recursion base case. Compare the current node's point to the search point.
        if (node.point.compareTo(p) == 0)
            return node;

        // Recurse on the current node's left or right node after comparing the x|y coords.
        // The comparison is the same as the one described in "insert".
        if (node.vertical && p.x() < node.point.x() || !node.vertical && p.y() < node.point.y())
            return contains(node.left, p);
        else
            return contains(node.right, p);
    }

    public void draw() {
        // draw all points to standard draw
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary)
        if (rect == null)
            throw new IllegalArgumentException("rect cannot be null");

        return range(root, new ArrayList<>(), rect);
    }

    private Iterable<Point2D> range(KdTreeNode node, ArrayList<Point2D> pointArray, RectHV rect) {
        if (node == null)
            return pointArray;

        // Check if the current node is in the rectangle.
        if (rect.contains(node.point))
            pointArray.add(node.point);

        // There are three situations we need to check for. We either check just left, just right, or both.
        // We check only left when the point's x is greater than the rect's xmax when vertical. If it's
        // horizontal we check that the point's y is less than the rec's ymin. Then we do the opposite for
        // the right side. If neither of these are true, check both left and right.
        if ((node.vertical && node.point.x() > rect.xmax()) || !node.vertical && node.point.y() > rect.ymax())
            range(node.left, pointArray, rect);
        else if ((node.vertical && node.point.x() < rect.xmin() || !node.vertical && node.point.y() < rect.ymin()))
            range(node.right, pointArray, rect);
        else {
            range(node.left, pointArray, rect);
            range(node.right, pointArray, rect);
        }

        return pointArray;
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) {
            throw new IllegalArgumentException("p cannot be null");
        }

        if (root == null)
            return null;

        return nearest(root, root.point, p, new RectHV(0, 0, 1, 1));
    }

    private Point2D nearest(KdTreeNode node, Point2D closest, Point2D point, RectHV parentRect) {
        // Recursion base case. Return if we reach the end of a branch.
        if (node == null)
            return closest;

        // Check to see if the current node is closer.
        if (point.distanceSquaredTo(node.point) < point.distanceSquaredTo(closest)) {
            closest = node.point;
        }


        // If the current node has two children we'll need to check both (possibly).
        if (node.left != null && node.right != null) {
            // First create rectangles for each child node.
            RectHV leftRect = getNodeRect(node.left, node, parentRect, true);
            RectHV rightRect = getNodeRect(node.right, node, parentRect, false);

            // Then check if the search point is closer to the left child, or the right.
            if (leftRect.distanceSquaredTo(point) < rightRect.distanceSquaredTo(point)) {
                closest = nearest(node.left, closest, point, leftRect);

                // After checking the left node see if we should still check the right.
                // We don't need to check the right node if the closest point is closer
                // than the right rectangle.
                if (rightRect.distanceSquaredTo(point) < point.distanceSquaredTo(closest))
                    closest = nearest(node.right, closest, point, rightRect);
            } else {
                // Do the same thing if the right node is closer. Check the right side then
                // check the left if needed.
                closest = nearest(node.right, closest, point, rightRect);

                if (leftRect.distanceSquaredTo(point) < point.distanceSquaredTo(closest))
                    closest = nearest(node.left, closest, point, leftRect);
            }
        // If the right node is null we'll only need to check the left. Right create the
        // rectangle then only check the left node if the rectangle is closer than the
        // current closest point.
        } else if (node.left != null) {
            RectHV leftRect = getNodeRect(node.left, node, parentRect, true);
            if (leftRect.distanceSquaredTo(point) < point.distanceSquaredTo(closest))
                closest = nearest(node.left, closest, point, leftRect);
        // Do the same with the node's right child if the left node is null.
        } else if (node.right != null) {
            RectHV rightRect = getNodeRect(node.right, node, parentRect, false);
            if (rightRect.distanceSquaredTo(point) < point.distanceSquaredTo(closest))
                closest = nearest(node.right, closest, point, rightRect);
        }

        return closest;
    }

    private RectHV getNodeRect(KdTreeNode child, KdTreeNode parent, RectHV parentRect, boolean left) {
        // Creates a rectangle based on the child node, parent node, parent rectangle and a boolean
        // indicating whether the node is the left child or right.
        //
        // Uses the following logic to create
        //     horizontal left
        //        xmin - parent rect xmin
        //        xmax - parent point x
        //        ymin - parent rect ymin
        //        ymax - parent rect ymax
        //
        //     horizontal right
        //        xmin - parent point x
        //        xmax - parent rect xmax
        //        ymin - parent rect ymin
        //        ymax - parent rect ymax
        //
        //     vertical left
        //        xmin - parent rect xmin
        //        xmax - parent rect xmax
        //        ymin - parent rect ymin
        //        ymax - parent point y
        //
        //     vertical right
        //        xmin - parent rect xmin
        //        xmax - parent rect xmax
        //        ymin - parent point y
        //        ymax - parent rect ymax

        if (!child.vertical && left) {
            return new RectHV(parentRect.xmin(), parentRect.ymin(), parent.point.x(), parentRect.ymax());
        } else if (!child.vertical && !left) {
            return new RectHV(parent.point.x(), parentRect.ymin(), parentRect.xmax(), parentRect.ymax());
        } else if (child.vertical && left) {
            return new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), parent.point.y());
        } else {
            return new RectHV(parentRect.xmin(), parent.point.y(), parentRect.xmax(), parentRect.ymax());
        }
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
