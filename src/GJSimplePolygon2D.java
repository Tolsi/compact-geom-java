/* File SimplePolygon2D.java 
 *
 * Project : Java Geometry Library
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
 */

// package



// Imports
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;










/**
 * Represent a polygonal domain whose boundary is a single closed polyline.
 */
public class GJSimplePolygon2D implements GJPolygon2D {

    // ===================================================================
    // Static constructors
    
    /**
     * Static factory for creating a new GJSimplePolygon2D from a collection of
     * points.
     * @since 0.8.1
     */
    public static GJSimplePolygon2D create(Collection<? extends GJPoint2D> points) {
    	return new GJSimplePolygon2D(points);
    }
    
    /**
     * Static factory for creating a new GJSimplePolygon2D from an array of
     * points.
     * @since 0.8.1
     */
    public static GJSimplePolygon2D create(GJPoint2D... points) {
    	return new GJSimplePolygon2D(points);
    }
    

    // ===================================================================
    // class variables

    /**
     * The inner ordered list of vertices. The last point is connected to the
     * first one.
     */
    protected ArrayList<GJPoint2D> vertices;

    // ===================================================================
    // constructors

    /**
     * Empty constructor: no vertex.
     */
    public GJSimplePolygon2D() {
    	vertices = new ArrayList<GJPoint2D>();
    }

    /**
     * Constructor from an array of points
     * 
     * @param vertices the vertices stored in an array of GJPoint2D
     */
    public GJSimplePolygon2D(GJPoint2D... vertices) {
        this.vertices = new ArrayList<GJPoint2D>(vertices.length);
        for (GJPoint2D vertex : vertices)
            this.vertices.add(vertex);
    }

	/**
	 * Constructor from two arrays, one for each coordinate.
	 * 
	 * @param xcoords
	 *            the x coordinate of each vertex
	 * @param ycoords
	 *            the y coordinate of each vertex
	 */
	public GJSimplePolygon2D(double[] xcoords, double[] ycoords) {
		vertices = new ArrayList<GJPoint2D>(xcoords.length);
		for (int i = 0; i < xcoords.length; i++)
			vertices.add(new GJPoint2D(xcoords[i], ycoords[i]));
	}

	public GJSimplePolygon2D(Collection<? extends GJPoint2D> points) {
		this.vertices = new ArrayList<GJPoint2D>(points.size());
		this.vertices.addAll(points);
	}

	/**
	 * Ensure the polygon has enough memory for storing the required number of
	 * vertices.
	 */
	public GJSimplePolygon2D(int nVertices) {
    	vertices = new ArrayList<GJPoint2D>(nVertices);
    }
	
    /**
     * Creates a simple polygon with the given linear ring representing its
     * boundary.
     * @param ring the boundary of the polygon
     */
    public GJSimplePolygon2D(GJLinearRing2D ring) {
        this.vertices = new ArrayList<GJPoint2D>(ring.vertexNumber());
        this.vertices.addAll(ring.vertices());
    }

    public GJSimplePolygon2D(GJSimplePolygon2D poly) {
    	this.vertices = new ArrayList<GJPoint2D>(poly.vertexNumber());
    	this.vertices.addAll(poly.vertices);
    }
    
    // ===================================================================
    // methods specific to GJSimplePolygon2D

    /**
     * Computes the winding number of the polygon. Algorithm adapted from
     * http://www.geometryalgorithms.com/Archive/algorithm_0103/algorithm_0103.htm
     * 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the number of windings of the curve around the point
     */
    public int getWindingNumber(double x, double y) {
        return GJPolygons2D.windingNumber(vertices, new GJPoint2D(x, y));
    }
        
    /**
     * Returns a simplified version of this polygon, by using Douglas-Peucker
     * algorithm.
     */
	public GJSimplePolygon2D simplify(double distMax) {
		return new GJSimplePolygon2D(GJPolylines2D.simplifyClosedPolyline(this.vertices, distMax));
	}
	
	/**
     * Returns the linear ring that composes the boundary of this polygon.
     * @since 0.9.3
     */
    public GJLinearRing2D getRing() {
    	return new GJLinearRing2D(this.vertices);
    }
    
    // ===================================================================
    // management of vertex list

    /**
     * Adds a point as the last vertex.
     */
    public void addVertex(GJPoint2D point) {
        this.vertices.add(point);
    }

    /**
     * Adds a point as the last vertex.
     * @since 0.9.3
     */
    public void insertVertex(int index, GJPoint2D point) {
        this.vertices.add(index, point);
    }

    /**
	 * Changes the position of the i-th vertex.
	 */
	public void setVertex(int index, GJPoint2D position) {
	    this.vertices.set(index, position);
	}

