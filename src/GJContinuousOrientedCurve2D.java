/**
 * Defines a part of the boundary of a planar domain. A ContinuousBoundary2D is
 * a continuous, oriented and non self-intersecting curve.
 * 
 * @author dlegland
 */
public interface GJContinuousOrientedCurve2D extends GJContinuousCurve2D,
        GJOrientedCurve2D {

    public abstract GJContinuousOrientedCurve2D reverse();

    public abstract GJContinuousOrientedCurve2D subCurve(double t0, double t1);

    public abstract GJContinuousOrientedCurve2D transform(GJAffineTransform2D trans);

    public abstract GJCurveSet2D<? extends GJContinuousOrientedCurve2D> clip(
            GJBox2D box);
}
