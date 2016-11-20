/**
 * circle inversion : performs a bijection between points outside the circle and
 * points inside the circle.
 */
public class GJCircleInversion2D implements GJBijection2D {

    // ===================================================================
    // static constructors

	public static GJCircleInversion2D create(GJPoint2D center, double radius) {
		return new GJCircleInversion2D(center, radius);
	}
	
	public static GJCircleInversion2D create(GJCircle2D circle) {
		return new GJCircleInversion2D(circle);
	}
	
    // ===================================================================
    // class variables
	
    protected GJPoint2D center;
    protected double radius;

    // ===================================================================
    // constructors

    /**
     * Construct a new circle inversion based on the unit circle centered on the
     * origin.
     */
    public GJCircleInversion2D() {
        this.center = new GJPoint2D();
        this.radius = 1;
    }

    public GJCircleInversion2D(GJCircle2D circle) {
        this.center = circle.center();
        this.radius = circle.radius();
    }

    public GJCircleInversion2D(GJPoint2D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public GJCircleInversion2D(double xc, double yc, double radius) {
        this.center = new GJPoint2D(xc, yc);
        this.radius = radius;
    }

    // ===================================================================
    // accessors

    public GJPoint2D center() {
    	return center;
    }
    
    public double radius() {
    	return radius;
    }
   
    
    // ===================================================================
    // methods implementing the GJBijection2D interface
    
   /**
    * Returns this circle inversion.
    */
    public GJCircleInversion2D invert() {
    	return this;
    }

    // ===================================================================
    // methods implementing the GJTransform2D interface

    public GJPoint2D transform(GJPoint2D pt) {
    	double r = radius;
        
        double d = r*r/ GJPoint2D.distance(pt, center);
        double theta = GJAngle2D.horizontalAngle(center, pt);
        return GJPoint2D.createPolar(center, d, theta);
    }

    /** Transforms an array of points, and returns the transformed points. */
    public GJPoint2D[] transform(GJPoint2D[] src, GJPoint2D[] dst) {

        double d, theta;
        double xc, yc, r;

        // createFromCollection the array if necessary
        if (dst==null)
            dst = new GJPoint2D[src.length];

        // createFromCollection instances of Points if necessary
        if (dst[0]==null)
            for (int i = 0; i<dst.length; i++)
                dst[i] = new GJPoint2D();

        xc = center.x();
        yc = center.y();
        r  = radius;

        // transform each point
        for (int i = 0; i<src.length; i++) {
            d = GJPoint2D.distance(src[i].x(), src[i].y(), xc, yc);
            d = r*r/d;
            theta = Math.atan2(src[i].y()-yc, src[i].x()-xc);
            dst[i] = new GJPoint2D(d*Math.cos(theta), d*Math.sin(theta));
        }

        return dst;
    }
}
