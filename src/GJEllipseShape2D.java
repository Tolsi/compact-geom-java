/**
 * 
 */


import java.util.Collection;





/**
 * A common interface for GJCircle2D and GJEllipse2D.
 * @author dlegland
 *
 */
public interface GJEllipseShape2D extends GJSmoothContour2D, GJConic2D {

    // ===================================================================
    // methods specific to GJEllipseShape2D interface

	/**
	 * Returns center of the ellipse shape.
	 */
	public GJPoint2D center();
    
    /**
     * Returns true if this ellipse shape is similar to a circle, i.e. has
     * same length for both semi-axes.
     */
	public boolean isCircle();
	
	/**
	 * If an ellipse shape is direct, it is the boundary of a convex domain.
	 * Otherwise, the complementary of the bounded domain is convex.
	 */
	public boolean isDirect();

	// ===================================================================
    // methods of GJCurve2D interface

    public GJEllipseShape2D reverse();

    public Collection<? extends GJEllipseShape2D> continuousCurves();

    // ===================================================================
    // methods of GJShape2D interface

    public GJEllipseShape2D transform(GJAffineTransform2D trans);
    
}
