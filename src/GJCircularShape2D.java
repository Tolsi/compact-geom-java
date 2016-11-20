/**
 * Tagging interface for grouping GJCircle2D and GJCircleArc2D.
 * @author dlegland
 *
 */
public interface GJCircularShape2D
extends GJCirculinearElement2D, GJSmoothOrientedCurve2D {

	
    // ===================================================================
    // method specific to GJCircularShape2D

	/**
	 * Returns the circle that contains this shape.
	 */
	public GJCircle2D supportingCircle();

    // ===================================================================
    // methods inherited from GJShape2D and GJCurve2D

	public GJCurveSet2D<? extends GJCircularShape2D> clip(GJBox2D box);
	public GJCircularShape2D subCurve(double t0, double t1);
	public GJCircularShape2D reverse();
}
