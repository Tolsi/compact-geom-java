/* File ContinuousCurve2D.java 
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
import java.util.*;






/**
 * Interface for all curves which can be drawn with one stroke. This includes
 * closed curves (ellipses, polygon boundaries...), infinite curves (straight
 * lines, parabolas, ...), and 'finite' curves, such as polylines, conic arcs,
 * line segments, splines... Note that an hyperbola is compound of 2 continuous
 * curves.
 * <p>
 * Such curves accept parametric representation, in the form :
 * <code>p(t)={x(t),y(t)}</code>, with <code>t</code> contained in
 * appropriate domain. Bounds of domain of definition can be obtained by methods
 * <code>t0()</code> and <code>t1()</code>.
 * <p>
 */

public interface GJContinuousCurve2D extends GJCurve2D {

    // ===================================================================
    // constants

    // ===================================================================
    // general methods

    /**
     * Returns true if the curve makes a loop, that is come back to starting
     * point after covering the path.
     */
    public abstract boolean isClosed();

    /**
     * Computes the left tangent at the given position. 
     * If the curve is smooth at position <code>t</code>, the result is the
     * same as the tangent computed for the corresponding smooth curve, and 
     * is equal to the result of rightTangent(double).
     * If the position <code>t</code> corresponds to a singular point, the
     * tangent of the smooth portion before <code>t</code> is computed.  
     * @param t the position on the curve
     * @return the left tangent vector at the curve for position t
     */
    public GJVector2D leftTangent(double t);
    
    /**
     * Computes the right tangent at the given position. 
     * If the curve is smooth at position <code>t</code>, the result is the
     * same as the tangent computed for the corresponding smooth curve, and 
     * is equal to the result of leftTangent(double).
     * If the position <code>t</code> corresponds to a singular point, the
     * tangent of the smooth portion after <code>t</code> is computed.  
     * @param t the position on the curve
     * @return the right tangent vector at the curve for position t
     */
    public GJVector2D rightTangent(double t);
    
    /**
     * Computes the curvature at the given position. The curvature is finite
     * for positions <code>t</code> that correspond to smooth parts, and is
     * infinite for singular points. 
     * @param t the position on the curve
     * @return the curvature of the curve for position t
     */
    public abstract double curvature(double t);

   
    /**
     * Returns a set of smooth curves.
     */
    public abstract Collection<? extends GJSmoothCurve2D> smoothPieces();

    /**
     * Returns an approximation of the curve as a polyline with <code>n</code>
     * line segments. If the curve is closed, the method should return an
     * instance of GJLinearRing2D. Otherwise, it returns an instance of
     * GJPolyline2D.
     * 
     * @param n the number of line segments
     * @return a polyline with <code>n</code> line segments.
     */
    public abstract GJLinearCurve2D asPolyline(int n);

    /**
     * Append the path of the curve to the given path.
     * 
     * @param path a path to modify
     * @return the modified path
     */
    public abstract java.awt.geom.GeneralPath appendPath(
            java.awt.geom.GeneralPath path);

    // ===================================================================
    // GJCurve2D methods

    /* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#reverse(D)
	 */
    public abstract GJContinuousCurve2D reverse();

    /* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#subCurve(double, double)
	 */
    public abstract GJContinuousCurve2D subCurve(double t0, double t1);

    // ===================================================================
    // GJShape2D methods

    /* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#clip(GJBox2D)
	 */
    public abstract GJCurveSet2D<? extends GJContinuousCurve2D> clip(GJBox2D box);

    /* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#transform(GJAffineTransform2D)
	 */
    public abstract GJContinuousCurve2D transform(GJAffineTransform2D trans);
}
