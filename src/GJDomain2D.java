import java.awt.*;
import java.util.Collection;
// Imports

/**
 * Interface for shapes that draws an 'interior' and an 'exterior'. An
 * AbstractDomain2D can be defined with a non-self intersecting set of GJCurve2D,
 * and contains all points lying 'on the left' of the parent curve.
 * <p>
 * Some Shape may seem very similar, for example GJConic2D and ConicCurve2D. The
 * reason is that a point can be contained in a GJConic2D but not in the
 * ConicCurve2D.
 */
public interface GJDomain2D extends GJShape2D {

    /**
     * Returns the boundary of the set. This boundary is either a continuous non
     * intersecting curve (connected domain), or a set of non intersecting
     * continuous curve (one continuous non-intersection for each connected part
     * of the domain).
     * <p>
     * The returned curve is oriented, with an interior and an exterior.
     * 
     * @return the boundary of the domain
     */
    public abstract GJBoundary2D boundary();

    /**
     * Returns the set of contours that enclose this domain. 
     * The result is a collection of shapes that implement the GJContour2D
     * interface. 
     * @see math.geom2d.domain.Contour2D
     */
    public abstract Collection<? extends GJContour2D> contours();
    
    /**
     * Returns the domain which complements this domain in the plane.
     * 
     * @return the complement of this domain.
     * @since 0.6.3
     */
    public abstract GJDomain2D complement();

    /**
     * Returns an approximation of the domain as a polygon, or a MultiPolygon.
     * @return a polygon
     * @since 0.10.2
     */
    public abstract GJPolygon2D asPolygon(int n);
    
    public abstract GJDomain2D transform(GJAffineTransform2D transform);

    public abstract GJDomain2D clip(GJBox2D box);

    /**
     * Draws the boundary of the domain, using current Stroke and color.
     * 
     * @param g2 the Graphics to draw on
     * @since 0.6.3
     */
    public abstract void draw(Graphics2D g2);

    /**
     * Fills the interior of the domain, using the Graphics current Paint.
     * 
     * @param g2 the Graphics to fill on
     * @since 0.6.3
     */
    public abstract void fill(Graphics2D g2);
}
