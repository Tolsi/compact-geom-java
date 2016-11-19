/**
 * File: 	GJBufferCalculator.java
 * Project: javageom-buffer
 * 
 * Distributed under the LGPL License.
 *
 * Created: 4 janv. 2011
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;










/**
 * Compute the buffer of a circulinear curve or domain, and gather some
 * methods for computing parallel curves.<p>
 * This class can be instantiated, but also contains a lot of static methods.
 * The default instance of GJBufferCalculator is accessible through the static
 * method 'getDefaultInstance'. The public constructor can be called if
 * different cap or join need to be specified.
 * 
 * @author dlegland
 *
 */
public class GJBufferCalculator {
	
    // ===================================================================
    // static methods and variables

	private static GJBufferCalculator defaultInstance = null;
	
	/**
	 * Returns the default instance of bufferCalculator.
	 */
	public static GJBufferCalculator getDefaultInstance() {
		if (defaultInstance == null)
			defaultInstance = new GJBufferCalculator();
		return defaultInstance;
	}
	
    // ===================================================================
    // Class variables

	private GJJoinFactory joinFactory;
	private GJCapFactory capFactory;
	
    // ===================================================================
    // Constructors

	/**
	 * Creates a new buffer calculator with default join and cap factories.
	 */
	public GJBufferCalculator() {
		this.joinFactory = new GJRoundJoinFactory();
		this.capFactory = new GJRoundCapFactory();
	}
	
	/**
	 * Creates a new buffer calculator with specific join and cap factories.
	 */
	public GJBufferCalculator(GJJoinFactory joinFactory, GJCapFactory capFactory) {
		this.joinFactory = joinFactory;
		this.capFactory = capFactory;
	}
	
	
    // ===================================================================
    // General methods

	/**
	 * Computes the parallel curve of a circulinear curve (composed only of
	 * pieces of lines and circles). 
	 * The result is itself a circulinear curve.
	 */
	public GJCirculinearCurve2D createParallel(
			GJCirculinearCurve2D curve, double dist) {
		
		// case of a continuous curve -> call specialized method
		if (curve instanceof GJCirculinearContinuousCurve2D) {
			return createContinuousParallel(
					(GJCirculinearContinuousCurve2D)curve, dist);
		} 
		
		// Create array for storing result
		GJCirculinearCurveArray2D<GJCirculinearContinuousCurve2D> parallels =
			new GJCirculinearCurveArray2D<GJCirculinearContinuousCurve2D>();
		
		// compute parallel of each continuous part, and add it to the result
		for (GJCirculinearContinuousCurve2D continuous :
			curve.continuousCurves()){
			GJCirculinearContinuousCurve2D contParallel =
				createContinuousParallel(continuous, dist);
			if (contParallel != null)
				parallels.add(contParallel);
		}
		
		// return the set of parallel curves
		return parallels;
	}

	public GJCirculinearBoundary2D createParallelBoundary(
			GJCirculinearBoundary2D boundary, double dist) {
		
		// in the case of a single contour, return the parallel of the contour
		if (boundary instanceof GJCirculinearContour2D)
			return createParallelContour((GJCirculinearContour2D) boundary, dist);
		
		// get the set of individual contours
		Collection<? extends GJCirculinearContour2D> contours =
			boundary.continuousCurves();
		
		// allocate the array of parallel contours
		Collection<GJCirculinearContour2D> parallelContours =
			new ArrayList<GJCirculinearContour2D>(contours.size());
		
		// compute the parallel of each contour
		for(GJCirculinearContour2D contour : contours)
			parallelContours.add(contour.parallel(dist));
		
		// Create an agglomeration of the curves
		return GJCirculinearContourArray2D.createCirculinearContour2DFromCollection(parallelContours);
	}

	public GJCirculinearContour2D createParallelContour(
			GJCirculinearContour2D contour, double dist) {
		
		// straight line is already a circulinear contour
		if (contour instanceof GJStraightLine2D) {
			return ((GJStraightLine2D) contour).parallel(dist);
		} 
		// The circle is already a circulinear contour
		if (contour instanceof GJCircle2D) {
			return ((GJCircle2D) contour).parallel(dist);
		} 

		// extract collection of parallel curves, that connect each other
		Collection<GJCirculinearContinuousCurve2D> parallelCurves =
			getParallelElements(contour, dist);
		
		// Create a new boundary with the set of parallel curves
		return GJBoundaryPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(parallelCurves,
				contour.isClosed());
	}
	
