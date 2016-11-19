/* File SmoothCurve2D.java 
 *
 * Project : Java Geometry Library
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

// package



// Imports




/**
 * Interface for smooth and continuous curves. Such curves accept first and
 * second derivatives at every point, and can be drawn with a parametric
 * representation for every values of t comprised between T0 and T1. 
 * Every instance of GJCurve2D is a compound of several GJSmoothCurve2D.
 */
public interface GJSmoothCurve2D extends GJContinuousCurve2D {

	/**
	 * Returns the tangent of the curve at the given position. 
	 * @param t a position on the curve
	 * @return the tangent vector computed for position t
	 * @see #normal(double) 
	 */
    public abstract GJVector2D tangent(double t);

	/**
	 * Returns the normal vector of the curve at the given position. 
	 * @param t a position on the curve
	 * @return the normal vector computed for position t
	 * @see #tangent(double)
	 */
    public abstract GJVector2D normal(double t);

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#reverse()
	 */
    public abstract GJSmoothCurve2D reverse();

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#subCurve(double, double)
	 */
    public abstract GJSmoothCurve2D subCurve(double t0, double t1);

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#clip(GJBox2D)
	 */
    public abstract GJCurveSet2D<? extends GJSmoothCurve2D> clip(GJBox2D box);

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#transform(GJAffineTransform2D)
	 */
    public abstract GJSmoothCurve2D transform(GJAffineTransform2D trans);
}
