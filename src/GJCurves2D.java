/**
 * 
 */



import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;









/**
 * Collects some useful methods for clipping curves.
 * 
 * @author dlegland
 */
public abstract class GJCurves2D {

	// ===================================================================
	// static methods

	/**
	 * Mapping of the parameter t, relative to the local curve, into the
	 * interval [0 1], [0 1[, ]0 1], or ]0 1[, depending on the values of t0 and
	 * t1.
	 * 
	 * @param t
	 *            a value between t0 and t1
	 * @param t0
	 *            the lower bound of parameterization domain
	 * @param t1
	 *            the upper bound of parameterization domain
	 * @return a value between 0 and 1
	 */
	public static double toUnitSegment(double t, double t0, double t1) {
		if (t <= t0)
			return 0;
		if (t >= t1)
			return 1;

		if (t0 == NEGATIVE_INFINITY && t1 == POSITIVE_INFINITY)
			return Math.atan(t) / Math.PI + .5;

		if (t0 == NEGATIVE_INFINITY)
			return Math.atan(t - t1) * 2 / Math.PI + 1;

		if (t1 == POSITIVE_INFINITY)
			return Math.atan(t - t0) * 2 / Math.PI;

		// t0 and t1 are both finite
		return (t - t0) / (t1 - t0);
	}

	/**
	 * Transforms the value t between 0 and 1 in a value comprised between t0
	 * and t1.
	 * 
	 * @param t
	 *            a value between 0 and 1
	 * @param t0
	 *            the lower bound of parameterization domain
	 * @param t1
	 *            the upper bound of parameterization domain
	 * @return a value between t0 and t1
	 */
	public static double fromUnitSegment(double t, double t0, double t1) {
		if (t <= 0)
			return t0;
		if (t >= 1)
			return t1;

		if (t0 == NEGATIVE_INFINITY && t1 == POSITIVE_INFINITY)
			return Math.tan((t - .5) * Math.PI);

		if (t0 == NEGATIVE_INFINITY)
			return Math.tan((t - 1) * Math.PI / 2) + t1;

		if (t1 == POSITIVE_INFINITY)
			return Math.tan(t * Math.PI / 2) + t0;

		// t0 and t1 are both finite
		return t * (t1 - t0) + t0;
	}

	/**
	 * Clip a curve, and return a GJCurveSet2D. If the curve is totally outside
	 * the box, return a GJCurveSet2D with 0 curves inside. If the curve is
	 * totally inside the box, return a GJCurveSet2D with only one curve, which is
	 * the original curve.
	 */
	public static GJCurveSet2D<? extends GJCurve2D> clipCurve(GJCurve2D curve,
															  GJBox2D box) {
		// Case of continuous curve:
		// convert the result of ClipContinuousCurve to CurveSet of GJCurve2D
		if (curve instanceof GJContinuousCurve2D)
			return GJCurves2D.clipContinuousCurve((GJContinuousCurve2D) curve, box);

		// case of a GJCurveSet2D
		if (curve instanceof GJCurveSet2D<?>)
			return GJCurves2D.clipCurveSet((GJCurveSet2D<?>) curve, box);

		// Unknown case
		System.err.println("Unknown curve class in GJBox2D.clipCurve()");
		return new GJCurveArray2D<GJCurve2D>();
	}

	/**
	 * clip a GJCurveSet2D.
	 */
	public static GJCurveSet2D<? extends GJCurve2D> clipCurveSet(
			GJCurveSet2D<?> curveSet, GJBox2D box) {
		// Clip the current curve
		GJCurveArray2D<GJCurve2D> result = new GJCurveArray2D<GJCurve2D>();
		GJCurveSet2D<?> clipped;

		// a clipped parts of current curve to the result
		for (GJCurve2D curve : curveSet) {
			clipped = GJCurves2D.clipCurve(curve, box);
			for (GJCurve2D clippedPart : clipped)
				result.add(clippedPart);
		}

		// return a set of curves
		return result;
	}

