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
