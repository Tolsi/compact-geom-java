import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Some utilities for working with circulinear curves.
 * 
 * @author dlegland
 * 
 */
public class GJCirculinearCurves2D {

	/**
	 * Converts a curve to a circulinear curve, by concatenating all elements of
	 * the curve to the appropriate circulinear curve type. If the curve
	 * contains one or more non-circulinear smooth curve, a
	 * GJNonCirculinearClassException is thrown.
	 */
	public static GJCirculinearCurve2D convert(GJCurve2D curve) {
		// first check type, to avoid unnecessary computations
		if (curve instanceof GJCirculinearCurve2D)
			return (GJCirculinearCurve2D) curve;

		// If the curve is continuous, creates a GJCirculinearContinuousCurve2D
		if (curve instanceof GJContinuousCurve2D) {
			// extract smooth pieces
			GJContinuousCurve2D continuous = (GJContinuousCurve2D) curve;
			Collection<? extends GJSmoothCurve2D> smoothPieces =
				continuous.smoothPieces();

			// prepare array of elements
			ArrayList<GJCirculinearElement2D> elements = new ArrayList<GJCirculinearElement2D>(
					smoothPieces.size());

			// class cast for each element, or throw an exception
			for (GJSmoothCurve2D smooth : smoothPieces) {
				if (smooth instanceof GJCirculinearElement2D)
					elements.add((GJCirculinearElement2D) smooth);
				else
					throw new GJNonCirculinearClassException(smooth);
			}

			// createFromCollection the resulting GJCirculinearContinuousCurve2D
			return new GJPolyCirculinearCurve2D<GJCirculinearElement2D>(elements);
		}

		// If the curve is continuous, creates a GJCirculinearContinuousCurve2D
		if (curve instanceof GJCurveSet2D<?>) {
			// extract smooth pieces
			GJCurveSet2D<?> set = (GJCurveSet2D<?>) curve;
			Collection<? extends GJContinuousCurve2D> continuousCurves = set
					.continuousCurves();

			// prepare array of elements
			ArrayList<GJCirculinearContinuousCurve2D> curves =
				new ArrayList<GJCirculinearContinuousCurve2D>(continuousCurves.size());

			// class cast for each element, or throw an exception
			for (GJContinuousCurve2D continuous : continuousCurves) {
				if (continuous instanceof GJCirculinearContinuousCurve2D)
					curves.add((GJCirculinearContinuousCurve2D) continuous);
				else
					curves.add((GJCirculinearContinuousCurve2D) convert(continuous));
			}

			// createFromCollection the resulting GJCirculinearContinuousCurve2D
			return GJCirculinearCurveArray2D.createFromCollection(curves);
		}

		//TODO: throw exception ?
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length(double)
	 */
	public static double getLength(
            GJCurveSet2D<? extends GJCirculinearCurve2D> curve, double pos) {
		// init
		double length = 0;

		// add length of each curve before current curve
		int index = curve.curveIndex(pos);
		for (int i = 0; i < index; i++)
			length += curve.get(i).length();

		// add portion of length for last curve
		if (index < curve.size()) {
			double pos2 = curve.localPosition(pos - 2 * index);
			length += curve.get(index).length(pos2);
		}

		// return result
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#position(double)
	 */
	public static double getPosition(
            GJCurveSet2D<? extends GJCirculinearCurve2D> curveSet, double length) {

		// position to compute
		double pos = 0;

		// index of current curve
		int index = 0;

		// cumulative length
		double cumLength = getLength(curveSet, curveSet.t0());

		// iterate on all curves
		for (GJCirculinearCurve2D curve : curveSet.curves()) {
			// length of current curve
			double curveLength = curve.length();

			// add either 2, or fraction of length
			if (cumLength + curveLength < length) {
				cumLength += curveLength;
				index++;
			} else {
				// add local position on current curve
				double pos2 = curve.position(length - cumLength);
				pos = curveSet.globalPosition(index, pos2);
				break;
			}
		}

		// return the result
		return pos;
	}

	/**
	 * Computes intersection point of a single curve, by iterating on pair of
	 * Circulinear elements composing the curve.
	 * 
	 * @return the set of self-intersection points
	 */
	public static Collection<GJPoint2D> findSelfIntersections(
			GJCirculinearCurve2D curve) {

		// createFromCollection array of circulinear elements
		ArrayList<GJCirculinearElement2D> elements = new ArrayList<GJCirculinearElement2D>();

		// extract all circulinear elements of the curve
		for (GJCirculinearContinuousCurve2D cont : curve.continuousCurves())
			elements.addAll(cont.smoothPieces());

		// createFromCollection array for storing result
		ArrayList<GJPoint2D> result = new ArrayList<GJPoint2D>(0);

		// iterate on each couple of elements
		int n = elements.size();
		for (int i = 0; i < n - 1; i++) {
			GJCirculinearElement2D elem1 = elements.get(i);
			for (int j = i; j < n; j++) {
				GJCirculinearElement2D elem2 = elements.get(j);
				// iterate on intersections between consecutive elements
				for (GJPoint2D inter : findIntersections(elem1, elem2)) {
					// do not keep extremities
					if (isCommonVertex(inter, elem1, elem2))
						continue;

					result.add(inter);
				}
			}
		}

		// return the set of intersections
		return result;
	}

	public static double[][] locateSelfIntersections(
			GJCurveSet2D<? extends GJCirculinearElement2D> curve) {

		// createFromCollection array for storing result
		ArrayList<Double> list1 = new ArrayList<Double>(0);
		ArrayList<Double> list2 = new ArrayList<Double>(0);
		double dt;

		// iterate on each couple of elements
		int n = curve.size();
		for (int i = 0; i < n - 1; i++) {
			GJCirculinearElement2D elem1 = curve.get(i);
			for (int j = i + 1; j < n; j++) {
				GJCirculinearElement2D elem2 = curve.get(j);
				// iterate on intersection between consecutive elements
				for (GJPoint2D inter : findIntersections(elem1, elem2)) {
					// do not keep extremities
					if (isCommonVertex(inter, elem1, elem2))
						continue;

					// add the intersection if we keep it
					dt = GJCurves2D.toUnitSegment(elem1.position(inter),
							elem1.t0(), elem1.t1());
					list1.add(2 * i + dt);

					dt = GJCurves2D.toUnitSegment(elem2.position(inter),
							elem2.t0(), elem2.t1());
					list2.add(2 * j + dt);
				}
			}
		}

		// convert the 2 lists into a n*2 array
		int np = list1.size();
		double[][] result = new double[np][2];
		for (int i = 0; i < np; i++) {
			result[i][0] = list1.get(i);
			result[i][1] = list2.get(i);
		}

		// return the array of positions
		return result;
	}

	/**
	 * Checks if the point is a common extremity between the two curve elements.
	 */
	private static boolean isCommonVertex(GJPoint2D inter,
                                          GJCirculinearCurve2D elem1, GJCirculinearCurve2D elem2) {

		double eps = GJShape2D.ACCURACY;

		// Test end of elem1 and start of elem2
		if (!Double.isInfinite(elem1.t1())
				&& !Double.isInfinite(elem2.t0()))
			if (inter.almostEquals(elem1.lastPoint(), eps)
					&& inter.almostEquals(elem2.firstPoint(), eps))
				return true;

		// Test end of elem2 and start of elem1
		if (!Double.isInfinite(elem1.t0())
				&& !Double.isInfinite(elem2.t1()))
			if (inter.almostEquals(elem1.firstPoint(), eps)
					&& inter.almostEquals(elem2.lastPoint(), eps))
				return true;

		return false;
	}

	/**
	 * Compute the set of intersection points between the two curves.
	 * 
	 * @return a collection of intersection points
	 */
	public static Collection<GJPoint2D> findIntersections(
            GJCirculinearCurve2D curve1, GJCirculinearCurve2D curve2) {

		// createFromCollection array of circulinear elements
		ArrayList<GJCirculinearElement2D> elements1 = new ArrayList<GJCirculinearElement2D>();
		ArrayList<GJCirculinearElement2D> elements2 = new ArrayList<GJCirculinearElement2D>();

		// extract all circulinear elements of the curve
		for (GJCirculinearContinuousCurve2D cont : curve1.continuousCurves())
			elements1.addAll(cont.smoothPieces());
		for (GJCirculinearContinuousCurve2D cont : curve2.continuousCurves())
			elements2.addAll(cont.smoothPieces());

		// createFromCollection array for storing result
		ArrayList<GJPoint2D> result = new ArrayList<GJPoint2D>(0);

		// iterate on each couple of elements
		int n1 = elements1.size();
		int n2 = elements2.size();
		for (int i = 0; i < n1; i++) {
			GJCirculinearElement2D elem1 = elements1.get(i);
			for (int j = 0; j < n2; j++) {
				GJCirculinearElement2D elem2 = elements2.get(j);
				// iterate on intersection between consecutive elements
				for (GJPoint2D inter : findIntersections(elem1, elem2)) {
					// add the intersection if we keep it
					result.add(inter);
				}
			}
		}

		// return the set of intersections
		return result;
	}

	/**
	 * Locate intersection points of two curves. The result is a N-by-2 array of
	 * double, where N is the number of intersections. For each row, the first
	 * element is the position on the first curve, and the second element is the
	 * position on the second curve.
	 */
	public static double[][] locateIntersections(GJCirculinearCurve2D curve1,
                                                 GJCirculinearCurve2D curve2) {

		// createFromCollection array for storing result
		ArrayList<Double> list1 = new ArrayList<Double>(0);
		ArrayList<Double> list2 = new ArrayList<Double>(0);

		// createFromCollection array of circulinear elements
		ArrayList<GJCirculinearElement2D> elements1 = new ArrayList<GJCirculinearElement2D>();
		ArrayList<GJCirculinearElement2D> elements2 = new ArrayList<GJCirculinearElement2D>();

		// extract all circulinear elements of the curve
		for (GJCirculinearContinuousCurve2D cont : curve1.continuousCurves())
			elements1.addAll(cont.smoothPieces());
		for (GJCirculinearContinuousCurve2D cont : curve2.continuousCurves())
			elements2.addAll(cont.smoothPieces());

		// iterate on each couple of elements
		int n1 = elements1.size();
		int n2 = elements2.size();
		for (int i = 0; i < n1; i++) {
			GJCirculinearElement2D elem1 = elements1.get(i);
			for (int j = 0; j < n2; j++) {
				GJCirculinearElement2D elem2 = elements2.get(j);
				// iterate on intersections between consecutive elements
				for (GJPoint2D inter : findIntersections(elem1, elem2)) {
					double pos1 = curve1.position(inter);
					double pos2 = curve2.position(inter);
					if (curve1.isSingular(pos1) && curve2.isSingular(pos2))
						continue;
					// add the intersection if we keep it
					list1.add(pos1);
					list2.add(pos2);
				}
			}
		}

		// convert the 2 lists into a n*2 array
		int np = list1.size();
		double[][] result = new double[np][2];
		for (int i = 0; i < np; i++) {
			result[i][0] = list1.get(i);
			result[i][1] = list2.get(i);
		}

		// return the array of positions
		return result;
	}

	/**
	 * Computes the intersections, if they exist, of two circulinear elements.
	 */
	public static Collection<GJPoint2D> findIntersections(
            GJCirculinearElement2D elem1, GJCirculinearElement2D elem2) {

		// find which shapes are linear
		boolean b1 = elem1 instanceof GJLinearShape2D;
		boolean b2 = elem2 instanceof GJLinearShape2D;
		
		// if both elements are linear, check parallism to avoid computing
		// intersection of parallel lines
		if (b1 && b2) {
			GJLinearShape2D line1 = (GJLinearShape2D) elem1;
			GJLinearShape2D line2 = (GJLinearShape2D) elem2;
			
			// test parallel elements
			GJVector2D v1 = line1.direction();
			GJVector2D v2 = line2.direction();
			if (GJVector2D.isColinear(v1, v2))
				return new ArrayList<GJPoint2D>(0);
			
			return line1.intersections(line2);
		}
		
		// First try to use linear shape methods
		if (elem1 instanceof GJLinearShape2D) {
			return elem2.intersections((GJLinearShape2D) elem1);
		}
		if (elem2 instanceof GJLinearShape2D) {
			return elem1.intersections((GJLinearShape2D) elem2);
		}

		// From now, both elem1 and elem2 are instances of CircleShape2D
		// It is therefore possible to extract support circles
		GJCircle2D circ1 = ((GJCircularShape2D) elem1).supportingCircle();
		GJCircle2D circ2 = ((GJCircularShape2D) elem2).supportingCircle();

		// createFromCollection array for storing result (max 2 possible intersections)
		ArrayList<GJPoint2D> pts = new ArrayList<GJPoint2D>(2);

		// for each of the circle intersections, check if they belong to
		// both elements
		for (GJPoint2D inter : GJCircle2D.circlesIntersections(circ1, circ2)) {
			if (elem1.contains(inter) && elem2.contains(inter))
				pts.add(inter);
		}

		// return found intersections
		return pts;
	}

	/**
	 * Split a continuous curve which self-intersects into a set of continuous
	 * circulinear curves which do not self-intersect.
	 * 
	 * @param curve
	 *            the curve to split
	 * @return a set of non-self-intersecting continuous curves
	 */
	public static Collection<GJCirculinearContinuousCurve2D> splitContinuousCurve(
			GJCirculinearContinuousCurve2D curve) {

		double pos0, pos1, pos2;

		// createFromCollection the array of resulting curves
		ArrayList<GJCirculinearContinuousCurve2D> result =
			new ArrayList<GJCirculinearContinuousCurve2D>();

		// Instances of GJCirculinearElement2D can not self-intersect
		if (curve instanceof GJCirculinearElement2D) {
			result.add(curve);
			return result;
		}

		// convert the curve to a poly-circulinear curve, to be able to call
		// the "locateSelfIntersections" method.
		GJPolyCirculinearCurve2D<GJCirculinearElement2D> polyCurve = createPolyCurve(
				curve.smoothPieces(), curve.isClosed());

		// identify couples of intersections
		double[][] couples = locateSelfIntersections(polyCurve);

		// case of curve without self-intersections
		if (couples.length == 0) {
			// createFromCollection continuous curve formed only by circulinear elements
			result.add(createPolyCurve(polyCurve.smoothPieces(),
					curve.isClosed()));
			return result;
		}

		// put all positions into a tree map
		TreeMap<Double, Double> twins = new TreeMap<Double, Double>();
		for (int i = 0; i < couples.length; i++) {
			pos1 = couples[i][0];
			pos2 = couples[i][1];
			twins.put(pos1, pos2);
			twins.put(pos2, pos1);
		}

		// an array for the portions of curves
		ArrayList<GJCirculinearElement2D> elements;

		// Process the first curve

		// createFromCollection new empty array of elements for current continuous curve
		elements = new ArrayList<GJCirculinearElement2D>();

		// get first intersection
		pos1 = polyCurve.t0();
		pos2 = twins.firstKey();
		pos0 = pos2;

		// add the first portion of curve, starting from the beginning
		addElements(elements, polyCurve.subCurve(pos1, pos2));
		do {
			// get the position of the new portion of curve
			pos1 = twins.remove(pos2);

			// check if there are still intersections to process
			if (twins.higherKey(pos1) == null)
				break;

			// get position of next intersection on the curve
			pos2 = twins.higherKey(pos1);

			// add elements
			addElements(elements, polyCurve.subCurve(pos1, pos2));
		} while (true);

		// add the last portion of curve, going to the end of original curve
		pos2 = polyCurve.t1();
		addElements(elements, polyCurve.subCurve(pos1, pos2));

		// add the continuous curve formed only by circulinear elements
		result.add(createPolyCurve(elements, curve.isClosed()));

		// Process other curves, while there are intersections left
		while (!twins.isEmpty()) {
			// createFromCollection new empty array of elements for current continuous curve
			elements = new ArrayList<GJCirculinearElement2D>();

			// get first intersection
			pos0 = twins.firstKey();
			pos1 = twins.get(pos0);
			pos2 = twins.higherKey(pos1);

			// add the first portion of curve, starting from the beginning
			addElements(elements, polyCurve.subCurve(pos1, pos2));

			while (pos2 != pos0) {
				// get the position of the new portion of curve
				pos1 = twins.remove(pos2);

				// check if there are still intersections to process
				if (twins.higherKey(pos1) == null)
					break;

				// get position of next intersection on the curve
				pos2 = twins.higherKey(pos1);

				// add elements
				addElements(elements, polyCurve.subCurve(pos1, pos2));
			}

			pos1 = twins.remove(pos2);

			// createFromCollection continuous curve formed only by circulinear elements
			// and add it to the set of curves
			result.add(createPolyCurve(elements, true));
		}

		return result;
	}

	/**
	 * This is a helper method, used to avoid excessive use of generics within
	 * other methods of the class.
	 */
	private static GJPolyCirculinearCurve2D<GJCirculinearElement2D> createPolyCurve(
            Collection<? extends GJCirculinearElement2D> elements, boolean closed) {
		return new GJPolyCirculinearCurve2D<GJCirculinearElement2D>(elements,
				closed);
	}

	public static Collection<GJCirculinearContour2D> splitIntersectingContours(
            GJCirculinearContour2D curve1, GJCirculinearContour2D curve2) {

		double pos0, pos1, pos2;

		// ----------------
		// Initializations

		// createFromCollection the array of resulting curves
		ArrayList<GJCirculinearContour2D> contours = new ArrayList<GJCirculinearContour2D>();

		// identify couples of intersections
		double[][] couples = locateIntersections(curve1, curve2);

		// case no intersection between the curves
		if (couples.length == 0) {
			// createFromCollection continuous curve formed only by circulinear elements
			contours.add(curve1);
			contours.add(curve2);
			return contours;
		}

		// stores couple of points in 'twins'
		TreeMap<Double, Double> twins1 = new TreeMap<Double, Double>();
		TreeMap<Double, Double> twins2 = new TreeMap<Double, Double>();

		// stores also positions on each curve in an ordered tree
		TreeSet<Double> positions1 = new TreeSet<Double>();
		TreeSet<Double> positions2 = new TreeSet<Double>();

		// iterate on intersections to populate the data
		for (int i = 0; i < couples.length; i++) {
			pos1 = couples[i][0];
			pos2 = couples[i][1];
			twins1.put(pos1, pos2);
			twins2.put(pos2, pos1);
			positions1.add(pos1);
			positions2.add(pos2);
		}

		// an array for the portions of curves
		ArrayList<GJCirculinearElement2D> elements;

		// Process other curves, while there are intersections left
		while (!twins1.isEmpty()) {
			// createFromCollection new empty array of elements for current contour
			elements = new ArrayList<GJCirculinearElement2D>();

			// get first intersection
			pos0 = twins2.firstEntry().getValue();
			pos1 = pos0;

			do {
				pos2 = nextValue(positions1, pos1);

				// add a portion of the first curve
				addElements(elements, curve1.subCurve(pos1, pos2));

				// get the position of end intersection on second curve
				pos1 = twins1.remove(pos2);

				// get position of next intersection on the second curve
				pos2 = nextValue(positions2, pos1);

				// add a portion of the second curve
				addElements(elements, curve2.subCurve(pos1, pos2));

				// get the position of end intersection on first curve
				pos1 = twins2.remove(pos2);

			} while (pos1 != pos0);

			// createFromCollection continuous curve formed only by circulinear elements
			// and add it to the set of curves
			contours.add(GJBoundaryPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(elements, true));
		}

		return contours;
	}

	/**
	 * Split a collection of contours which possibly intersect each other to a
	 * set of contours which do not intersect each others. Each contour is
	 * assumed not to self-intersect.
	 */
	public static Collection<GJCirculinearContour2D> splitIntersectingContours(
			Collection<? extends GJCirculinearContour2D> curves) {

		double pos0 = 0, pos1, pos2;

		// ----------------
		// Initializations

		// convert collection to array
		GJCirculinearContour2D[] curveArray =
			curves.toArray(new GJCirculinearContour2D[0]);

		// Create array of tree maps for storing
		// 1) index of crossing curve for each intersection of i-th curve
		// 2) position on crossing curve of the intersection point
		int nCurves = curves.size();
		ArrayList<TreeMap<Double, Integer>> twinIndices = 
			new ArrayList<TreeMap<Double, Integer>>(nCurves);
		ArrayList<TreeMap<Double, Double>> twinPositions = 
			new ArrayList<TreeMap<Double, Double>>(nCurves);

		// Populate the two arrays with empty trees
		for (int i = 0; i < nCurves; i++) {
			twinIndices.add(i, new TreeMap<Double, Integer>());
			twinPositions.add(i, new TreeMap<Double, Double>());
		}

		// Create array of tree sets for storing positions of intersections
		// on each curve
		ArrayList<TreeSet<Double>> positions = 
			new ArrayList<TreeSet<Double>>(nCurves);

		// populate the array with empty tree sets
		for (int i = 0; i < nCurves; i++) {
			positions.add(i, new TreeSet<Double>());
		}

		// identify couples of intersections on each couple (i,j) of curves
		for (int i = 0; i < nCurves - 1; i++) {
			GJCirculinearContour2D curve1 = curveArray[i];

			for (int j = i + 1; j < nCurves; j++) {
				GJCirculinearContour2D curve2 = curveArray[j];
				double[][] couples = locateIntersections(curve1, curve2);

				// iterate on intersections to populate the data
				for (int k = 0; k < couples.length; k++) {
					// position on each curve
					pos1 = couples[k][0];
					pos2 = couples[k][1];

					// add positions in their tree sets
					positions.get(i).add(pos1);
					positions.get(j).add(pos2);

					// store indices of corresponding intersecting curves
					twinIndices.get(i).put(pos1, j);
					twinIndices.get(j).put(pos2, i);

					// store positions of intersection point on the
					// corresponding curve
					twinPositions.get(i).put(pos1, pos2);
					twinPositions.get(j).put(pos2, pos1);
				}
			}
		}

		// createFromCollection the array of resulting curves
		ArrayList<GJCirculinearContour2D> contours = new ArrayList<GJCirculinearContour2D>();

		// process curves without intersections
		for (int i = 0; i < nCurves; i++) {
			// If the curve has no intersection, use it as contour
			if (twinPositions.get(i).isEmpty()) {
				contours.add(curveArray[i]);
			}
		}

		// process infinite curves
		for (int i = 0; i < nCurves; i++) {
			// filter bounded curves
			if (curveArray[i].isBounded())
				continue;

			// If the curve has no intersection, it was already processed
			if (twinPositions.get(i).isEmpty()) {
				continue;
			}

			// find first unprocessed intersection
			pos0 = twinPositions.get(i).firstEntry().getKey();
			int ind0 = twinIndices.get(i).firstEntry().getValue();

			// createFromCollection new empty array of elements for current contour
			ArrayList<GJCirculinearElement2D> elements = new ArrayList<GJCirculinearElement2D>();

			// add portion of curve until intersection
			GJCirculinearContour2D curve0 = curveArray[i];
			addElements(elements, curve0.subCurve(curve0.t0(), pos0));

			// init
			pos1 = twinPositions.get(i).firstEntry().getValue();
			int ind = ind0;

			do {
				// the current contour
				GJCirculinearContour2D curve = curveArray[ind];

				// extract next position
				pos2 = nextValue(positions.get(ind), pos1);

				if ((pos2 < pos1) && !curve.isBounded()) {
					// We got the last point of an infinite curve.
					// That means we just finished the current free contour
					// and we just need to add elements
					addElements(elements,
							curve.subCurve(pos1, curve.t1()));
				} else {
					// simple case:
					// add a portion of the current curve to the element list
					addElements(elements, curve.subCurve(pos1, pos2));

					// get the position of end intersection on second curve
					pos1 = twinPositions.get(ind).remove(pos2);
					ind = twinIndices.get(ind).remove(pos2);
				}
			} while (ind != ind0);

			twinPositions.get(i).remove(pos0);
			twinIndices.get(i).remove(pos0);

			// createFromCollection continuous curve formed only by circulinear elements
			// and add it to the set of curves
			contours.add(GJBoundaryPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(elements, true));
		}

		// Process other curves, while there are intersections left
		while (!isAllEmpty(twinPositions)) {
			// createFromCollection new empty array of elements for current contour
			ArrayList<GJCirculinearElement2D> elements = new ArrayList<GJCirculinearElement2D>();

			// indices of the two considered curves.
			int ind0 = 0, ind;

			// get first intersection
			for (int i = 0; i < nCurves; i++) {
				// find a curve with unprocessed intersections
				if (twinPositions.get(i).isEmpty())
					continue;

				// find first unprocessed intersection
				pos0 = twinPositions.get(i).firstEntry().getValue();
				ind0 = twinIndices.get(i).firstEntry().getValue();
				break;
			}

			if (ind0 == 0) {
				System.out.println("No more intersections, but was not detected");
			}

			pos1 = pos0;
			ind = ind0;

			do {
				pos2 = nextValue(positions.get(ind), pos1);

				// add a portion of the first curve
				addElements(elements, curveArray[ind].subCurve(pos1, pos2));

				// get the position of end intersection on second curve
				pos1 = twinPositions.get(ind).remove(pos2);
				ind = twinIndices.get(ind).remove(pos2);
			} while (pos1 != pos0 || ind != ind0);

			// createFromCollection continuous curve formed only by circulinear elements
			// and add it to the set of curves
			contours.add(GJBoundaryPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(elements, true));
		}

		return contours;
	}

	/**
	 * Add all circulinear elements of the given curve to the collection of
	 * circulinear elements.
	 */
	private static void addElements(Collection<GJCirculinearElement2D> elements,
			GJCirculinearContinuousCurve2D curve) {
		elements.addAll(curve.smoothPieces());
	}

	private static boolean isAllEmpty(Collection<TreeMap<Double, Double>> coll) {
		for (TreeMap<?, ?> map : coll) {
			if (!map.isEmpty())
				return false;
		}
		return true;
	}

	/**
	 * Returns either the next value, or the first value of the tree if the
	 * given value is the last one of the tree.
	 */
	private static double nextValue(TreeSet<Double> tree, double value) {
		if (tree.higher(value) == null)
			return tree.first();
		else
			return tree.higher(value);
	}

	public static double getDistanceCurvePoints(GJCirculinearCurve2D curve,
                                                Collection<? extends GJPoint2D> points) {
		double minDist = Double.MAX_VALUE;
		for (GJPoint2D point : points) {
			minDist = Math.min(minDist, curve.distance(point));
		}
		return minDist;
	}
}
