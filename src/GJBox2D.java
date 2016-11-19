/* File Box2D.java 
 *
 * Project : Java Geometry Library
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 05 mar. 2007
 */

// package



// Imports
import static java.lang.Double.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;














import static java.lang.Math.*;

/**
 * This class defines bounds of a shape. It stores limits in each direction:
 * <code>x</code> and <code>y</code>. It also provides methods for clipping
 * others shapes, depending on their type.
 */
public class GJBox2D implements GJGeometricObject2D, Cloneable {

    // ===================================================================
    // Static factory
        
    /**
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJBox2D create(double xmin, double xmax, double ymin, double ymax) {
    	return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /**
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJBox2D create(GJPoint2D p1, GJPoint2D p2) {
    	return new GJBox2D(p1, p2);
    }

    /**
     * The box corresponding to the unit square, with bounds [0 1] in each
     * direction
     * @since 0.9.1
     */
    public final static GJBox2D UNIT_SQUARE_BOX = GJBox2D.create(0, 1, 0, 1);
    
    /**
     * The box corresponding to the the whole plane, with infinite bounds
     * in each direction.
     * @since 0.9.1
     */
    public final static GJBox2D INFINITE_BOX = GJBox2D.create(
    		NEGATIVE_INFINITY, POSITIVE_INFINITY, 
    		NEGATIVE_INFINITY, POSITIVE_INFINITY);
    
    // ===================================================================
    // class variables

    private double xmin = 0;
    private double xmax = 0;
    private double ymin = 0;
    private double ymax = 0;

    // ===================================================================
    // constructors

    /** Empty constructor (size and position zero) */
    public GJBox2D() {
        this(0, 0, 0, 0);
    }

    /**
     * Main constructor, given bounds for x coord, then bounds for y coord.
     */
    public GJBox2D(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }
    
	/** Constructor from awt, to allow easy construction from existing apps. */
    public GJBox2D(java.awt.geom.Rectangle2D rect) {
		this(rect.getX(), rect.getX() + rect.getWidth(), 
			rect.getY(), rect.getY() + rect.getHeight());
    }

    /**
     * Constructor from 2 points, giving extreme coordinates of the box.
     */
    public GJBox2D(GJPoint2D p1, GJPoint2D p2) {
    	double x1 = p1.x();
    	double y1 = p1.y();
    	double x2 = p2.x();
    	double y2 = p2.y();
        this.xmin = Math.min(x1, x2);
        this.xmax = Math.max(x1, x2);
        this.ymin = Math.min(y1, y2);
        this.ymax = Math.max(y1, y2);
    }

    /** Constructor from a point, a width and an height */
    public GJBox2D(GJPoint2D point, double w, double h) {
		this(point.x(), point.x() + w, point.y(), point.y() + h);
    }

    // ===================================================================
    // accessors to GJBox2D fields

    public double getMinX() {
        return xmin;
    }

    public double getMinY() {
        return ymin;
    }

    public double getMaxX() {
        return xmax;
    }

    public double getMaxY() {
        return ymax;
    }

    public double getWidth() {
		return xmax - xmin;
	}

	public double getHeight() {
		return ymax - ymin;
    }

    /** Returns true if all bounds are finite. */
    public boolean isBounded() {
        if (isInfinite(xmin))
            return false;
        if (isInfinite(ymin))
            return false;
        if (isInfinite(xmax))
            return false;
        if (isInfinite(ymax))
            return false;
        return true;
    }

    // ===================================================================
    // tests of inclusion

    /**
     * Checks if this box contains the given point. 
     */
    public boolean contains(GJPoint2D point) {
        double x = point.x();
        double y = point.y();
		if (x < xmin)
			return false;
		if (y < ymin)
			return false;
		if (x > xmax)
			return false;
		if (y > ymax)
			return false;
		return true;
	}

    /**
     * Checks if this box contains the point defined by the given coordinates. 
     */
	public boolean contains(double x, double y) {
		if (x < xmin)
			return false;
		if (y < ymin)
			return false;
		if (x > xmax)
			return false;
		if (y > ymax)
			return false;
		return true;
	}

