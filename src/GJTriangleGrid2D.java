/**
 * 
 */



import java.util.ArrayList;
import java.util.Collection;









/**
 * Defines a triangle grid, with various size and orientation. The grid contains
 * triangle with all edges the same length.
 * 
 * @author dlegland
 */
public class GJTriangleGrid2D implements GJGrid2D {

    double x0    = 0;
    double y0    = 0;
    double s     = 1;

    double theta = 0;

    /**
     * Returns TRUE if the number <code>n</code> is even (like 0, 2, 4...).
     * 
     * @param n an integer
     * @return TRUE if n is even.
     */
    private final static boolean isEven(int n) {
        return Math.abs(n*.5-Math.floor(n*.5))<.25;
    }

    public GJTriangleGrid2D() {
        this(0, 0, 1, 0);
    }

    /**
     * @param s size of the triangle tile
     */
    public GJTriangleGrid2D(double s) {
        this(0, 0, s, 0);
    }

    /**
     * @param x0 x-coord of grid origin
     * @param y0 y-coord of grid origin
     */
    public GJTriangleGrid2D(double x0, double y0) {
        this(x0, y0, 1, 0);
    }

    /**
     * @param x0 x-coord of grid origin
     * @param y0 y-coord of grid origin
     * @param s size of the triangle tile
     */
    public GJTriangleGrid2D(double x0, double y0, double s) {
        this(x0, y0, s, 0);
    }

    /**
     * @param x0 x-coord of grid origin
     * @param y0 y-coord of grid origin
     * @param s size of the triangle tile
     * @param theta orientation of the grid with horizontal
     */
    public GJTriangleGrid2D(double x0, double y0, double s, double theta) {
        this.x0 = x0;
        this.y0 = y0;
        this.s = s;
        this.theta = theta;
    }

    /**
     * Assumes unit grid.
     * 
     * @param point the grid origin
     */
    public GJTriangleGrid2D(GJPoint2D point) {
        this(point.x(), point.y(), 1, 0);
    }

    /**
     * @param point the grid origin
     * @param s size of the triangle tile
     */
    public GJTriangleGrid2D(GJPoint2D point, double s) {
        this(point.x(), point.y(), s, 0);
    }

    /**
     * @param point the grid origin
     * @param s size of the triangle tile
     * @param theta orientation of the grid with horizontal
     */
    public GJTriangleGrid2D(GJPoint2D point, double s, double theta) {
        this(point.x(), point.y(), s, theta);
    }

    /**
     * @deprecated grids are supposed to be immutable (0.8.0)
     */
    @Deprecated
    public void setOrigin(GJPoint2D point) {
        this.x0 = point.getX();
        this.y0 = point.getY();
    }

    public GJPoint2D getOrigin() {
        return new GJPoint2D(x0, y0);
    }

    public double getSize() {
        return s;
    }

    /**
     * @deprecated grids are supposed to be immutable (0.8.0)
     */
    @Deprecated
    public void setSize(double s) {
        this.s = s;
    }

    /**
     * @deprecated grids are supposed to be immutable (0.8.0)
     */
    @Deprecated
    public void setAngle(double theta) {
        this.theta = theta;
    }

