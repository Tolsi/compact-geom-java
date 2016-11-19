/* file : Polyline2D.java
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



import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;











/**
 * A polyline is a continuous curve where each piece of the curve is a
 * GJLineSegment2D.
 * 
 * @author dlegland
 */
public class GJPolyline2D extends GJLinearCurve2D
implements GJCirculinearContinuousCurve2D, Cloneable {

    // ===================================================================
    // Static methods
    
    /**
     * Static factory for creating a new GJPolyline2D from a collection of
     * points.
     * @since 0.8.1
     */
    public static GJPolyline2D create(Collection<? extends GJPoint2D> points) {
    	return new GJPolyline2D(points);
    }
    
    /**
     * Static factory for creating a new GJPolyline2D from an array of
     * points.
     * @since 0.8.1
     */
    public static GJPolyline2D create(GJPoint2D... points) {
    	return new GJPolyline2D(points);
    }

    
    // ===================================================================
    // Contructors

    public GJPolyline2D() {
    	super(1);
    }

    /**
     * Creates a new polyline by allocating enough memory for the specified
     * number of vertices.
     * @param nVertices
     */
    public GJPolyline2D(int nVertices) {
    	super(nVertices);
    }

    public GJPolyline2D(GJPoint2D initialPoint) {
        this.vertices.add(initialPoint);
    }

    public GJPolyline2D(GJPoint2D... vertices) {
        super(vertices);
    }

    public GJPolyline2D(Collection<? extends GJPoint2D> vertices) {
        super(vertices);
    }

    public GJPolyline2D(double[] xcoords, double[] ycoords) {
    	super(xcoords, ycoords);
    }
    
    public GJPolyline2D(GJLinearCurve2D lineString) {
    	super(lineString.vertices);
    	if (lineString.isClosed()) 
    		this.vertices.add(lineString.firstPoint());
    }
    
    // ===================================================================
    // Methods implementing GJLinearCurve2D methods

    /**
     * Returns a simplified version of this polyline, by using Douglas-Peucker
     * algorithm.
     */
    public GJPolyline2D simplify(double distMax) {
    	return new GJPolyline2D(GJPolylines2D.simplifyPolyline(this.vertices, distMax));
    }

    /**
     * Returns an array of GJLineSegment2D. The number of edges is the number of
     * vertices minus one.
     * 
     * @return the edges of the polyline
     */
    public Collection<GJLineSegment2D> edges() {
        int n = vertices.size();
        ArrayList<GJLineSegment2D> edges = new ArrayList<GJLineSegment2D>(n);

        if (n < 2)
            return edges;

        for (int i = 0; i < n-1; i++)
            edges.add(new GJLineSegment2D(vertices.get(i), vertices.get(i+1)));

        return edges;
    }
    
    public int edgeNumber() {
    	int n = vertices.size(); 
    	if (n > 1) 
    		return n - 1;
    	return 0;
    }
    
    public GJLineSegment2D edge(int index) {
    	return new GJLineSegment2D(vertices.get(index), vertices.get(index+1));
    }

    public GJLineSegment2D lastEdge() {
        int n = vertices.size();
        if (n < 2)
            return null;
        return new GJLineSegment2D(vertices.get(n-2), vertices.get(n-1));
    }

    // ===================================================================
    // Methods implementing the GJCirculinearCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearContinuousCurve2D transform(GJCircleInversion2D inv) {
		
		// Create array for storing transformed arcs
		Collection<GJLineSegment2D> edges = this.edges();
		ArrayList<GJCirculinearContinuousCurve2D> arcs =
			new ArrayList<GJCirculinearContinuousCurve2D>(edges.size());
		
		// Transform each arc
		for(GJLineSegment2D edge : edges) {
			arcs.add(edge.transform(inv));
		}
		
		// createFromCollection the transformed shape
		return new GJPolyCirculinearCurve2D<GJCirculinearContinuousCurve2D>(arcs);
	}


	// ===================================================================
    // Methods implementing the GJContinuousCurve2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#windingAngle(GJPoint2D)
     */
    public double windingAngle(GJPoint2D point) {
        double angle = 0;
        int n = vertices.size();
        for (int i = 0; i<n-1; i++)
            angle += new GJLineSegment2D(vertices.get(i), vertices.get(i+1))
                    .windingAngle(point);

        return angle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#isInside(GJPoint2D)
     */
    public boolean isInside(GJPoint2D pt) {
        if (new GJLinearRing2D(this.vertexArray()).isInside(pt))
            return true;

        // can not compute orientation if number of vertices if too low
		if (this.vertices.size() < 3)
			return false;

		// check line corresponding to first edge
        GJPoint2D p0 = this.firstPoint();
        GJPoint2D q0 = this.vertex(1);
        if (new GJStraightLine2D(q0, p0).isInside(pt))
            return false;

		// check line corresponding to last edge
        GJPoint2D p1 = this.lastPoint();
        GJPoint2D q1 = this.vertex(this.vertexNumber() - 2);
        if (new GJStraightLine2D(p1, q1).isInside(pt))
            return false;

        // check line joining the two extremities
        if (new GJStraightLine2D(p0, p1).isInside(pt))
            return true;

        return false;
    }

    
    // ===================================================================
    // Methods inherited from GJContinuousCurve2D

    /**
     * Returns false, as GJPolyline2D is open by definition.
     */
    public boolean isClosed() {
        return false;
    }

    
    // ===================================================================
    // Methods inherited from GJCurve2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#point(double, math.geom2d.GJPoint2D)
     */
    public GJPoint2D point(double t) {
        // format position to stay between limits
        double t0 = this.t0();
        double t1 = this.t1();
        t = Math.max(Math.min(t, t1), t0);

        // index of vertex before point
        int ind0 = (int) Math.floor(t+ GJShape2D.ACCURACY);
        double tl = t - ind0;
        GJPoint2D p0 = vertices.get(ind0);

		// check if equal to a vertex
		if (Math.abs(t - ind0) < GJShape2D.ACCURACY)
			return p0;

        // index of vertex after point
        int ind1 = ind0+1;
        GJPoint2D p1 = vertices.get(ind1);

        // position on line;
		double x0 = p0.x();
		double y0 = p0.y();
		double dx = p1.x() - x0;
		double dy = p1.y() - y0;
		return new GJPoint2D(x0 + tl * dx, y0 + tl * dy);
	}

    /**
     * Returns the number of points in the polyline, minus one.
     */
    public double t1() {
        return vertices.size() - 1;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }

	/**
     * Returns the last point of this polyline, or null if the polyline does 
     * not contain any point.
     */
	@Override
    public GJPoint2D lastPoint() {
        if (vertices.size() == 0)
            return null;
        return vertices.get(vertices.size()-1);
    }

    /**
     * Returns the polyline with same points considered in reverse order.
     * Reversed polyline keep same references as original polyline.
     */
    public GJPolyline2D reverse() {
        GJPoint2D[] points2 = new GJPoint2D[vertices.size()];
        int n = vertices.size();
        for (int i = 0; i < n; i++)
			points2[i] = vertices.get(n - 1 - i);
        return new GJPolyline2D(points2);
    }

	@Override
    public Collection<? extends GJPolyline2D> continuousCurves() {
    	return wrapCurve(this);
    }


    /**
     * Return an instance of GJPolyline2D. If t1 is lower than t0, return an
     * instance of GJPolyline2D with zero points.
     */
    public GJPolyline2D subCurve(double t0, double t1) {
        // code adapted from GJCurveSet2D

        GJPolyline2D res = new GJPolyline2D();

		if (t1 < t0)
			return res;

		// number of points in the polyline
		int indMax = (int) this.t1();

      // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), indMax);
        t1 = Math.min(Math.max(t1, 0), indMax);

        // find curves index
        int ind0 = (int) Math.floor(t0);
        int ind1 = (int) Math.floor(t1);

        // need to subdivide only one line segment
        if (ind0 == ind1) {
            // extract limit points
            res.addVertex(this.point(t0));
            res.addVertex(this.point(t1));
            // return result
            return res;
        }

        // add the point corresponding to t0
        res.addVertex(this.point(t0));

        // add all the whole points between the 2 cuts
        for (int n = ind0 + 1; n <= ind1; n++)
            res.addVertex(vertices.get(n));

        // add the last point
        res.addVertex(this.point(t1));

        // return the polyline
        return res;
    }

    // ===================================================================
    // Methods implementing the GJShape2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#transform(math.geom2d.GJAffineTransform2D)
     */
    public GJPolyline2D transform(GJAffineTransform2D trans) {
        GJPoint2D[] pts = new GJPoint2D[vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
            pts[i] = trans.transform(vertices.get(i));
        return new GJPolyline2D(pts);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJContinuousCurve2D#appendPath(java.awt.geom.GeneralPath)
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {

        if (vertices.size() < 2)
            return path;

        // get point iterator
        Iterator<GJPoint2D> iter = vertices.iterator();

        // avoid first point
        GJPoint2D point = iter.next();
       
        // line to each other point
        while (iter.hasNext()) {
            point = iter.next();
            path.lineTo((float) (point.x()), (float) (point.y()));
        }

        return path;
    }

    /**
     * Returns a general path iterator.
     */
    public java.awt.geom.GeneralPath asGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        if (vertices.size()<2)
            return path;

        // get point iterator
        Iterator<GJPoint2D> iter = vertices.iterator();

        // move to first point
        GJPoint2D point = iter.next();
        path.moveTo((float) (point.x()), (float) (point.y()));

        // line to each other point
        while (iter.hasNext()) {
            point = iter.next();
            path.lineTo((float) (point.x()), (float) (point.y()));
        }

        return path;
    }


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this == obj)
    		return true;
    	
        if (!(obj instanceof GJPolyline2D))
            return false;
        GJPolyline2D polyline = (GJPolyline2D) obj;

        if (vertices.size() != polyline.vertices.size())
            return false;
        
        for (int i = 0; i < vertices.size(); i++)
            if (!(vertices.get(i)).almostEquals(polyline.vertices.get(i), eps))
                return false;
        return true;
    }

    // ===================================================================
    // Methods inherited from the Object Class

    @Override
    public boolean equals(Object object) {
    	if (this==object)
    		return true;
        if (!(object instanceof GJPolyline2D))
            return false;
        GJPolyline2D polyline = (GJPolyline2D) object;

        if (vertices.size()!=polyline.vertices.size())
            return false;
        for (int i = 0; i<vertices.size(); i++)
            if (!(vertices.get(i)).equals(polyline.vertices.get(i)))
                return false;
        return true;
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJPolyline2D clone() {
        ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>(vertices.size());
        for(GJPoint2D point : vertices)
            array.add(point);
        return new GJPolyline2D(array);
    }

}
