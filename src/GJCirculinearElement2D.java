/**
 * File: 	GJCirculinearElement2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */








/**
 * <p>
 * Circulinear elements are lowest level of circulinear curve: each
 * circulinear curve can be divided into a set of circulinear elements.</p>
 * <p>
 * Circulinear elements can be either linear elements (implementations of 
 * GJLinearShape2D), or circular elements (circle or circle arcs).</p>
 * 
 * @author dlegland
 *
 */
public interface GJCirculinearElement2D extends GJCirculinearContinuousCurve2D,
		GJSmoothOrientedCurve2D {

	public GJCirculinearElement2D parallel(double d);
	public GJCirculinearElement2D transform(GJCircleInversion2D inv);
	
	public GJCurveSet2D<? extends GJCirculinearElement2D> clip(GJBox2D box);
    public GJCirculinearElement2D subCurve(double t0, double t1);
	public GJCirculinearElement2D reverse();
}