	/**
	 * <p>
	 * Clips a continuous curve and returns a set of continuous curves.
	 * </p>
	 * <p>
	 * Algorithm is the following one:
	 * <ul>
	 * <li>Compute intersections between curve and box boundary</li>
	 * <li>Sort intersections according to their position on the curve</li>
	 * <li>Remove intersections which do not cross (they only touch) the box
	 * boundary</li>
	 * <li>Add portions of curves located between two intersections and inside
	 * of the box</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Special processing is added when the first point of the curve lies on the
	 * boundary of the box, and when the curve is closed (when the first point
	 * of the curve is inside the box, the method return a portion of curve
	 * between the last intersection and the first intersection).
	 * </p>
	 */
	public static GJCurveSet2D<GJContinuousCurve2D> clipContinuousCurve(
			GJContinuousCurve2D curve, GJBox2D box) {

		// Create GJCurveSet2D for storing the result
		GJCurveArray2D<GJContinuousCurve2D> res = new GJCurveArray2D<GJContinuousCurve2D>();

		// ------ Compute ordered list of intersections

		// createFromCollection array of intersection points
		ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>();

		// add all the intersections with edges of the box boundary
		for (GJLinearShape2D edge : box.edges())
			points.addAll(curve.intersections(edge));

		// convert list to point array, sorted wrt to their position on the
		// curve
		SortedSet<Double> set = new TreeSet<Double>();
		for (GJPoint2D p : points)
			set.add(new Double(curve.position(p)));

		// iterator on the intersection positions
		Iterator<Double> iter = set.iterator();

		// ----- remove intersections which do not cross the boundary

		// init arrays
		int nInter = set.size();
		double[] positions = new double[nInter + 2];
		double[] between = new double[nInter + 1];

		// fill up array of positions, with extreme positions of curve
		positions[0] = curve.t0();
		for (int i = 0; i < nInter; i++)
			positions[i + 1] = iter.next();
		positions[nInter + 1] = curve.t1();

		// compute positions of points between intersections
		for (int i = 0; i < nInter + 1; i++)
			between[i] = choosePosition(positions[i], positions[i + 1]);

		// array of positions to remove
		ArrayList<Double> toRemove = new ArrayList<Double>();

		// remove an intersection point if the curve portions before and after
		// are both either inside or outside of the box.
		for (int i = 0; i < nInter; i++) {
			GJPoint2D p1 = curve.point(between[i]);
			GJPoint2D p2 = curve.point(between[i + 1]);
			boolean b1 = box.contains(p1);
			boolean b2 = box.contains(p2);
			if (b1 == b2)
				toRemove.add(positions[i + 1]);
		}

		// remove unnecessary intersections
		set.removeAll(toRemove);

		// iterator on the intersection positions
		iter = set.iterator();

		// ----- Check case of no intersection point

		// if no intersection point, the curve is totally either inside or
		// outside the box
		if (set.size() == 0) {
			// compute position of an arbitrary point on the curve
			GJPoint2D point;
			if (curve.isBounded()) {
				point = curve.firstPoint();
			} else {
				double pos = choosePosition(curve.t0(), curve.t1());
				point = curve.point(pos);
			}

			// if the box contains a point, it contains the whole curve
			if (box.contains(point))
				res.add(curve);
			return res;
		}

		// ----- Check if the curve starts inside of the box

		// the flag for a curve that starts inside the box
		boolean inside = false;
		boolean touch = false;

		// different behavior if curve is bounded or not
		double t0 = curve.t0();
		if (isLeftInfinite(curve)) {
			// choose point between -infinite and first intersection
			double pos = choosePosition(t0, set.iterator().next());
			inside = box.contains(curve.point(pos));
		} else {
			// extract first point of the curve
			GJPoint2D point = curve.firstPoint();
			inside = box.contains(point);

			// if first point is on the boundary, then choose another point
			// located between first point and first intersection
			if (box.boundary().contains(point)) {
				touch = true;

				double pos = choosePosition(t0, iter.next());
				while (Math.abs(pos - t0) < GJShape2D.ACCURACY && iter.hasNext())
					pos = choosePosition(t0, iter.next());
				if (Math.abs(pos - t0) < GJShape2D.ACCURACY)
					pos = choosePosition(t0, curve.t1());
				point = curve.point(pos);

				// remove the first point from the list of intersections
				set.remove(t0);

				// if inside, adds the first portion of the curve,
				// and remove next intersection
				if (box.contains(point)) {
					pos = set.iterator().next();
					res.add(curve.subCurve(t0, pos));
					set.remove(pos);
				}

				// update iterator
				iter = set.iterator();

				inside = false;
			}
		}

		// different behavior depending if first point lies inside the box
		double pos0 = Double.NaN;
		if (inside && !touch)
			if (curve.isClosed())
				pos0 = iter.next();
			else
				res.add(curve.subCurve(curve.t0(), iter.next()));

		// ----- add portions of curve between each couple of intersections

		double pos1, pos2;
		while (iter.hasNext()) {
			pos1 = iter.next().doubleValue();
			if (iter.hasNext())
				pos2 = iter.next().doubleValue();
			else
				pos2 = curve.isClosed() && !touch ? pos0 : curve.t1();
			res.add(curve.subCurve(pos1, pos2));
		}

		return res;
	}

