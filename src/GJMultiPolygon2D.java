


import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;













/**
 * A polygonal domain whose boundary is composed of several disjoint continuous
 * GJLinearRing2D.
 * 
 * @author dlegland
 */
public class GJMultiPolygon2D implements GJDomain2D, GJPolygon2D {

    // ===================================================================
    // Static constructors
	
	public static GJMultiPolygon2D create(Collection<GJLinearRing2D> rings) {
		return new GJMultiPolygon2D(rings);
	}

	public static GJMultiPolygon2D create(GJLinearRing2D... rings) {
		return new GJMultiPolygon2D(rings);
	}

	
    // ===================================================================
    // class members

    ArrayList<GJLinearRing2D> rings = new ArrayList<GJLinearRing2D>(1);

    
    // ===================================================================
    // Constructors

    public GJMultiPolygon2D() {
    }

    /**
     * Ensures the inner buffer has enough capacity for storing the required
     * number of rings.
     */
    public GJMultiPolygon2D(int nRings) {
    	this.rings.ensureCapacity(nRings);
    }

    public GJMultiPolygon2D(GJLinearRing2D... rings) {
        for (GJLinearRing2D ring : rings)
            this.rings.add(ring);
    }

    public GJMultiPolygon2D(GJPolygon2D polygon) {
    	if (polygon instanceof GJSimplePolygon2D) {
    		rings.add(((GJSimplePolygon2D) polygon).getRing());
    	} else {
    		rings.addAll(polygon.boundary().curves());
    	}
    }

    public GJMultiPolygon2D(Collection<GJLinearRing2D> lines) {
        rings.addAll(lines);
    }

    // ===================================================================
    // Management of rings

    public void addRing(GJLinearRing2D ring) {
        rings.add(ring);
    }

    public void insertRing(int index, GJLinearRing2D ring) {
    	rings.add(index, ring);
    }
    
    public void removeRing(GJLinearRing2D ring) {
        rings.remove(ring);
    }

    public void clearRings() {
    	rings.clear();
    }
    
    public GJLinearRing2D getRing(int index) {
        return rings.get(index);
    }

    public void setRing(int index, GJLinearRing2D ring) {
        rings.set(index, ring);
    }

    public int ringNumber() {
        return rings.size();
    }

    
    // ===================================================================
    // methods implementing the GJPolygon2D interface

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

    public Collection<GJLineSegment2D> edges() {
    	int nEdges = edgeNumber();
        ArrayList<GJLineSegment2D> edges = new ArrayList<GJLineSegment2D>(nEdges);
        for (GJLinearRing2D ring : rings)
            edges.addAll(ring.edges());
        return edges;
    }

    public int edgeNumber() {
        int count = 0;
        for (GJLinearRing2D ring : rings)
            count += ring.vertexNumber();
        return count;
    }

    public Collection<GJPoint2D> vertices() {
    	int nv = vertexNumber();
        ArrayList<GJPoint2D> points = new ArrayList<GJPoint2D>(nv);
        for (GJLinearRing2D ring : rings)
            points.addAll(ring.vertices());
        return points;
    }

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices minus one
     */
    public GJPoint2D vertex(int i) {
        int count = 0;
        GJLinearRing2D boundary = null;

        for (GJLinearRing2D ring : rings) {
            int nv = ring.vertexNumber();
            if (count + nv > i) {
                boundary = ring;
                break;
            }
            count += nv;
        }

        if (boundary == null)
            throw new IndexOutOfBoundsException();

        return boundary.vertex(i-count);
    }

