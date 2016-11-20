import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * A GJContourArray2D is a set of contours. Each contour in the set defines its
 * own domain.
 * <p>
 * 
 * @author dlegland
 */
public class GJContourArray2D<T extends GJContour2D> extends GJCurveArray2D<T>
implements GJBoundary2D {

    // ===================================================================
    // static methods

    /**
     * Static factory for creating a new GJContourArray2D from a collection of
     * contours.
     * @since 0.8.1
     */
    public static <T extends GJContour2D> GJContourArray2D<T> createFromCollection(
    		Collection<T> curves) {
    	return new GJContourArray2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJContourArray2D from an array of
     * contours.
     * @since 0.8.1
     */
    public static <T extends GJContour2D> GJContourArray2D<T> createFromCollection(
    		T... curves) {
    	return new GJContourArray2D<T>(curves);
    }

    // ===================================================================
    // Constructors

    public GJContourArray2D() {
    }

    public GJContourArray2D(int size) {
    	super(size);
    }

    public GJContourArray2D(T... curves) {
        super(curves);
    }

    public GJContourArray2D(Collection<? extends T> curves) {
        super(curves);
    }

    public GJContourArray2D(T curve) {
        super();
        this.add(curve);
    }

    
    // ===================================================================
    // Methods implementing GJBoundary2D interface

    public Collection<? extends T> continuousCurves() {
    	return Collections.unmodifiableCollection(this.curves);
    }

    public GJDomain2D domain() {
        return new GJGenericDomain2D(this);
    }

    public void fill(Graphics2D g2) {
        g2.fill(this.getGeneralPath());
    }

    // ===================================================================
    // Methods implementing GJOrientedCurve2D interface

    public double windingAngle(GJPoint2D point) {
        double angle = 0;
        for (GJOrientedCurve2D curve : this.curves())
            angle += curve.windingAngle(point);
        return angle;
    }

    public double signedDistance(GJPoint2D p) {
        return signedDistance(p.x(), p.y());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#signedDistance(math.geom2d.GJPoint2D)
     */
    public double signedDistance(double x, double y) {
        double minDist = Double.POSITIVE_INFINITY;
        double dist = Double.POSITIVE_INFINITY;

        for (GJOrientedCurve2D curve : this.curves()) {
            dist = Math.min(dist, curve.signedDistance(x, y));
            if (Math.abs(dist)<Math.abs(minDist))
                minDist = dist;
        }
        return minDist;
    }

    public boolean isInside(GJPoint2D point) {
        return this.signedDistance(point.x(), point.y()) < 0;
    }

    // ===================================================================
    // Methods implementing GJCurve2D interface

    @Override
    public GJContourArray2D<? extends GJContour2D> reverse() {
        GJContour2D[] curves2 = new GJContour2D[curves.size()];
        int n = curves.size();
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        return new GJContourArray2D<GJContour2D>(curves2);
    }

    @Override
    public GJCurveSet2D<? extends GJContinuousOrientedCurve2D> subCurve(
            double t0, double t1) {
        // get the subcurve
        GJCurveSet2D<? extends GJCurve2D> curveSet = super.subCurve(t0, t1);

        // createFromCollection subcurve array
        ArrayList<GJContinuousOrientedCurve2D> curves =
        	new ArrayList<GJContinuousOrientedCurve2D>();
        for (GJCurve2D curve : curveSet.curves())
            curves.add((GJContinuousOrientedCurve2D) curve);

        // Create CurveSet for the result
        return new GJCurveArray2D<GJContinuousOrientedCurve2D>(curves);
    }

    // ===================================================================
    // Methods implementing the GJShape2D interface

    /**
     * Clip the curve by a box. The result is an instance of
     * GJCurveSet2D<GJContinuousOrientedCurve2D>, which contains
     * only instances of GJContinuousOrientedCurve2D.
     * If the curve is not clipped, the result is an instance of 
     * GJCurveSet2D<GJContinuousOrientedCurve2D> which contains 0 curves.
     */
    @Override
    public GJCurveSet2D<? extends GJContinuousOrientedCurve2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJContinuousOrientedCurve2D> result =
        	new GJCurveArray2D<GJContinuousOrientedCurve2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJContinuousOrientedCurve2D)
                result.add((GJContinuousOrientedCurve2D) curve);
        }
        return result;
    }

    @Override
    public GJContourArray2D<? extends GJContour2D> transform(
            GJAffineTransform2D trans) {
        GJContourArray2D<GJContour2D> result =
        	new GJContourArray2D<GJContour2D>(curves.size());
        for (GJCurve2D curve : curves)
            result.add((GJContour2D) curve.transform(trans));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        // check class
        if (!(obj instanceof GJContourArray2D<?>))
            return false;
        // call superclass method
        return super.equals(obj);
    }

}
