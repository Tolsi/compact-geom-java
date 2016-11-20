/* File LineSegment2D.java 
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
 */

// package



/**
 * Line segment, defined as the set of points located between the two end
 * points.
 */
public class GJLineSegment2D extends GJAbstractLine2D
implements Cloneable, GJCirculinearElement2D {

    // ===================================================================
    // static constructors

    /**
     * Static factory for creating a new line segment between two points.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJLineSegment2D create(GJPoint2D p1, GJPoint2D p2) {
    	return new GJLineSegment2D(p1, p2);
    }

    /**
     * Returns the straight line that is the median of the edge extremities.
     */
	public static GJStraightLine2D getMedian(GJLineSegment2D edge) {
		return new GJStraightLine2D(
				edge.x0 + edge.dx * .5, 
				edge.y0 + edge.dy * .5, 
				-edge.dy, edge.dx);
    }
    
    /**
     * Returns angle between two edges sharing one vertex.
     */
	public static double getEdgeAngle(GJLineSegment2D edge1, GJLineSegment2D edge2) {
		double x0, y0, x1, y1, x2, y2;

		if (Math.abs(edge1.x0 - edge2.x0) < GJShape2D.ACCURACY
				&& Math.abs(edge1.y0 - edge2.y0) < GJShape2D.ACCURACY) {
			x0 = edge1.x0;
			y0 = edge1.y0;
			x1 = edge1.x0 + edge1.dx;
			y1 = edge1.y0 + edge1.dy;
			x2 = edge2.x0 + edge2.dx;
			y2 = edge2.y0 + edge2.dy;
		} else if (Math.abs(edge1.x0 + edge1.dx - edge2.x0) < GJShape2D.ACCURACY
				&& Math.abs(edge1.y0 + edge1.dy - edge2.y0) < GJShape2D.ACCURACY) {
			x0 = edge1.x0 + edge1.dx;
			y0 = edge1.y0 + edge1.dy;
			x1 = edge1.x0;
			y1 = edge1.y0;
			x2 = edge2.x0 + edge2.dx;
			y2 = edge2.y0 + edge2.dy;
		} else if (Math.abs(edge1.x0 + edge1.dx - edge2.x0 - edge2.dx) < GJShape2D.ACCURACY
				&& Math.abs(edge1.y0 + edge1.dy - edge2.y0 - edge2.dy) < GJShape2D.ACCURACY) {
			x0 = edge1.x0 + edge1.dx;
			y0 = edge1.y0 + edge1.dy;
			x1 = edge1.x0;
			y1 = edge1.y0;
			x2 = edge2.x0;
			y2 = edge2.y0;
		} else if (Math.abs(edge1.x0 - edge2.x0 - edge2.dx) < GJShape2D.ACCURACY
				&& Math.abs(edge1.y0 - edge2.y0 - edge2.dy) < GJShape2D.ACCURACY) {
			x0 = edge1.x0;
			y0 = edge1.y0;
			x1 = edge1.x0 + edge1.dx;
			y1 = edge1.y0 + edge1.dy;
			x2 = edge2.x0;
			y2 = edge2.y0;
		} else {// no common vertex -> return NaN
			return Double.NaN;
		}

		return GJAngle2D.angle(new GJVector2D(x1 - x0, y1 - y0), new GJVector2D(x2
				- x0, y2 - y0));
	}

    /**
     * Checks if two line segment intersect. Uses the GJPoint2D.ccw() method,
     * which is based on Sedgewick algorithm.
     * 
     * @param edge1 a line segment
     * @param edge2 a line segment
     * @return true if the 2 line segments intersect
     */
	public static boolean intersects(GJLineSegment2D edge1, GJLineSegment2D edge2) {
		GJPoint2D e1p1 = edge1.firstPoint();
		GJPoint2D e1p2 = edge1.lastPoint();
		GJPoint2D e2p1 = edge2.firstPoint();
		GJPoint2D e2p2 = edge2.lastPoint();

		boolean b1 = GJPoint2D.ccw(e1p1, e1p2, e2p1)
				* GJPoint2D.ccw(e1p1, e1p2, e2p2) <= 0;
		boolean b2 = GJPoint2D.ccw(e2p1, e2p2, e1p1)
				* GJPoint2D.ccw(e2p1, e2p2, e1p2) <= 0;
		return b1 &&b2;
    }

    
    // ===================================================================
    // constructors

    /** Defines a new Edge with two extremities. */
    public GJLineSegment2D(GJPoint2D point1, GJPoint2D point2) {
        this(point1.x(), point1.y(), point2.x(), point2.y());
    }

    /** Defines a new Edge with two extremities. */
    public GJLineSegment2D(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2-x1, y2-y1);
    }
    
    // ===================================================================
    // Methods specific to GJLineSegment2D

    /**
     * Returns the opposite vertex of the edge.
     * 
     * @param point one of the vertices of the edge
     * @return the other vertex, or null if point is nor a vertex of the edge
     */
	public GJPoint2D opposite(GJPoint2D point) {
		if (point.equals(new GJPoint2D(x0, y0)))
			return new GJPoint2D(x0 + dx, y0 + dy);
		if (point.equals(new GJPoint2D(x0 + dx, y0 + dy)))
			return new GJPoint2D(x0, y0);
		return null;
	}

	/**
	 * Returns the median of the edge, that is the locus of points located at
	 * equal distance of each vertex.
	 */
	public GJStraightLine2D getMedian() {
		// initial point is the middle of the edge -> x = x0+.5*dx
		// direction vector is the initial direction vector rotated by pi/2.
		return new GJStraightLine2D(x0 + dx * .5, y0 + dy * .5, -dy, dx);
	}


    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

    /**
     * Returns the length of the line segment.
     */
	@Override
    public double length() {
        return Math.hypot(dx, dy);
    }

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#parallel(double)
	 */
	public GJLineSegment2D parallel(double d) {
		double d2 = Math.hypot(dx, dy);
		if (Math.abs(d2) < GJShape2D.ACCURACY)
			throw new GJDegeneratedLine2DException(
					"Can not compute parallel of degnerated edge", this);
		d2 = d / d2;
		return new GJLineSegment2D(
				x0 + dy * d2, y0 - dx * d2, 
				x0 + dx + dy * d2, y0 + dy - dx * d2);
	}

	
    // ===================================================================
    // Methods implementing the GJOrientedCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJOrientedCurve2D#signedDistance(math.geom2d.GJPoint2D)
	 */
	@Override
    public double signedDistance(double x, double y) {
        GJPoint2D proj = super.projectedPoint(x, y);
        if (contains(proj))
            return super.signedDistance(x, y);

		double d = this.distance(x, y);
		return super.signedDistance(x, y) > 0 ? d : -d;
	}
    
    

    // ===================================================================
    // Methods implementing the GJCurve2D interface

    /**
     * Returns the first point of the edge.
     * 
     * @return the first point of the edge
     */
	@Override
    public GJPoint2D firstPoint() {
        return new GJPoint2D(x0, y0);
    }

    /**
     * Returns the last point of the edge.
     * 
     * @return the last point of the edge
     */
	@Override
	public GJPoint2D lastPoint() {
		return new GJPoint2D(x0 + dx, y0 + dy);
	}

    /**
     * Returns the parameter of the first point of the edge, equals to 0.
     */
    public double t0() {
        return 0.0;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }

    /**
     * Returns the parameter of the last point of the edge, equals to 1.
     */
    public double t1() {
        return 1.0;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }

	public GJPoint2D point(double t) {
		t = Math.min(Math.max(t, 0), 1);
		return new GJPoint2D(x0 + dx * t, y0 + dy * t);
	}

    /**
     * Returns the LineSegment which start from last point of this line segment,
     * and which ends at the fist point of this last segment.
     */
	public GJLineSegment2D reverse() {
		return new GJLineSegment2D(x0 + dx, y0 + dy, x0, y0);
	}

    // ===================================================================
    // Methods implementing the GJShape2D interface

    /**
     * Returns true
     */
    public boolean isBounded() {
        return true;
    }

    public boolean contains(double xp, double yp) {
        if (!super.supportContains(xp, yp))
            return false;

        // compute position on the line
        double t = positionOnLine(xp, yp);

		if (t < -ACCURACY)
			return false;
		if (t - 1 > ACCURACY)
			return false;

        return true;
    }

    /**
     * Get the distance of the point (x, y) to this edge.
     */
    @Override
	public double distance(double x, double y) {
        // compute position on the line
    	GJStraightLine2D line = this.supportingLine();
        double t = line.positionOnLine(x, y);

        // clamp with parameterization bounds of edge
		t = Math.max(Math.min(t, 1), 0);
		t = Math.min(t, 1);
		
		// compute position of projected point on the edge
		GJPoint2D proj = line.point(t);
		
		// return distance to projected point
		return proj.distance(x, y);
    }

    @Override
	public GJLineSegment2D transform(GJAffineTransform2D trans) {
		double[] tab = trans.coefficients();
		double x1 = x0 * tab[0] + y0 * tab[1] + tab[2];
		double y1 = x0 * tab[3] + y0 * tab[4] + tab[5];
		double x2 = (x0 + dx) * tab[0] + (y0 + dy) * tab[1] + tab[2];
		double y2 = (x0 + dx) * tab[3] + (y0 + dy) * tab[4] + tab[5];
		return new GJLineSegment2D(x1, y1, x2, y2);
	}

    /**
     * Returns the bounding box of this line segment.
     */
    public GJBox2D boundingBox() {
        return new GJBox2D(x0, x0+dx, y0, y0+dy);
    }

    // =================================
    // Methods implementing the Shape interface

    /**
     * Appends a line to the current path.
     * 
     * @param path the path to modify
     * @return the modified path
     */
	public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
		path.lineTo((float) x0 + dx, (float) y0 + dy);
		return path;
	}

	/**
	 * deprecated
	 */
	public java.awt.geom.GeneralPath getGeneralPath() {
		java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
		path.moveTo((float) x0, (float) y0);
		path.lineTo((float) (x0 + dx), (float) (y0 + dy));
		return path;
	}


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D,
	 * double)
	 */
	public boolean almostEquals(GJGeometricObject2D obj, double eps) {
		if (this == obj)
			return true;

		if (!(obj instanceof GJLineSegment2D))
			return false;
		GJLineSegment2D edge = (GJLineSegment2D) obj;

		if (Math.abs(x0 - edge.x0) > eps)
			return false;
		if (Math.abs(y0 - edge.y0) > eps)
			return false;
		if (Math.abs(dx - edge.dx) > eps)
			return false;
		if (Math.abs(dy - edge.dy) > eps)
			return false;

		return true;
	}

    // ===================================================================
    // Methods implementing the Object interface

	@Override
	public String toString() {
		return new String("GJLineSegment2D[(" + x0 + "," + y0 + ")-(" + (x0 + dx)
				+ "," + (y0 + dy) + ")]");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GJLineSegment2D))
			return false;
		GJLineSegment2D that = (GJLineSegment2D) obj;

        // Compare each field
		if (!GJEqualUtils.areEqual(this.x0, that.x0))
			return false;
		if (!GJEqualUtils.areEqual(this.y0, that.y0))
			return false;
		if (!GJEqualUtils.areEqual(this.dx, that.dx))
			return false;
		if (!GJEqualUtils.areEqual(this.dy, that.dy))
			return false;

		return true;
	}

	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
	@Override
	public GJLineSegment2D clone() {
		return new GJLineSegment2D(x0, y0, x0 + dx, y0 + dy);
	}
}