    /**
     * Sets the position of the i-th vertex of this polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public void setVertex(int i, GJPoint2D point) {
        int count = 0;
        GJLinearRing2D boundary = null;

        for (GJLinearRing2D ring : rings) {
            int nv = ring.vertexNumber();
            if (count + nv > i) {
                boundary = ring;
                break;
            }
            count += nv;
        }

        if (boundary == null)
            throw new IndexOutOfBoundsException();

        boundary.setVertex(i-count, point);
    }

	/**
	 * Adds a vertex at the end of the last ring of this polygon.
	 * 
	 * @throws RuntimeException
	 *             if this MultiPolygon does not contain any ring
	 */
    public void addVertex(GJPoint2D position) {
    	// get the last ring
    	if (rings.size() == 0) {
    		throw new RuntimeException("Can not add a vertex to a multipolygon with no ring");
    	}
		GJLinearRing2D ring = rings.get(rings.size() - 1);
		ring.addVertex(position);
    }
    
    /**
     * Inserts a vertex at the given position
     * 
     * @throws RuntimeException if this polygon has no ring
     * @throws IllegalArgumentException if index is not smaller than vertex number
     */
    public void insertVertex(int index, GJPoint2D point) {
    	// check number of rings
    	if (rings.size() == 0) {
    		throw new RuntimeException("Can not add a vertex to a multipolygon with no ring");
    	}
    	
    	// Check number of vertices
    	int nv = this.vertexNumber();
    	if (nv <= index) {
    		throw new IllegalArgumentException("Can not insert vertex at position " +
    				index + " (max is " + nv + ")");
    	}
    	
    	// Find the ring that correspond to index
        int count = 0;
        GJLinearRing2D boundary = null;

        for (GJLinearRing2D ring : rings) {
            nv = ring.vertexNumber();
            if (count + nv > index) {
                boundary = ring;
                break;
            }
            count += nv;
        }

        if (boundary == null)
            throw new IndexOutOfBoundsException();

        boundary.insertVertex(index-count, point);
    }

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices minus one
     */
    public void removeVertex(int i) {
        int count = 0;
        GJLinearRing2D boundary = null;

        for (GJLinearRing2D ring : rings) {
            int nv = ring.vertexNumber();
            if (count + nv > i) {
                boundary = ring;
                break;
            }
            count += nv;
        }

        if (boundary == null)
            throw new IndexOutOfBoundsException();

        boundary.removeVertex(i-count);
    }

    /**
     * Returns the total number of vertices in this polygon. 
     * The total number is computed as the sum of vertex number in each ring
     * of the polygon.
     */
    public int vertexNumber() {
        int count = 0;
        for (GJLinearRing2D ring : rings)
            count += ring.vertexNumber();
        return count;
    }

