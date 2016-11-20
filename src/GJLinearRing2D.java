import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * A GJLinearRing2D is a GJPolyline2D whose last point is connected to the first one.
 * This is typically the boundary of a GJSimplePolygon2D.
 * </p>
 * <p>
 * The name 'GJLinearRing2D' was used for 2 reasons:
 * <ul><li>it is short</li> <li>it is consistent with the JTS name</li></ul>
 * </p>
 * @author dlegland
 */
public class GJLinearRing2D extends GJLinearCurve2D implements GJCirculinearRing2D {

    // ===================================================================
    // Static methods
    
    /**
     * Static factory for creating a new GJLinearRing2D from a collection of
     * points.
     * @since 0.8.1
     */
    public static GJLinearRing2D create(Collection<? extends GJPoint2D> points) {
    	return new GJLinearRing2D(points);
    }
    
    /**
     * Static factory for creating a new GJLinearRing2D from an array of
     * points.
     * @since 0.8.1
     */
    public static GJLinearRing2D create(GJPoint2D... vertices) {
    	return new GJLinearRing2D(vertices);
    }
    

    // ===================================================================
    // Constructors
    
	public GJLinearRing2D() {
        super();
    }

	public GJLinearRing2D(int n) {
        super(n);
    }

	public GJLinearRing2D(GJPoint2D... vertices) {
        super(vertices);
    }

    public GJLinearRing2D(double[] xcoords, double[] ycoords) {
        super(xcoords, ycoords);
    }

    public GJLinearRing2D(Collection<? extends GJPoint2D> points) {
        super(points);
    }

    public GJLinearRing2D(GJLinearCurve2D lineString) {
    	super(lineString.vertices);
    }
    
    // ===================================================================
    // Methods specific to ClosedPolyline2D

    /**
     * Computes the signed area of the linear ring. Algorithm is taken from page:
     * <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed are
     * is positive if polyline is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polyline is self-intersecting.
     * 
     * @return the signed area of the polyline.
     */
	public double area() {
		// start from edge joining last and first vertices
		GJPoint2D prev = this.vertices.get(this.vertices.size() - 1);

		// Iterate over all couples of adjacent vertices
		double area = 0;
		for (GJPoint2D point : this.vertices) {
			// add area of elementary parallelogram
			area += prev.x() * point.y() - prev.y() * point.x();
			prev = point;
		}
		
		// divides by 2 to consider only elementary triangles
		return area /= 2;
	}

    // ===================================================================
    // Methods specific to GJLinearCurve2D

	/**
	 * Returns a simplified version of this linear ring, by using
	 * Douglas-Peucker algorithm.
	 */
	public GJLinearRing2D simplify(double distMax) {
		return new GJLinearRing2D(GJPolylines2D.simplifyClosedPolyline(this.vertices, distMax));
	}

	/**
     * Returns an array of GJLineSegment2D. The number of edges is the same as
     * the number of vertices.
     * 
     * @return the edges of the polyline
     */
    @Override
	public Collection<GJLineSegment2D> edges() {
		// createFromCollection resulting array
		int n = vertices.size();
		ArrayList<GJLineSegment2D> edges = new ArrayList<GJLineSegment2D>(n);

		// do not process empty polylines
		if (n < 2)
			return edges;

		// createFromCollection one edge for each couple of vertices
		for (int i = 0; i < n - 1; i++)
			edges.add(new GJLineSegment2D(vertices.get(i), vertices.get(i + 1)));

		// add a supplementary edge at the end, but only if vertices differ
		GJPoint2D p0 = vertices.get(0);
		GJPoint2D pn = vertices.get(n - 1);
		
		// TODO: should not make the test...
		if (pn.distance(p0) > GJShape2D.ACCURACY)
			edges.add(new GJLineSegment2D(pn, p0));

		// return resulting array
		return edges;
	}

    public int edgeNumber() {
    	int n = vertices.size(); 
    	if (n > 1) 
    		return n;
    	return 0;
    }
    
    public GJLineSegment2D edge(int index) {
    	int i2 = (index + 1) % vertices.size();
    	return new GJLineSegment2D(vertices.get(index), vertices.get(i2));
    }

