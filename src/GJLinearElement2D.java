/**
 * A continuous linear shape, like a straight line, a line segment or a ray.
 * 
 * @author dlegland
 */
public interface GJLinearElement2D extends GJCirculinearElement2D, GJLinearShape2D {

    public GJLinearElement2D transform(GJAffineTransform2D trans);
    public GJLinearElement2D subCurve(double y0, double t1);
    public GJCurveSet2D<? extends GJLinearElement2D> clip(GJBox2D box);
}