    /**
     * Tests if the specified Shape is totally contained in this GJBox2D. Note that
     * the test is performed on the bounding box of the shape, then for rotated
     * rectangles, this method can return false with a shape totally contained
     * in the rectangle. The problem does not exist for horizontal rectangle,
     * since edges of rectangle and bounding box are parallel.
     */
    public boolean containsBounds(GJShape2D shape) {
        if (!shape.isBounded())
            return false;
        for (GJPoint2D point : shape.boundingBox().vertices())
            if (!contains(point))
                return false;

        return true;
    }

    // ===================================================================
    // information on the boundary

    /**
     * Returns a set of straight of lines defining half-planes, that all contain
     * the box. If the box is bounded, the number of straight lines is 4,
     * otherwise it can be less.
     * 
     * @return a set of straight lines
     */
    public Collection<GJStraightLine2D> clippingLines() {
        ArrayList<GJStraightLine2D> lines = new ArrayList<GJStraightLine2D>(4);

		if (isFinite(ymin))
			lines.add(new GJStraightLine2D(0, ymin, 1, 0));
		if (isFinite(xmax))
			lines.add(new GJStraightLine2D(xmax, 0, 0, 1));
		if (isFinite(ymax))
			lines.add(new GJStraightLine2D(0, ymax, -1, 0));
		if (isFinite(xmin))
			lines.add(new GJStraightLine2D(xmin, 0, 0, -1));
        return lines;
    }

    /**
     * Returns the set of linear shapes that constitutes the boundary of this
     * box.
     */
    public Collection<GJLinearShape2D> edges() {
        ArrayList<GJLinearShape2D> edges = new ArrayList<GJLinearShape2D>(4);

        if (isBounded()) {
            edges.add(new GJLineSegment2D(xmin, ymin, xmax, ymin));
            edges.add(new GJLineSegment2D(xmax, ymin, xmax, ymax));
            edges.add(new GJLineSegment2D(xmax, ymax, xmin, ymax));
            edges.add(new GJLineSegment2D(xmin, ymax, xmin, ymin));
            return edges;
        }

		if (!isInfinite(ymin)) {
			if (isInfinite(xmin) && isInfinite(xmax))
				edges.add(new GJStraightLine2D(0, ymin, 1, 0));
			else if (!isInfinite(xmin) && !isInfinite(xmax))
				edges.add(new GJLineSegment2D(xmin, ymin, xmax, ymin));
			else
				edges.add(new GJLineArc2D(0, ymin, 1, 0, xmin, xmax));
		}

		if (!isInfinite(xmax)) {
			if (isInfinite(ymin) && isInfinite(ymax))
				edges.add(new GJStraightLine2D(xmax, 0, 0, 1));
			else if (!isInfinite(ymin) && !isInfinite(ymax))
				edges.add(new GJLineSegment2D(xmax, ymin, xmax, ymax));
			else
				edges.add(new GJLineArc2D(xmax, 0, 0, 1, ymin, ymax));
		}

		if (!isInfinite(ymax)) {
			if (isInfinite(xmin) && isInfinite(xmax))
				edges.add(new GJStraightLine2D(0, ymax, 1, 0));
			else if (!isInfinite(xmin) && !isInfinite(xmax))
				edges.add(new GJLineSegment2D(xmax, ymax, xmin, ymax));
			else
				edges.add(new GJLineArc2D(0, ymin, 1, 0, xmin, xmax).reverse());
		}

		if (!isInfinite(xmin)) {
			if (isInfinite(ymin) && isInfinite(ymax))
				edges.add(new GJStraightLine2D(xmin, 0, 0, -1));
			else if (!isInfinite(ymin) && !isInfinite(ymax))
				edges.add(new GJLineSegment2D(xmin, ymax, xmin, ymin));
			else
				edges.add(new GJLineArc2D(xmin, 0, 0, 1, ymin, ymax).reverse());
        }

        return edges;
    }

