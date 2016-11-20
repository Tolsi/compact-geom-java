import java.util.ArrayList;
import java.util.Collection;


/**
 * Provides a base implementation for smooth curves.
 * @author dlegland
 */
public abstract class GJAbstractSmoothCurve2D extends GJAbstractContinuousCurve2D
implements GJSmoothCurve2D, Cloneable {


	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#leftTangent(double)
	 */
    public GJVector2D leftTangent(double t){
    	return this.tangent(t);
    }
    
	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#rightTangent(double)
	 */
    public GJVector2D rightTangent(double t){
    	return this.tangent(t);
    }
    
	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#normal(double)
	 */
    public GJVector2D normal(double t){
    	return this.tangent(t).rotate(-Math.PI / 2);
    }
    
	/* (non-Javadoc)
	 * @see math.geom2d.curve.GJContinuousCurve2D#smoothPieces()
	 */
	public Collection<? extends GJSmoothCurve2D> smoothPieces() {
		return wrapCurve(this);
	}

	/** 
	 * Returns an empty set of GJPoint2D, as a smooth curve does not have
	 * singular points by definition. 
	 * @see math.geom2d.curve.Curve2D#singularPoints()
	 */
	public Collection<GJPoint2D> singularPoints() {
		return new ArrayList<GJPoint2D>(0);
	}

	/** 
	 * Returns a set of GJPoint2D, containing the extremities of the curve
	 * if they are not infinite. 
	 * @see math.geom2d.curve.Curve2D#vertices()
	 */
	public Collection<GJPoint2D> vertices() {
		ArrayList<GJPoint2D> array = new ArrayList<GJPoint2D>(2);
		if (!Double.isInfinite(this.t0()))
			array.add(this.firstPoint());
		if (!Double.isInfinite(this.t1()))
			array.add(this.lastPoint());
		return array;
	}

	/**
	 * Returns always false, as a smooth curve does not have singular points
	 * by definition.
	 * @see math.geom2d.curve.Curve2D#isSingular(double)
	 */
	public boolean isSingular(double pos) {
		return false;
	}
	
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
	public abstract GJSmoothCurve2D clone();
}
