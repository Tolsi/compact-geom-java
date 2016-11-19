/**
 * File: 	GJGrahamScan2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 18 janv. 09
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;







/**
 * Computation of the convex hull using Graham scan algorithm. 
 * Note that in its current implementation, this algorithms fails when some 
 * points are colinear.
 * 
 * @author dlegland
 *
 */
public class GJGrahamScan2D implements GJConvexHull2D {

    /**
     * Creates a new Convex hull calculator.
     */
    public GJGrahamScan2D() {
    }

    /* (non-Javadoc)
     * @see math.geom2d.polygon.convhull.GJConvexHull2D#convexHull(java.util.Collection)
     */
    public GJPolygon2D convexHull(Collection<? extends GJPoint2D> points) {
        int nbPoints = points.size();
        //TODO: manage small values of n
        
        // Find point with lowest y-coord
        GJPoint2D lowestPoint = null;
        double lowestY = Double.MAX_VALUE;
        for(GJPoint2D point : points){
            double y = point.y();
            if(y<lowestY){
                lowestPoint = point;
                lowestY = y;
            }
        }
        
        // build the comparator, using the lowest point
        Comparator<GJPoint2D> comparator =
            new CompareByPseudoAngle(lowestPoint);
        
        // createFromCollection a sorted set
        ArrayList<GJPoint2D> sorted = new ArrayList<GJPoint2D>(nbPoints);
        sorted.addAll(points);
        Collections.sort(sorted, comparator);
        
        // main loop
        // i-> current vertex of point cloud
        // m-> current hull vertex
        int m = 2;
        for(int i=3; i<nbPoints; i++){
            while(GJPoint2D.ccw(sorted.get(m), sorted.get(m-1),
                    sorted.get(i))>=0)
                m--;
            m++;
            Collections.swap(sorted, m, i);
        }

        // Format result to return a polygon
        List<GJPoint2D> hull = sorted.subList(0, Math.min(m+1, nbPoints));
        return new GJSimplePolygon2D(hull);
    }

    private class CompareByPseudoAngle implements Comparator<GJPoint2D>{
        GJPoint2D basePoint;
        public CompareByPseudoAngle(GJPoint2D base) {
            this.basePoint = base;
        }
        
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(GJPoint2D point1, GJPoint2D point2) {
            double angle1 = GJAngle2D.pseudoAngle(basePoint, point1);
            double angle2 = GJAngle2D.pseudoAngle(basePoint, point2);
            
            if(angle1<angle2) return -1;
            if(angle1>angle2) return +1;
            //TODO: and what about colinear points ?
            return 0;
        }
    }
}
