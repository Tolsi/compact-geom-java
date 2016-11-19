/* file : PolyOrientedCurve2D.java
 * 
 * Project : geometry
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
 * 
 * Created on 1 mai 2006
 *
 */



// Imports
import java.util.Collection;















import static java.lang.Math.*;


/**
 * A GJPolyOrientedCurve2D is a set of piecewise smooth curve arcs, such that the
 * end of a curve is the beginning of the next curve, and such that they do not
 * intersect nor self-intersect.
 * <p>
 * 
 * @see GJBoundaryPolyCurve2D
 * @author dlegland
 */
public class GJPolyOrientedCurve2D<T extends GJContinuousOrientedCurve2D> extends
        GJPolyCurve2D<T> implements GJContinuousOrientedCurve2D {

    // ===================================================================
    // static constructors

    /**
     * Static factory for creating a new GJPolyOrientedCurve2D from a collection of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousOrientedCurve2D> GJPolyOrientedCurve2D<T>
    createContinuousOrientedCurve2DFromCollection(Collection<T> curves) {
    	return new GJPolyOrientedCurve2D<T>(curves);
    }
    
    /**
     * Static factory for creating a new GJPolyOrientedCurve2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousOrientedCurve2D>
    GJPolyOrientedCurve2D<T> create(T... curves) {
    	return new GJPolyOrientedCurve2D<T>(curves);
    }

    /**
     * Static factory for creating a new GJPolyOrientedCurve2D from an array of
     * curves.
     * @since 0.8.1
     */
    public static <T extends GJContinuousOrientedCurve2D>
    GJPolyOrientedCurve2D<T> createClosed(T... curves) {
    	return new GJPolyOrientedCurve2D<T>(curves, true);
    }

    /**
     * Static factory for creating a new GJPolyOrientedCurve2D from a collection of
     * curves and a flag indicating if the curve is closed or not.
     * @since 0.9.0
     */
    public static <T extends GJContinuousOrientedCurve2D> GJPolyOrientedCurve2D<T>
    createContinuousOrientedCurve2DFromCollection(Collection<T> curves, boolean closed) {
    	return new GJPolyOrientedCurve2D<T>(curves, closed);
    }
    
    /**
     * Static factory for creating a new GJPolyOrientedCurve2D from an array of
     * curves and a flag indicating if the curve is closed or not.
     * @since 0.9.0
     */
    public static <T extends GJContinuousOrientedCurve2D>
    GJPolyOrientedCurve2D<T> createFromCollection(T[] curves, boolean closed) {
    	return new GJPolyOrientedCurve2D<T>(curves, closed);
    }

   
    // ===================================================================
    // Constructors

    public GJPolyOrientedCurve2D() {
        super();
    }

    public GJPolyOrientedCurve2D(int size) {
        super(size);
    }

    public GJPolyOrientedCurve2D(T... curves) {
        super(curves);
    }

    public GJPolyOrientedCurve2D(T[] curves, boolean closed) {
        super(curves, closed);
    }

    public GJPolyOrientedCurve2D(Collection<? extends T> curves) {
        super(curves);
    }

    public GJPolyOrientedCurve2D(Collection<? extends T> curves, boolean closed) {
        super(curves, closed);
    }

    
    // ===================================================================
    // Methods specific to GJPolyOrientedCurve2D

    public double windingAngle(GJPoint2D point) {
        double angle = 0;
        for (GJOrientedCurve2D curve : this.curves)
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
        double dist = this.distance(x, y);

        if (this.isInside(new GJPoint2D(x, y)))
            dist = -dist;

        return dist;
    }

    /**
     * Determines if the given point lies within the domain bounded by this
     * curve.
     */
    public boolean isInside(GJPoint2D point) {
        double pos = this.project(point);

        if (!this.isSingular(pos)) {
            // Simply call the method isInside on the child curve
            return this.childCurve(pos).isInside(point);
        }
        
        // number of curves
        int n = this.size();

        // vertex index and position
        int i = this.curveIndex(pos);
        if (pos / 2 - i > .25)
        	i++;

        // Test case of point equal to last position
        if (round(pos) == 2 * n - 1) {
        	pos = 0;
        	i = 0;
        }

        GJPoint2D vertex = this.point(pos);

        // indices of previous and next curves
        int iPrev = i > 0 ? i - 1 : n - 1;
        int iNext = i;

        // previous and next curves
        T prev = this.curves.get(iPrev);
        T next = this.curves.get(iNext);

        // tangent vectors of the 2 neighbor curves
        GJVector2D v1 = computeTangent(prev, prev.t1());
        GJVector2D v2 = computeTangent(next, next.t0());

        // compute on which side of each ray the test point lies
        boolean in1 = new GJStraightLine2D(vertex, v1).isInside(point);
        boolean in2 = new GJStraightLine2D(vertex, v2).isInside(point);

        // check if angle between vectors is acute or obtuse
        double diff = GJAngle2D.angle(v1, v2);
        double eps = 1e-12;
        if (diff < PI - eps) {
        	// Acute angle
        	return in1 && in2;
        } 
        
        if (diff > PI + eps) {
        	// obtuse angle
            return in1 || in2;
        }
        
        // Extract curvatures of both curves around singular point
        GJSmoothCurve2D smoothPrev = GJCurves2D.getLastSmoothCurve(prev);
        GJSmoothCurve2D smoothNext = GJCurves2D.getFirstSmoothCurve(next);
        double kappaPrev = smoothPrev.curvature(smoothPrev.t1());
        double kappaNext = smoothNext.curvature(smoothNext.t0());
        
        // get curvature signs
        double sp = Math.signum(kappaPrev);
        double sn = Math.signum(kappaNext);
        
        // Both curvatures have same sign
        // -> point is inside if both curvature are positive
        if (sn * sp > 0) {
        	return kappaPrev > 0 && kappaNext > 0;
        }
        
        // One of the curvature is zero (straight curve)
		if (sn * sp == 0) {
			if (sn == 0 && sp == 0) {
				throw new IllegalArgumentException("colinear lines...");
			}
			
			if (sp == 0)
				return kappaNext > 0;
			else
				return kappaPrev > 0;
		}
        
		// if curvatures have opposite signs, curves point in the same
		// direction but with opposite direction.
		if (kappaPrev > 0 && kappaNext < 0) {
			return Math.abs(kappaPrev) > Math.abs(kappaNext);
		} else {
			return Math.abs(kappaPrev) < Math.abs(kappaNext);
		}
    }
    
    /**
     * Computes the tangent of the curve at the given position.
     */
    private static GJVector2D computeTangent(GJContinuousCurve2D curve, double pos) {
        // For smooth curves, simply call the getTangent() method
        if (curve instanceof GJSmoothCurve2D)
            return ((GJSmoothCurve2D) curve).tangent(pos);

        // Extract sub curve and recursively call this method on the sub curve
        if (curve instanceof GJCurveSet2D<?>) {
            GJCurveSet2D<?> curveSet = (GJCurveSet2D<?>) curve;
            double pos2 = curveSet.localPosition(pos);
            GJCurve2D subCurve = curveSet.childCurve(pos);
            return computeTangent((GJContinuousCurve2D) subCurve, pos2);
        }

        throw new IllegalArgumentException(
        		"Unknown type of curve: should be either continuous or curveset");
    }

    @Override
    public GJPolyOrientedCurve2D<? extends GJContinuousOrientedCurve2D> reverse() {
        GJContinuousOrientedCurve2D[] curves2 =
        	new GJContinuousOrientedCurve2D[curves.size()];
        int n = curves.size();
        for (int i = 0; i < n; i++)
            curves2[i] = curves.get(n-1-i).reverse();
        return new GJPolyOrientedCurve2D<GJContinuousOrientedCurve2D>(curves2);
    }

    /**
     * Returns a portion of this curve as an instance of GJPolyOrientedCurve2D.
     */
    @Override
    public GJPolyOrientedCurve2D<? extends GJContinuousOrientedCurve2D> subCurve(
            double t0, double t1) {
        GJPolyCurve2D<?> set = super.subCurve(t0, t1);
        GJPolyOrientedCurve2D<GJContinuousOrientedCurve2D> subCurve =
        	new GJPolyOrientedCurve2D<GJContinuousOrientedCurve2D>();
        subCurve.setClosed(false);

        // convert to PolySmoothCurve by adding curves.
        for (GJCurve2D curve : set.curves())
            subCurve.add((GJContinuousOrientedCurve2D) curve);

        return subCurve;
    }

    /**
     * Clips the GJPolyCurve2D by a box.
     * The result is an instance of GJCurveSet2D,
     * which contains only instances of GJContinuousOrientedCurve2D. If the
     * GJPolyCurve2D is not clipped, the result is an instance of
     * GJCurveSet2D which contains 0 curves.
     */
    @Override
    public GJCurveSet2D<? extends GJContinuousOrientedCurve2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        int n = set.size();
        GJCurveArray2D<GJContinuousOrientedCurve2D> result =
        	new GJCurveArray2D<GJContinuousOrientedCurve2D>(n);

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJContinuousOrientedCurve2D)
                result.add((GJContinuousOrientedCurve2D) curve);
        }
        return result;
    }

    @Override
    public GJPolyOrientedCurve2D<? extends GJContinuousOrientedCurve2D> transform(GJAffineTransform2D trans) {
        GJPolyOrientedCurve2D<GJContinuousOrientedCurve2D> result =
        	new GJPolyOrientedCurve2D<GJContinuousOrientedCurve2D>();
        for (GJContinuousOrientedCurve2D curve : curves)
            result.add(curve.transform(trans));
        result.setClosed(this.closed);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        // check class
        if (!(obj instanceof GJCurveSet2D<?>))
            return false;
        // call superclass method
        return super.equals(obj);
    }

}
