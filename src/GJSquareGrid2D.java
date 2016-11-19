/**
 * 
 */



import java.util.ArrayList;
import java.util.Collection;








/**
 * Defines a square grid, which can have different size in each direction. The
 * grid is always parallel to the main axes.
 * 
 * @author dlegland
 */
public class GJSquareGrid2D implements GJGrid2D {

    double x0 = 0;
    double y0 = 0;

    double sx = 1;
    double sy = 1;

    public GJSquareGrid2D() {

    }

    public GJSquareGrid2D(GJPoint2D origin) {
        this(origin.x(), origin.y(), 1, 1);
    }

    public GJSquareGrid2D(GJPoint2D origin, double s) {
        this(origin.x(), origin.y(), s, s);
    }

    public GJSquareGrid2D(GJPoint2D origin, double sx, double sy) {
        this(origin.x(), origin.y(), sx, sy);
    }

    public GJSquareGrid2D(double x0, double y0, double s) {
        this(x0, y0, s, s);
    }

    public GJSquareGrid2D(double s) {
        this(0, 0, s, s);
    }

    public GJSquareGrid2D(double sx, double sy) {
        this(0, 0, sx, sy);
    }

    public GJSquareGrid2D(double x0, double y0, double sx, double sy) {
        this.x0 = x0;
        this.y0 = y0;
        this.sx = sx;
        this.sy = sy;
    }

    /**
     * @deprecated grids are supposed to be immutable (0.8.0)
     */
    @Deprecated
    public void setOrigin(GJPoint2D point) {
        this.x0 = point.x();
        this.y0 = point.y();
    }

    public GJPoint2D getOrigin() {
        return new GJPoint2D(x0, y0);
    }

    public double getSizeX() {
        return sx;
    }

    public double getSizeY() {
        return sy;
    }

    /**
     * @deprecated grids are supposed to be immutable (0.8.0)
     */
    @Deprecated
    public void setSize(double s) {
        sx = s;
        sy = s;
    }

    /**
     * @deprecated grids are supposed to be immutable (0.8.0)
     */
    @Deprecated
    public void setSize(double sx, double sy) {
        this.sx = sx;
        this.sy = sy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.grid.GJGrid2D#getClosestVertex(math.geom2d.GJPoint2D)
     */
	public GJPoint2D getClosestVertex(GJPoint2D point) {
		double nx = Math.round((point.x() - x0) / sx);
		double ny = Math.round((point.y() - y0) / sy);
		return new GJPoint2D(nx * sx + x0, ny * sy + y0);
	}

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.grid.GJGrid2D#getEdges(math.geom2d.GJBox2D)
     */
    public Collection<GJLineSegment2D> getEdges(GJBox2D box) {
        double x, y; // iterations
        double xmin, ymin, xmax, ymax; // limits
        double xi, yi; // first point in the box

        // extract bounds of the box
        xmin = box.getMinX();
        ymin = box.getMinY();
        xmax = box.getMaxX();
        ymax = box.getMaxY();

        // coordinates of first vertex in the box
		xi = Math.ceil((xmin - x0) / sx) * sx + x0;
		yi = Math.ceil((ymin - y0) / sy) * sy + y0;

    ArrayList<GJLineSegment2D> array = new ArrayList<GJLineSegment2D>();

        // add horizontal lines
		for (y = yi; y - ymax < GJShape2D.ACCURACY; y += sy)
            array.add(new GJLineSegment2D(xmin, y, xmax, y));

        // add vertical lines
		for (x = xi; x - xmax < GJShape2D.ACCURACY; x += sx)
            array.add(new GJLineSegment2D(x, ymin, x, ymax));

        // return the set of lines
        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.grid.GJGrid2D#getVertices(math.geom2d.GJBox2D)
     */
    public GJPointSet2D getVertices(GJBox2D box) {
        double x, y; // iterations
        double xmin, ymin, xmax, ymax; // limits
        double xi, yi; // first point in the box

        // extract bounds of the box
        xmin = box.getMinX();
        ymin = box.getMinY();
        xmax = box.getMaxX();
        ymax = box.getMaxY();

        // coordinates of first vertex in the box
        xi = Math.ceil((xmin-x0)/sx)*sx+x0;
        yi = Math.ceil((ymin-y0)/sy)*sy+y0;

        ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>();

        // iterate on lines in each direction
        for (y = yi; y-ymax< GJShape2D.ACCURACY; y += sy)
            for (x = xi; x-xmax< GJShape2D.ACCURACY; x += sx)
                array.add(new GJPoint2D(x, y));

        // return the set of lines
        return new GJPointArray2D(array);
    }
}
