/**
 * File: 	GJCirculinearRing2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */



/**
 * Interface for circulinear contours which are both bounded and closed.
 * @author dlegland
 * @see GJGenericCirculinearRing2D
 */
public interface GJCirculinearRing2D extends GJCirculinearContour2D {

	public GJCirculinearDomain2D domain();
	public GJCirculinearRing2D parallel(double d);
	public GJCirculinearRing2D reverse();
}
