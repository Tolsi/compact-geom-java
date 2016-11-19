/**
 * File: 	SmoothBoundary2D.java
 * Project: javaGeom-circulinear
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 juil. 09
 */





/**
 * Tagging interface to represent in unified way smooth curves that are
 * also contours.
 * @author dlegland
 *
 */
public interface GJSmoothContour2D
extends GJSmoothOrientedCurve2D, GJContour2D {

    // ===================================================================
    // redefines declaration of some interfaces

	/**
	 * Transforms the contour, and returns an instance of GJSmoothContour2D.
	 */
	GJSmoothContour2D transform(GJAffineTransform2D trans);

	/**
	 * Reverses the contour, and returns an instance of GJSmoothContour2D.
	 */
	GJSmoothContour2D reverse();
}
