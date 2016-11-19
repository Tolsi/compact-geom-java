/**
 * File: 	CircleLine2D.java
 * Project: javaGeom-circulinear
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 juil. 09
 */






/**
 * Tagging interface to be able to consider in a same way circles and lines.
 * @author dlegland
 *
 */
public interface CircleLine2D extends CirculinearContour2D,
		CirculinearElement2D, SmoothContour2D {
	
    // ===================================================================
    // redefines declaration of some interfaces

	public CircleLine2D parallel(double dist);
	public CircleLine2D transform(CircleInversion2D inv);
	public CircleLine2D reverse();
}
