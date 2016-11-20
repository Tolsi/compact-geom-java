/**
 * Interface for all bijective transformations in the euclidean plane. A
 * transformation is a bijection if there is a direct relation from sources to
 * output. In this case, this means we can find the inverse transformation for
 * each bijection.
 */
public interface GJBijection2D extends GJTransform2D {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    // ===================================================================
    // constructors

    // ===================================================================
    // accessors

    // ===================================================================
    // modifiers

    // ===================================================================
    // general methods

    public abstract GJBijection2D invert();
}