    public double getTheta() {
        return theta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.grid.GJGrid2D#getClosestVertex(GJPoint2D)
     */
    public GJPoint2D getClosestVertex(GJPoint2D point) {
        // createFromCollection the base line
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        GJStraightLine2D baseLine = new GJStraightLine2D(x0, y0, cot, sit);

        // compute distance to line, and deduces indices of surrounding lines
        double s2 = s*Math.sqrt(3)/2;
        double d = baseLine.signedDistance(point);
        int n1 = (int) Math.floor(d/s2);
        int n2 = (int) Math.ceil(d/s2);

        // compute the two surrounding lines
        GJStraightLine2D line1 = baseLine.parallel(n1*s2);
        GJStraightLine2D line2 = baseLine.parallel(n2*s2);

        // projection of point on the surrounding lines
        double t = line1.project(point);

        GJPoint2D p1, p2, p3;
        if (isEven(n1)) {
            p1 = line1.point(Math.floor(t/s)*s);
            p2 = line1.point(Math.ceil(t/s)*s);
            p3 = line2.point((Math.floor(t/s)+.5)*s);
        } else {
            p1 = line1.point((Math.floor(t/s)+.5)*s);
            p2 = line2.point(Math.floor(t/s)*s);
            p3 = line2.point(Math.ceil(t/s)*s);
        }

        GJPoint2D res = p1;
        double minDist = res.distance(point);

        double d2 = p2.distance(point);
        if (d2<minDist) {
            res = p2;
            minDist = d2;
        }

        double d3 = p3.distance(point);
        if (d3<minDist) {
            res = p3;
            minDist = d3;
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.grid.GJGrid2D#getEdges(math.geom2d.GJBox2D)
     */
    public Collection<GJLineSegment2D> getEdges(GJBox2D box) {

        // init the array of line segments
        ArrayList<GJLineSegment2D> array = new ArrayList<GJLineSegment2D>();

        double d = s*Math.sqrt(3)/2;
        double dmin, dmax;

        for (int k = 0; k<3; k++) {
            // consider a line through origin with one of the 2 orientations
            double theta2 = this.theta+Math.PI*(k)/3.0;
            double cot = Math.cos(theta2);
            double sit = Math.sin(theta2);
            GJStraightLine2D baseLine = new GJStraightLine2D(x0, y0, cot, sit);

            // get extreme distances of box corners to the base line
            dmin = Double.POSITIVE_INFINITY;
            dmax = Double.NEGATIVE_INFINITY;
            for (GJPoint2D point : box.vertices()) {
                double dist = baseLine.signedDistance(point);
                dmin = Math.min(dmin, dist);
                dmax = Math.max(dmax, dist);
            }

            // compute the number of lines in each direction
            double s2 = s*Math.sqrt(3)/2;
            int i0 = (int) Math.ceil(dmin/s2);
            int i1 = (int) Math.floor(dmax/s2);

            // add each clipped line
            for (int i = i0; i<=i1; i++) {
                GJStraightLine2D line = baseLine.parallel(d*i);
                for (GJLinearShape2D arc : line.clip(box)) {
                    if (arc instanceof GJLineSegment2D)
                        array.add((GJLineSegment2D) arc);
                }
            }
        }
        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.grid.GJGrid2D#getVertices(math.geom2d.GJBox2D)
     */
    public GJPointSet2D getVertices(GJBox2D box) {

        // init the array of line segments
        ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>();

        double d = s*Math.sqrt(3)/2;
        double dmin, dmax;

        // consider a line through origin with one of the 2 orientations
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        GJStraightLine2D baseLine = new GJStraightLine2D(x0, y0, cot, sit);

        // get extreme distances of box corners to the base line
        dmin = Double.POSITIVE_INFINITY;
        dmax = Double.NEGATIVE_INFINITY;
        for (GJPoint2D point : box.vertices()) {
            double dist = baseLine.signedDistance(point);
            dmin = Math.min(dmin, dist);
            dmax = Math.max(dmax, dist);
        }

        // compute the number of lines in each direction
        int i0 = (int) Math.ceil(dmin/s);
        int i1 = (int) Math.floor(dmax/s);

        // consider only the first line
        for (int i = i0; i<=i1; i++) {
            // compute supporting line, supposing that the norm of the
            // direction vector equals 1 (should be the case)
            GJStraightLine2D line = baseLine.parallel(d*i);

            // extract the line segment
            GJLineSegment2D seg = (GJLineSegment2D) line.clip(box).firstCurve();

            // compute position of extreme points, which is also the geodesic
            // distance
            double t1 = line.position(seg.firstPoint());
            double t2 = line.position(seg.lastPoint());

            // check if point on this line are shifted or not
            double t0 = isEven(i) ? 0 : s*.5;

            // compute the number of points in each side of the origin
            int j0 = (int) Math.ceil((t1-t0)/s);
            int j1 = (int) Math.floor((t2-t0)/s);

            // iterate on points
            if (j1<j0)
                continue;
            for (int j = j0; j<=j1; j++)
                array.add(line.point(j*s+t0));
        }

        return new GJPointArray2D(array);
    }

}
