/* File Domain2D.java 
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
 * 
 * author : Legland
 * Created on 18 sept. 2004
 */



import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;








/**
 * A domain defined from its boundary. The boundary curve must be correctly
 * oriented, non self intersecting, and clearly separating interior and
 * exterior.
 * <p>
 * All contains and intersect tests are computed from the signed distance of the
 * boundary curve.
 * 
 * @author Legland
 */
public class GJGenericDomain2D implements GJDomain2D {

    // ===================================================================
    // Static factories
	
	public static GJGenericDomain2D create(GJBoundary2D boundary) {
		return new GJGenericDomain2D(boundary);
	}
	
	
    // ===================================================================
    // Class variables

	/**
	 * The inner boundary that defines this domain.
	 */
	protected GJBoundary2D boundary = null;

    // ===================================================================
    // Constructors

    public GJGenericDomain2D(GJBoundary2D boundary) {
        this.boundary = boundary;
    }

    // ===================================================================
    // methods implementing the GJDomain2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#asPolygon(int)
	 */
	public GJPolygon2D asPolygon(int n) {
		Collection<? extends GJContour2D> contours = boundary.continuousCurves();
		ArrayList<GJLinearRing2D> rings = new ArrayList<GJLinearRing2D>(contours.size());
		for (GJContour2D contour : contours) {
			// Check that the curve is bounded
	        if (!contour.isBounded())
	            throw new GJUnboundedShape2DException(this);
	        
	        // If contour is bounded, it should be closed
	        if (!contour.isClosed())
	        	throw new IllegalArgumentException("Can not transform open curve to linear ring");

	        GJLinearCurve2D poly = contour.asPolyline(n);
	        assert poly instanceof GJLinearRing2D : "expected result as a linear ring";
	        
			rings.add((GJLinearRing2D) poly);
		}

		return new GJMultiPolygon2D(rings);
	}

    public GJBoundary2D boundary() {
        return boundary;
    }

	/* (non-Javadoc)
	 * @see math.geom2d.domain.GJDomain2D#contours()
	 */
	public Collection<? extends GJContour2D> contours() {
		return this.boundary.continuousCurves();
	}

    public GJDomain2D complement() {
        return new GJGenericDomain2D(boundary.reverse());
    }

    // ===================================================================
    // methods implementing the GJShape2D interface

    public double distance(GJPoint2D p) {
        return Math.max(boundary.signedDistance(p.x(), p.y()), 0);
    }

    public double distance(double x, double y) {
        return Math.max(boundary.signedDistance(x, y), 0);
    }

    /**
     * Returns true if the domain is bounded. The domain is unbounded if either
     * its boundary is unbounded, or a point located outside of the boundary
     * bounding box is located inside of the domain.
     */
    public boolean isBounded() {
        // If boundary is not bounded, the domain is not bounded.
        if (!boundary.isBounded())
            return false;

        // If boundary is bounded, get the bounding box, choose a point
        // outside of the box, and check if its belongs to the domain.
        GJBox2D box = boundary.boundingBox();
        GJPoint2D point = new GJPoint2D(box.getMinX(), box.getMinY());

        return !boundary.isInside(point);
    }

    public boolean isEmpty() {
        return boundary.isEmpty()&&!this.contains(0, 0);
    }

    public GJDomain2D clip(GJBox2D box) {
        return new GJGenericDomain2D(
        		GJBoundaries2D.clipBoundary(this.boundary(), box));
    }

    /**
     * If the domain is bounded, returns the bounding box of its boundary,
     * otherwise returns an infinite bounding box.
     */
    public GJBox2D boundingBox() {
        if (this.isBounded())
            return boundary.boundingBox();
        return GJBox2D.INFINITE_BOX;
    }

    /**
     * Returns a new domain which is created from the transformed domain of this
     * boundary.
     */
    public GJGenericDomain2D transform(GJAffineTransform2D trans) {
        GJBoundary2D transformed = boundary.transform(trans);
        if (!trans.isDirect())
            transformed = transformed.reverse();
        return new GJGenericDomain2D(transformed);
    }

    public boolean contains(double x, double y) {
		return boundary.signedDistance(x, y) <= 0;
    }

    // ===================================================================
    // methods implementing the Shape interface

    public boolean contains(GJPoint2D p) {
        return contains(p.x(), p.y());
    }

    public void draw(Graphics2D g2) {
        boundary.draw(g2);
    }

    public void fill(Graphics2D g2) {
        boundary.fill(g2);
    }
    

	// ===================================================================
	// methods implementing the GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    public boolean almostEquals(GJGeometricObject2D obj, double eps) {
    	if (this==obj)
    		return true;
    	
        if(!(obj instanceof GJGenericDomain2D))
            return false;
        GJGenericDomain2D domain = (GJGenericDomain2D) obj;
        
        if(!boundary.almostEquals(domain.boundary, eps)) return false;
        return true;
    }


	// ===================================================================
	// methods overriding the Object class

    @Override
    public String toString() {
    	return "GJGenericDomain2D(boundary=" + boundary + ")";
    }
    
	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
    @Override
    public boolean equals(Object obj) {
    	if (this==obj)
    		return true;
    	
        if(!(obj instanceof GJGenericDomain2D))
            return false;
        GJGenericDomain2D domain = (GJGenericDomain2D) obj;
        
        if(!boundary.equals(domain.boundary)) return false;
        return true;
    }
}