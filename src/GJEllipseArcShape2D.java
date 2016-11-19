/**
 * 
 */





/**
 * An interface to gather GJCircleArc2D and GJEllipseArc2D.
 * @author dlegland
 *
 */
public interface GJEllipseArcShape2D extends GJSmoothOrientedCurve2D {
	
	public GJEllipseArcShape2D reverse();
	public GJEllipseArcShape2D subCurve(double t0, double t1);
	
	public GJEllipseArcShape2D transform(GJAffineTransform2D trans);
}
