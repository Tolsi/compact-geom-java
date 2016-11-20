import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Provides a base implementation for continuous curves.
 * @author dlegland
 */
public abstract class GJAbstractContinuousCurve2D
implements GJContinuousCurve2D, Cloneable {

	/**
	 * Wrap the given curve into an array list with the appropriate generic.
	 */
	protected static <T extends GJContinuousCurve2D> Collection<T> wrapCurve(T curve) {
		ArrayList<T> list = new ArrayList<T> (1);
		list.add(curve);
		return list;
	}
	
	/**
	 * Converts this continuous curve to an instance of GJLinearCurve2D with
	 * the given number of edges. Returns either an instance of GJPolyline2D
	 * or GJLinearRing2D, depending on the curve is closed or not.
	 * This method can be overridden to return the correct type.
	 * 
	 * @see math.geom2d.curve.ContinuousCurve2D#asPolyline(int)
	 */
	public GJLinearCurve2D asPolyline(int n) {
		// Check that the curve is bounded
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);

		if (this.isClosed()) {
			return asPolylineClosed(n);
		} else {
			return asPolylineOpen(n);
		}
	}
	
	/**
	 * Assumes the curve is open, and returns an instance of GJPolyline2D.
	 * @param n the number of edges of the resulting polyline
	 * @return a new GJPolyline2D approximating the original curve
	 */
	protected GJPolyline2D asPolylineOpen(int n) {
		// Check that the curve is bounded
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);

        // compute start and increment values
        double t0 = this.t0();
        double dt = (this.t1() - t0) / n;

        // allocate array of points, and compute each value.
        // Computes also value for last point.
        GJPoint2D[] points = new GJPoint2D[n + 1];
        for (int i = 0; i < n + 1; i++)
        	points[i] = this.point(t0 + i * dt);

        return new GJPolyline2D(points);
	}

	/**
	 * Assumes the curve is closed, and returns an instance of GJLinearRing2D.
	 * @param n the number of edges of the resulting linear ring
	 * @return a new GJLinearRing2D approximating the original curve
	 */
	protected GJLinearRing2D asPolylineClosed(int n) {
		// Check that the curve is bounded
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);

        // compute start and increment values
        double t0 = this.t0();
        double dt = (this.t1() - t0) / n;

		// compute position of points, without the last one, 
		// which is included by default with linear rings
        GJPoint2D[] points = new GJPoint2D[n];
		for (int i = 0; i < n; i++)
			points[i] = this.point(t0 + i * dt);

		return new GJLinearRing2D(points);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#getContinuousCurves()
	 */
	public Collection<? extends GJContinuousCurve2D> continuousCurves() {
		return wrapCurve(this);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#getFirstPoint()
	 */
	public GJPoint2D firstPoint() {
		double t0 = this.t0();
		if(Double.isInfinite(t0))
			throw new GJUnboundedShape2DException(this);
		return this.point(t0);
	}


	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#getLastPoint()
	 */
	public GJPoint2D lastPoint() {
		double t1 = this.t1();
		if(Double.isInfinite(t1))
			throw new GJUnboundedShape2DException(this);
		return this.point(t1);
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#draw(java.awt.Graphics2D)
	 */
	public void draw(Graphics2D g2) {
		g2.draw(this.asAwtShape());
	}

	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJCurve2D#asAWTShape()
	 */
	public Shape asAwtShape() {
		// Check that the curve is bounded
        if (!this.isBounded())
            throw new GJUnboundedShape2DException(this);

        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        
		GJPoint2D point = this.firstPoint();
        path.moveTo((float) point.x(), (float)  point.y());
        path = this.appendPath(path);
        return path;
	}
	
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
	@Override
    public abstract GJContinuousCurve2D clone();
}
