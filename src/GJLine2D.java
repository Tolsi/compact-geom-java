import java.util.Collection;
// Imports

/**
 * Line object defined from 2 points. This object keep points reference in
 * memory, and recomputes properties directly from points. GJLine2D is
 * mutable.
 * <p>
 * Example :
 * <p>
 * <code>
 * // Create an Edge2D<br>
 * GJLine2D line = new GJLine2D(new GJPoint2D(0, 0), new GJPoint2D(1, 2));<br>
 * // Change direction of line, by changing second point :<br>
 * line.setPoint2(new GJPoint2D(4, 5));<br>
 * // Change position and direction of the line, by changing first point. <br>
 * // 'line' is now the edge (2,3)-(4,5)<br>
 * line.setPoint1(new GJPoint2D(2, 3));<br>
 * </code>
 * <p>
 * <p>
 * This class may be slower than Edge2D or GJStraightLine2D, because parameters
 * are updated each time a computation is made, causing lot of additional
 * processing. Moreover, as inner point fields are public, it is not as safe
 * as {@link math.geom2d.line.LineSegment2D}.
 */
public class GJLine2D extends GJAbstractSmoothCurve2D
implements GJLinearElement2D, Cloneable {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    /**
     * The origin point.
     */
    public GJPoint2D p1;
    
    /**
     * The destination point.
     */
    public GJPoint2D p2;


    // ===================================================================
    // constructors

    /**
     * Checks if two line intersect. Uses the
     * {@link math.geom2d.Point2D#ccw(GJPoint2D, GJPoint2D, GJPoint2D) GJPoint2D.ccw}
     * method, which is based on Sedgewick algorithm.
     * 
     * @param line1 a GJLine2D object
     * @param line2 a GJLine2D object
     * @return true if the 2 lines intersect
     */
    public static boolean intersects(GJLine2D line1, GJLine2D line2) {
        GJPoint2D e1p1 = line1.firstPoint();
        GJPoint2D e1p2 = line1.lastPoint();
        GJPoint2D e2p1 = line2.firstPoint();
        GJPoint2D e2p2 = line2.lastPoint();

		boolean b1 = GJPoint2D.ccw(e1p1, e1p2, e2p1)
				* GJPoint2D.ccw(e1p1, e1p2, e2p2) <= 0;
		boolean b2 = GJPoint2D.ccw(e2p1, e2p2, e1p1)
				* GJPoint2D.ccw(e2p1, e2p2, e1p2) <= 0;
		return b1 && b2;
    }

    // ===================================================================
    // constructors

    /** Define a new GJLine2D with two extremities. */
    public GJLine2D(GJPoint2D point1, GJPoint2D point2) {
        this.p1 = point1;
        this.p2 = point2;
    }

    /** Define a new GJLine2D with two extremities. */
    public GJLine2D(double x1, double y1, double x2, double y2) {
        p1 = new GJPoint2D(x1, y1);
        p2 = new GJPoint2D(x2, y2);
    }

    /**
     * Copy constructor.
     */
    public GJLine2D(GJLine2D line) {
    	this(line.getPoint1(), line.getPoint2());
    }
    
    // ===================================================================
    // Static factory

    /**
     * Static factory for creating a new GJLine2D, starting from p1
     * and finishing at p2.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJLine2D create(GJPoint2D p1, GJPoint2D p2) {
    	return new GJLine2D(p1, p2);
    }
    
    
    // ===================================================================
    // Methods specific to GJLine2D

    /**
     * Return the first point of the edge. It corresponds to getPoint(0).
     * 
     * @return the first point.
     */
    public GJPoint2D getPoint1() {
        return p1;
    }

    /**
     * Return the last point of the edge. It corresponds to getPoint(1).
     * 
     * @return the last point.
     */
    public GJPoint2D getPoint2() {
        return p2;
    }

    public double getX1() {
        return p1.x();
    }

    public double getY1() {
        return p1.y();
    }

    public double getX2() {
        return p2.x();
    }

    public double getY2() {
        return p2.y();
    }

    /**
     * Return the opposite vertex of the edge.
     * 
     * @param point : one of the vertices of the edge
     * @return the other vertex
     */
    public GJPoint2D getOtherPoint(GJPoint2D point) {
        if (point.equals(p1))
            return p2;
        if (point.equals(p2))
            return p1;
        return null;
    }

    public void setPoint1(GJPoint2D point) {
        p1 = point;
    }

    public void setPoint2(GJPoint2D point) {
        p2 = point;
    }

    // ===================================================================
    // methods implementing the GJLinearShape2D interface

    public boolean isColinear(GJLinearShape2D line) {
        return new GJLineSegment2D(p1, p2).isColinear(line);
    }

    /**
     * Test if the this object is parallel to the given one. This method is
     * overloaded to update parameters before computation.
     */
    public boolean isParallel(GJLinearShape2D line) {
        return new GJLineSegment2D(p1, p2).isParallel(line);
    }

    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.computeBuffer(this, dist);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#parallel(double)
	 */
	public GJLine2D parallel(double d) {
		double x0 = getX1();
		double y0 = getY1();
		double dx = getX2()-x0;
		double dy = getY2()-y0;
        double d2 = d / Math.hypot(dx, dy);
		return new GJLine2D(
				x0 + dy * d2, y0 - dx * d2, 
				x0 + dx + dy * d2, y0 + dy - dx * d2);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length()
	 */
	public double length() {
		return p1.distance(p2);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length(double)
	 */
	public double length(double pos) {
		double dx = p2.x() - p1.x();
		double dy = p2.y() - p1.y();
		return pos * Math.hypot(dx, dy);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#position(double)
	 */
	public double position(double length) {
		double dx = p2.x() - p1.x();
		double dy = p2.y() - p1.y();
		return length / Math.hypot(dx, dy);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearElement2D transform(GJCircleInversion2D inv) {
		// Extract inversion parameters
        GJPoint2D center 	= inv.center();
        double r 		= inv.radius();
        
        // compute distance of line to inversion center
        GJPoint2D po 	= new GJStraightLine2D(this).projectedPoint(center);
        double d 	= this.distance(po);
        
        // Degenerate case of a line passing through the center.
        // returns the line itself.
        if (Math.abs(d) < GJShape2D.ACCURACY){
        	GJPoint2D p1 = this.firstPoint().transform(inv);
        	GJPoint2D p2 = this.lastPoint().transform(inv);
        	return new GJLineSegment2D(p1, p2);
        }
        
        // angle from center to line
        double angle = GJAngle2D.horizontalAngle(center, po);

        // center of transformed circle
        double r2 	= r * r / d / 2;
        GJPoint2D c2 	= GJPoint2D.createPolar(center, r2, angle);

        // choose direction of circle arc
        boolean direct = !this.isInside(center);
        
        // compute angle between center of transformed circle and end points
        double theta1 = GJAngle2D.horizontalAngle(c2, p1);
        double theta2 = GJAngle2D.horizontalAngle(c2, p2);
        
        // createFromCollection the new circle arc
        return new GJCircleArc2D(c2, r2, theta1, theta2, direct);
	}
	
    // ===================================================================
    // methods implementing the GJLinearShape2D interface

	/* (non-Javadoc)
	 */
    public double[][] parametric() {
        return new GJLineSegment2D(p1, p2).parametric();
    }

    public double[] cartesianEquation() {
        return new GJLineSegment2D(p1, p2).cartesianEquation();
    }

    public double[] polarCoefficients() {
        return new GJLineSegment2D(p1, p2).polarCoefficients();
    }

    public double[] polarCoefficientsSigned() {
        return new GJLineSegment2D(p1, p2).polarCoefficientsSigned();
    }

    // ===================================================================
    // methods implementing the GJLinearShape2D interface
    
    public double horizontalAngle() {
        return new GJLineSegment2D(p1, p2).horizontalAngle();
    }
  
    /* (non-Javadoc)
     * @see math.geom2d.line.GJLinearShape2D#intersection(math.geom2d.line.GJLinearShape2D)
     */
    public GJPoint2D intersection(GJLinearShape2D line) {
        return new GJLineSegment2D(p1, p2).intersection(line);
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.GJLinearShape2D#origin()
     */
    public GJPoint2D origin() {
        return p1;
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.GJLinearShape2D#supportingLine()
     */
    public GJStraightLine2D supportingLine() {
        return new GJStraightLine2D(p1, p2);
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.GJLinearShape2D#direction()
     */
    public GJVector2D direction() {
        return new GJVector2D(p1, p2);
    }

    
    // ===================================================================
    // methods implementing the GJOrientedCurve2D interface
    
    public double signedDistance(GJPoint2D p) {
        return signedDistance(p.x(), p.y());
    }

    public double signedDistance(double x, double y) {
        return new GJLineSegment2D(p1, p2).signedDistance(x, y);
    }

    
    // ===================================================================
    // methods implementing the GJContinuousCurve2D interface
    
    /* (non-Javadoc)
     * @see math.geom2d.curve.GJContinuousCurve2D#smoothPieces()
     */
    @Override
	public Collection<? extends GJLine2D> smoothPieces() {
    	return wrapCurve(this);
    }

    /**
     * Returns false.
     * @see math.geom2d.curve.ContinuousCurve2D#isClosed()
     */
    public boolean isClosed() {
        return false;
    }
    
    // ===================================================================
    // methods implementing the GJShape2D interface

    /**
     * Returns the distance of the point <code>p</code> to this edge.
     */
    public double distance(GJPoint2D p) {
        return distance(p.x(), p.y());
    }

    /**
     * Returns the distance of the point (x, y) to this edge.
     */
    public double distance(double x, double y) {
    	// project the point on the support line 
        GJStraightLine2D support = new GJStraightLine2D(p1, p2);
        GJPoint2D proj = support.projectedPoint(x, y);
        
        // if this line contains the projection, return orthogonal distance
        if (contains(proj))
            return proj.distance(x, y);
        
        // return distance to closest extremity
		double d1 = Math.hypot(p1.x() - x, p1.y() - y);
		double d2 = Math.hypot(p2.x() - x, p2.y() - y);
        return Math.min(d1, d2);
    }

    /**
     * Creates a straight line parallel to this object, and passing through
     * the given point.
     * 
     * @param point the point to go through
     * @return the parallel through the point
     */
    public GJStraightLine2D parallel(GJPoint2D point) {
        return new GJLineSegment2D(p1, p2).parallel(point);
    }

    /**
     * Creates a straight line perpendicular to this object, and passing
     * through the given point.
     * 
     * @param point the point to go through
     * @return the perpendicular through point
     */
    public GJStraightLine2D perpendicular(GJPoint2D point) {
        return new GJLineSegment2D(p1, p2).perpendicular(point);
    }

    /**
     * Clips the line object by a box. The result is an instance of GJCurveSet2D,
     * which contains only instances of GJLineArc2D. If the line object is not
     * clipped, the result is an instance of GJCurveSet2D which
     * contains 0 curves.
     */
    public GJCurveSet2D<? extends GJLine2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJLine2D> result =
        	new GJCurveArray2D<GJLine2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJLine2D)
                result.add((GJLine2D) curve);
        }
        return result;
    }

    /**
     * Returns the bounding box of the GJLine2D.
     */
    public GJBox2D boundingBox() {
        return new GJBox2D(p1, p2);
    }

    // ===================================================================
    // methods inherited from GJSmoothCurve2D interface

    public GJVector2D tangent(double t) {
        return new GJVector2D(p1, p2);
    }

    /**
     * Returns 0 as every linear shape.
     */
    public double curvature(double t) {
        return 0.0;
    }

    // ===================================================================
    // methods inherited from GJOrientedCurve2D interface

    public double windingAngle(GJPoint2D point) {
        return new GJLineSegment2D(p1, p2).windingAngle(point);
    }

    public boolean isInside(GJPoint2D point) {
        return new GJLineSegment2D(p1, p2).signedDistance(point)<0;
    }

    // ===================================================================
    // methods inherited from GJCurve2D interface

    /**
     * Returns 0.
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
     * Returns 1.
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
		double x = p1.x() * (1 - t) + p2.x() * t;
		double y = p1.y() * (1 - t) + p2.y() * t;
		return new GJPoint2D(x, y);
    }

    /**
     * Get the first point of the curve.
     * 
     * @return the first point of the curve
     */
    @Override
    public GJPoint2D firstPoint() {
        return p1;
    }

    /**
     * Get the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    @Override
    public GJPoint2D lastPoint() {
        return p2;
    }

    /**
     * Returns the position of the point on the line. If point belongs to the
     * line, this position is defined by the ratio:
     * <p>
     * <code> t = (xp - x0)/dx <\code>, or equivalently :<p>
     * <code> t = (yp - y0)/dy <\code>.<p>
     * If point does not belong to edge, return Double.NaN. The current implementation 
     * uses the direction with the biggest derivative, in order to avoid divisions 
     * by zero.
     */
    public double position(GJPoint2D point) {
        return new GJLineSegment2D(p1, p2).position(point);
    }

    public double project(GJPoint2D point) {
        return new GJLineSegment2D(p1, p2).project(point);
    }

    /**
     * Returns the GJLine2D object which starts at <code>point2</code> and ends at
     * <code>point1</code>.
     */
    public GJLine2D reverse() {
        return new GJLine2D(p2, p1);
    }

    @Override
	public Collection<? extends GJLine2D> continuousCurves() {
    	return wrapCurve(this);
    }

    /**
     * Returns a new GJLine2D, which is the portion of the line delimited by
     * parameters t0 and t1.
     */
    public GJLine2D subCurve(double t0, double t1) {
        if(t0 > t1) 
            return null;
        t0 = Math.max(t0, t0());
        t1 = Math.min(t1, t1());
        return new GJLine2D(this.point(t0), this.point(t1));
    }

    /* (non-Javadoc)
     * @see math.geom2d.curve.GJCurve2D#intersections(math.geom2d.line.GJLinearShape2D)
     */
    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
        return new GJLineSegment2D(p1, p2).intersections(line);
    }

    // ===================================================================
    // methods inherited from GJShape2D interface

    public GJLine2D transform(GJAffineTransform2D trans) {
        return new GJLine2D(
                p1.transform(trans), 
                p2.transform(trans));
    }

    // ===================================================================
    // methods inherited from Shape interface

    /**
     * Returns true if the point (x, y) lies on the line, with precision given
     * by GJShape2D.ACCURACY.
     */
    public boolean contains(double x, double y) {
        return new GJLineSegment2D(p1, p2).contains(x, y);
    }

    /**
     * Returns true if the point p lies on the line, with precision given by
     * GJShape2D.ACCURACY.
     */
    public boolean contains(GJPoint2D p) {
        return contains(p.x(), p.y());
    }

    /**
     * Returns true
     */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns false
     */
    public boolean isEmpty() {
        return false;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        path.moveTo((float) p1.x(), (float) p1.y());
        path.lineTo((float) p2.x(), (float) p2.y());
        return path;
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        path.lineTo((float) p2.x(), (float) p2.y());
        return path;
    }


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        // check class
        if(!(obj instanceof GJLine2D))
            return false;
        
        // cast class, and compare members
        GJLine2D edge = (GJLine2D) obj;
        return p1.almostEquals(edge.p1, eps) && p2.almostEquals(edge.p2, eps);
    }

    // ===================================================================
    // methods inherited from Object interface

    @Override
    public String toString() {
        return "GJLine2D(" + p1 + ")-(" + p2 + ")";
    }

    /**
     * Two GJLine2D are equals if the share the two same points,
     * in the same order.
     * 
     * @param obj the edge to compare to.
     * @return true if extremities of both edges are the same.
     */
    @Override
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
        if(!(obj instanceof GJLine2D))
            return false;
        
        // cast class, and compare members
        GJLine2D edge = (GJLine2D) obj;
        return p1.equals(edge.p1) && p2.equals(edge.p2);
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJLine2D clone() {
        return new GJLine2D(p1, p2);
    }
}
