import java.awt.*;
// Imports

/**
 * <p> 
 * Main interface for all geometric objects, including points, lines, curves,
 * or planar regions... Instances of GJShape2D can be either bounded
 * (a point, a line segment, a square...) or unbounded (a parabola,
 * a half-plane...).</p>
 * <p>
 * GJShape2D implementations implements a more specialized interface depending
 * on the shape inner dimension:
 * {@link math.geom2d.curve.Curve2D GJCurve2D},
 * {@link math.geom2d.domain.Domain2D GJDomain2D} or
 * {@link math.geom2d.point.PointShape2D GJPointShape2D}.</p>
 * <p>
 * GJShape2D interface provide convenient method to check if the shape
 * {@link #isEmpty() is empty}, to {@link #transform(GJAffineTransform2D)
 * transform} or to {@link #clip(GJBox2D) clip} the shape, get its
 * {@link #boundingBox() bounding box}, or its
 * {@link #distance(GJPoint2D) distance} to a given point.</p>
 */
public interface GJShape2D extends GJGeometricObject2D {

    // ===================================================================
    // constants

    /**
     * The constant used for testing results.
     */
    public final static double  ACCURACY  = 1e-12;

    /**
     * Checks if the shape contains the planar point defined by (x,y).
     */
    public abstract boolean contains(double x, double y);

    /**
     * Checks if the shape contains the given point.
     */
    public abstract boolean contains(GJPoint2D p);

    /**
     * Returns the distance of the shape to the given point, or the distance of
     * point to the frontier of the shape in the case of a plain shape.
     */
    public abstract double distance(GJPoint2D p);

    /**
     * Returns the distance of the shape to the given point, specified by x and
     * y, or the distance of point to the frontier of the shape in the case of
     * a plain (i.e. fillable) shape.
     */
    public abstract double distance(double x, double y);

    /**
     * Returns true if the shape is bounded, that is if we can draw a finite
     * rectangle enclosing the shape. For example, a straight line or a parabola
     * are not bounded.
     */
    public abstract boolean isBounded();

    /**
     * Returns true if the shape does not contain any point. This is the case
     * for example for GJPointSet2D without any point.
     * 
     * @return true if the shape does not contain any point.
     */
    public abstract boolean isEmpty();

    /**
     * Returns the bounding box of the shape.
     * 
     * @return the bounding box of the shape.
     */
    public abstract GJBox2D boundingBox();

    /**
     * Clip the shape with the given box, and returns a new shape. The box must
     * be bounded.
     * 
     * @param box the clipping box
     * @return the clipped shape
     */
    public abstract GJShape2D clip(GJBox2D box);

    /**
     * Transforms the shape by an affine transform. Subclasses may override the
     * type of returned shape.
     * 
     * @param trans an affine transform
     * @return the transformed shape
     */
    public abstract GJShape2D transform(GJAffineTransform2D trans);

    /**
     * Draws the shape on the given graphics. 
     * If the shape is empty, nothing is drawn.
     * If the shape is unbounded, an exception is thrown.
     */
    public abstract void draw(Graphics2D g2);
}
