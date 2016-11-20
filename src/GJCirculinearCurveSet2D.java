/**
 * A specialization of GJCurveSet2D that accepts only instances of
 * GJCirculinearCurve2D.
 * @author dlegland
 *
 */
public interface GJCirculinearCurveSet2D<T extends GJCirculinearCurve2D>
extends GJCurveSet2D<T>, GJCirculinearCurve2D {
 
    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	public GJCirculinearCurveSet2D<? extends GJCirculinearCurve2D> clip(GJBox2D box);

	public GJCirculinearCurveSet2D<? extends GJCirculinearCurve2D> subCurve(
            double t0, double t1);
	
	public GJCirculinearCurveSet2D<? extends GJCirculinearCurve2D> reverse();
}
