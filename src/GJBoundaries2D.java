import java.util.ArrayList;
import java.util.Iterator;


/**
 * Collects some useful methods for operating on boundary curves.
 * 
 * @author dlegland
 */
public abstract class GJBoundaries2D {

    /**
     * Clip a curve, and return a GJCurveSet2D. If the curve is totally outside
     * the box, return a GJCurveSet2D with 0 curves inside. If the curve is
     * totally inside the box, return a GJCurveSet2D with only one curve, which is
     * the original curve.
     */
    public final static GJCurveSet2D<GJContinuousOrientedCurve2D>
    clipContinuousOrientedCurve(GJContinuousOrientedCurve2D curve, GJBox2D box) {

    	// createFromCollection result array
    	GJCurveArray2D<GJContinuousOrientedCurve2D> result =
        	new GJCurveArray2D<GJContinuousOrientedCurve2D>();
    	
    	// for each clipped curve, add its pieces
        for (GJContinuousCurve2D cont : GJCurves2D.clipContinuousCurve(curve, box))
            if (cont instanceof GJContinuousOrientedCurve2D)
                result.add((GJContinuousOrientedCurve2D) cont);

        return result;
    }

    /**
     * Clips a boundary and closes the result curve. Returns an instance of
     * GJContourArray2D.
     */
    public final static GJContourArray2D<GJContour2D> clipBoundary(
            GJBoundary2D boundary, GJBox2D box) {

    	// basic check-up
        if (!box.isBounded())
            throw new GJUnboundedBox2DException(box);

        // iteration variable
        GJContinuousOrientedCurve2D curve;

        // The set of boundary curves. Each curve of this set is either a
        // curve of the original boundary, or a composition of a portion of
        // original boundary with a portion of the box.
		GJContourArray2D<GJContour2D> res = new GJContourArray2D<GJContour2D>();

        // to store result of curve clipping
        GJCurveSet2D<GJContinuousOrientedCurve2D> clipped;

        // to store set of all clipped curves
        GJCurveArray2D<GJContinuousOrientedCurve2D> curveSet =
        	new GJCurveArray2D<GJContinuousOrientedCurve2D>();

        // Iterate on contours: clip each contour with box, 
        // and add clipped curves to the array 'curveSet'
        for (GJContour2D contour : boundary.continuousCurves()) {
            clipped = GJBoundaries2D.clipContinuousOrientedCurve(contour, box);

            for (GJContinuousOrientedCurve2D clip : clipped)
                curveSet.add(clip);
        }

        // array of position on the box for first and last point of each curve
        int nc = curveSet.size();
        double[] startPositions = new double[nc];
        double[] endPositions = new double[nc];

        // Flag indicating if the curve intersects the boundary of the box
        boolean intersect = false;

        // also createFromCollection array of curves
        GJContinuousOrientedCurve2D[] curves = new GJContinuousOrientedCurve2D[nc];

        // boundary of the box
        GJCurve2D boxBoundary = box.boundary();

        // compute position on the box for first and last point of each curve
        Iterator<GJContinuousOrientedCurve2D> iter = curveSet.curves().iterator();

        for (int i = 0; i < nc; i++) {
            // save current curve
            curve = iter.next();
            curves[i] = curve;

            if (curve.isClosed()) {
                startPositions[i] = Double.NaN;
                endPositions[i] = Double.NaN;
                continue;
            }

            // compute positions of first point and last point on box boundary
            startPositions[i] = boxBoundary.position(curve.firstPoint());
            endPositions[i] = boxBoundary.position(curve.lastPoint());

            // set up the flag
            intersect = true;
        }

        // theoretical number of boundary curves. Set to the number of clipped
        // curves, but total number can be reduced if several clipped curves
        // belong to the same boundary curve.
        int nb = nc;

        // current index of curve
        int c = 0;

		// iterate while there are boundary curve to build
		while (c < nb) {
			int ind = c;
			// find the current curve (used curves are removed from array)
			while (curves[ind] == null)
				ind++;

            // current curve
            curve = curves[ind];

            // if curve is closed, we can switch to next curve
            if (curve.isClosed()) {
                // Add current boundary to the set of boundary curves
                if (curve instanceof GJContour2D) {
                    res.add((GJContour2D) curve);
                } else {
                    GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D> bnd =
                    	new GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>();
                    bnd.add(curve);
                    res.add(bnd);
                }
                curves[ind] = null;

                // switch to next curve
                c++;
                continue;
            }

            // createFromCollection a new Boundary curve
            GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D> boundary0 =
            	new GJBoundaryPolyCurve2D<GJContinuousOrientedCurve2D>();

            // add current curve to boundary curve
            boundary0.add(curve);

            // get last points (to add a line with next curve)
            GJPoint2D p0 = curve.firstPoint();
            GJPoint2D p1 = curve.lastPoint();

            // index of first curve, used as a stop flag
            int ind0 = ind;

            // store indices of curves, to remove them later
            ArrayList<Integer> indices = new ArrayList<Integer>();
            indices.add(new Integer(ind));

            // position of last point of current curve on box boundary
            ind = findNextCurveIndex(startPositions, endPositions[ind0]);

            // iterate while we don't come back to first point
            while (ind != ind0) {
                // find the curve whose first point is just after last point
                // of current curve on box boundary
                curve = curves[ind];

                // add a link between previous curve and current curve
                GJPoint2D p0i = curve.firstPoint();
				boundary0.add(getBoundaryPortion(box, p1, p0i));

                // add to current boundary
                boundary0.add(curve);

                indices.add(new Integer(ind));

                // find index and position of next curve
                ind = findNextCurveIndex(startPositions, endPositions[ind]);

                // get last points
                p1 = curve.lastPoint();

                // decrease total number of boundary curves
                nb--;
            }

            // add a line from last point to first point
            boundary0.add(getBoundaryPortion(box, p1, p0));

            // Add current boundary to the set of boundary curves
            res.add(boundary0);

            // remove curves from array
            Iterator<Integer> iter2 = indices.iterator();
            while (iter2.hasNext())
                curves[iter2.next().intValue()] = null;

            // next curve !
            c++;
        }

        // Add processing when the box boundary does not intersect the curve.
        // In this case add the boundary of the box to the resulting boundary
        // set.
        if (!intersect) {
            GJPoint2D vertex = box.vertices().iterator().next();
            if (boundary.isInside(vertex))
                res.add(box.asRectangle().boundary().firstCurve());
        }

        // return the result
        return res;
    }

