/**
 * File: 	GJCirculinearContinuousCurve2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */


import java.util.Collection;







/**
 * A tagging interface defining a circulinear curve which is continuous.
 * @author dlegland
 *
 */
public interface GJCirculinearContinuousCurve2D
extends GJCirculinearCurve2D, GJContinuousOrientedCurve2D {
	
	// ===================================================================
    // redefines declaration of GJCirculinearCurve2D interfaces

	public GJCirculinearContinuousCurve2D parallel(double d);
	public GJCirculinearContinuousCurve2D transform(GJCircleInversion2D inv);
	
	// ===================================================================
    // redefines declaration of some parent interfaces

	/**
     * Returns a set of circulinear elements, which are basis for circulinear
     * curves.
     */
    public abstract Collection<? extends GJCirculinearElement2D> smoothPieces();

    public GJCurveSet2D<? extends GJCirculinearContinuousCurve2D> clip(GJBox2D box);
	public GJCirculinearContinuousCurve2D subCurve(double t0, double t1);
	public GJCirculinearContinuousCurve2D reverse();
}
