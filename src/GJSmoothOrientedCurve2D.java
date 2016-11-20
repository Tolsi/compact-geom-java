/**
 * Interface for smooth and oriented curves. The aim of this interface is mainly
 * to specify refinement of method declarations.
 */
public interface GJSmoothOrientedCurve2D extends GJSmoothCurve2D,
        GJContinuousOrientedCurve2D {

    public abstract GJSmoothOrientedCurve2D reverse();

    public abstract GJSmoothOrientedCurve2D subCurve(double t0, double t1);

    public abstract GJCurveSet2D<? extends GJSmoothOrientedCurve2D> clip(GJBox2D box);

    public abstract GJSmoothOrientedCurve2D transform(GJAffineTransform2D trans);
}
