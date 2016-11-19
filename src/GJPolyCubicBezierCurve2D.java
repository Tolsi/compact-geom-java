/* file : PolyCubicBezierCurve2D.java
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
 * Created on 8 mai 2006
 *
 */



import java.util.Collection;







/**
 * A set of Bezier curves that forms a continuous curve.
 * 
 * @author dlegland
 */
public class GJPolyCubicBezierCurve2D extends GJPolyCurve2D<GJCubicBezierCurve2D> {

    // ===================================================================
    // Static methods

    /**
     * Creates a series a cubic bezier curves, by grouping 4 adjacent points.
     * Two consecutive curves share one point, N curves will require 3*n+1
     * points.
     */
    public final static GJPolyCubicBezierCurve2D create(GJPoint2D... points){
    	// number of points
    	int np = points.length;
    	
    	// compute number of curves
		int nc = (np - 1) / 3;
    	
    	// createFromCollection array of curves
    	GJPolyCubicBezierCurve2D polyBezier = new GJPolyCubicBezierCurve2D(nc);
    	
    	// build each curve
		for (int i = 0; i < np - 3; i += 3) {
			polyBezier.add(new GJCubicBezierCurve2D(
					points[i],
					points[i + 1], 
					points[i + 2], 
					points[i + 3]));
		}
		
    	// return the curve
    	return polyBezier;
    }

    
    /**
     * Creates a series a cubic bezier curves, by grouping consecutive couples
     * of points and vectors. A polycurve composed of N Bezier curves requires
     * N+1 points and N+1 vectors. 
     */
    public final static GJPolyCubicBezierCurve2D create(
            GJPoint2D[] points, GJVector2D[] vectors){
    	// number of points
    	int np = Math.min(points.length, vectors.length);
    	
		// compute number of curves
		int nc = (np - 1) / 2;

		// createFromCollection array of curves
		GJPolyCubicBezierCurve2D polyBezier = new GJPolyCubicBezierCurve2D(nc);

		// build each curve
		for (int i = 0; i < nc - 1; i += 2) {
			polyBezier.add(new GJCubicBezierCurve2D(
					points[i],
					vectors[i], 
					points[i + 1], 
					vectors[i + 1]));
		}
		
    	// return the curve
    	return polyBezier;
    }

    
	// ===================================================================
    // Constructors

    public GJPolyCubicBezierCurve2D() {
        super();
    }

    public GJPolyCubicBezierCurve2D(int n) {
        super(n);
    }

    public GJPolyCubicBezierCurve2D(GJCubicBezierCurve2D... curves) {
        super(curves);
    }

    public GJPolyCubicBezierCurve2D(Collection<GJCubicBezierCurve2D> curves) {
        super(curves);
    }
    

    // ===================================================================
    // Methods specific to GJPolyCubicBezierCurve2D

    /**
     * Returns a new set of GJPolyCubicBezierCurve2D.
     */
    @Override
    public GJCurveSet2D<? extends GJPolyCubicBezierCurve2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveSet2D<GJPolyCubicBezierCurve2D> result =
        	new GJCurveArray2D<GJPolyCubicBezierCurve2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJPolyCubicBezierCurve2D)
                result.add((GJPolyCubicBezierCurve2D) curve);
        }
        return result;
    }

    @Override
    public GJPolyCubicBezierCurve2D transform(GJAffineTransform2D trans) {
        GJPolyCubicBezierCurve2D result = new GJPolyCubicBezierCurve2D(this.curves.size());
        for (GJCubicBezierCurve2D curve : curves)
            result.add(curve.transform(trans));
        return result;
    }

}