	/**
	 * Compute the parallel curve of a Circulinear and continuous curve. 
	 * The result is itself an instance of GJCirculinearContinuousCurve2D.
	 */
	public GJCirculinearContinuousCurve2D createContinuousParallel(
			GJCirculinearContinuousCurve2D curve, double dist) {
		
		// For circulinear elements, getParallel() is already implemented
		if (curve instanceof GJCirculinearElement2D) {
			return ((GJCirculinearElement2D) curve).parallel(dist);
		} 

		// extract collection of parallel curves, that connect each other
		Collection<GJCirculinearContinuousCurve2D> parallelCurves =
			getParallelElements(curve, dist);
		
		// Create a new circulinear continuous curve with the set of parallel
		// curves
		return GJPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(parallelCurves, curve.isClosed());
	}
	
	private Collection<GJCirculinearContinuousCurve2D> getParallelElements(
			GJCirculinearContinuousCurve2D curve, double dist) {
		
		// extract collection of circulinear elements
		Collection<? extends GJCirculinearElement2D> elements =
			curve.smoothPieces();
		
		Iterator<? extends GJCirculinearElement2D> iterator =
			elements.iterator();

		// previous curve
		GJCirculinearElement2D previous = null;
		GJCirculinearElement2D current = null;

		// createFromCollection array for storing result
		ArrayList<GJCirculinearContinuousCurve2D> parallelCurves =
			new ArrayList<GJCirculinearContinuousCurve2D> ();

		// check if curve is empty
		if (!iterator.hasNext())
			return parallelCurves;

		// add parallel to the first curve
		current = iterator.next();
		GJCirculinearElement2D parallel = current.parallel(dist);
		parallelCurves.add(parallel);

		// iterate on circulinear element couples
		GJCirculinearContinuousCurve2D join;
		while (iterator.hasNext()){
			// update the couple of circulinear elements
			previous = current;
			current = iterator.next();

			// add circle arc between the two curve elements
			join = joinFactory.createJoin(previous, current, dist);
			if (join.length() > 0)
				parallelCurves.add(join);
			
			// add parallel to set of parallels
			parallelCurves.add(current.parallel(dist));
		}

		// Add eventually a circle arc to close the parallel curve
		if (curve.isClosed()) {
			previous = current;
			current = elements.iterator().next();
			
			join = joinFactory.createJoin(previous, current, dist);
			if (join.length() > 0)
				parallelCurves.add(join);
		}

		return parallelCurves;
	}
	
	/**
	 * Compute the buffer of a circulinear curve.<p>
	 * The algorithm is as follow:
	 * <ol>
	 * <li> split the curve into a set of curves without self-intersections
	 * <li> for each split curve, compute the contour of its buffer
	 * <li> split self-intersecting contours into set of disjoint contours
	 * <li> split all contour which intersect each other to disjoint contours
	 * <li> remove contours which are too close from the original curve
	 * <li> createFromCollection a new domain with the final set of contours
	 * </ol>
	 */
	public GJCirculinearDomain2D computeBuffer(
			GJCirculinearCurve2D curve, double dist) {
		
		ArrayList<GJCirculinearContour2D> contours =
			new ArrayList<GJCirculinearContour2D>();
		
		// iterate on all continuous curves
		for (GJCirculinearContinuousCurve2D cont : curve.continuousCurves()) {
			// split the curve into a set of non self-intersecting curves
			for (GJCirculinearContinuousCurve2D splitted :
				GJCirculinearCurves2D.splitContinuousCurve(cont)) {
				// compute the rings composing the simple curve buffer
				contours.addAll(computeBufferSimpleCurve(splitted, dist));
			}
		}
		
		// split contours which intersect each others
		contours = new ArrayList<GJCirculinearContour2D>(
				GJCirculinearCurves2D.splitIntersectingContours(contours));
		
		// Remove contours that cross or that are too close from base curve
		ArrayList<GJCirculinearContour2D> contours2 =
			new ArrayList<GJCirculinearContour2D>(contours.size());
		Collection<GJPoint2D> intersects;
		Collection<GJPoint2D> vertices;
		
		for (GJCirculinearContour2D contour : contours) {
			
			// do not keep contours which cross original curve
			intersects = GJCirculinearCurves2D.findIntersections(curve, contour);
			
			// remove intersection points that are vertices of the reference curve
			vertices = curve.singularPoints();
			vertices = curve.vertices();
			intersects.removeAll(vertices);
			
			if (intersects.size() > 0)
				continue;
			
			// check that vertices of contour are not too close from original
			// curve
			double distCurves = 
				getDistanceCurveSingularPoints(curve, contour);
			if(distCurves < dist- GJShape2D.ACCURACY)
				continue;
			
			// keep the contours that meet the above conditions
			contours2.add(contour);
		}
		
		// All the rings are created, we can now createFromCollection a new domain with the
		// set of rings
		return new GJGenericCirculinearDomain2D(
				GJCirculinearContourArray2D.createCirculinearContour2DFromCollection(contours2));
	}
	