    public final static int findNextCurveIndex(double[] positions, double pos) {
        int ind = -1;
        double posMin = Double.MAX_VALUE;
        for (int i = 0; i<positions.length; i++) {
            // avoid NaN
            if (Double.isNaN(positions[i]))
                continue;
            // avoid values before
            if (positions[i]-pos< GJShape2D.ACCURACY)
                continue;

            // test if closer that other points
            if (positions[i]<posMin) {
                ind = i;
                posMin = positions[i];
            }
        }

        if (ind!=-1)
            return ind;

        // if not found, return index of smallest value (mean that pos is last
        // point on the boundary, so we need to start at the beginning).
        for (int i = 0; i<positions.length; i++) {
            if (Double.isNaN(positions[i]))
                continue;
            if (positions[i]-posMin< GJShape2D.ACCURACY) {
                ind = i;
                posMin = positions[i];
            }
        }
        return ind;
    }

    /**
     * Extracts a portion of the boundary of a bounded box.
     * 
     * @param box the box from which one extract a portion of boundary
     * @param p0 the first point of the portion
     * @param p1 the last point of the portion
     * @return the portion of the bounding box boundary as a GJPolyline2D
     */
    public final static GJPolyline2D getBoundaryPortion(GJBox2D box, GJPoint2D p0,
                                                        GJPoint2D p1) {
        GJBoundary2D boundary = box.boundary();

        // position of start and end points
        double t0 = boundary.position(p0);
        double t1 = boundary.position(p1);

        // curve index of each point
        int ind0 = (int) Math.floor(t0);
        int ind1 = (int) Math.floor(t1);

        // Simple case: returns a polyline with only 2 vertices
		if (ind0 == ind1 && t0 < t1)
			return new GJPolyline2D(new GJPoint2D[] { p0, p1 });

        // Create an array to store vertices
        // Array can contain at most 6 vertices: 4 for the box corners,
        // and 2 for curve extremities.
        ArrayList<GJPoint2D> vertices = new ArrayList<GJPoint2D>(6);

        // add the first point.
        vertices.add(p0);

		// compute index of first box boundary edge
		int ind = (ind0 + 1) % 4;

		// add all vertices segments between the 2 end points
		while (ind != ind1) {
			vertices.add(boundary.point(ind));
			ind = (ind + 1) % 4;
		}
        vertices.add(boundary.point(ind));

        // add the last line segment
        vertices.add(p1);

        return new GJPolyline2D(vertices);
    }
}
