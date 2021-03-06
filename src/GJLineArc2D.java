import java.util.ArrayList;
import java.util.Collection;


/**
 * GJLineArc2D is a generic class to represent edges, straight lines, and rays.
 * It is defined like other linear shapes: origin point, and direction vector.
 * Moreover, two internal variables t0 and t1 define the limit of the object
 * (with t0<t1).
 * <ul>
 * <li> t0=0 and t1=1: this is an edge. </li>
 * <li> t0=-inf and t1=inf: this is a straight line. </li>
 * <li> t0=0 and t1=inf: this is a ray.</li>
 * <li> t0=-inf and 0: this is an inverted ray.</li>
 * </ul>
 * @author dlegland
 */
public class GJLineArc2D extends GJAbstractLine2D
implements GJSmoothOrientedCurve2D, Cloneable {

    // ===================================================================
    // Static constructor
    
    /**
     * Static factory for creating a new GJLineArc2D
     * @since 0.8.1
     */
    public static GJLineArc2D create(GJPoint2D p1, GJPoint2D p2, double t0, double t1) {
    	return new GJLineArc2D(p1, p2, t0, t1);
    }

    
    // ===================================================================
    // class variables
    
    /** Lower bound of this arc parameterization */
    protected double t0 = 0;

    /** Upper bound of this arc parameterization */
    protected double t1 = 1;

    // ===================================================================
    // Constructors

    /**
     * @param point1 the point located at t=0
     * @param point2 the point located at t=1
     * @param t0 the lower bound of line arc parameterization
     * @param t1 the upper bound of line arc parameterization
     */
    public GJLineArc2D(GJPoint2D point1, GJPoint2D point2, double t0, double t1) {
        this(point1.x(), point1.y(), 
        		point2.x()-point1.x(), point2.y()-point1.y(), 
        		t0, t1);
    }

    /**
     * Construct a line arc contained in the same straight line as first
     * argument, with bounds of arc given by t0 and t1
     * 
     * @param line an object defining the supporting line
     * @param t0 the lower bound of line arc parameterization
     * @param t1 the upper bound of line arc parameterization
     */
    public GJLineArc2D(GJLinearShape2D line, double t0, double t1) {
        super(line.origin(), line.direction());
        this.t0 = t0;
        this.t1 = t1;
    }


    /**
     * Construct a line arc by the parameters of the supporting line and two
     * positions on the line.
     * 
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param dx the x-coordinate of the direction vector
     * @param dy the y-coordinate of the direction vector
     * @param t0 the starting position of the arc
     * @param t1 the ending position of the arc
     */
    public GJLineArc2D(double x1, double y1, double dx, double dy, double t0,
                       double t1) {
        super(x1, y1, dx, dy);
        this.t0 = t0;
        this.t1 = t1;
    }

    
    // ===================================================================
    // methods specific to GJLineArc2D

    /**
     * Returns the length of the line arc.
     */
	@Override
    public double length() {
		if (this.isBounded())
			return firstPoint().distance(lastPoint());
		else
            return Double.POSITIVE_INFINITY;
    }

	public double getX1() {
		if (t0 != Double.NEGATIVE_INFINITY)
			return x0 + t0 * dx;
		else
			return Double.NEGATIVE_INFINITY;
	}

	public double getY1() {
		if (t0 != Double.NEGATIVE_INFINITY)
			return y0 + t0 * dy;
		else
			return Double.NEGATIVE_INFINITY;
	}

    public double getX2() {
		if (t1 != Double.POSITIVE_INFINITY)
			return x0 + t1 * dx;
        else
            return Double.POSITIVE_INFINITY;
    }

    public double getY2() {
		if (t1 != Double.POSITIVE_INFINITY)
			return y0 + t1 * dy;
        else
            return Double.POSITIVE_INFINITY;
    }

    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#parallel(double)
	 */
	public GJLineArc2D parallel(double d) {
		double d2 = d / Math.hypot(dx, dy);
		return new GJLineArc2D(x0 + dy * d2, y0 - dx * d2, dx, dy, t0, t1);
	}


	// ===================================================================
    // methods of GJCurve2D interface

    /**
     * Returns the parameter of the first point of the line arc, 
     * arbitrarily set to 0.
     */
    public double t0() {
        return t0;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }

    /**
     * Returns the parameter of the last point of the line arc, 
     * arbitrarily set to 1.
     */
    public double t1() {
        return t1;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }

    public GJPoint2D point(double t) {
		if (t < t0)
			t = t0;
		if (t > t1)
            t = t1;

        if (Double.isInfinite(t))
            throw new GJUnboundedShape2DException(this);
        else
			return new GJPoint2D(x0 + dx * t, y0 + dy * t);
    }

    /**
     * Returns the first point of the edge. In the case of a line, or a ray
     * starting from -infinity, throws an GJUnboundedShape2DException.
     * 
     * @return the last point of the arc
     */
	@Override
    public GJPoint2D firstPoint() {
        if (!Double.isInfinite(t0))
			return new GJPoint2D(x0 + t0 * dx, y0 + t0 * dy);
        else
            throw new GJUnboundedShape2DException(this);
    }

    /**
     * Returns the last point of the edge. In the case of a line, or a ray
     * ending at infinity, throws an GJUnboundedShape2DException.
     * 
     * @return the last point of the arc
     */
	@Override
    public GJPoint2D lastPoint() {
        if (!Double.isInfinite(t1))
			return new GJPoint2D(x0 + t1 * dx, y0 + t1 * dy);
        else
            throw new GJUnboundedShape2DException(this);
    }

	@Override
    public Collection<GJPoint2D> singularPoints() {
		ArrayList<GJPoint2D> list = new ArrayList<GJPoint2D>(2);
		if (t0 != Double.NEGATIVE_INFINITY)
			list.add(this.firstPoint());
		if (t1 != Double.POSITIVE_INFINITY)
			list.add(this.lastPoint());
		return list;
    }

	@Override
    public boolean isSingular(double pos) {
        if (Math.abs(pos-t0)< GJShape2D.ACCURACY)
            return true;
        if (Math.abs(pos-t1)< GJShape2D.ACCURACY)
            return true;
        return false;
    }

    @Override
    public Collection<? extends GJLineArc2D> continuousCurves() {
    	return wrapCurve(this);
    }

    /**
     * Returns the line arc which have the same trace, but has the inverse
     * parameterization.
     */
    public GJLineArc2D reverse() {
        return new GJLineArc2D(x0, y0, -dx, -dy, -t1, -t0);
    }

    /**
     * Returns a new GJLineArc2D, which is the portion of this GJLineArc2D delimited
     * by parameters t0 and t1.
     */
    @Override
    public GJLineArc2D subCurve(double t0, double t1) {
        t0 = Math.max(t0, this.t0());
        t1 = Math.min(t1, this.t1());
        return new GJLineArc2D(this, t0, t1);
    }

    // ===================================================================
    // methods of GJShape2D interface

    /** 
     * Returns true if both t0 and t1 are different from infinity. 
     */
    public boolean isBounded() {
    	return t0 != Double.NEGATIVE_INFINITY && t1 != Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the bounding box of this line arc.
     */
    public GJBox2D boundingBox() {
    	return new GJBox2D(x0 + t0 * dx, x0 + t1 * dx, y0 + t0 * dy, y0 + t1 * dy);
    }

    // ===================================================================
    // methods of Shape interface

    @Override
    public boolean contains(GJPoint2D pt) {
        return contains(pt.x(), pt.y());
    }

    public boolean contains(double xp, double yp) {
        if (!super.supportContains(xp, yp))
            return false;

        // compute position on the line
        double t = positionOnLine(xp, yp);

		if (t - t0 < -ACCURACY)
			return false;
		if (t - t1 > ACCURACY)
			return false;

        return true;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
		path.moveTo((float) (x0 + t0 * dx), (float) (y0 + t0 * dy));
		path.lineTo((float) (x0 + t1 * dx), (float) (y0 + t1 * dy));
   return path;
    }

    /**
     * Appends a line to the current path. If t0 or t1 is infinite, throws a new
     * GJUnboundedShape2DException.
     * 
     * @param path the path to modify
     * @return the modified path
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);
        if (t0==Double.NEGATIVE_INFINITY)
            return path;
        if (t1==Double.POSITIVE_INFINITY)
            return path;
        path.lineTo((float) getX1(), (float) getY1());
        path.lineTo((float) getX2(), (float) getY2());
        return path;
    }

    @Override
    public GJLineArc2D transform(GJAffineTransform2D trans) {
		double[] tab = trans.coefficients();
		double x1 = x0 * tab[0] + y0 * tab[1] + tab[2];
		double y1 = x0 * tab[3] + y0 * tab[4] + tab[5];
		return new GJLineArc2D(x1, y1,
				dx * tab[0] + dy * tab[1], dx * tab[3] + dy * tab[4], t0, t1);
    }

    @Override
    public String toString() {
        return new String("GJLineArc2D(" + x0 + "," + y0 + "," +
            		dx + "," + dy + "," + t0 + "," + t1 +")");
   }


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this == obj)
    		return true;
    	
        if (!(obj instanceof GJLineArc2D))
            return false;
        GJLineArc2D that = (GJLineArc2D) obj;

        // Compare each field
		if (!almostEquals(this.x0, that.x0, eps)) 
			return false;
		if (!almostEquals(this.y0, that.y0, eps)) 
			return false;
		if (!almostEquals(this.dx, that.dx, eps)) 
			return false;
		if (!almostEquals(this.dy, that.dy, eps)) 
			return false;
		if (!almostEquals(this.t0, that.t0, eps)) 
			return false;
		if (!almostEquals(this.t1, that.t1, eps)) 
			return false;

		return true;
    }

    /**
     * Compares two double values with a given accuracy, with correct result
     * for infinite values. Undefined results for NaNs.
     */
    private static boolean almostEquals(double d1, double d2, double eps) {
		if (d1 == Double.POSITIVE_INFINITY && d2 == Double.POSITIVE_INFINITY)
    		return true;
		if (d1 == Double.NEGATIVE_INFINITY && d2 == Double.NEGATIVE_INFINITY)
    		return true;
		
		return Math.abs(d1 - d2) < eps;
    }    
    
    // ===================================================================
    // methods of Object interface

    @Override
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
        if (!(obj instanceof GJLineArc2D))
            return false;
        GJLineArc2D that = (GJLineArc2D) obj;

        // Compare each field
		if (!GJEqualUtils.areEqual(this.x0, that.x0))
			return false;
		if (!GJEqualUtils.areEqual(this.y0, that.y0))
			return false;
		if (!GJEqualUtils.areEqual(this.dx, that.dx))
			return false;
		if (!GJEqualUtils.areEqual(this.dy, that.dy))
			return false;
		if (!GJEqualUtils.areEqual(this.t0, that.t0))
			return false;
		if (!GJEqualUtils.areEqual(this.t1, that.t1))
			return false;

        return true;
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJLineArc2D clone() {
        return new GJLineArc2D(x0, y0, dx, dy, t0, t1);
    }
}