    /**
     * Computes the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(GJPoint2D point) {
    	double minDist = Double.POSITIVE_INFINITY;
    	int index = -1;
    	
    	int i = 0;
    	for (GJLinearRing2D ring : this.rings) {
    		for (GJPoint2D vertex : ring.vertices()) {
    			double dist = vertex.distance(point);
        		if (dist < minDist) {
        			index = i;
        			minDist = dist;
        		}
        		i++;
    		}
    		
    	}
    	
    	return index;
    }
    

	// ===================================================================
    // methods implementing the GJDomain2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearDomain2D#transform(math.geom2d.transform.GJCircleInversion2D)
	 */
	public GJCirculinearDomain2D transform(GJCircleInversion2D inv) {
		return new GJGenericCirculinearDomain2D(
				this.boundary().transform(inv).reverse());
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		return GJPolygons2D.createBuffer(this, dist);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#asPolygon(int)
	 */
	public GJPolygon2D asPolygon(int n) {
		return this;
	}

    public GJCirculinearContourArray2D<GJLinearRing2D> boundary() {
        return GJCirculinearContourArray2D.createCirculinearContour2DFromCollection(rings);
    }

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<GJLinearRing2D> contours() {
		return Collections.unmodifiableList(rings);
	}

    public GJPolygon2D complement() {
        // allocate memory for array of reversed rings
        ArrayList<GJLinearRing2D> reverseLines =
        	new ArrayList<GJLinearRing2D>(rings.size());
        
        // reverse each ring
        for (GJLinearRing2D ring : rings)
            reverseLines.add(ring.reverse());
        
        // createFromCollection the new MultiMpolygon2D with set of reversed rings
        return new GJMultiPolygon2D(reverseLines);
    }

    // ===================================================================
    // methods inherited from interface GJShape2D

    public GJBox2D boundingBox() {
        // start with empty bounding box
        GJBox2D box = new GJBox2D(
        		Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 
                Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        
        // compute union of all bounding boxes
        for (GJLinearRing2D ring : this.rings)
            box = box.union(ring.boundingBox());
        
        // return result
        return box;
    }

    /**
     * Clips the polygon with the specified box.
     */
    public GJPolygon2D clip(GJBox2D box) {
    	return GJPolygons2D.clipPolygon(this, box);
    }

    public double distance(GJPoint2D p) {
        return Math.max(this.boundary().signedDistance(p), 0);
    }

    public double distance(double x, double y) {
        return Math.max(this.boundary().signedDistance(x, y), 0);
    }

    public boolean isBounded() {
        // If boundary is not bounded, the polygon is not
        GJBoundary2D boundary = this.boundary();
        if (!boundary.isBounded())
            return false;

        // Computes the signed area
        double area = 0;
        for (GJLinearRing2D ring : rings)
            area += ring.area();

        // bounded if positive area
        return area>0;
    }

    /**
     * The GJMultiPolygon2D is empty either if it contains no ring, or if all
     * rings are empty.
     */
    public boolean isEmpty() {
        // return true if at least one ring is not empty
        for (GJLinearRing2D ring : rings)
            if (!ring.isEmpty())
                return false;
        return true;
    }

    public GJMultiPolygon2D transform(GJAffineTransform2D trans) {
        // allocate memory for transformed rings
        ArrayList<GJLinearRing2D> transformed =
            new ArrayList<GJLinearRing2D>(rings.size());
        
        // transform each ring
        for (GJLinearRing2D ring : rings)
            transformed.add(ring.transform(trans));
        
        // creates a new GJMultiPolygon2D with the set of trasnformed rings
        return new GJMultiPolygon2D(transformed);
    }

    public boolean contains(GJPoint2D point) {
        double angle = 0;
        for (GJLinearRing2D ring : this.rings)
            angle += ring.windingAngle(point);
      
        double area = this.area();
    	if (area > 0) {
    		return angle > Math.PI;
    	} else {
    		return angle > -Math.PI;
    	}
    }

    public boolean contains(double x, double y) {
        return this.contains(new GJPoint2D(x, y));
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.boundary().getGeneralPath());
    }

    public void fill(Graphics2D g) {
        g.fill(this.boundary().getGeneralPath());
    }
    

	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this == obj)
    		return true;
    	
        if (!(obj instanceof GJMultiPolygon2D))
            return false;
        GJMultiPolygon2D polygon = (GJMultiPolygon2D) obj;

        // check if the two objects have same number of rings
        if (polygon.rings.size() != this.rings.size()) 
            return false;
        
        // check each couple of ring
        for (int i = 0; i < rings.size(); i++)
            if(!this.rings.get(i).almostEquals(polygon.rings.get(i), eps))
                return false;
        
        return true;
    }

	// ===================================================================
	// methods overriding the Object class

	@Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;

    	if (!(obj instanceof GJMultiPolygon2D))
            return false;
        
        // check if the two objects have same number of rings
        GJMultiPolygon2D polygon = (GJMultiPolygon2D) obj;
        if (polygon.rings.size() != this.rings.size()) 
            return false;
        
        // check each couple of ring
        for (int i = 0; i < rings.size(); i++)
            if (!this.rings.get(i).equals(polygon.rings.get(i)))
                return false;
        
        return true;
    }
   
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    public GJMultiPolygon2D clone() {
        // allocate memory for new ring array
        ArrayList<GJLinearRing2D> array = new ArrayList<GJLinearRing2D>(rings.size());
        
        // clone each ring
        for(GJLinearRing2D ring : rings)
            array.add(new GJLinearRing2D(ring));
        
        // createFromCollection a new polygon with cloned rings
        return new GJMultiPolygon2D(array);
    }
}
