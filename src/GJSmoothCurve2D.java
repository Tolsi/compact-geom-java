/**
 * Interface for smooth and continuous curves. Such curves accept first and
 * second derivatives at every point, and can be drawn with a parametric
 * representation for every values of t comprised between T0 and T1. 
 * Every instance of GJCurve2D is a compound of several GJSmoothCurve2D.
 */
public interface GJSmoothCurve2D extends GJContinuousCurve2D {

	/**
	 * Returns the tangent of the curve at the given position. 
	 * @param t a position on the curve
	 * @return the tangent vector computed for position t
	 * @see #normal(double) 
	 */
    public abstract GJVector2D tangent(double t);

	/**
	 * Returns the normal vector of the curve at the given position. 
	 * @param t a position on the curve
	 * @return the normal vector computed for position t
	 * @see #tangent(double)
	 */
    public abstract GJVector2D normal(double t);

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#reverse()
	 */
    public abstract GJSmoothCurve2D reverse();

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#subCurve(double, double)
	 */
    public abstract GJSmoothCurve2D subCurve(double t0, double t1);

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#clip(GJBox2D)
	 */
    public abstract GJCurveSet2D<? extends GJSmoothCurve2D> clip(GJBox2D box);
}