	/**
	 * Compute buffer of a point set.
	 */
	public GJCirculinearDomain2D computeBuffer(GJPointSet2D set,
											   double dist) {
		// createFromCollection array for storing result
		Collection<GJCirculinearContour2D> contours =
			new ArrayList<GJCirculinearContour2D>(set.size());
		
		// for each point, add a new circle
		for (GJPoint2D point : set) {
			contours.add(new GJCircle2D(point, Math.abs(dist), dist > 0));
		}
		
		// process circles to remove intersections
		contours = GJCirculinearCurves2D.splitIntersectingContours(contours);
		
		// Remove contours that cross or that are too close from base curve
		ArrayList<GJCirculinearContour2D> contours2 =
			new ArrayList<GJCirculinearContour2D>(contours.size());
		for (GJCirculinearContour2D ring : contours) {
			
			// check that vertices of contour are not too close from original
			// curve
			double minDist = GJCirculinearCurves2D.getDistanceCurvePoints(
					ring, set.points());
			if(minDist < dist- GJShape2D.ACCURACY)
				continue;
			
			// keep the contours that meet the above conditions
			contours2.add(ring);
		}

		return new GJGenericCirculinearDomain2D(
				GJCirculinearContourArray2D.createCirculinearContour2DFromCollection(contours2));
	}

	/**
	 * Computes the buffer of a simple curve.
	 * This method should replace the method 'computeBufferSimpleContour'.
	 */
	private Collection<? extends GJCirculinearContour2D>
	computeBufferSimpleCurve(GJCirculinearContinuousCurve2D curve, double d) {
		
		Collection<GJCirculinearContour2D> contours =
			new ArrayList<GJCirculinearContour2D>(2);

		// the parallel in each side
		GJCirculinearContinuousCurve2D parallel1, parallel2;
		parallel1 = createContinuousParallel(curve, d);
		parallel2 = createContinuousParallel(curve, -d).reverse();
		
		if (curve.isClosed()) {
			// each parallel is itself a contour
			contours.add(convertCurveToBoundary(parallel1));
			contours.add(convertCurveToBoundary(parallel2));
		} else {
			// createFromCollection a new contour from the two parallels and 2 caps
			contours.addAll(createSingleContourFromTwoParallels(parallel1, parallel2));
		}
				
		// some contours may intersect, so we split them
		Collection<GJCirculinearContour2D> contours2 =
			removeIntersectingContours(contours, curve, d);

		// return the set of created contours
		return contours2;
	}
	
