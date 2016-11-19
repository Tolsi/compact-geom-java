/**
 * 
 */



import java.util.ArrayList;
import java.util.Collection;






/**
 * Computes the convex hull of a set of points as a single GJPolygon2D by
 * gift wrapping algorithm, also know as Jarvis March.</p>
 * 
 * The complexity of the algorithm is of O(n * h), where n is the number of 
 * input points and h the number of vertices of the convex hull. This low
 * complexity makes it the best choice algorithm for computing convex hull.
 * 
 * @author dlegland
 */
public class GJJarvisMarch2D implements GJConvexHull2D {

	/**
	 * Creates a new Convex hull calculator.
	 */
	public GJJarvisMarch2D() {
	}

	/**
	 * Computes the convex hull of a set of points as a single GJPolygon2D.
	 * Current implementation start at the point with lowest y-coord. The points
	 * are considered in counter-clockwise order. Result is an instance of
	 * GJSimplePolygon2D. Complexity is O(n*h), with n number of points, h number
	 * of points of the hull. Worst case complexity is O(n^2).
	 */
	public GJPolygon2D convexHull(Collection<? extends GJPoint2D> points) {
		// Init iteration on points
		GJPoint2D lowestPoint = null;
		double y;
		double ymin = Double.MAX_VALUE;

		// Iteration on the set of points to find point with lowest y-coord
		for (GJPoint2D point : points) {
			y = point.y();
			if (y < ymin) {
				ymin = y;
				lowestPoint = point;
			}
		}

		// initialize array of points located on convex hull
		ArrayList<GJPoint2D> hullPoints = new ArrayList<GJPoint2D>();

		// Init iteration on points
		GJPoint2D currentPoint = lowestPoint;
		GJPoint2D nextPoint = null;
		double angle = 0;

		// Iterate on point set to find point with smallest angle with respect
		// to previous line
		do {
			hullPoints.add(currentPoint);
			nextPoint = findNextPoint(currentPoint, angle, points);
			angle = GJAngle2D.horizontalAngle(currentPoint, nextPoint);
			currentPoint = nextPoint;
		} while (currentPoint != lowestPoint);

		// Create a polygon with points located on the convex hull
		return new GJSimplePolygon2D(hullPoints);
	}

	private GJPoint2D findNextPoint(GJPoint2D basePoint, double startAngle,
									Collection<? extends GJPoint2D> points) {
		GJPoint2D minPoint = null;
		double minAngle = Double.MAX_VALUE;
		double angle;

		for (GJPoint2D point : points) {
			// Avoid to test same point
			if (basePoint.equals(point))
				continue;

			// Compute angle between current direction and next point
			angle = GJAngle2D.horizontalAngle(basePoint, point);
			angle = GJAngle2D.formatAngle(angle - startAngle);

			// Keep current point if angle is minimal
			if (angle < minAngle) {
				minAngle = angle;
				minPoint = point;
			}
		}

		return minPoint;
	}
}