    /**
     * Returns the last edge of this linear ring. The last edge connects the
     * last vertex with the first one.
     */
    public GJLineSegment2D lastEdge() {
		int n = vertices.size();
		if (n < 2)
			return null;
		return new GJLineSegment2D(vertices.get(n-1), vertices.get(0));
    }

	// ===================================================================
    // Methods inherited from GJCirculinearCurve2D

    public GJCirculinearRing2D parallel(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return GJGenericCirculinearRing2D.createGenericCirculinearRing2DFromCollection(
    			bc.createContinuousParallel(this, dist).smoothPieces());
    }
    
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearRing2D transform(GJCircleInversion2D inv) {
		
		// Create array for storing transformed arcs
		Collection<GJLineSegment2D> edges = this.edges();
		ArrayList<GJCirculinearElement2D> arcs =
			new ArrayList<GJCirculinearElement2D>(edges.size());
		
		// Transform each arc
		for(GJLineSegment2D edge : edges) {
			arcs.add(edge.transform(inv));
		}
		
		// createFromCollection the transformed shape
		return new GJGenericCirculinearRing2D(arcs);
	}

	// ===================================================================
    // Methods inherited from GJBoundary2D

    public GJCirculinearDomain2D domain() {
        return new GJGenericCirculinearDomain2D(this);
    }

    public void fill(Graphics2D g2) {
        g2.fill(this.asGeneralPath());
    }

    // ===================================================================
    // Methods inherited from interface GJOrientedCurve2D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#windingAngle(GJPoint2D)
     */
    public double windingAngle(GJPoint2D point) {
        int wn = GJPolygons2D.windingNumber(this.vertices, point);
        return wn * 2 * Math.PI;
    }

