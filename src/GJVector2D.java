import static java.lang.Math.*;

// Imports

/**
 * A vector in the 2D plane. Provides methods to compute cross product and dot
 * product, addition and subtraction of vectors.
 */
public class GJVector2D implements GJGeometricObject2D, Cloneable {

    // ===================================================================
    // static functions

    /**
     * Static factory for creating a new vector in Cartesian coordinates.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJVector2D create(double x, double y) {
        return new GJVector2D(x, y);
    }
    
    /**
     * Static factory for creating a new vector from the coordinate of a point.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static GJVector2D create(GJPoint2D point) {
        return new GJVector2D(point.x, point.y);
    }
    
    /**
     * Creates a new vector by specifying the distance to the origin, and the
     * angle with the horizontal.
     */
	public static GJVector2D createPolar(double rho, double theta) {
		return new GJVector2D(rho * cos(theta), rho * sin(theta));
	}

    /**
     * Get the dot product of the two vectors, defined by :
     * <p>
     * <code> dx1*dy2 + dx2*dy1</code>
     * <p>
     * Dot product is zero if the vectors defined by the 2 vectors are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     */
	public static double dot(GJVector2D v1, GJVector2D v2) {
		return v1.x * v2.x + v1.y * v2.y;
    }

	/**
	 * Get the cross product of the two vectors, defined by :
	 * <p>
	 * <code> dx1*dy2 - dx2*dy1</code>
	 * <p>
	 * Cross product is zero for colinear vectors. It is positive if angle
	 * between vector 1 and vector 2 is comprised between 0 and PI, and negative
	 * otherwise.
	 */
	public static double cross(GJVector2D v1, GJVector2D v2) {
		return v1.x * v2.y - v2.x * v1.y;
	}

    /**
     * Tests if the two vectors are colinear
     * 
     * @return true if the vectors are colinear
     */
	public static boolean isColinear(GJVector2D v1, GJVector2D v2) {
		v1 = v1.normalize();
		v2 = v2.normalize();
		return abs(v1.x * v2.y - v1.y * v2.x) < GJShape2D.ACCURACY;
	}

    /**
     * Tests if the two vectors are orthogonal
     * 
     * @return true if the vectors are orthogonal
     */
    public static boolean isOrthogonal(GJVector2D v1, GJVector2D v2) {
        v1 = v1.normalize();
		v2 = v2.normalize();
		return abs(v1.x * v2.x + v1.y * v2.y) < GJShape2D.ACCURACY;
	}


    // ===================================================================
    // class variables

    /** the x-coordinate of the vector */
    protected double x;
    
    /** the y-coordinate of the vector */
    protected double y;

    
    // ===================================================================
    // constructors

    /** 
     * Constructs a new Vectors initialized with x=1 and y=0. 
     */
    public GJVector2D() {
        this(1, 0);
    }

    /** 
     * Constructs a new vector with the given coordinates. 
     * Consider creating a new Vector using static factory.
     */
    public GJVector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new vector between the origin and the given point.
     */
    public GJVector2D(GJPoint2D point) {
        this(point.x, point.y);
    }

    /**
     * Constructs a new vector between two points
     */
	public GJVector2D(GJPoint2D point1, GJPoint2D point2) {
		this(point2.x - point1.x, point2.y - point1.y);
	}

    // ===================================================================
    // accessors

	/**
	 * Returns the x coordinates of this vector.
	 */
    public double x() {
        return this.x;
    }

	/**
	 * Returns the x coordinates of this vector.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinates of this vector.
	 */
    public double y() {
        return this.y;
    }

	/**
	 * Returns the y coordinates of this vector.
	 */
	public double getY() {
		return y;
	}

    /**
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     * 
     * @return the vector opposite to <code>this</code>.
     */
    public GJVector2D opposite() {
        return new GJVector2D(-this.x, -this.y);
    }

    /**
     * Computes the norm of the vector
     * 
     * @return the euclidean norm of the vector
     */
    public double norm() {
        return hypot(x, y);
    }

    /**
     * Returns the angle with the horizontal axis, in radians.
     * 
     * @return the horizontal angle of the vector
     */
    public double angle() {
        return GJAngle2D.horizontalAngle(this);
    }

