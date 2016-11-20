/* File Rectangle2D.java 
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
import java.util.Iterator;













/**
 * GJRectangle2D defines a rectangle rotated around its first corner.
 */
public class GJRectangle2D implements GJPolygon2D {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    protected double x0;
    protected double y0;
    protected double w;
    protected double h;
    

    // ===================================================================
    // constructors

    /**
	 * Main constructor, specifying position of reference corner and rectangle
	 * dimensions.  
	 */
    public GJRectangle2D(double x0, double y0, double w, double h) {
        this.x0 = x0;
        this.y0 = y0;
        this.w = w;
        this.h = h;
    }

    /** 
     * Empty constructor (size and position zero) 
     */
    public GJRectangle2D() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructor from awt, to allow easy construction from existing apps. 
     */
    public GJRectangle2D(java.awt.geom.Rectangle2D rect) {
        this.x0 = rect.getX();
        this.y0 = rect.getY();
        this.w = rect.getWidth();
        this.h = rect.getHeight();
    }


    /** 
     * Creates a rectangle from two corner points. Origin and dimensions are
     * automatically determined.
     */
    public GJRectangle2D(GJPoint2D p1, GJPoint2D p2) {
    	this.x0 = Math.min(p1.x(), p2.x());
    	this.y0 = Math.min(p1.y(), p2.y());
    	this.w = Math.max(p1.x(), p2.x()) - this.x0;
    	this.h = Math.max(p1.y(), p2.y()) - this.y0;
    }

    // ===================================================================
    // accessors

    public double getX() {
        return x0;
    }

