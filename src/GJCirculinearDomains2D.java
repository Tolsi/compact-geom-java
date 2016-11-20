import java.util.ArrayList;
import java.util.Collection;

/**
 * Some utilities for working with circulinear curves.
 * @author dlegland
 *
 */
public class GJCirculinearDomains2D {
	
	public final static GJCirculinearDomain2D computeBuffer(
            GJCirculinearDomain2D domain, double dist) {
		
		ArrayList<GJCirculinearContour2D> rings =
			new ArrayList<GJCirculinearContour2D>();
		
		// iterate on all continuous curves
		for(GJCirculinearContour2D contour : domain.contours()) {
			// split the curve into a set of non self-intersecting curves
			for(GJCirculinearContinuousCurve2D simpleCurve :
				GJCirculinearCurves2D.splitContinuousCurve(contour)) {
				GJCirculinearContour2D boundary =
					new GJBoundaryPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(
							simpleCurve.smoothPieces(), contour.isClosed());
				// compute the rings composing the simple curve buffer
				rings.addAll(computeBufferSimpleRing(boundary, dist));
			}
		}
		
		// All the rings are created, we can now createFromCollection a new domain with the
		// set of rings
		return new GJGenericCirculinearDomain2D(
				new GJCirculinearContourArray2D<GJCirculinearContour2D>(rings));
	}
	
	/**
	 * Computes the rings that form the domain of a circulinear curve which
	 * does not self-intersect.
	 */
	public final static Collection<GJCirculinearContour2D>
	computeBufferSimpleRing(GJCirculinearContour2D curve, double d) {
		
		// prepare an array to store the set of rings
		ArrayList<GJCirculinearContour2D> rings =
			new ArrayList<GJCirculinearContour2D>();
		
		// the parallel in the positive side
		GJCirculinearContinuousCurve2D parallel1 = curve.parallel(d);
		
		// split each parallel into continuous curves
		GJCirculinearCurveArray2D<GJCirculinearContinuousCurve2D> curves =
			new GJCirculinearCurveArray2D<GJCirculinearContinuousCurve2D>();
		
		// select only curve parts which do not cross original curve
		for(GJCirculinearContinuousCurve2D split :
				GJCirculinearCurves2D.splitContinuousCurve(parallel1)) {
			if(GJCirculinearCurves2D.findIntersections(curve, split).size()==0)
				curves.add(split);
		}
		
		// createFromCollection a new boundary for each parallel curve
		for(GJCirculinearContinuousCurve2D split : curves) {
			rings.add(
					new GJBoundaryPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(
							split.smoothPieces(), split.isClosed()));
		}
		
		// prepare an array to store the set of rings
		ArrayList<GJCirculinearContour2D> rings2 =
			new ArrayList<GJCirculinearContour2D>();

		// iterate on the set of rings
		for(GJCirculinearContour2D ring : rings)
			// split rings into curves which do not self-intersect
			for(GJCirculinearContinuousCurve2D split :
				GJCirculinearCurves2D.splitContinuousCurve(ring)) {
				
				// compute distance to original curve
				// (assuming it is sufficient to compute distance to vertices
				// of the reference curve).
				double dist = GJCirculinearCurves2D.getDistanceCurvePoints(
						curve, split.singularPoints());
				
				// check if distance condition is verified
				if(dist-d<-GJShape2D.ACCURACY)
					continue;
				
				// convert the set of elements to a Circulinear ring
				rings2.add(
						new GJBoundaryPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(
								split.smoothPieces(), split.isClosed()));
		}
		
		// return the set of created rings
		return rings2;
	}
}
