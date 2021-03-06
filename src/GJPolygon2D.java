import java.util.Collection;


/**
 * Represent any class made of a finite set of simply connected edges. This
 * include simple polygons, multiple polygons, or more specialized shapes like
 * rectangles, squares...
 * The boundary of a polygon is a collection of GJLinearRing2D.
 */
public interface GJPolygon2D extends GJCirculinearDomain2D {

    /** Returns the vertices (singular points) of the polygon */
    public Collection<GJPoint2D> vertices();

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public GJPoint2D vertex(int i);

	/**
	 * Sets the position of the i-th vertex
	 * 
	 * @param i
	 *            the vertex index
	 * @param point
	 *            the new position of the vertex
	 * @throws UnsupportedOperationException
	 *             if this polygon implementation does not support vertex
	 *             modification
	 */
    public void setVertex(int i, GJPoint2D point);
    
	/**
	 * Adds a vertex as last vertex of this polygon.
	 * 
	 * @param point
	 *            the position of the new vertex
	 * @throws UnsupportedOperationException
	 *             if this polygon implementation does not support vertex
	 *             modification
	 */
    public void addVertex(GJPoint2D point);

	/**
	 * Inserts a vertex at the specified position.
	 * 
	 * @param index
	 *            index at which the specified vertex is to be inserted
	 * @param point
	 *            the position of the new vertex
	 * @throws UnsupportedOperationException
	 *             if this polygon implementation does not support vertex
	 *             modification
	 */
    public void insertVertex(int index, GJPoint2D point);
    
    /**
     * Removes the vertex at the given index.
     * @param index index of the vertex to remove
	 * @throws UnsupportedOperationException
	 *             if this polygon implementation does not support vertex
	 *             modification
     */
    public void removeVertex(int index);
    
    /**
     * Returns the number of vertices of the polygon
     * 
     * @since 0.6.3
     */
    public int vertexNumber();

    /**
     * Returns the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(GJPoint2D point);
    
    /** Return the edges as line segments of the polygon */
    public Collection<? extends GJLineSegment2D> edges();

    /** Returns the number of edges of the polygon */
    public int edgeNumber();
    
    /** 
     * Returns the centroid (center of mass) of the polygon.
     */
    public GJPoint2D centroid();

    /** 
     * Returns the signed area of the polygon.
     */
    public double area();

    
    // ===================================================================
    // methods inherited from the GJDomain2D interface

    /**
     * Overrides the definition of boundary() such that the boundary of a 
     * polygon is defined as a set of GJLinearRing2D.
     */
    public GJCirculinearContourArray2D<? extends GJLinearRing2D>
    boundary();

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<? extends GJLinearRing2D> contours();
    
    /**
     * Returns the complementary polygon.
     * 
     * @return the polygon complementary to this
     */
    public GJPolygon2D complement();
    
    // ===================================================================
    // methods inherited from the GJShape2D interface

    /**
     * Returns the new Polygon created by an affine transform of this polygon.
     */
    public GJPolygon2D transform(GJAffineTransform2D trans);

    public GJPolygon2D clip(GJBox2D box);
}
