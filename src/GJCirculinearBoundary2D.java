/**
 * File: 	GJCirculinearBoundary2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */


import java.util.Collection;







/**
 * A Boundary which is composed of Circulinear elements.
 * @author dlegland
 *
 */
public interface GJCirculinearBoundary2D extends GJCirculinearCurve2D, GJBoundary2D {
	
    // ===================================================================
    // redefines declaration of some interfaces

	public GJCirculinearDomain2D domain();
	public GJCirculinearBoundary2D parallel(double d);
    public Collection<? extends GJCirculinearContour2D> continuousCurves();
	public GJCurveSet2D<? extends GJCirculinearContinuousCurve2D> clip(GJBox2D box);
    public GJCirculinearBoundary2D transform(GJCircleInversion2D inv);
	public GJCirculinearBoundary2D reverse();
}
