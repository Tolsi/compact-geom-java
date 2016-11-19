/**
 * File: 	GJConvexHull2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 18 janv. 09
 */


import java.util.Collection;





/**
 * Generic interface for classes that allow computing the convex hull of a
 * set of points.
 * @author dlegland
 *
 */
public interface GJConvexHull2D {

	/**
	 * Computes the convex hull of the given collection of points.
	 * @param points a set of points
	 * @return the convex polygon corresponding to the convex hull
	 */
    public abstract GJPolygon2D convexHull(Collection<? extends GJPoint2D> points);
}