	/**
	 * Clip a continuous smooth curve. Currently just call the static method
	 * clipContinuousCurve, and cast clipped curves.
	 */
	public static GJCurveSet2D<GJSmoothCurve2D> clipSmoothCurve(
			GJSmoothCurve2D curve, GJBox2D box) {
		GJCurveArray2D<GJSmoothCurve2D> result = new GJCurveArray2D<GJSmoothCurve2D>();
		for (GJContinuousCurve2D cont : GJCurves2D.clipContinuousCurve(curve,
				box))
			if (cont instanceof GJSmoothCurve2D)
				result.add((GJSmoothCurve2D) cont);

		return result;
	}

	/**
	 * Clip a continuous smooth curve by the half-plane defined by a line. This
	 * method is mainly used to help debugging when implementing curves.
	 */
	public static GJCurveSet2D<GJSmoothCurve2D> clipSmoothCurve(
			GJSmoothCurve2D curve, GJStraightLine2D line) {

		// get the list of intersections with the line
		ArrayList<GJPoint2D> list = new ArrayList<GJPoint2D>();
		list.addAll(curve.intersections(line));

		// convert list to point array, sorted with respect to their position
		// on the curve, but do not add tangent points with curvature greater
		// than 0
		SortedSet<java.lang.Double> set = new TreeSet<java.lang.Double>();
		double position;
		GJVector2D vector = line.direction();
		for (GJPoint2D point : list) {
			// get position of intersection on the curve (use project to avoid
			// round-off problems)
			position = curve.project(point);

			// Condition of colinearity with direction vector of line
			GJVector2D tangent = curve.tangent(position);
			if (GJVector2D.isColinear(tangent, vector)) {
				// condition on the curvature (close to zero = cusp point)
				double curv = curve.curvature(position);
				if (Math.abs(curv) > GJShape2D.ACCURACY)
					continue;
			}
			set.add(new java.lang.Double(position));
		}

		// Create GJCurveSet2D for storing the result
		GJCurveArray2D<GJSmoothCurve2D> res = new GJCurveArray2D<GJSmoothCurve2D>();

		// extract first point of the curve, or a point arbitrarily far
		GJPoint2D point1;
		if (Double.isInfinite(curve.t0()))
			point1 = curve.point(-1000);
		else
			point1 = curve.firstPoint();

		// Extract first valid intersection point, if it exists
		double pos1, pos2;
		Iterator<java.lang.Double> iter = set.iterator();

		// if no intersection point, the curve is either totally inside
		// or totally outside the box
		if (!iter.hasNext()) {
			// Find a point on the curve and not on the line
			// First tries with first point
			double t0 = curve.t0();
			if (t0 == NEGATIVE_INFINITY)
				t0 = -100;
			while (line.contains(point1)) {
				double t1 = curve.t1();
				if (t1 == POSITIVE_INFINITY)
					t1 = +100;
				t0 = (t0 + t1) / 2;
				point1 = curve.point(t0);
			}
			if (line.signedDistance(point1) < 0)
				res.add(curve);
			return res;
		}

		// different behavior depending if first point lies inside the box
		if (line.signedDistance(point1) < 0 && !line.contains(point1)) {
			pos1 = iter.next().doubleValue();
			res.add(curve.subCurve(curve.t0(), pos1));
		}

		// add the portions of curve between couples of intersections
		while (iter.hasNext()) {
			pos1 = iter.next().doubleValue();
			if (iter.hasNext())
				pos2 = iter.next().doubleValue();
			else
				pos2 = curve.t1();
			res.add(curve.subCurve(pos1, pos2));
		}

		return res;
	}

