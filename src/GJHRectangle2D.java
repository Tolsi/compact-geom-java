/* File HRectangle2D.java 
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



import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;












// Imports

/**
 * GJHRectangle2D defines a rectangle with edges parallel to main axis. Thus, it
 * can not be rotated, contrary to GJRectangle2D. This class is actually simply a
 * wrapper of class <code>java.awt.geom.GJRectangle2D.Double</code> with
 * interface <code>AbstractPolygon</code>.
 * @deprecated since 0.11.0
 */
@Deprecated
public class GJHRectangle2D extends java.awt.geom.Rectangle2D.Double implements
        GJPolygon2D {

    // ===================================================================
    // constants

    private static final long serialVersionUID = 1L;

    // ===================================================================
    // class variables

    // ===================================================================
    // constructors

    /** Main constructor */
    public GJHRectangle2D(double x0, double y0, double w, double h) {
        super(x0, y0, w, h);
    }

    /** Empty constructor (size and position zero) */
    public GJHRectangle2D() {
        super(0, 0, 0, 0);
    }

    /** Constructor from awt, to allow easy construction from existing apps. */
    public GJHRectangle2D(java.awt.geom.Rectangle2D rect) {
        super(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    /** Main constructor */
    public GJHRectangle2D(GJPoint2D point, double w, double h) {
        super(point.getX(), point.getY(), w, h);
    }

    // ===================================================================
    // methods inherited from interface GJPolygon2D


    public Collection<GJPoint2D> vertices() {
        ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>(4);
        points.add(new GJPoint2D(x, y));
        points.add(new GJPoint2D(x+width, y));
        points.add(new GJPoint2D(x+width, y+height));
        points.add(new GJPoint2D(x, y+height));
        return points;
    }

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and 3
     */
    public GJPoint2D vertex(int i) {
        switch (i) {
        case 0:
            return new GJPoint2D(x, y);
        case 1:
            return new GJPoint2D(x+width, y);
        case 2:
            return new GJPoint2D(x+width, y+height);
        case 3:
            return new GJPoint2D(x, y+height);
        default:
            throw new IndexOutOfBoundsException();
        }
    }

	public void setVertex(int i, GJPoint2D point) {
		throw new UnsupportedOperationException("Vertices of HRectangle objects can not be modified");
	}

	public void addVertex(GJPoint2D point) {
		throw new UnsupportedOperationException("Vertices of HRectangle objects can not be modified");
	}

	public void insertVertex(int i, GJPoint2D point) {
		throw new UnsupportedOperationException("Vertices of HRectangle objects can not be modified");
	}

	public void removeVertex(int i) {
		throw new UnsupportedOperationException("Vertices of HRectangle objects can not be modified");
	}

    /**
     * Returns the number of vertex, which is 4.
     * 
     * @since 0.6.3
     */
    public int vertexNumber() {
        return 4;
    }

    /**
     * Computes the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(GJPoint2D point) {
    	double minDist = java.lang.Double.POSITIVE_INFINITY;
    	int index = -1;
    	
    	int i = 0;
    	for (GJPoint2D vertex : this.vertices()) {
    		double dist = vertex.distance(point);
    		if (dist < minDist) {
    			index = i;
    			minDist = dist;
    		}
    		i++;
    	}
    	
    	return index;
    }
    
    public Collection<GJLineSegment2D> edges() {
        ArrayList<GJLineSegment2D> edges = new ArrayList<GJLineSegment2D>(4);
        edges.add(new GJLineSegment2D(x, y, x+width, y));
        edges.add(new GJLineSegment2D(x+width, y, x+width, y+height));
        edges.add(new GJLineSegment2D(x+width, y+height, x, y+height));
        edges.add(new GJLineSegment2D(x, y+height, x, y));
        return edges;
    }

    public int edgeNumber() {
        return 4;
    }

    /**
     * Computes the signed area of the polygon. 
     * @return the signed area of the polygon.
     * @since 0.9.1
     */
    public double area() {
    	return GJPolygons2D.computeArea(this);
    }

    /**
     * Computes the centroid (center of mass) of the polygon. 
     * @return the centroid of the polygon
     * @since 0.9.1
     */
    public GJPoint2D centroid() {
    	return GJPolygons2D.computeCentroid(this);
    }
    
    
	// ===================================================================
    // methods implementing the GJCirculinearShape2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearDomain2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearDomain2D transform(GJCircleInversion2D inv) {
		return new GJGenericCirculinearDomain2D(
				this.boundary().transform(inv));
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#getBuffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.computeBuffer(this.boundary(), dist);
	}

    // ===================================================================
    // methods inherited from interface GJDomain2D

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#getAsPolygon(int)
	 */
	public GJPolygon2D asPolygon(int n) {
		return this;
	}

    public GJCirculinearContourArray2D<GJLinearRing2D> boundary() {
        return new GJCirculinearContourArray2D<GJLinearRing2D>(this.asRing());
    }

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<GJLinearRing2D> contours() {
       ArrayList<GJLinearRing2D> rings = new ArrayList<GJLinearRing2D>(1);
       rings.add(this.asRing());
       return rings;
	}

	private GJLinearRing2D asRing() {
        GJPoint2D pts[] = new GJPoint2D[4];
        pts[0] = new GJPoint2D(x, y);
        pts[1] = new GJPoint2D(width+x, y);
        pts[2] = new GJPoint2D(width+x, y+height);
        pts[3] = new GJPoint2D(x, y+height);
		
        return new GJLinearRing2D(pts);
	}
	
    public GJPolygon2D complement() {
        GJPoint2D pts[] = new GJPoint2D[4];
        pts[0] = new GJPoint2D(x, y);
        pts[1] = new GJPoint2D(x, y+height);
        pts[2] = new GJPoint2D(width+x, y+height);
        pts[3] = new GJPoint2D(width+x, y);
        return new GJSimplePolygon2D(pts);
    }

    // ===================================================================
    // methods overriding the GJShape2D interface

    /**
     * Returns the distance of the point to the polygon. The result is the
     * minimal distance computed for each edge if the polygon, or ZERO if the
     * point lies inside the polygon.
     */
    public double distance(GJPoint2D p) {
        return distance(p.getX(), p.getY());
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
     * Returns the clipping of the rectangle, as an instance of GJHRectangle2D. If
     * rectangle is outside clipping box, returns an instance of HRectangle with
     * 0 width and height.
     */
    public GJHRectangle2D clip(GJBox2D box) {
        double xmin = Math.max(this.getMinX(), box.getMinX());
        double xmax = Math.min(this.getMaxX(), box.getMaxX());
        double ymin = Math.max(this.getMinY(), box.getMinY());
        double ymax = Math.min(this.getMaxY(), box.getMaxY());
        if (xmin>xmax||ymin>ymax)
            return new GJHRectangle2D(xmin, ymin, 0, 0);
        else
            return new GJHRectangle2D(xmin, xmax, xmax-xmin, ymax-ymin);
    }

    /** Always returns true, because a rectangle is always bounded. */
    public boolean isBounded() {
        return true;
    }

    public GJBox2D boundingBox() {
        return new GJBox2D(this.getMinX(), this.getMaxX(), this.getMinY(), this
                .getMaxY());
    }

    /**
     * Return the new Polygon created by an affine transform of this polygon.
     */
    public GJSimplePolygon2D transform(GJAffineTransform2D trans) {
        int nPoints = 4;
        GJPoint2D[] array = new GJPoint2D[nPoints];
        GJPoint2D[] res = new GJPoint2D[nPoints];
        Iterator<GJPoint2D> iter = this.vertices().iterator();
        for (int i = 0; i<nPoints; i++) {
            array[i] = iter.next();
            res[i] = new GJPoint2D();
        }

        trans.transform(array, res);
        return new GJSimplePolygon2D(res);
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.boundary().getGeneralPath());
    }

    public void fill(Graphics2D g2) {
        g2.fill(this.boundary().getGeneralPath());
    }



	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        // check class, and cast type
        if (!(obj instanceof GJHRectangle2D))
            return false;
        GJHRectangle2D rect = (GJHRectangle2D) obj;

        // check all 4 corners of the first rectangle
        boolean ok;
        for (GJPoint2D point : this.vertices()) {
            ok = false;

            // compare with all 4 corners of second rectangle
            for (GJPoint2D point2 : rect.vertices())
                if (point.almostEquals(point2, eps))
                    ok = true;

            // if the point does not belong to the corners of the other
            // rectangle,
            // then the two rectangles are different
            if (!ok)
                return false;
        }

        // test ok for 4 corners, then the two rectangles are the same.
        return true;
    }

    // ===================================================================
    // general methods

    /**
     * Test if rectangles are the same. We consider two rectangles are equal if
     * their corners are the same. Then, we can have different origins and
     * different angles, but equal rectangles.
     */
    @Override
    public boolean equals(Object obj) {

        // check class, and cast type
        if (!(obj instanceof GJHRectangle2D))
            return false;
        GJHRectangle2D rect = (GJHRectangle2D) obj;

        // check all 4 corners of the first rectangle
        boolean ok;
        for (GJPoint2D point : this.vertices()) {
            ok = false;

            // compare with all 4 corners of second rectangle
            for (GJPoint2D point2 : rect.vertices())
                if (point.equals(point2))
                    ok = true;

            // if the point does not belong to the corners of the other
            // rectangle,
            // then the two rectangles are different
            if (!ok)
                return false;
        }

        // test ok for 4 corners, then the two rectangles are the same.
        return true;
    }

	public boolean contains(GJPoint2D p) {
		return this.contains(p.getX(), p.getY());
	}

}