	/**
     * Removes a vertex of the polygon.
     * 
     * @param point the vertex to be removed.
     */
    public boolean removeVertex(GJPoint2D point) {
        return this.vertices.remove(point);
    }

    /**
     * Removes a vertex of the polygon specified by its index.
     * @since 0.9.3
     */
    public void removeVertex(int index) {
        this.vertices.remove(index);
    }

    /**
     * Removes all the vertices of the polygon.
     */
    public void clearVertices() {
        this.vertices.clear();
    }
    
    /**
     * Computes the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(GJPoint2D point) {
    	double minDist = Double.POSITIVE_INFINITY;
    	int index = -1;
    	
    	for (int i = 0; i < vertices.size(); i++) {
    		double dist = vertices.get(i).distance(point);
    		if (dist < minDist) {
    			index = i;
    			minDist = dist;
    		}
    	}
    	
    	return index;
    }
    
    /**
     * Computes the signed area of the polygon. Algorithm is taken from page: <a
     * href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed are
     * is positive if polygon is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polygon is self-intersecting.
     * 
     * @return the signed area of the polygon.
     */
    public double area() {
    	return GJPolygons2D.computeArea(this);
    }

    /**
     * Computes the centroid (center of mass) of the polygon. Algorithm is taken
     * from page: <a
     * href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>.
     * 
     * @return the centroid of the polygon
     */
    public GJPoint2D centroid() {
    	return GJPolygons2D.computeCentroid(this);
    }

    /**
     * Returns the points of the polygon. The result is a pointer to the inner
     * collection of vertices.
     */
    public Collection<GJPoint2D> vertices() {
        return vertices;
    }

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public GJPoint2D vertex(int i) {
        return vertices.get(i);
    }

    /**
     * Returns the number of vertices of the polygon.
     * 
     * @since 0.6.3
     */
    public int vertexNumber() {
        return vertices.size();
    }

    /**
     * Returns the set of edges, as a collection of GJLineSegment2D.
     */
    public Collection<GJLineSegment2D> edges() {

        int nPoints = this.vertices.size();
        ArrayList<GJLineSegment2D> edges = new ArrayList<GJLineSegment2D>(nPoints);

        if (nPoints==0)
            return edges;

        for (int i = 0; i<nPoints-1; i++)
            edges.add(new GJLineSegment2D(vertices.get(i), vertices.get(i+1)));

        edges.add(new GJLineSegment2D(vertices.get(nPoints-1), vertices.get(0)));

        return edges;
    }

    /**
     * Returns the number of edges. For a simple polygon, this equals the
     * number of vertices.
     */
    public int edgeNumber() {
        return vertices.size();
    }

    
	// ===================================================================
    // methods inherited from GJDomain2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearDomain2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearDomain2D transform(GJCircleInversion2D inv) {
		GJCirculinearBoundary2D boundary =
			this.boundary().transform(inv).reverse();
		return new GJGenericCirculinearDomain2D(boundary);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		// check for multiple vertices
		if (GJPointSets2D.hasMultipleVertices(this.vertices, true)) {
			List<GJPoint2D> pts2 =
				GJPointSets2D.filterMultipleVertices(this.vertices, true);
			GJSimplePolygon2D poly2 = new GJSimplePolygon2D(pts2);
	    	return GJCirculinearDomains2D.computeBuffer(poly2, dist);
		}
		
