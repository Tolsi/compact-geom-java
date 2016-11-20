/**
 * general class for all transformation in the plane, linear or not linear.
 */
public interface GJTransform2D {

    /** Transforms a point */
    public abstract GJPoint2D transform(GJPoint2D src);

    /** Transforms an array of points, and returns the transformed points. */

    public abstract GJPoint2D[] transform(GJPoint2D[] src, GJPoint2D[] dst);

}