	public static int findNextCurveIndex(double[] positions, double pos) {
		int ind = -1;
		double posMin = java.lang.Double.MAX_VALUE;
		for (int i = 0; i < positions.length; i++) {
			// avoid NaN
			if (java.lang.Double.isNaN(positions[i]))
				continue;
			// avoid values before
			if (positions[i] - pos < GJShape2D.ACCURACY)
				continue;

			// test if closer that other points
			if (positions[i] < posMin) {
				ind = i;
				posMin = positions[i];
			}
		}

		if (ind != -1)
			return ind;

		// if not found, return index of smallest value (mean that pos is last
		// point on the boundary, so we need to start at the beginning).
		for (int i = 0; i < positions.length; i++) {
			if (java.lang.Double.isNaN(positions[i]))
				continue;
			if (positions[i] - posMin < GJShape2D.ACCURACY) {
				ind = i;
				posMin = positions[i];
			}
		}
		return ind;
	}

	/**
	 * Choose an arbitrary position between positions t0 and t1, which can be
	 * infinite.
	 * 
	 * @param t0
	 *            the first bound of a curve parameterization
	 * @param t1
	 *            the second bound of a curve parameterization
	 * @return a position located between t0 and t1
	 */
	public static double choosePosition(double t0, double t1) {
		if (Double.isInfinite(t0)) {
			if (Double.isInfinite(t1))
				return 0;
			return t1 - 10;
		}

		if (Double.isInfinite(t1))
			return t0 + 10;

		return (t0 + t1) / 2;
	}

	public static boolean isLeftInfinite(GJCurve2D curve) {
		// basic check
		if (curve.isBounded())
			return false;

		// extract the first smooth curve
		GJContinuousCurve2D cont =
			curve.continuousCurves().iterator().next();
		GJSmoothCurve2D smooth =
			cont.smoothPieces().iterator().next();

		// check first position of first curve
		return Double.isInfinite(smooth.t0());
	}

	public static boolean isRightInfinite(GJCurve2D curve) {
		// basic check
		if (curve.isBounded())
			return false;

		// extract the first smooth curve
		GJSmoothCurve2D lastCurve = null;
		for (GJContinuousCurve2D cont : curve.continuousCurves())
			for (GJSmoothCurve2D smooth : cont.smoothPieces())
				lastCurve = smooth;

		// check last position of last curve
		return Double.isInfinite(lastCurve.t1());
	}
	
    public static GJContinuousCurve2D getFirstContinuousCurve(GJCurve2D curve) {
    	if (curve instanceof GJContinuousCurve2D)
    		return (GJContinuousCurve2D) curve;
    	
    	Collection<? extends GJContinuousCurve2D> curves =
    		curve.continuousCurves();
    	if (curves.size() == 0)
    		return null;
    	
    	return curves.iterator().next();
    }

    public static GJContinuousCurve2D getLastContinuousCurve(GJCurve2D curve) {
    	if (curve instanceof GJContinuousCurve2D)
    		return (GJContinuousCurve2D) curve;
    	GJContinuousCurve2D res = null;
    	for (GJContinuousCurve2D continuous : curve.continuousCurves())
    		res = continuous;
    	return res;
    }

