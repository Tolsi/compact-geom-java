import java.awt.*;
import java.util.Collection;


/**
 * A single continuous oriented curve, which defines the boundary of a planar
 * domain. The boundary curve is composed of several continuous and oriented
 * curves linked together to form a continuous curve. The resulting boundary
 * curve is either a closed curve, or an infinite curve at both ends.
 * 
 * @author dlegland
 */
public class GJBoundaryPolyCurve2D<T extends GJContinuousOrientedCurve2D> extends
        GJPolyOrientedCurve2D<T> implements GJContour2D {

    // ===================================================================
    // Static methods

    /**
     * Static factory for creating a new GJBoundaryPolyCurve2D from a collection
     * of curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousOrientedCurve2D> GJBoundaryPolyCurve2D<T> createBoundaryPolyCurve2DFromCollection(
    		Collection<T> curves) {
    	return new GJBoundaryPolyCurve2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJBoundaryPolyCurve2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousOrientedCurve2D> GJBoundaryPolyCurve2D<T> createBoundaryPolyCurve2DFromCollection(
    		T... curves) {
    	return new GJBoundaryPolyCurve2D<T>(curves);
    }

    
    // ===================================================================
    // Constructors

    /**
     * Creates an empty GJBoundaryPolyCurve2D.
     */
    public GJBoundaryPolyCurve2D() {
        super();
    }

    /**
     * Creates a GJBoundaryPolyCurve2D by reserving space for n curves.
     * @param n the number of curves to store
     */
    public GJBoundaryPolyCurve2D(int n) {
        super(n);
    }

    /**
     * Creates a GJBoundaryPolyCurve2D from the specified set of curves.
     */
    public GJBoundaryPolyCurve2D(T... curves) {
        super(curves);
    }

    /**
     * Creates a GJBoundaryPolyCurve2D from the specified set of curves.
     */
    public GJBoundaryPolyCurve2D(Collection<? extends T> curves) {
        super(curves);
    }

    
    // ===================================================================
    // Methods overriding GJCurveSet2D methods

    /**
     * Overrides the isClosed() in the following way: return true if all curves
     * are bounded. If at least one curve is unbounded, return false.
     */
    @Override
    public boolean isClosed() {
        for (T curve : curves) {
            if (!curve.isBounded())
                return false;
        }
        return true;
    }

    // ===================================================================
    // Methods implementing GJBoundary2D interface

    public Collection<GJBoundaryPolyCurve2D<T>> continuousCurves() {
    	return wrapCurve(this);
    }

    public GJDomain2D domain() {
        return new GJGenericDomain2D(this);
    }

    public void fill(Graphics2D g2) {
        g2.fill(this.getGeneralPath());
    }

    // ===================================================================
    // Methods implementing GJOrientedCurve2D interface

    @Override
    public GJBoundaryPolyCurve2D<? extends GJContinuousOrientedCurve2D> reverse() {
        GJContinuousOrientedCurve2D[] curves2 =
        	new GJContinuousOrientedCurve2D[curves.size()];
        int n = curves.size();
        for (int i = 0; i < n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        return new GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>(curves2);
    }

    @Override
    public GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D> transform(
            GJAffineTransform2D trans) {
    	// createFromCollection result curve
        GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D> result =
        	new GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>(curves.size());
        
        // reverse each curve and add it to result
        for (GJContinuousOrientedCurve2D curve : curves)
            result.add(curve.transform(trans));
        return result;
    }
}
