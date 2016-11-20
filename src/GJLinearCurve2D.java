import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Abstract class that is the base implementation of GJPolyline2D and GJLinearRing2D.
 * @author dlegland
 *
 */
public abstract class GJLinearCurve2D extends GJAbstractContinuousCurve2D
implements GJCirculinearContinuousCurve2D {

    // ===================================================================
    // class variables
    
    protected ArrayList<GJPoint2D> vertices;


    // ===================================================================
    // Contructors

    protected GJLinearCurve2D() {
    	this.vertices = new ArrayList<GJPoint2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the 
     * specified number of vertices.
     * @param nVertices
     */
    protected GJLinearCurve2D(int nVertices) {
    	this.vertices = new ArrayList<GJPoint2D>(nVertices);
    }

    protected GJLinearCurve2D(GJPoint2D... vertices) {
    	this.vertices = new ArrayList<GJPoint2D>(vertices.length);
        for (GJPoint2D vertex : vertices)
            this.vertices.add(vertex);
    }

    protected GJLinearCurve2D(Collection<? extends GJPoint2D> vertices) {
    	this.vertices = new ArrayList<GJPoint2D>(vertices.size());
        this.vertices.addAll(vertices);
    }

    protected GJLinearCurve2D(double[] xcoords, double[] ycoords) {
    	this.vertices = new ArrayList<GJPoint2D>(xcoords.length);
		int n = xcoords.length;
    	this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
            vertices.add(new GJPoint2D(xcoords[i], ycoords[i]));
    }


    // ===================================================================
    // Methods specific to GJLinearCurve2D

	/**
	 * Returns a simplified version of this linear curve. Sub classes may
	 * override this method to return a more specialized type.
	 */
	public abstract GJLinearCurve2D simplify(double distMax);
    
    /**
     * Returns an iterator on the collection of points.
     */
    public Iterator<GJPoint2D> vertexIterator() {
        return vertices.iterator();
    }
    
    /**
     * Returns the collection of vertices as an array of GJPoint2D.
     * 
     * @return an array of GJPoint2D
     */
    public GJPoint2D[] vertexArray() {
    	return this.vertices.toArray(new GJPoint2D[]{});
    }

    /**
     * Adds a vertex at the end of this polyline.
     * @return true if the vertex was correctly added
     * @since 0.9.3
     */
    public boolean addVertex(GJPoint2D vertex) {
    	 return vertices.add(vertex);
    }
    
    /**
     * Inserts a vertex at a given position in the polyline.
     * @since 0.9.3
     */
    public void insertVertex(int index, GJPoint2D vertex) {
    	vertices.add(index, vertex);
    }
    
    /**
     * Removes the first instance of the given vertex from this polyline.
     * @param vertex the position of the vertex to remove
     * @return true if the vertex was actually removed
     * @since 0.9.3
     */
    public boolean removeVertex(GJPoint2D vertex) {
        return vertices.remove(vertex);
    }
    
    /**
     * Removes the vertex specified by the index.
     * @param index the index of the vertex to remove
     * @return the position of the vertex removed from the polyline
     * @since 0.9.3
     */
    public GJPoint2D removeVertex(int index) {
    	return this.vertices.remove(index);
    }

    /**
     * Changes the position of the i-th vertex.
     *  @since 0.9.3
     */
    public void setVertex(int index, GJPoint2D position) {
        this.vertices.set(index, position);
    }

    public void clearVertices() {
        vertices.clear();
    }

    /**
     * Returns the vertices of the polyline.
     */
    public Collection<GJPoint2D> vertices() {
        return vertices;
    }

    /**
     * Returns the i-th vertex of the polyline.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public GJPoint2D vertex(int i) {
        return vertices.get(i);
    }

    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexNumber() {
        return vertices.size();
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
    
    // ===================================================================
    // Management of edges

    /**
     * Returns the i-th edge of this linear curve. 
     */
    public abstract GJLineSegment2D edge(int index);
    
    /**
     * Returns the number of edges of this linear curve.
     */
    public abstract int edgeNumber();
    
    /**
     * Returns a collection of GJLineSegment2D that represent the individual
     * edges of this linear curve. 
     * 
     * @return the edges of the polyline
     */
    public abstract Collection<GJLineSegment2D> edges();

    public GJLineSegment2D firstEdge() {
        if (vertices.size() < 2)
            return null;
        return new GJLineSegment2D(vertices.get(0), vertices.get(1));
    }

    public abstract GJLineSegment2D lastEdge();

    
    // ===================================================================
    // methods implementing the GJCirculinearCurve2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length()
	 */
	public double length() {
		double sum = 0;
		for(GJLineSegment2D edge : this.edges())
			sum += edge.length();
		return sum;
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#length(double)
	 */
	public double length(double pos) {
		//init
		double length = 0;
		
		// add length of each curve before current curve
		int index = (int) Math.floor(pos);
		for(int i=0; i<index; i++)
			length += this.edge(i).length();
		
		// add portion of length for last curve
		if(index < vertices.size()-1) {
			double pos2 = pos-index;
			length += this.edge(index).length(pos2);
		}
		
		// return computed length
		return length;
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#position(double)
	 */
	public double position(double length) {
		
		// position to compute
		double pos = 0;
		
		// index of current curve
		int index = 0;
		
		// cumulative length
		double cumLength = this.length(this.t0());
		
		// iterate on all curves
		for(GJLineSegment2D edge : edges()) {
			// length of current curve
			double edgeLength = edge.length();
			
			// add either 2, or fraction of length
			if(cumLength + edgeLength < length) {
				cumLength += edgeLength;
				index ++;
			} else {
				// add local position on current curve
				double pos2 = edge.position(length - cumLength);
				pos = index + pos2;
				break;
			}			
		}
		
		// return the result
		return pos;
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();

		// basic check to avoid degenerate cases
		if (GJPointSets2D.hasMultipleVertices(this.vertices)) {
			GJPolyline2D poly2 = GJPolyline2D.create(
					GJPointSets2D.filterMultipleVertices(this.vertices));
			return bc.computeBuffer(poly2, dist);			
		}
		
		return bc.computeBuffer(this, dist);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearCurve2D#parallel(double)
	 */
	public GJCirculinearContinuousCurve2D parallel(double d) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.createContinuousParallel(this, d);
	}


    // ===================================================================
    // Methods implementing GJOrientedCurve2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#signedDistance(GJPoint2D)
     */
    public double signedDistance(GJPoint2D point) {
        double dist = this.distance(point.x(), point.y());
        if (isInside(point))
            return -dist;
        else
            return dist;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJOrientedCurve2D#signedDistance(double, double)
     */
    public double signedDistance(double x, double y) {
        double dist = this.distance(x, y);
        if (isInside(new GJPoint2D(x, y)))
            return -dist;
        else
            return dist;
    }


	// ===================================================================
    // Methods inherited from GJContinuousCurve2D

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#leftTangent(double)
	 */
	public GJVector2D leftTangent(double t) {
		int index = (int) Math.floor(t);
		if(Math.abs(t-index) < GJShape2D.ACCURACY)
			index--;
		return this.edge(index).tangent(0);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#rightTangent(double)
	 */
	public GJVector2D rightTangent(double t) {
		int index = (int) Math.ceil(t);
		return this.edge(index).tangent(0);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#curvature(double)
	 */
	public double curvature(double t) {
		double index = Math.round(t);
		if (Math.abs(index - t) > GJShape2D.ACCURACY)
			return 0;
		else
			return Double.POSITIVE_INFINITY;
	}

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJContinuousCurve2D#smoothPieces()
     */
    public Collection<? extends GJLineSegment2D> smoothPieces() {
        return edges();
    }

    
    // ===================================================================
    // Methods inherited from interface GJCurve2D

    /**
     * Returns 0.
     */
    public double t0() {
        return 0;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }

    /**
     * Returns the first point of the linear curve.
     */
    public GJPoint2D firstPoint() {
        if (vertices.size()==0)
            return null;
        return vertices.get(0);
    }

    public Collection<GJPoint2D> singularPoints() {
        return vertices;
    }

    public boolean isSingular(double pos) {
        if (Math.abs(pos - Math.round(pos)) < GJShape2D.ACCURACY)
            return true;
        return false;
    }
   
    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#position(math.geom2d.GJPoint2D)
     */
    public double position(GJPoint2D point) {
        int ind = 0;
        double dist, minDist = Double.POSITIVE_INFINITY;
        double x = point.x();
        double y = point.y();

        int i = 0;
        GJLineSegment2D closest = null;
        for (GJLineSegment2D edge : this.edges()) {
            dist = edge.distance(x, y);
            if (dist < minDist) {
                minDist = dist;
                ind = i;
                closest = edge;
            }
            i++;
        }

        return closest.position(point) + ind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#intersections(math.geom2d.GJLinearShape2D)
     */
    public Collection<GJPoint2D> intersections(GJLinearShape2D line) {
        ArrayList<GJPoint2D> list = new ArrayList<GJPoint2D>();

        // extract intersections with each edge, and add to a list
        GJPoint2D point;
        for (GJLineSegment2D edge : this.edges()) {
        	// do not process edges parallel to intersection line
        	if (edge.isParallel(line))
        		continue;
        	
			point = edge.intersection(line);
			if (point != null)
				if (!list.contains(point))
					list.add(point);
		}

        // return result
        return list;
    }

    public Collection<? extends GJLinearCurve2D> continuousCurves() {
		return wrapCurve(this);
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJCurve2D#position(math.geom2d.GJPoint2D)
     */
    public double project(GJPoint2D point) {
        double dist, minDist = Double.POSITIVE_INFINITY;
        double x = point.x();
        double y = point.y();
        double pos = Double.NaN;

        int i = 0;
        for (GJLineSegment2D edge : this.edges()) {
            dist = edge.distance(x, y);
            if (dist < minDist) {
                minDist = dist;
                pos = edge.project(point)+i;
            }
            i++;
        }

        return pos;
    }

    
    // ===================================================================
    // Methods inherited from interface GJShape2D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#distance(double, double)
     */
    public double distance(double x, double y) {
        double dist = Double.MAX_VALUE;
        for (GJLineSegment2D edge : this.edges()) {
        	if (edge.length()==0)
        		continue;
            dist = Math.min(dist, edge.distance(x, y));
        }
        return dist;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#distance(GJPoint2D)
     */
    public double distance(GJPoint2D point) {
        return distance(point.x(), point.y());
    }

    /**
     * Returns true if the polyline does not contain any point.
     */
    public boolean isEmpty() {
        return vertices.size()==0;
    }

    /** Always returns true, because a linear curve is always bounded. */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns the bounding box of this linear curve.
     */
    public GJBox2D boundingBox() {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        Iterator<GJPoint2D> iter = vertices.iterator();
        GJPoint2D point;
        double x, y;
        while (iter.hasNext()) {
            point = iter.next();
            x = point.x();
            y = point.y();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }

        return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(double, double)
     */
    public boolean contains(double x, double y) {
        for (GJLineSegment2D edge : this.edges()) {
        	if (edge.length()==0)
        		continue;
            if (edge.contains(x, y))
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(GJPoint2D)
     */
    public boolean contains(GJPoint2D point) {
        return this.contains(point.x(), point.y());
    }

    /**
     * Clips the polyline by a box. The result is an instance of GJCurveSet2D,
     * which contains only instances of GJPolyline2D. If the polyline is not
     * clipped, the result is an instance of GJCurveSet2D which
     * contains 0 curves.
     */
    public GJCurveSet2D<? extends GJLinearCurve2D> clip(GJBox2D box) {
        // Clip the curve
        GJCurveSet2D<? extends GJCurve2D> set = GJCurves2D.clipCurve(this, box);

        // Stores the result in appropriate structure
        GJCurveArray2D<GJLinearCurve2D> result =
        	new GJCurveArray2D<GJLinearCurve2D>(set.size());

        // convert the result
        for (GJCurve2D curve : set.curves()) {
            if (curve instanceof GJLinearCurve2D)
                result.add((GJLinearCurve2D) curve);
        }
        return result;
    }

    /**
     * Returns a general path iterator.
     */
    public java.awt.geom.GeneralPath asGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        if (vertices.size() < 2)
            return path;
        return this.appendPath(path);
    }
    
    public void draw(Graphics2D g2) {
    	g2.draw(this.asGeneralPath());
    }

}