    /**
     * Returns the boundary of this box. The boundary can be bounded, in the
     * case of a bounded box. It is unbounded if at least one bound of the box
     * is infinite. If both x bounds or both y-bounds are infinite, the
     * boundary is constituted from 2 straight lines.
     * @return the box boundary
     */
    public GJBoundary2D boundary() {

        // First case of totally bounded box
        if (isBounded()) {
            GJPoint2D pts[] = new GJPoint2D[4];
            pts[0] = new GJPoint2D(xmin, ymin);
            pts[1] = new GJPoint2D(xmax, ymin);
            pts[2] = new GJPoint2D(xmax, ymax);
            pts[3] = new GJPoint2D(xmin, ymax);
            return new GJLinearRing2D(pts);
        }

        // extract boolean info on "boundedness" in each direction
        boolean bx0 = !isInfinite(xmin);
        boolean bx1 = !isInfinite(xmax);
        boolean by0 = !isInfinite(ymin);
        boolean by1 = !isInfinite(ymax);

		// case of boxes unbounded in both x directions
		if (!bx0 && !bx1) {
			if (!by0 && !by1)
				return new GJContourArray2D<GJStraightLine2D>();
			if (by0 && !by1)
				return new GJStraightLine2D(0, ymin, 1, 0);
			if (!by0 && by1)
				return new GJStraightLine2D(0, ymax, -1, 0);
			return new GJContourArray2D<GJStraightLine2D>(new GJStraightLine2D[] {
                    new GJStraightLine2D(0, ymin, 1, 0),
                    new GJStraightLine2D(0, ymax, -1, 0) });
        }

		// case of boxes unbounded in both y directions
		if (!by0 && !by1) {
			if (!bx0 && !bx1)
				return new GJContourArray2D<GJStraightLine2D>();
			if (bx0 && !bx1)
				return new GJStraightLine2D(xmin, 0, 0, -1);
			if (!bx0 && bx1)
				return new GJStraightLine2D(xmax, 0, 0, 1);
			return new GJContourArray2D<GJStraightLine2D>(new GJStraightLine2D[] {
					new GJStraightLine2D(xmin, 0, 0, -1),
					new GJStraightLine2D(xmax, 0, 0, 1) });
		}

		// "corner boxes"

		if (bx0 && by0) // lower left corner
			return new GJBoundaryPolyCurve2D<GJLineArc2D>(new GJLineArc2D[] {
					new GJLineArc2D(xmin, ymin, 0, -1, NEGATIVE_INFINITY, 0),
					new GJLineArc2D(xmin, ymin, 1, 0, 0, POSITIVE_INFINITY) });

		if (bx1 && by0) // lower right corner
			return new GJBoundaryPolyCurve2D<GJLineArc2D>(new GJLineArc2D[] {
					new GJLineArc2D(xmax, ymin, 1, 0, NEGATIVE_INFINITY, 0),
					new GJLineArc2D(xmax, ymin, 0, 1, 0, POSITIVE_INFINITY) });

		if (bx1 && by1) // upper right corner
			return new GJBoundaryPolyCurve2D<GJLineArc2D>(new GJLineArc2D[] {
					new GJLineArc2D(xmax, ymax, 0, 1, NEGATIVE_INFINITY, 0),
					new GJLineArc2D(xmax, ymax, -1, 0, 0, POSITIVE_INFINITY) });

		if (bx0 && by1) // upper left corner
			return new GJBoundaryPolyCurve2D<GJLineArc2D>(new GJLineArc2D[] {
					new GJLineArc2D(xmin, ymax, -1, 0, NEGATIVE_INFINITY, 0),
					new GJLineArc2D(xmin, ymax, 0, -1, 0, POSITIVE_INFINITY) });

        // Remains only 4 cases: boxes unbounded in only one direction

        if (bx0)
			return new GJBoundaryPolyCurve2D<GJAbstractLine2D>(new GJAbstractLine2D[] {
					new GJLineArc2D(xmin, ymax, -1, 0, NEGATIVE_INFINITY, 0),
					new GJLineSegment2D(xmin, ymax, xmin, ymin),
					new GJLineArc2D(xmin, ymin, 1, 0, 0, POSITIVE_INFINITY) });

        if (bx1)
			return new GJBoundaryPolyCurve2D<GJAbstractLine2D>(new GJAbstractLine2D[] {
					new GJLineArc2D(xmax, ymin, 1, 0, NEGATIVE_INFINITY, 0),
					new GJLineSegment2D(xmax, ymin, xmax, ymax),
					new GJLineArc2D(xmax, ymax, -1, 0, 0,	POSITIVE_INFINITY) });

        if (by0)
            return new GJBoundaryPolyCurve2D<GJAbstractLine2D>(new GJAbstractLine2D[] {
                    new GJLineArc2D(xmin, ymin, 0, -1, NEGATIVE_INFINITY, 0),
                    new GJLineSegment2D(xmin, ymin, xmax, ymin),
                    new GJLineArc2D(xmax, ymin, 0, 1, 0, POSITIVE_INFINITY) });

        if (by1)
            return new GJBoundaryPolyCurve2D<GJAbstractLine2D>(new GJAbstractLine2D[] {
                    new GJLineArc2D(xmax, ymax, 0, 1, NEGATIVE_INFINITY, 0),
                    new GJLineSegment2D(xmax, ymax, xmin, ymax),
                    new GJLineArc2D(xmin, ymax, 0, -1, 0, POSITIVE_INFINITY) });

        return null;
    }

