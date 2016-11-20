/**
 * A curve that can be inscribed in a straight line, like a ray, a straight
 * line, or a line segment. Classes implementing this interface can be
 * discontinuous, contrary to the interface GJLinearElement2D.
 * 
 * @author dlegland
 */
public interface GJLinearShape2D extends GJCirculinearCurve2D {

	/**
	 * Returns the straight line that contains this linear shape. 
	 * The direction is the same, and if possible the direction vector 
	 * should be the same. 
	 * @return the straight line that contains this linear shape 
	 */
    public abstract GJStraightLine2D supportingLine();

    /**
     * Returns the angle with axis (O,i), counted counter-clockwise. Result 
     * is given between 0 and 2*pi.
     */
    public abstract double horizontalAngle();

    /**
     * Returns a point in the linear shape.
     * 
     * @return a point in the linear shape.
     */
    public abstract GJPoint2D origin();

    /**
     * Return one direction vector of the linear shape.
     * 
     * @return a direction vector
     */
    public abstract GJVector2D direction();

    /**
     * Returns the unique intersection with a linear shape. If the intersection
     * doesn't exist (parallel lines), returns null.
     */
    public abstract GJPoint2D intersection(GJLinearShape2D line);

    /**
     * Transforms this linear shape. 
     */
    public GJLinearShape2D transform(GJAffineTransform2D trans);
}