	/**
	 * Creates the unique contour based on two parallels of the base curve, by
	 * adding appropriate circle arcs at extremities of the base curve.
	 */
	private Collection<GJCirculinearContour2D>
	createSingleContourFromTwoParallels(
			GJCirculinearContinuousCurve2D curve1,
			GJCirculinearContinuousCurve2D curve2) {
		
		// createFromCollection array for storing result
		ArrayList<GJCirculinearContour2D> contours =
			new ArrayList<GJCirculinearContour2D>();
		
		GJCirculinearContinuousCurve2D cap;
		
		// createFromCollection new ring using two open curves and two circle arcs
		if (curve1 != null && curve2 != null){
			// array of elements for creating new ring.
			ArrayList<GJCirculinearElement2D> elements =
				new ArrayList<GJCirculinearElement2D>();

			// some shortcuts for computing infinity of curve
			boolean b0 = !GJCurves2D.isLeftInfinite(curve1);
			boolean b1 = !GJCurves2D.isRightInfinite(curve1);

			if (b0 && b1) {
					// case of a curve finite at each extremity

					// extremity points
					GJPoint2D p11 = curve1.firstPoint();
					GJPoint2D p12 = curve1.lastPoint();
					GJPoint2D p21 = curve2.firstPoint();
					GJPoint2D p22 = curve2.lastPoint();

					// Check how to associate open curves and circle arcs
					elements.addAll(curve1.smoothPieces());					
					cap = capFactory.createCap(p12, p21);
					elements.addAll(cap.smoothPieces());
					elements.addAll(curve2.smoothPieces());
					cap = capFactory.createCap(p22, p11);
					elements.addAll(cap.smoothPieces());
					
					// createFromCollection the last ring
					contours.add(new GJGenericCirculinearRing2D(elements));
					
			} else if (!b0 && !b1) {
				// case of an infinite curve at both extremities
				// In this case, the two parallel curves do not join,
				// and are added as contours individually					
				contours.add(convertCurveToBoundary(curve1));
				contours.add(convertCurveToBoundary(curve2));
				
			} else if (b0 && !b1) {
				// case of a curve starting from infinity, and finishing
				// on a given point

				// extremity points
				GJPoint2D p11 = curve1.firstPoint();
				GJPoint2D p22 = curve2.lastPoint();

				// add elements of the new contour
				elements.addAll(curve2.smoothPieces());
				cap = capFactory.createCap(p22, p11);
				elements.addAll(cap.smoothPieces());
				elements.addAll(curve1.smoothPieces());

				// createFromCollection the last ring
				contours.add(new GJGenericCirculinearRing2D(elements));
				
			} else if (b1 && !b0) {
				// case of a curve starting at a point and finishing at
				// the infinity

				// extremity points
				GJPoint2D p12 = curve1.lastPoint();
				GJPoint2D p21 = curve2.firstPoint();

				// add elements of the new contour
				elements.addAll(curve1.smoothPieces());
				cap = capFactory.createCap(p12, p21);
				elements.addAll(cap.smoothPieces());
				elements.addAll(curve2.smoothPieces());

				// createFromCollection the last contour
				contours.add(new GJGenericCirculinearRing2D(elements));

			}
		}
		
		return contours;
	}
	
	private Collection<GJCirculinearContour2D> removeIntersectingContours (
			Collection<GJCirculinearContour2D> contours,
			GJCirculinearCurve2D curve, double d) {
		// prepare an array to store the set of rings
		ArrayList<GJCirculinearContour2D> contours2 =
			new ArrayList<GJCirculinearContour2D>();

		// iterate on the set of rings
		for (GJCirculinearContour2D contour : contours)
			// split rings into curves which do not self-intersect
			for (GJCirculinearContinuousCurve2D splitted :
				GJCirculinearCurves2D.splitContinuousCurve(contour)) {
				
				// compute distance to original curve
				// (assuming it is sufficient to compute distance to vertices
				// of the reference curve).
				double dist = GJCirculinearCurves2D.getDistanceCurvePoints(
						curve, splitted.singularPoints());
				
				// check if distance condition is verified
				if (dist-d < -GJShape2D.ACCURACY)
					continue;
				
				// convert the set of elements to a Circulinear ring
				contours2.add(convertCurveToBoundary(splitted));
		}
		
		// return the set of created rings
		return contours2;		
	}
	
	/**
	 * Converts the given continuous curve to an instance of
	 * GJCirculinearContour2D. This can be the curve itself, a new instance of
	 * GJGenericCirculinearRing2D if the curve is bounded, or a new instance of
	 * GJBoundaryPolyCirculinearCurve2D if the curve is unbounded.
	 */
	private GJCirculinearContour2D convertCurveToBoundary (
			GJCirculinearContinuousCurve2D curve) {
		// basic case: curve is already a contour
		if (curve instanceof GJCirculinearContour2D)
			return (GJCirculinearContour2D) curve;
		
		// if the curve is closed, return an instance of GJGenericCirculinearRing2D
		if (curve.isClosed())
			return GJGenericCirculinearRing2D.createGenericCirculinearRing2DFromCollection(curve.smoothPieces());
		
		return GJBoundaryPolyCirculinearCurve2D.createCirculinearContinuousCurve2DFromCollection(curve.smoothPieces());
	}
	
	private double getDistanceCurveSingularPoints(
			GJCirculinearCurve2D ref, GJCirculinearCurve2D curve){
		// extract singular points
		Collection<GJPoint2D> points = curve.singularPoints();
		
		// If no singular point, choose an arbitrary point on the curve
		if (points.isEmpty()) {
			points = new ArrayList<GJPoint2D>();
			double t = GJCurves2D.choosePosition(curve.t0(), curve.t1());
			points.add(curve.point(t));
		}
		
		// Iterate on points to get minimal distance
		double minDist = Double.MAX_VALUE;
		for (GJPoint2D point : points){
			minDist = Math.min(minDist, ref.distance(point));
		}
		return minDist;
	}
}
