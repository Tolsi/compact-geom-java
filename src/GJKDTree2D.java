import java.util.*;

/**
 * A data structure for storing a great number of points. During construction
 * of the tree, median point in current coordinate is chosen for each step,
 * ensuring the final tree is balanced. The cost for retrieving a point is
 * O(log n).<br>
 * The cost for building the tree is O(n log^2 n), that can take some time for
 * large points sets.<br>
 * This implementation is semi-dynamic: points can be added, but can not be
 * removed.
 * @author dlegland
 *
 */
public class GJKDTree2D {
    //TODO: make GJKDTree2D implements GJPointSet2D
    public class Node{
        private GJPoint2D point;
        private Node left;
        private Node right;

        public Node(GJPoint2D point){
            this.point  = point;
            this.left   = null;
            this.right  = null;
        }

        public Node(GJPoint2D point, Node left, Node right){
            this.point  = point;
            this.left   = left;
            this.right  = right;
        }

        public GJPoint2D getPoint() {
            return point;
        }

        public Node getLeftChild() {
            return left;
        }

        public Node getRightChild() {
            return right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private class XComparator implements Comparator<GJPoint2D> {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(GJPoint2D p1, GJPoint2D p2){
            if (p1.x() < p2.x())
                return -1;
            if (p1.x() > p2.x())
                return +1;
            return Double.compare(p1.y(), p2.y());
   }
    }

    private class YComparator implements Comparator<GJPoint2D> {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(GJPoint2D p1, GJPoint2D p2){
            if(p1.y()<p2.y())
                return -1;
            if(p1.y()>p2.y())
                return +1;
            return Double.compare(p1.x(), p2.x());
        }
    }

    private Node root;

    private Comparator<GJPoint2D> xComparator;
    private Comparator<GJPoint2D> yComparator;

    public GJKDTree2D(ArrayList<GJPoint2D> points) {
        this.xComparator = new XComparator();
        this.yComparator = new YComparator();
        root = makeTree(points, 0);
    }

    private Node makeTree(List<GJPoint2D> points, int depth) {
        // Add a leaf
        if(points.size() == 0)
            return null;

        // select direction
        int dir = depth%2;

        // sort points according to i-th dimension
        if (dir == 0) {
            // Compare points based on their x-coordinate
            Collections.sort(points, xComparator);
        } else {
            // Compare points based on their x-coordinate
            Collections.sort(points, yComparator);
        }

        int n = points.size();
        int med = n/2;    // compute median

        return new Node(
                points.get(med),
                makeTree(points.subList(0, med), depth+1),
                makeTree(points.subList(med+1, n), depth+1));
    }

    public Node getRoot() {
        return root;
    }

    public boolean contains(GJPoint2D value){
        return contains(value, root, 0);
    }

    private boolean contains(GJPoint2D point, Node node, int depth){
        if(node==null) return false;

        // select direction
        int dir = depth%2;

        // sort points according to i-th dimension
        int res;
        if(dir==0){
            // Compare points based on their x-coordinate
            res = xComparator.compare(point, node.point);
        }else{
            // Compare points based on their x-coordinate
            res = yComparator.compare(point, node.point);
        }

        if(res<0)
            return contains(point, node.left, depth+1);
        if(res>0)
            return contains(point, node.right, depth+1);

        return true;
    }

    public Node getNode(GJPoint2D point) {
        return getNode(point, root, 0);
    }

    private Node getNode(GJPoint2D point, Node node, int depth){
        if(node==null) return null;
        // select direction
        int dir = depth%2;

        // sort points according to i-th dimension
        int res;
        if(dir==0){
            // Compare points based on their x-coordinate
            res = xComparator.compare(point, node.point);
        }else{
            // Compare points based on their x-coordinate
            res = yComparator.compare(point, node.point);
        }

        if(res<0)
            return getNode(point, node.left, depth+1);
        if(res>0)
            return getNode(point, node.right, depth+1);

        return node;
    }

    public void add(GJPoint2D point){
       add(point, root, 0);
    }

    private void add(GJPoint2D point, Node node, int depth) {
        // select direction
        int dir = depth%2;

        // sort points according to i-th dimension
        int res;
        if(dir==0){
            // Compare points based on their x-coordinate
            res = xComparator.compare(point, node.point);
        }else{
            // Compare points based on their x-coordinate
            res = yComparator.compare(point, node.point);
        }

        if(res<0){
            if(node.left==null)
                node.left = new Node(point);
            else
                add(point, node.left, depth+1);
        }
        if(res>0)
            if(node.right==null)
                node.right = new Node(point);
            else
                add(point, node.right, depth+1);
    }

    public Collection<GJPoint2D> rangeSearch(GJBox2D range) {
        ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>();
        rangeSearch(range, points, root, 0);
        return points;
    }

    /**
     * range search, by recursively adding points to the collection.
     */
    private void rangeSearch(GJBox2D range,
                             Collection<GJPoint2D> points, Node node, int depth) {
        if(node==null)
            return;

        // extract the point
        GJPoint2D point = node.getPoint();
        double x = point.x();
        double y = point.y();

        // check if point is in range
        boolean tx1 = range.getMinX()<x;
        boolean ty1 = range.getMinY()<y;
        boolean tx2 = x <= range.getMaxX();
        boolean ty2 = y <= range.getMaxY();

        // adds the point if it is present
        if(tx1 && tx2 && ty1 && ty2)
            points.add(point);

        // select direction
        int dir = depth%2;

        if(dir==0 ? tx1 : ty1)
            rangeSearch(range, points, node.left, depth+1);
        if(dir==0 ? tx2 : ty2)
            rangeSearch(range, points, node.right, depth+1);
    }


    public GJPoint2D nearestNeighbor(GJPoint2D point) {
        return nearestNeighbor(point, root, root, 0).getPoint();
    }

    /**
     * Return either the same node as candidate, or another node whose point
     * is closer.
     */
    private Node nearestNeighbor(GJPoint2D point, Node candidate, Node node,
                                 int depth) {
        // Check if the current node is closest that current candidate
        double distCand = candidate.point.distance(point);
        double dist     = node.point.distance(point);
        if(dist<distCand){
            candidate = node;
        }

        // select direction
        int dir = depth%2;

        Node node1, node2;

        // First try on the canonical side,
        // the result is the closest node found by depth-firth search
        GJPoint2D anchor = node.getPoint();
        GJStraightLine2D line;
        if(dir==0){
            boolean b = point.x() < anchor.x();
            node1 = b ? node.left : node.right;
            node2 = b ? node.right : node.left;
            line = new GJStraightLine2D(anchor, new GJVector2D(0, 1));
        } else {
            boolean b = point.y() < anchor.y();
            node1 = b ? node.left : node.right;
            node2 = b ? node.right : node.left;
            line = new GJStraightLine2D(anchor, new GJVector2D(1, 0));
        }

        if(node1!=null) {
            // Try to find a better candidate
            candidate = nearestNeighbor(point, candidate, node1, depth+1);

            // recomputes distance to the (possibly new) candidate
            distCand = candidate.getPoint().distance(point);
        }

        // If line is close enough, there can be closer points to the other
        // side of the line
        if(line.distance(point)<distCand && node2!=null) {
            candidate = nearestNeighbor(point, candidate, node2, depth+1);
        }

        return candidate;
    }


    /**
     * Gives a small example of use.
     */
    public static void main(String[] args){
        int n = 3;
        ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>(n);
        points.add(new GJPoint2D(5, 5));
        points.add(new GJPoint2D(10, 10));
        points.add(new GJPoint2D(20, 20));

        System.out.println("Check GJKDTree2D");

        GJKDTree2D tree = new GJKDTree2D(points);

        System.out.println(tree.contains(new GJPoint2D(5, 5)));
        System.out.println(tree.contains(new GJPoint2D(6, 5)));
    }

}