		// 
		return GJCirculinearDomains2D.computeBuffer(this, dist);
	}

	// ===================================================================
    // methods inherited from GJDomain2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#asPolygon(int)
	 */
	public GJPolygon2D asPolygon(int n) {
		return this;
	}

    /**
     * Returns a set of one GJLinearRing2D, which encloses the polygon.
     */
    public GJCirculinearContourArray2D<GJLinearRing2D> boundary() {
        GJPoint2D[] array = new GJPoint2D[this.vertices.size()];
        for (int i = 0; i<this.vertices.size(); i++)
            array[i] = this.vertices.get(i);

        return new GJCirculinearContourArray2D<GJLinearRing2D>(
        		new GJLinearRing2D(array));
    }

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<GJLinearRing2D> contours() {
        ArrayList<GJLinearRing2D> rings = new ArrayList<GJLinearRing2D>(1);
        rings.add(new GJLinearRing2D(vertices));
        return rings;
	}

    /**
     * Returns the polygon created by reversing the order of the vertices.
     */
    public GJSimplePolygon2D complement() {
        int nPoints = this.vertices.size();

        GJPoint2D[] res = new GJPoint2D[nPoints];

        if (nPoints>0)
            res[0] = this.vertices.get(0);

        for (int i = 1; i<nPoints; i++) {
            res[i] = this.vertices.get(nPoints-i);
        }
        return new GJSimplePolygon2D(res);
    }

    
    // ===================================================================
    // methods inherited from GJShape2D interface

    /**
     * Returns the distance of the point to the polygon. The result is the
     * minimal distance computed for each edge if the polygon, or ZERO if the
     * point lies inside the polygon.
     */
    public double distance(GJPoint2D p) {
        return distance(p.x(), p.y());
    }

    /**
     * Returns the distance of the point to the polygon. The result is the
     * minimal distance computed for each edge if the polygon, or ZERO if the
     * point lies inside the polygon.
     */
    public double distance(double x, double y) {
        double dist = boundary().signedDistance(x, y);
        return Math.max(dist, 0);
    }

    /**
     * Returns the shape formed by the polygon clipped by the given box.
     */
    public GJPolygon2D clip(GJBox2D box) {
        return GJPolygons2D.clipPolygon(this, box);
    }

    /**
     * Returns the bounding box of the polygon.
     */
    public GJBox2D boundingBox() {
        return boundary().boundingBox();
    }

    /**
     * Returns true if polygon is oriented counter-clockwise, false otherwise.
     */
    public boolean isBounded() {
        return this.area()>0;
    }

    public boolean isEmpty() {
        return vertices.size() == 0;
    }

    /**
     * Returns the new Polygon created by an affine transform of this polygon.
     * If the transform is not direct, the order of vertices is reversed.
     */
    public GJSimplePolygon2D transform(GJAffineTransform2D trans) {
        int nPoints = this.vertices.size();

        GJPoint2D[] array = new GJPoint2D[nPoints];
        GJPoint2D[] res = new GJPoint2D[nPoints];

		for (int i = 0; i < nPoints; i++) {
			array[i] = this.vertices.get(i);
            res[i] = new GJPoint2D();
        }
        trans.transform(array, res);

        GJSimplePolygon2D poly = new GJSimplePolygon2D(res);
        if (!trans.isDirect())
            poly = poly.complement();

        return poly;
    }

    // ===================================================================
    // methods inherited from Shape interface

    /**
     * Returns true if the point p lies inside the polygon, with precision
     * given by GJShape2D.ACCURACY.
     */
    public boolean contains(GJPoint2D p) {
        return contains(p.x(), p.y());
    }

    /**
     * Returns true if the point (x, y) lies inside the polygon, with precision
     * given by GJShape2D.ACCURACY.
     */
    public boolean contains(double x, double y) {
    	if (this.boundary().contains(x, y))
    		return true;
    	
    	double area = this.area();
    	int winding = this.getWindingNumber(x, y);
    	if (area > 0) {
    		return winding == 1;
    	} else {
    		return winding == 0;
    	}
    }

    /**
     * Returns a general path iterator.
     */
    public java.awt.geom.GeneralPath getGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
		if (vertices.size() < 2)
			return path;

        // move to first point
        GJPoint2D point = vertices.get(0);
        path.moveTo((float) (point.x()), (float) (point.y()));

        // line to each point
        for (int i = 0; i<vertices.size(); i++) {
            point = vertices.get(i);
            path.lineTo((float) (point.x()), (float) (point.y()));
        }

        // close polygon
        point = vertices.get(0);
        path.lineTo((float) (point.x()), (float) (point.y()));
        path.closePath();

        return path;
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.getGeneralPath());
    }

    public void fill(Graphics2D g) {
        g.fill(this.getGeneralPath());
    }


	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this == obj)
    		return true;
    	
        if (!(obj instanceof GJSimplePolygon2D))
            return false;
        GJSimplePolygon2D polygon = (GJSimplePolygon2D) obj;

        int nv = this.vertexNumber();
        if (polygon.vertexNumber() != nv)
            return false;

        for (int i = 0; i < nv ; i++) {
        	if (!this.vertex(i).almostEquals(polygon.vertex(i), eps))
        		return false;
        }

        return true;
    }

    // ===================================================================
    // methods inherited from Object interface

    /**
     * Tests if the two polygons are equal. Test first the number of vertices,
     * then the bounding boxes, then if each vertex of the polygon is contained
     * in the vertices array of this polygon.
     */
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
        if (!(obj instanceof GJSimplePolygon2D))
            return false;

        GJSimplePolygon2D polygon = (GJSimplePolygon2D) obj;

        int nv = this.vertexNumber();
        if (polygon.vertexNumber() != nv)
            return false;

        for (int i = 0; i < nv ; i++) {
        	if (!this.vertex(i).equals(polygon.vertex(i)))
        		return false;
        }

        return true;
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJSimplePolygon2D clone() {
        ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>(vertices.size());
        for(GJPoint2D point : vertices)
            array.add(point);
        return new GJSimplePolygon2D(array);
    }

}