    public boolean isInside(double x, double y) {
        return this.isInside(new GJPoint2D(x, y));
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#isInside(GJPoint2D)
     */
    public boolean isInside(GJPoint2D point) {
        // TODO: choose convention for points on the boundary
    	if (this.contains(point))
    		return true;
    	
    	double area = this.area();
    	int winding = GJPolygons2D.windingNumber(this.vertices, point);
    	if (area > 0) {
    		return winding == 1;
    	} else {
    		return winding == 0;
    	}

    }

    // ===================================================================
    // Methods inherited from interface GJContinuousCurve2D

    /**
     * Returns true, by definition of linear ring.
     */
    public boolean isClosed() {
        return true;
    }

    // ===================================================================
    // Methods inherited from interface GJCurve2D

    /**
     * Returns point from position as double. Position t can be from 0 to n,
     * with n equal to the number of vertices of the linear ring.
     */
    public GJPoint2D point(double t) {
		// format position to stay between limits
		double t0 = this.t0();
		double t1 = this.t1();
		t = Math.max(Math.min(t, t1), t0);

		int n = vertices.size();

		// index of vertex before point
		int ind0 = (int) Math.floor(t + GJShape2D.ACCURACY);
		double tl = t - ind0;

		if (ind0 == n)
			ind0 = 0;
		GJPoint2D p0 = vertices.get(ind0);

		// check if equal to a vertex
		if (Math.abs(t - ind0) < GJShape2D.ACCURACY)
			return p0;

		// index of vertex after point
		int ind1 = ind0 + 1;
		if (ind1 == n)
			ind1 = 0;
		GJPoint2D p1 = vertices.get(ind1);

		// position on line;
		double x0 = p0.x();
		double y0 = p0.y();
		double dx = p1.x() - x0;
		double dy = p1.y() - y0;

		return new GJPoint2D(x0 + tl * dx, y0 + tl *dy);
    }

    /**
     * Returns the number of points in the linear ring.
     */
    public double t1() {
        return vertices.size();
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return this.t1();
    }

    /**
     * Returns the first point, as this is the same as the last point.
     */
    @Override
	public GJPoint2D lastPoint() {
		if (vertices.size() == 0)
			return null;
        return vertices.get(0);
    }

	@Override
    public Collection<? extends GJLinearRing2D> continuousCurves() {
    	return wrapCurve(this);
    }

    /**
     * Returns the linear ring with same points taken in reverse order. The
     * first points is still the same. Points of reverse curve are the same as
     * the original curve (same references).
     */
    public GJLinearRing2D reverse() {
		GJPoint2D[] points2 = new GJPoint2D[vertices.size()];
		int n = vertices.size();
		if (n > 0)
			points2[0] = vertices.get(0);
		
		for (int i = 1; i < n; i++)
			points2[i] = vertices.get(n - i);

		return new GJLinearRing2D(points2);
    }

    /**
     * Return an instance of GJPolyline2D. If t1 is lower than t0, the returned
     * Polyline contains the origin of the curve.
     */
    public GJPolyline2D subCurve(double t0, double t1) {
        // code adapted from GJCurveSet2D

        GJPolyline2D res = new GJPolyline2D();

        // number of points in the polyline
        int indMax = this.vertexNumber();

        // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), indMax);
        t1 = Math.min(Math.max(t1, 0), indMax);

		// find curves index
		int ind0 = (int) Math.floor(t0 + GJShape2D.ACCURACY);
		int ind1 = (int) Math.floor(t1 + GJShape2D.ACCURACY);

		// need to subdivide only one line segment
		if (ind0 == ind1 && t0 < t1) {
			// extract limit points
			res.addVertex(this.point(t0));
			res.addVertex(this.point(t1));
			// return result
			return res;
        }

		// add the point corresponding to t0
		res.addVertex(this.point(t0));

		if (ind1 > ind0) {
			// add all the whole points between the 2 cuts
			for (int n = ind0 + 1; n <= ind1; n++)
				res.addVertex(vertices.get(n));
		} else {
			// add all points until the end of the set
			for (int n = ind0 + 1; n < indMax; n++)
				res.addVertex(vertices.get(n));

			// add all points from the beginning of the set
			for (int n = 0; n <= ind1; n++)
				res.addVertex(vertices.get(n));
		}

		// add the last point
        res.addVertex(this.point(t1));

        // return the curve set
        return res;
    }

    // ===================================================================
    // Methods inherited from interface GJShape2D

    /**
     * Returns the transformed shape, as a LinerRing2D.
     */
    public GJLinearRing2D transform(GJAffineTransform2D trans) {
		GJPoint2D[] pts = new GJPoint2D[vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
			pts[i] = trans.transform(vertices.get(i));
		return new GJLinearRing2D(pts);
	}

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJContinuousCurve2D#appendPath(java.awt.geom.GeneralPath)
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {

        if (vertices.size()<2)
            return path;

        // move to last first point of the curve (then a line will be drawn to
        // the first point)
        GJPoint2D p0 = this.lastPoint();
        path.moveTo((float) p0.x(), (float) p0.y());
        
        // process each point
        for(GJPoint2D point : vertices)
            path.lineTo((float) point.x(), (float) point.y());
        
        // close the path, even if the path is already at the right position
        path.closePath();
        
        return path;
    }


    // ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if (!(obj instanceof GJLinearRing2D))
            return false;
        GJLinearRing2D ring = (GJLinearRing2D) obj;

        if (vertices.size() != ring.vertices.size())
            return false;
        
        for (int i = 0; i < vertices.size(); i++)
            if (!(vertices.get(i)).almostEquals(ring.vertices.get(i), eps))
                return false;
        return true;
    }

    @Override
	public boolean equals(Object object) {
		if (!(object instanceof GJLinearRing2D))
			return false;
		GJLinearRing2D ring = (GJLinearRing2D) object;

		if (vertices.size() != ring.vertices.size())
			return false;
		for (int i = 0; i < vertices.size(); i++)
			if (!(vertices.get(i)).equals(ring.vertices.get(i)))
				return false;
		return true;
    }
    
    
    // ===================================================================
	// methods implementing the Object interface

	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
	public GJLinearRing2D clone() {
		ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>(vertices.size());
		for (GJPoint2D point : vertices)
			array.add(point);
		return new GJLinearRing2D(array);
    }
}
