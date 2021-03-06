import java.awt.geom.GeneralPath;
// Imports

/**
 * Ray, or half-line, defined from an origin and a direction vector. It is
 * composed of all points satisfying the parametric equation:
 * <p>
 * <code>x(t) = x0+t*dx<code><br>
 * <code>y(t) = y0+t*dy<code></p> 
 * With <code>t<code> comprised between 0 and +INFINITY.
 */
public class GJRay2D extends GJAbstractLine2D implements Cloneable {

    // ===================================================================
    // Static constructors

    /**
     * Static factory for creating a new ray.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJRay2D create(GJPoint2D origin, GJVector2D direction) {
    	return new GJRay2D(origin, direction);
    }
    
    /**
     * Static factory for creating a new ray, originating from
     * <code>origin<\code>, and going in the
     * direction of <code>target<\code>.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJRay2D create(GJPoint2D origin, GJPoint2D target) {
    	return new GJRay2D(origin, target);
    }
    

    // ===================================================================
    // constructors

    /**
     * Empty constructor for GJRay2D. Default is ray starting at origin, and
     * having a slope of 1*dx and 0*dy.
     */
    public GJRay2D() {
        this(0, 0, 1, 0);
    }

    /**
     * Creates a new GJRay2D, originating from
     * <code>point1<\code>, and going in the
     * direction of <code>point2<\code>.
     */
    public GJRay2D(GJPoint2D point1, GJPoint2D point2) {
        this(point1.x(), point1.y(), 
        		point2.x()-point1.x(),
        		point2.y()-point1.y());
    }

    /**
     * Creates a new GJRay2D, originating from point
     * <code>(x1,y1)<\code>, and going 
     * in the direction defined by vector <code>(dx, dy)<\code>.
     */
    public GJRay2D(double x1, double y1, double dx, double dy) {
        super(x1, y1, dx, dy);
    }

    /**
     * Creates a new GJRay2D, originating from point <code>point<\code>, and going
     * in the direction defined by vector <code>(dx,dy)<\code>.
     */
    public GJRay2D(GJPoint2D point, double dx, double dy) {
        this(point.x(), point.y(), dx, dy);
    }

    /**
     * Creates a new GJRay2D, originating from point <code>point<\code>, and going
     * in the direction specified by <code>vector<\code>.
     */
    public GJRay2D(GJPoint2D point, GJVector2D vector) {
        this(point.x(), point.y(), vector.x(), vector.y());
    }

    /**
     * Creates a new GJRay2D, originating from point <code>point<\code>, and going
     * in the direction specified by <code>angle<\code> (in radians).
     */
    public GJRay2D(GJPoint2D point, double angle) {
        this(point.x(), point.y(), Math.cos(angle), Math.sin(angle));
    }

    /**
     * Creates a new GJRay2D, originating from point
     * <code>(x, y)<\code>, and going 
     * in the direction specified by <code>angle<\code> (in radians).
     */
    public GJRay2D(double x, double y, double angle) {
        this(x, y, Math.cos(angle), Math.sin(angle));
    }

    /**
     * Define a new Ray, with same characteristics as given object.
     */
    public GJRay2D(GJLinearShape2D line) {
        super(line.origin(), line.direction());
    }

    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/**
	 * Returns a new ray parallel to this one, at the given relative distance.
	 * @see math.geom2d.circulinear.CirculinearCurve2D#parallel(double)
	 */
	public GJRay2D parallel(double d) {
        double dd = Math.hypot(dx, dy);
		return new GJRay2D(x0 + dy * d / dd, y0 - dx * d / dd, dx, dy);
	}

    // ===================================================================
    // methods implementing the GJContinuousCurve2D interface

    /** Throws an infiniteShapeException */
    public GeneralPath appendPath(GeneralPath path) {
        throw new GJUnboundedShape2DException(this);
    }

    /** Throws an infiniteShapeException */
    public GeneralPath getGeneralPath() {
        throw new GJUnboundedShape2DException(this);
    }

    // ===================================================================
    // methods implementing the GJCurve2D interface

	@Override
    public GJPoint2D firstPoint() {
        return new GJPoint2D(x0, y0);
    }

    public GJPoint2D point(double t) {
        t = Math.max(t, 0);
        return new GJPoint2D(x0+t*dx, y0+t*dy);
    }
    
    public double t0() {
        return 0;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }

    /**
     * Returns the position of the last point of the ray, which is always
     * Double.POSITIVE_INFINITY.
     */
    public double t1() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }

    /**
     * Reverses this curve, and return the result as an instance of 
     * GJInvertedRay2D.
     * @see GJInvertedRay2D#reverse()
     */
    public GJInvertedRay2D reverse() {
        return new GJInvertedRay2D(x0, y0, -dx, -dy);
    }

    // ===================================================================
    // methods implementing the GJShape2D interface

    /** Always returns false, because a ray is not bounded. */
    public boolean isBounded() {
        return false;
    }

    public boolean contains(double x, double y) {
        if (!this.supportContains(x, y))
            return false;
        double t = this.positionOnLine(x, y);
        return t>-GJShape2D.ACCURACY;
    }

    public GJBox2D boundingBox() {
        double t = Double.POSITIVE_INFINITY;
        GJPoint2D p0 = new GJPoint2D(x0, y0);
        GJPoint2D p1 = new GJPoint2D(t * dx, t* dy);
        return new GJBox2D(p0, p1);
    }

    @Override
	public GJRay2D transform(GJAffineTransform2D trans) {
		double[] tab = trans.coefficients();
		double x1 = x0 * tab[0] + y0 * tab[1] + tab[2];
		double y1 = x0 * tab[3] + y0 * tab[4] + tab[5];
		return new GJRay2D(x1, y1,
				dx * tab[0] + dy * tab[1], dx * tab[3] + dy * tab[4]);
	}


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if (!(obj instanceof GJRay2D))
            return false;
        GJRay2D ray = (GJRay2D) obj;

        if (Math.abs(x0-ray.x0)>eps)
            return false;
        if (Math.abs(y0-ray.y0)>eps)
            return false;
        if (Math.abs(dx-ray.dx)>eps)
            return false;
        if (Math.abs(dy-ray.dy)>eps)
            return false;
        
        return true;
    }

    // ===================================================================
    // methods implementing the Object interface

    @Override
    public String toString() {
        return new String("GJRay2D(" + x0 + "," + y0 + "," +
        		dx + "," + dy + ")");
    }
    
    @Override
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
        if (!(obj instanceof GJRay2D))
            return false;
        GJRay2D that = (GJRay2D) obj;
        
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
    public GJRay2D clone() {
        return new GJRay2D(x0, y0, dx, dy);
        
    }
}
