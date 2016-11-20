import static java.lang.Math.*;

/**
 * This class is only devoted to static computations.
 * 
 * @author dlegland
 */
public class GJAngle2D {

	/** The constant for PI, equivalent to 180 degrees.*/
	public final static double M_PI 	= Math.PI;
	
	/** The constant for 2*PI, equivalent to 360 degrees.*/
	public final static double M_2PI 	= Math.PI * 2;
	
	/** The constant for PI/2, equivalent to 90 degrees.*/
	public final static double M_PI_2 	= Math.PI / 2;
	
	/** The constant for 3*PI/2, equivalent to 270 degrees.*/
	public final static double M_3PI_2 	= 3 * Math.PI / 2;
	
	/** The constant for 3*PI/4, equivalent to 45 degrees.*/
	public final static double M_PI_4 	= Math.PI / 4;

	/**
	 * Formats an angle between 0 and 2*PI.
	 * 
	 * @param angle
	 *            the angle before formatting
	 * @return the same angle, between 0 and 2*PI.
	 */
	public static double formatAngle(double angle) {
		return ((angle % M_2PI) + M_2PI) % M_2PI;
	}

	/**
	 * Returns the horizontal angle formed by the line joining the origin and
	 * the given point.
	 */
	public static double horizontalAngle(GJPoint2D point) {
		return (Math.atan2(point.y, point.x) + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the horizontal angle formed by the line joining the origin and
	 * the point with given coordinate.
	 */
	public static double horizontalAngle(double x, double y) {
		return (Math.atan2(y, x) + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the horizontal angle formed by the line joining the origin and
	 * the point with given coordinate.
	 */
	public static double horizontalAngle(GJVector2D vect) {
		return (Math.atan2(vect.y, vect.x) + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the horizontal angle formed by the line joining the two given
	 * points.
	 */
	public static double horizontalAngle(GJLinearShape2D object) {
		GJVector2D vect = object.supportingLine().direction();
		return (Math.atan2(vect.y, vect.x) + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the horizontal angle formed by the line joining the two given
	 * points.
	 */
	public static double horizontalAngle(GJPoint2D p1, GJPoint2D p2) {
		return (Math.atan2(p2.y - p1.y, p2.x - p1.x) + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the horizontal angle formed by the line joining the two given
	 * points.
	 */
	public static double horizontalAngle(double x1, double y1, double x2,
			double y2) {
		return (atan2(y2 - y1, x2 - x1) + M_2PI) % (M_2PI);
	}

	/**
	 * <p>
	 * Computes the pseudo-angle of a line joining the 2 points. The
	 * pseudo-angle has same ordering property has natural angle, but is
	 * expected to be computed faster. The result is given between 0 and 360.
	 * </p>
	 * 
	 * @param p1
	 *            the initial point
	 * @param p2
	 *            the final point
	 * @return the pseudo angle of line joining p1 to p2
	 */
	public static double pseudoAngle(GJPoint2D p1, GJPoint2D p2) {
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		double s = abs(dx) + abs(dy);
		double t = (s == 0) ? 0.0 : dy / s;
		if (dx < 0) {
			t = 2 - t;
		} else if (dy < 0) {
			t += 4;
		}
		return t * 90;
	}

	/**
	 * Returns the oriented angle between two (directed) straight objects. 
	 * Result is given in radians, between 0 and 2*PI.
	 */
	public static double angle(GJLinearShape2D obj1, GJLinearShape2D obj2) {
		double angle1 = obj1.horizontalAngle();
		double angle2 = obj2.horizontalAngle();
		return (angle2 - angle1 + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the oriented angle between two vectors. 
	 * Result is given in radians, between 0 and 2*PI.
	 */
	public static double angle(GJVector2D vect1, GJVector2D vect2) {
		double angle1 = horizontalAngle(vect1);
		double angle2 = horizontalAngle(vect2);
		return (angle2 - angle1 + M_2PI) % (M_2PI);
	}

	/**
	 *  Returns the oriented angle between the ray formed by (p2, p1) 
	 *  and the ray formed by (p2, p3). 
	 *  Result is given in radians, between 0 and 2*PI.
	 */
	public static double angle(GJPoint2D p1, GJPoint2D p2, GJPoint2D p3) {
		double angle1 = horizontalAngle(p2, p1);
		double angle2 = horizontalAngle(p2, p3);
		return (angle2 - angle1 + M_2PI) % (M_2PI);
	}

	/**
	 *  Returns the oriented angle between the ray formed by (p2, p1)
	 *  and the ray formed by (p2, p3), where pi = (xi,yi), i=1,2,3. 
	 *  Result is given in radians, between 0 and 2*PI.
	 */
	public static double angle(double x1, double y1, double x2, double y2,
			double x3, double y3) {
		double angle1 = horizontalAngle(x2, y2, x1, y1);
		double angle2 = horizontalAngle(x2, y2, x3, y3);
		return (angle2 - angle1 + M_2PI) % (M_2PI);
	}

	/**
	 * Returns the absolute angle between the ray formed by (p2, p1) 
	 * and the ray formed by (p2, p3). 
	 * Result is given in radians, between 0 and PI.
	 */
	public static double absoluteAngle(GJPoint2D p1, GJPoint2D p2, GJPoint2D p3) {
		double angle1 = horizontalAngle(new GJVector2D(p2, p1));
		double angle2 = horizontalAngle(new GJVector2D(p2, p3));
		angle1 = (angle2 - angle1 + M_2PI) % (M_2PI);
		if (angle1 < Math.PI)
			return angle1;
		else
			return M_2PI - angle1;
	}

	/**
	 * Returns the absolute angle between the ray formed by (p2, p1) 
	 * and the ray formed by (p2, p3), where pi = (xi,yi), i=1,2,3. 
	 * Result is given in radians, between 0 and PI.
	 */
	public static double absoluteAngle(double x1, double y1, double x2,
			double y2, double x3, double y3) {
		double angle1 = horizontalAngle(x2, y2, x1, y1);
		double angle2 = horizontalAngle(x2, y2, x3, y3);
		angle1 = (angle2 - angle1 + M_2PI) % (M_2PI);
		if (angle1 < Math.PI)
			return angle1;
		else
			return M_2PI - angle1;
	}

	/**
	 * Checks whether two angles are equal, with respect to the given error
	 * bound.
	 * 
	 * @param angle1
	 *            first angle to compare
	 * @param angle2
	 *            second angle to compare
	 * @param eps 
	 *            the threshold value for comparison
	 * @return true if the two angle are equal modulo 2*PI
	 */
	public static boolean almostEquals(double angle1, double angle2, double eps) {
		angle1 = formatAngle(angle1);
		angle2 = formatAngle(angle2);
		double diff = formatAngle(angle1 - angle2);
		if (diff < eps)
			return true;
		if (abs(diff - PI * 2) < eps)
			return true;
		return false;
	}

	/**
	 * Checks whether two angles are equal, given a default threshold value.
	 * 
	 * @param angle1
	 *            first angle to compare
	 * @param angle2
	 *            second angle to compare
	 * @return true if the two angle are equal modulo 2*PI
	 */
	public static boolean equals(double angle1, double angle2) {
		angle1 = formatAngle(angle1);
		angle2 = formatAngle(angle2);
		double diff = formatAngle(angle1 - angle2);
		if (diff < GJShape2D.ACCURACY)
			return true;
		if (abs(diff - PI * 2) < GJShape2D.ACCURACY)
			return true;
		return false;
	}

	/**
	 * Tests if an angle belongs to an angular interval, defined by two limit
	 * angle, counted Counter-clockwise.
	 * 
	 * @param startAngle
	 *            the beginning of the angular domain
	 * @param endAngle
	 *            the end of the angular domain
	 * @param angle
	 *            the angle to test
	 * @return true if angle is between the 2 limits
	 */
	public static boolean containsAngle(double startAngle, double endAngle,
			double angle) {
		startAngle 	= formatAngle(startAngle);
		endAngle 	= formatAngle(endAngle);
		angle 		= formatAngle(angle);
		if (startAngle < endAngle)
			return angle >= startAngle && angle <= endAngle;
		else
			return angle <= endAngle || angle >= startAngle;
	}

	/**
	 * Tests if an angle belongs to an angular interval, defined by two limit
	 * angles, and an orientation flag.
	 * 
	 * @param startAngle
	 *            the beginning of the angular domain
	 * @param endAngle
	 *            the end of the angular domain
	 * @param angle
	 *            the angle to test
	 * @param direct
	 *            is true if angular domain is oriented Counter clockwise, and
	 *            false if angular domain is oriented clockwise.
	 * @return true if angle is between the 2 limits
	 */
	public static boolean containsAngle(double startAngle, double endAngle,
			double angle, boolean direct) {
		startAngle 	= formatAngle(startAngle);
		endAngle 	= formatAngle(endAngle);
		angle 		= formatAngle(angle);
		if (direct) {
			if (startAngle < endAngle)
				return angle >= startAngle && angle <= endAngle;
			else
				return angle <= endAngle || angle >= startAngle;
		} else {
			if (startAngle < endAngle)
				return angle <= startAngle || angle >= endAngle;
			else
				return angle >= endAngle && angle <= startAngle;
		}
	}
}