    public static GJSmoothCurve2D getFirstSmoothCurve(GJCurve2D curve) {
    	if (curve instanceof GJSmoothCurve2D)
    		return (GJSmoothCurve2D) curve;
    	
    	// Extract last continuous piece of the last continuous curve
    	GJContinuousCurve2D continuous = getFirstContinuousCurve(curve);
    	if (continuous == null)
    		return null;
    	
    	Collection<? extends GJSmoothCurve2D> curves =
    		continuous.smoothPieces();
    	if (curves.size() == 0)
    		return null;
    	
    	return curves.iterator().next();
    }

    public static GJSmoothCurve2D getLastSmoothCurve(GJCurve2D curve) {
    	if (curve instanceof GJSmoothCurve2D)
    		return (GJSmoothCurve2D) curve;
    	
    	// Extract last continuous piece of the last continuous curve
    	GJContinuousCurve2D continuous = getLastContinuousCurve(curve);
    	GJSmoothCurve2D res = null;
    	for (GJSmoothCurve2D smooth : continuous.smoothPieces())
    		res = smooth;
    	return res;
    }

    public enum JunctionType {
    	SALIENT, REENTRANT, FLAT
    }
    
    /**
     * Returns the junction type between the end of first curve and the
     * beginning of second curve. 
     */
    public static JunctionType getJunctionType(GJCurve2D prev, GJCurve2D next) {
    	
    	// Extract corresponding smooth curves
        GJSmoothCurve2D smoothPrev = getLastSmoothCurve(prev);
        GJSmoothCurve2D smoothNext = getFirstSmoothCurve(next);
    	
        // tangent vectors of the 2 neighbor curves
        GJVector2D v1 = computeTangent(smoothPrev, smoothPrev.t1());
        GJVector2D v2 = computeTangent(smoothNext, smoothNext.t0());

        // check if angle between vectors is acute or obtuse
        double diff = GJAngle2D.angle(v1, v2);
        double eps = 1e-12;
        if (diff < eps || diff > (2*PI-eps)) {
        	return JunctionType.FLAT;
        }
        
        if (diff < PI - eps) {
        	// Acute angle
        	return JunctionType.SALIENT;
        } 
        
        if (diff > PI + eps) {
        	// obtuse angle
        	return JunctionType.REENTRANT;
        }
        
        // Extract curvatures of both curves around singular point
        double kappaPrev = smoothPrev.curvature(smoothPrev.t1());
        double kappaNext = smoothNext.curvature(smoothNext.t0());
        
        // get curvature signs
        double sp = Math.signum(kappaPrev);
        double sn = Math.signum(kappaNext);
        
        // Both curvatures have same sign
        if (sn * sp > 0) {
        	if (sn > 0)
        		return JunctionType.REENTRANT;
        	else
        		return JunctionType.SALIENT;
        }
        
        // One of the curvature is zero (straight curve)
        if (sp == 0) {
        	if (sn < 0)
        		return JunctionType.SALIENT;
        	else if (sn > 0)
        		return JunctionType.REENTRANT;
        	else {
        		// Both curvatures are zero => problem...
        		throw new IllegalArgumentException("colinear lines...");
        	}
        } else if (sn == 0) {
        	if (sp < 0)
        		return JunctionType.SALIENT;
        	else if (sp > 0)
        		return JunctionType.REENTRANT;				
        }

        
		// curvatures have opposite signs: curves point in opposite directions.
		// We need to check curvature values
		if (sp == 1 && sn == -1) {
			return abs(kappaPrev) < abs(kappaNext) ? 
					JunctionType.SALIENT : JunctionType.REENTRANT;
		} else if (sp == -1 && sn == 1){
			return abs(kappaPrev) > abs(kappaNext) ? 
					JunctionType.SALIENT : JunctionType.REENTRANT;
		}

		throw new RuntimeException("Could not determine junction type");
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

}
