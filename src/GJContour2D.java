/**
 * A continuous oriented curve which delimits a connected planar domain.
 * A contour can be closed (like ellipse, circle, closed polyline...) or open
 * (parabola, straight line...).
 * @author dlegland
 * @since 0.9.0
 */
public interface GJContour2D extends GJBoundary2D, GJContinuousOrientedCurve2D {

	/**
	 * Computes the reversed contour. 
	 */
    public abstract GJContour2D reverse();

	/**
	 * Computes the transformed contour. 
	 */
    public abstract GJContour2D transform(GJAffineTransform2D trans);
}