    /**
     * Returns the vector with same direction as this one, but with norm equal
     * to 1.
     */
	public GJVector2D normalize() {
		double r = hypot(this.x, this.y);
		return new GJVector2D(this.x / r, this.y / r);
	}

    // ===================================================================
    // compare with other vectors

    /**
     * test if the two vectors are colinear
     * 
     * @return true if the vectors are colinear
     */
    public boolean isColinear(GJVector2D v) {
        return GJVector2D.isColinear(this, v);
    }

    /**
     * test if the two vectors are orthogonal
     * 
     * @return true if the vectors are orthogonal
     */
    public boolean isOrthogonal(GJVector2D v) {
        return GJVector2D.isOrthogonal(this, v);
    }

    // ===================================================================
    // operations between vectors

    /**
     * Get the dot product with point <code>p</code>. Dot product id defined
     * by:
     * <p>
     * <code> x1*y2 + x2*y1</code>
     * <p>.
     * 
     * Dot product is zero if the vectors defined by the 2 points are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     */
	public double dot(GJVector2D v) {
		return x * v.x + y * v.y;
	}

    /**
     * Get the cross product with point <code>p</code>. Cross product is
     * defined by :
     * <p>
     * <code> x1*y2 - x2*y1</code>
     * <p>.
     * 
     * Cross product is zero for colinear vector. It is positive if angle
     * between vector 1 and vector 2 is comprised between 0 and PI, and negative
     * otherwise.
     */
	public double cross(GJVector2D v) {
		return x * v.y - v.x * y;
	}

    /**
     * Returns the sum of current vector with vector given as parameter. Inner
     * fields are not modified.
     */
	public GJVector2D plus(GJVector2D v) {
		return new GJVector2D(x + v.x, y + v.y);
	}

    /**
     * Returns the subtraction of current vector with vector given as
     * parameter. Inner fields are not modified.
     */
	public GJVector2D minus(GJVector2D v) {
		return new GJVector2D(x - v.x, y - v.y);
	}

    /**
     * Multiplies the vector by a scalar amount. Inner fields are not 
     * @param k the scale factor
     * @return the scaled vector
     * @since 0.7.0
     */
	public GJVector2D times(double k) {
		return new GJVector2D(this.x * k, this.y * k);
	}
    
	/**
	 * Rotates the vector by the given angle.
	 * @param theta the angle of rotation, in radians counter-clockwise
	 */
	public GJVector2D rotate(double theta) {
		double cot = cos(theta);
		double sit = sin(theta);
		double x2 = x * cot - y * sit; 
		double y2 = x * sit + y * cot;
		return new GJVector2D(x2, y2);
	}
	
    /**
     * Transform the vector, by using only the first 4 parameters of the
     * transform. Translation of a vector returns the same vector.
     * 
     * @param trans an affine transform
     * @return the transformed vector.
     */
    public GJVector2D transform(GJAffineTransform2D trans) {
		double[] tab = trans.coefficients();
		return new GJVector2D(x * tab[0] + y * tab[1], x * tab[3] + y * tab[4]);
	}

    /**
     * Test whether this object is the same as another vector, with respect
     * to a given threshold.
     */
	public boolean almostEquals(GJGeometricObject2D obj, double eps) {
		if (this == obj)
			return true;

		if (!(obj instanceof GJVector2D))
			return false;
		GJVector2D v = (GJVector2D) obj;

		if (Math.abs(this.x - v.x) > eps)
			return false;
		if (Math.abs(this.y - v.y) > eps)
			return false;

		return true;
	}

	/**
	 * Test whether this object is exactly the same as another vector.
	 * 
	 * @see #almostEquals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof GJVector2D))
			return false;
		GJVector2D that = (GJVector2D) obj;

        // Compare each field
		if (!GJEqualUtils.areEqual(this.x, that.x))
			return false;
		if (!GJEqualUtils.areEqual(this.y, that.y))
			return false;

		return true;
	}

	/**
	 * Display the coordinates of the vector. Typical output is:
	 * <code>x=3 y=4</code>.
	 */
	@Override
	public String toString() {
		return new String("x=" + x + " y=" + y);
	}

	/**
	 * @deprecated not necessary to clone immutable object
	 */
	@Deprecated
	@Override
	public GJVector2D clone() {
		return new GJVector2D(x, y);
	}
}