    public double getY() {
        return y0;
    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

        
    // ===================================================================
	// methods inherited from interface GJPolygon2D
	
	/**
	 * Returns the vertices of the rectangle as a collection of points.
	 * 
	 * @return the vertices of the rectangle.
	 */
	public Collection<GJPoint2D> vertices() {
		// Allocate memory
	    ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>(4);
	    
	    // add each vertex
		array.add(new GJPoint2D(x0, y0));
		array.add(new GJPoint2D(x0 + w, y0));
		array.add(new GJPoint2D(x0 + w, y0 + h));
		array.add(new GJPoint2D(x0, y0 + h));
	
		// return result array
	    return array;
	}

	/**
	 * Returns the number of vertices of the rectangle, which is 4.
	 * 
	 * @since 0.6.3
	 */
	public int vertexNumber() {
	    return 4;
	}

	/**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and 3
     */
    public GJPoint2D vertex(int i) {
        switch (i) {
        case 0:
            return new GJPoint2D(x0, y0);
        case 1:
            return new GJPoint2D(x0+w, y0);
        case 2:
            return new GJPoint2D(x0+w, y0+h);
        case 3:
            return new GJPoint2D(x0, y0+h);
        default:
            throw new IndexOutOfBoundsException();
        }
    }

	public void setVertex(int i, GJPoint2D point) {
		throw new UnsupportedOperationException("Vertices of Rectangle objects can not be modified");
	}

	public void addVertex(GJPoint2D point) {
		throw new UnsupportedOperationException("Vertices of Rectangle objects can not be modified");
	}

	public void insertVertex(int i, GJPoint2D point) {
		throw new UnsupportedOperationException("Vertices of Rectangle objects can not be modified");
	}

	public void removeVertex(int i) {
		throw new UnsupportedOperationException("Vertices of Rectangle objects can not be modified");
	}

    /**
     * Computes the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(GJPoint2D point) {
    	double minDist = Double.POSITIVE_INFINITY;
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
    
    /**
     * Returns the four edges that constitute the boundary of this rectangle.
     */
    public Collection<GJLineSegment2D> edges() {
        ArrayList<GJLineSegment2D> edges = new ArrayList<GJLineSegment2D>(4);
		edges.add(new GJLineSegment2D(x0, y0, x0 + w, y0));
		edges.add(new GJLineSegment2D(x0 + w, y0, x0 + w, y0 + h));
		edges.add(new GJLineSegment2D(x0 + w, y0 + h, x0, y0 + h));
		edges.add(new GJLineSegment2D(x0, y0 + h, x0, y0));
        return edges;
    }

    /**
     * Returns 4, as a rectangle has four edges.
     */
    public int edgeNumber() {
        return 4;
    }

    /**
     * Computes the area of this rectangle, given by the product of width by
     * height. 
     * @return the signed area of the polygon.
     * @since 0.9.1
     */
    public double area() {
    	return this.w * this.h;
    }

    /**
     * Computes the centroid (center of mass) of this rectangle.  
     * @return the centroid of the polygon
     * @since 0.9.1
     */
    public GJPoint2D centroid() {
    	double xc = x0 + this.w  / 2;
    	double yc = y0 + this.h  / 2;
    	return new GJPoint2D(xc, yc);
    }
    
    // ===================================================================
    // methods inherited from GJDomain2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#asPolygon(int)
	 */
	public GJPolygon2D asPolygon(int n) {
		return this;
	}

	// ===================================================================
	// methods inherited from interface GJCirculinearShape2D
	
	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearDomain2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearDomain2D transform(GJCircleInversion2D inv) {
		return new GJGenericCirculinearDomain2D(
				this.boundary().transform(inv));
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.computeBuffer(this.boundary(), dist);
	}

	
    // ===================================================================
    // methods inherited from interface GJDomain2D

   public GJCirculinearContourArray2D<GJLinearRing2D> boundary() {
        return new GJCirculinearContourArray2D<GJLinearRing2D>(asRing());
    }

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<GJLinearRing2D> contours() {
       ArrayList<GJLinearRing2D> rings = new ArrayList<GJLinearRing2D>(1);
       rings.add(this.asRing());
       return rings;
	}

	/**
	 * Returns the ring that constitute the boundary of this rectangle.
	 * @return
	 */
	private GJLinearRing2D asRing() {
        GJPoint2D pts[] = new GJPoint2D[4];
		pts[0] = new GJPoint2D(x0, y0);
		pts[1] = new GJPoint2D(x0 + w, y0);
		pts[2] = new GJPoint2D(x0 + w, y0 + h);
		pts[3] = new GJPoint2D(x0, y0 + h);
		
        return new GJLinearRing2D(pts);
	}

	/**
	 * Returns a new simple Polygon whose vertices are in reverse order of
	 * this rectangle.
	 */
	public GJPolygon2D complement() {
        GJPoint2D pts[] = new GJPoint2D[4];
		pts[0] = new GJPoint2D(x0, y0);
		pts[1] = new GJPoint2D(x0, y0 + h);
		pts[2] = new GJPoint2D(x0 + w, y0 + h);
		pts[3] = new GJPoint2D(x0 + w, y0);

        return new GJSimplePolygon2D(pts);
    }

    // ===================================================================
    // methods inherited from GJShape2D interface

    /** 
     * Always returns true, because a rectangle is always bounded. 
     */
    public boolean isBounded() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

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
     * Returns the clipped polygon.
     */
    public GJPolygon2D clip(GJBox2D box) {
    	return GJPolygons2D.clipPolygon(this, box);
    }

    /**
     * Returns the bounding box of the rectangle.
     */
    public GJBox2D boundingBox() {
		return new GJBox2D(x0, x0 + w, y0, y0 + h);
    }

    /**
     * Returns the new Polygon created by an affine transform of this polygon.
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

    // ===================================================================
    // methods inherited from Shape interface

    /**
     * Checks if this rectangle contains the given point.
     */
    public boolean contains(GJPoint2D point) {
        return contains(point.x(), point.y());
    }

    /**
     * Checks if this rectangle contains the point given by (x,y)
     */
    public boolean contains(double x, double y) {
        if (x < this.x0)
        	return false;
        if (x > this.x0 + this.w)
        	return false;
        if (y < this.y0)
        	return false;
        if (y > this.y0 + this.h)
        	return false;
        return true;
    }

    public void draw(Graphics2D g2) {
    	this.asRing().draw(g2);
    }

    public void fill(Graphics2D g2) {
    	this.asRing().fill(g2);
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
        if (!(obj instanceof GJRectangle2D))
            return false;
        GJRectangle2D rect = (GJRectangle2D) obj;

        // check all 4 corners of the first rectangle
        boolean ok;
        for (GJPoint2D point : this.vertices()) {
            ok = false;

            // compare with all 4 corners of second rectangle
            for (GJPoint2D point2 : rect.vertices())
                if (point.almostEquals(point2, eps)) {
                    ok = true;
                    break;
                }

            // if the point does not belong to the corners of the other
            // rectangle, then the two rect are different
            if (!ok)
                return false;
        }

        // test ok for 4 corners, then the two rectangles are the same.
        return true;
    }

    // ===================================================================
    // methods inherited from Object interface

    /**
     * Tests if rectangles are the same.
     */
    @Override
    public boolean equals(Object obj) {
       	if (this == obj)
    		return true;

       	// check class, and cast type
        if (!(obj instanceof GJRectangle2D))
            return false;
        GJRectangle2D that = (GJRectangle2D) obj;

        // Compare each field
		if (!GJEqualUtils.areEqual(this.x0, that.x0))
			return false;
		if (!GJEqualUtils.areEqual(this.y0, that.y0))
			return false;
		if (!GJEqualUtils.areEqual(this.w, that.w))
			return false;
		if (!GJEqualUtils.areEqual(this.h, that.h))
			return false;

        return true;
    }

}
