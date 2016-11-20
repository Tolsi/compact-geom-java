import java.awt.*;
import java.util.Collection;


/**
 * A GJBoundary2D is the curve which defines the contour of a domain in the plane.
 * It is compound of one or several non-intersecting and oriented curves.
 * Curves composing the boundary implements the GJContour2D interface.
 * 
 * @see GJContour2D
 * @author dlegland
 */
public interface GJBoundary2D extends GJOrientedCurve2D {

    /**
     * Returns true if the point is 'inside' the domain bounded by the curve.
     * 
     * @param pt a point in the plane
     * @return true if the point is on the left side of the curve.
     */
    public boolean isInside(GJPoint2D pt);

    /**
     * Overloads the declaration of continuousCurves to return a collection
     * of contours (instances of GJContour2D).
     */
    public Collection<? extends GJContour2D> continuousCurves();

    /**
     * Returns the domain delimited by this boundary.
     * 
     * @return the domain delimited by this boundary
     */
    public GJDomain2D domain();

    /**
     * Forces the subclasses to return an instance of GJBoundary2D.
     */
    public GJBoundary2D reverse();

    /**
     * Forces the subclasses to return an instance of GJBoundary2D.
     */
    public GJBoundary2D transform(GJAffineTransform2D trans);

    /**
     * Fills the interior of the boundary, using the Graphics current Paint.
     * 
     * @param g2 the Graphics to fill on
     */
    public void fill(Graphics2D g2);
}
