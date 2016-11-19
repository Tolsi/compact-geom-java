/* file : OrientedCurve2D.java
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
 * Created on 25 d�c. 2006
 *
 */









/**
 * An GJOrientedCurve2D defines an 'inside' and an 'outside'. It is typically a
 * part of the boundary of a domain. Several GJOrientedCurve2D form a
 * GJContour2D, and one or several GJContour2D form a GJBoundary2D.
 * 
 * @author dlegland
 */
public interface GJOrientedCurve2D extends GJCurve2D {

    /**
     * Return the angle portion that the curve turn around the given point.
     * Result is a signed angle.
     * 
     * @param point a point of the plane
     * @return a signed angle
     */
    public abstract double windingAngle(GJPoint2D point);

    /**
     * Returns the signed distance of the curve to the given point. 
     * The distance is positive if the point lies outside the shape, and 
     * negative if the point lies inside the shape. In both cases, absolute 
     * value of distance is equals to the distance to the border of the shape.
     * 
     * @param point a point of the plane
     * @return the signed distance to the curve
     */
    public abstract double signedDistance(GJPoint2D point);

    /**
     * The same as distanceSigned(GJPoint2D), but by passing 2 double as
     * arguments.
     * 
     * @param x x-coord of a point
     * @param y y-coord of a point
     * @return the signed distance of the point (x,y) to the curve
     */
    public abstract double signedDistance(double x, double y);

    /**
     * Returns true if the point is 'inside' the domain bounded by the curve.
     * 
     * @param pt a point in the plane
     * @return true if the point is on the left side of the curve.
     */
    public abstract boolean isInside(GJPoint2D pt);

    public abstract GJOrientedCurve2D reverse();

    // TODO: what to do with non-continuous oriented curves ?
    // public abstract GJOrientedCurve2D subCurve(double t0, double t1);

    public abstract GJCurveSet2D<? extends GJOrientedCurve2D> clip(GJBox2D box);

    /**
     * Transforms the oriented curve, and returns another oriented curve. 
     * If transform is not direct, the domains bounded by the transformed
     * curve should be complemented to have same orientation as the original
     * domain.    
     */
    public abstract GJOrientedCurve2D transform(GJAffineTransform2D trans);
}
