/* File Conic2D.java 
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
 * Interface for all conic curves: parametric conics, or ellipses, parabolas,
 * and hyperbolas. Degenerate conics are also encompassed by this interface.
 */
public interface GJConic2D extends GJBoundary2D {

    // ===================================================================
    // constants

    /**
     * The different types of conic.
     */
    public enum Type {
        /**
         * Degenerate conic, for example a conic given by the equation
         * <code>x^2+1=0</code>)
         */
        NOT_A_CONIC,
        /** Ellipse */
        ELLIPSE,
        /** Hyperbola */
        HYPERBOLA,
        /** Parabola */
        PARABOLA,
        /** Circle */
        CIRCLE,
        /** Straight Line */
        STRAIGHT_LINE,
        /** Union of two lines */
        TWO_LINES,
        /** Single point */
        POINT;
    }

    // ===================================================================
    // class variables

    // ===================================================================
    // constructors

    // ===================================================================
    // accessors

    // type accessors ------------

    public abstract Type conicType();

    /**
     * Returns the coefficient of the Cartesian representation of the conic.
     * Cartesian equation has the form :
     * <p>
     * a*x^2 + b*x*y + c*y^2 + d*x + e*y + f
     * <p>
     * The length of the array is then of size 6.
     */
    public abstract double[] conicCoefficients();

    /**
     * Returns the eccentricity of the conic.
     */
    public abstract double eccentricity();

    // ===================================================================
    // modifiers

    public abstract GJConic2D reverse();

    public abstract GJConic2D transform(GJAffineTransform2D trans);

    public abstract GJCurveSet2D<? extends GJContinuousOrientedCurve2D> clip(
            GJBox2D box);
}