    public Collection<GJPoint2D> vertices() {
        ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>(4);
        boolean bx0 = isFinite(xmin);
        boolean bx1 = isFinite(xmax);
        boolean by0 = isFinite(ymin);
		boolean by1 = isFinite(ymax);
		if (bx0 && by0)
			points.add(new GJPoint2D(xmin, ymin));
		if (bx1 && by0)
			points.add(new GJPoint2D(xmax, ymin));
		if (bx0 && by1)
			points.add(new GJPoint2D(xmin, ymax));
		if (bx1 && by1)
			points.add(new GJPoint2D(xmax, ymax));
		return points;
	}

    private final static boolean isFinite(double value) {
    	if (isInfinite(value))
    		return false;
    	if (isNaN(value))
    		return false;
    	return true;
    }
    
    /** Returns the number of vertices of the box. */
    public int vertexNumber() {
        return this.vertices().size();
    }

    // ===================================================================
    // combination of box with other boxes

    /**
     * Returns the GJBox2D which contains both this box and the specified box.
     * 
     * @param box the bounding box to include
     * @return a new GJBox2D
     */
    public GJBox2D union(GJBox2D box) {
        double xmin = Math.min(this.xmin, box.xmin);
        double xmax = Math.max(this.xmax, box.xmax);
        double ymin = Math.min(this.ymin, box.ymin);
        double ymax = Math.max(this.ymax, box.ymax);
        return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Returns the GJBox2D which is contained both by this box and by the
     * specified box.
     * 
     * @param box the bounding box to include
     * @return a new GJBox2D
     */
    public GJBox2D intersection(GJBox2D box) {
        double xmin = Math.max(this.xmin, box.xmin);
        double xmax = Math.min(this.xmax, box.xmax);
        double ymin = Math.max(this.ymin, box.ymin);
        double ymax = Math.min(this.ymax, box.ymax);
        return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Changes the bounds of this box to also include bounds of the argument.
     * 
     * @param box the bounding box to include
     * @return this
     */
    public GJBox2D merge(GJBox2D box) {
        this.xmin = Math.min(this.xmin, box.xmin);
        this.xmax = Math.max(this.xmax, box.xmax);
        this.ymin = Math.min(this.ymin, box.ymin);
        this.ymax = Math.max(this.ymax, box.ymax);
        return this;
    }

    /**
     * Clip this bounding box such that after clipping, it is totally contained
     * in the given box.
     * 
     * @return the clipped box
     */
    public GJBox2D clip(GJBox2D box) {
        this.xmin = Math.max(this.xmin, box.xmin);
        this.xmax = Math.min(this.xmax, box.xmax);
        this.ymin = Math.max(this.ymin, box.ymin);
        this.ymax = Math.min(this.ymax, box.ymax);
        return this;
    }

    /**
     * Returns the new box created by an affine transform of this box.
     * If the box is unbounded, return an infinite box in all directions.
     */
    public GJBox2D transform(GJAffineTransform2D trans) {
    	// special case of unbounded box
    	if (!this.isBounded()) 
    		return GJBox2D.INFINITE_BOX;

    	// initialize with extreme values
    	double xmin = POSITIVE_INFINITY;
    	double xmax = NEGATIVE_INFINITY;
    	double ymin = POSITIVE_INFINITY;
    	double ymax = NEGATIVE_INFINITY;

    	// update bounds with coordinates of transformed box vertices
    	for (GJPoint2D point : this.vertices()) {
    		point = point.transform(trans);
    		xmin = Math.min(xmin, point.x());
    		ymin = Math.min(ymin, point.y());
    		xmax = Math.max(xmax, point.x());
    		ymax = Math.max(ymax, point.y());
    	}
    	
    	// createFromCollection the resulting box
    	return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    // ===================================================================
    // conversion methods

    /**
     * Converts to AWT rectangle.
     * 
     * @return an instance of java.awt.geom.GJRectangle2D
     */
    public java.awt.Rectangle asAwtRectangle() {
        int xr = (int) floor(this.xmin);
        int yr = (int) floor(this.ymin);
        int wr = (int) ceil(this.xmax-xr);
        int hr = (int) ceil(this.ymax-yr);
        return new java.awt.Rectangle(xr, yr, wr, hr);
    }

    /**
     * Converts to AWT GJRectangle2D. Result is an instance of
     * java.awt.geom.GJRectangle2D.Double.
     * 
     * @return an instance of java.awt.geom.GJRectangle2D
     */
    public java.awt.geom.Rectangle2D asAwtRectangle2D() {
		return new java.awt.geom.Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
	}

    /**
     * Converts to a rectangle. 
     * 
     * @return an instance of GJPolygon2D
     */
    public GJPolygon2D asRectangle() {
        return GJPolygons2D.createRectangle(xmin, ymin, xmax, ymax);
    }

    /**
     * Draws the boundary of the box on the specified graphics.
     * @param g2 the instance of graphics to draw in.
     * @throws GJUnboundedBox2DException if the box is unbounded
     */
    public void draw(Graphics2D g2) {
        if (!isBounded())
            throw new GJUnboundedBox2DException(this);
        this.boundary().draw(g2);
    }

    /**
     * Fills the content of the box on the specified graphics.
     * @param g2 the instance of graphics to draw in.
     * @throws GJUnboundedBox2DException if the box is unbounded
     */
    public void fill(Graphics2D g2) {
        if (!isBounded())
            throw new GJUnboundedBox2DException(this);
        this.boundary().fill(g2);
    }

    /**
     * @deprecated useless (0.11.1)
     */
    @Deprecated
    public GJBox2D boundingBox() {
        return new GJBox2D(xmin, xmax, ymin, ymax);
    }
    
    /**
     * Tests if boxes are the same. Two boxes are the same if they have the
     * same bounds, up to the specified threshold value.
     */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        // check class, and cast type
        if (!(obj instanceof GJBox2D))
            return false;
        GJBox2D box = (GJBox2D) obj;

        if (Math.abs(this.xmin - box.xmin) > eps)
        	return false;
        if (Math.abs(this.xmax - box.xmax) > eps)
        	return false;
        if (Math.abs(this.ymin - box.ymin) > eps)
        	return false;
        if (Math.abs(this.ymax - box.ymax) > eps)
        	return false;
        
        return true;
    }

    // ===================================================================
    // methods from Object interface

    @Override
    public String toString() {
        return new String("GJBox2D("+xmin+","+xmax+","+ymin+","+ymax+")");
    }

    /**
     * Test if boxes are the same. two boxes are the same if the have exactly 
     * the same bounds.
     */
    @Override
    public boolean equals(Object obj) {
    	if (this==obj)
    		return true;
    	
        // check class, and cast type
        if (!(obj instanceof GJBox2D))
            return false;
        GJBox2D that = (GJBox2D) obj;

        // Compare each field
		if (!GJEqualUtils.areEqual(this.xmin, that.xmin))
			return false;
		if (!GJEqualUtils.areEqual(this.xmax, that.xmax))
			return false;
		if (!GJEqualUtils.areEqual(this.ymin, that.ymin))
			return false;
		if (!GJEqualUtils.areEqual(this.ymax, that.ymax))
			return false;
        
        return true;
    }
    
	/**
	 * @deprecated not necessary to clone immutable objects (0.11.2)
	 */
	@Deprecated
    @Override
    public GJBox2D clone() {
        return new GJBox2D(xmin, xmax, ymin, ymax);
    }
}
