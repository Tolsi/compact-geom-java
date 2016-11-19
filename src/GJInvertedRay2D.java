/* File InvertedRay2D.java 
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



import java.awt.geom.GeneralPath;




// Imports

/**
 * Inverted ray is defined from an origin and a direction vector. It is composed
 * of all points satisfying the parametric equation:
 * <p>
 * <code>x(t) = x0+t*dx<code><br>
 * <code>y(t) = y0+t*dy<code></p> 
 * with <code>t<code> comprised between -INFINITY and 0.
 * This is complementary class to GJRay2D.
 */
public class GJInvertedRay2D extends GJAbstractLine2D implements Cloneable {

    // ===================================================================
    // Static factory

    /**
     * Static factory for creating a new inverted ray with given direction
     * to target.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJInvertedRay2D create(GJPoint2D target, GJVector2D direction) {
    	return new GJInvertedRay2D(target, direction);
    }
    
    
    // ===================================================================
    // constructors

    /**
     * Empty constructor for GJRay2D. Default is ray starting at origin, and
     * having a slope of 1*dx and 0*dy.
     */
    public GJInvertedRay2D() {
        this(0, 0, 1, 0);
    }

    /**
     * Creates a new GJRay2D, originating from
     * <code>point1<\code>, and going in the
     * direction of <code>point2<\code>.
     */
    public GJInvertedRay2D(GJPoint2D point1, GJPoint2D point2) {
        this(point1.x(), point1.y(), 
        		point2.x()-point1.x(), 
        		point2.y()-point1.y());
    }

    /**
     * Creates a new GJRay2D, originating from point
     * <code>(x1,y1)<\code>, and going 
     * in the direction defined by vector <code>(dx, dy)<\code>.
     */
    public GJInvertedRay2D(double x1, double y1, double dx, double dy) {
        super(x1, y1, dx, dy);
    }

    /**
     * Creates a new GJRay2D, originating from point <code>point<\code>, and going
     * in the direction defined by vector <code>(dx,dy)<\code>.
     */
    public GJInvertedRay2D(GJPoint2D point, double dx, double dy) {
        this(point.x(), point.y(), dx, dy);
    }

    /**
     * Creates a new GJRay2D, originating from point <code>point<\code>, and going
     * in the direction specified by <code>vector<\code>.
     */
    public GJInvertedRay2D(GJPoint2D point, GJVector2D vector) {
        this(point.x(), point.y(), vector.x(), vector.y());
    }

    /**
     * Creates a new GJRay2D, originating from point <code>point<\code>, and going
     * in the direction specified by <code>angle<\code> (in radians).
     */
    public GJInvertedRay2D(GJPoint2D point, double angle) {
        this(point.x(), point.y(), Math.cos(angle), Math.sin(angle));
    }

    /**
     * Creates a new GJRay2D, originating from point
     * <code>(x, y)<\code>, and going 
     * in the direction specified by <code>angle<\code> (in radians).
     */
    public GJInvertedRay2D(double x, double y, double angle) {
        this(x, y, Math.cos(angle), Math.sin(angle));
    }

    /**
     * Define a new Ray, with same characteristics as given object.
     */
    public GJInvertedRay2D(GJLinearShape2D line) {
        super(line.origin(), line.direction());
    }

    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/**
	 * Returns another instance of GJInvertedRay2D, parallel to this one,
	 * and located at the given distance.
	 * @see math.geom2d.circulinear.CirculinearCurve2D#parallel(double)
	 */
	public GJInvertedRay2D parallel(double d) {
        double dd = Math.hypot(dx, dy);
		return new GJInvertedRay2D(x0 + dy * d / dd, y0 - dx * d / dd, dx, dy);
	}

    // ===================================================================
    // methods implementing the GJContinuousCurve2D interface

    /** Throws an infiniteShapeException */
    public GeneralPath appendPath(GeneralPath path) {
        throw new GJUnboundedShape2DException(this);
    }

    /** Throws an infiniteShapeException */
    public java.awt.geom.GeneralPath getGeneralPath() {
        throw new GJUnboundedShape2DException(this);
    }

    // ===================================================================
    // methods implementing the GJCurve2D interface

    public GJPoint2D point(double t) {
        t = Math.min(t, 0);
		return new GJPoint2D(x0 + t * dx, y0 + t * dy);
    }

    /**
     * Returns Negative infinity.
     */
    public double t0() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }

    /**
     * Returns 0.
     */
    public double t1() {
        return 0;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }

    /**
     * Reverses this curve, and return the result as an instance of GJRay2D.
     * @see GJRay2D#reverse()
     */
    public GJRay2D reverse() {
        return new GJRay2D(x0, y0, -dx, -dy);
    }

    // ===================================================================
    // methods implementing the GJShape2D interface

    /** Always returns false, because n inverted ray is not bounded. */
    public boolean isBounded() {
        return false;
    }

    public boolean contains(double x, double y) {
        if (!this.supportContains(x, y))
            return false;
        double t = this.positionOnLine(x, y);
        return t < GJShape2D.ACCURACY;
    }

    public GJBox2D boundingBox() {
        double t = Double.NEGATIVE_INFINITY;
        GJPoint2D p0 = new GJPoint2D(x0, y0);
        GJPoint2D p1 = new GJPoint2D(t * dx, t* dy);
		return new GJBox2D(p0, p1);
	}

    @Override
	public GJInvertedRay2D transform(GJAffineTransform2D trans) {
		double[] tab = trans.coefficients();
		double x1 = x0 * tab[0] + y0 * tab[1] + tab[2];
		double y1 = x0 * tab[3] + y0 * tab[4] + tab[5];
		return new GJInvertedRay2D(x1, y1,
				dx * tab[0] + dy * tab[1], dx * tab[3] + dy * tab[4]);
	}

    // ===================================================================
    // methods implementing the Shape interface


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
		if (this == obj)
			return true;
    	
		if (!(obj instanceof GJInvertedRay2D))
			return false;
		GJInvertedRay2D ray = (GJInvertedRay2D) obj;
		if (Math.abs(x0 - ray.x0) > eps)
			return false;
		if (Math.abs(y0 - ray.y0) > eps)
			return false;
		if (Math.abs(dx - ray.dx) > eps)
			return false;
		if (Math.abs(dy - ray.dy) > eps)
			return false;

		return true;
    }

    // ===================================================================
    // methods implementing the Object interface

    @Override
    public String toString() {
        return new String("GJInvertedRay2D(" + x0 + "," + y0 + "," +
        		dx + "," + dy + ")");
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GJInvertedRay2D))
			return false;
		GJInvertedRay2D that = (GJInvertedRay2D) obj;
		
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
    public GJInvertedRay2D clone() {
        return new GJInvertedRay2D(x0, y0, dx, dy);
    }

}
