/**
 * 
 */


/**
 * Generic class providing utilities for manipulating conics. Provides in
 * particular methods for reducing a conic.
 * 
 * @author dlegland
 */
public class GJConics2D {

    /**
     * Transforms a conic centered around the origin, by dropping the
     * translation part of the transform. The array must be contains at least
     * 3 elements. If it contains 6 elements, the 3 remaining elements are
     * supposed to be 0, 0, and -1 in that order.
     * 
     * @param coefs an array of double with at least 3 coefficients
     * @param trans an affine transform
     * @return an array of double with as many elements as the input array
     */
    public final static double[] transformCentered(double[] coefs,
            GJAffineTransform2D trans) {
        // Extract transform coefficients
        double[][] mat = trans.affineMatrix();
        double a = mat[0][0];
        double b = mat[1][0];
        double c = mat[0][1];
        double d = mat[1][1];

        // Extract first conic coefficients
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];

        // compute matrix determinant
		double delta = a * d - b * c;
		delta = delta * delta;

		double A2 = (A * d * d + C * b * b - B * b * d) / delta;
		double B2 = (B * (a * d + b * c) - 2 * (A * c * d + C * a * b)) / delta;
		double C2 = (A * c * c + C * a * a - B * a * c) / delta;

        // return only 3 parameters if needed
        if (coefs.length==3)
            return new double[] { A2, B2, C2 };

        // Compute other coefficients
		double D = coefs[3];
		double E = coefs[4];
		double F = coefs[5];
		double D2 = D * d - E * b;
		double E2 = E * a - D * c;
        return new double[] { A2, B2, C2, D2, E2, F };
    }

    /**
     * Transforms a conic by an affine transform.
     * 
     * @param coefs an array of double with 6 coefficients
     * @param trans an affine transform
     * @return the coefficients of the transformed conic
     */
    public final static double[] transform(double[] coefs,
            GJAffineTransform2D trans) {
        // Extract coefficients of the inverse transform
        double[][] mat = trans.invert().affineMatrix();
        double a = mat[0][0];
        double b = mat[1][0];
        double c = mat[0][1];
        double d = mat[1][1];
        double e = mat[0][2];
        double f = mat[1][2];

        // Extract conic coefficients
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];
        double D = coefs[3];
        double E = coefs[4];
        double F = coefs[5];

		// Compute coefficients of the transformed conic
		double A2 = A * a * a + B * a * b + C * b * b;
		double B2 = 2 * (A * a * c + C * b * d) + B * (a * d + b * c);
		double C2 = A * c * c + B * c * d + C * d * d;
		double D2 = 2 * (A * a * e + C * b * f) + B * (a * f + b * e) + D * a + E * b;
		double E2 = 2 * (A * c * e + C * d * f) + B * (c * f + d * e) + D * c + E * d;
		double F2 = A * e * e + B * e * f + C * f * f + D * e + E * f + F;

        // Return the array of coefficients
        return new double[] { A2, B2, C2, D2, E2, F2 };
    }

    // -----------------------------------------------------------------
    // Some special conics

    static class ConicStraightLine2D extends GJStraightLine2D implements GJConic2D {

        double[] coefs = new double[] { 0, 0, 0, 1, 0, 0 };

        public ConicStraightLine2D(GJStraightLine2D line) {
            super(line);
			coefs = new double[] { 0, 0, 0, dy, -dx, dx * y0 - dy * x0 };
        }

        public ConicStraightLine2D(double a, double b, double c) {
            super(GJStraightLine2D.createCartesian(a, b, c));
            coefs = new double[] { 0, 0, 0, a, b, c };
        }

        public double[] conicCoefficients() {
            return coefs;
        }

        public Type conicType() {
            return GJConic2D.Type.STRAIGHT_LINE;
        }

        /** Return NaN. */
        public double eccentricity() {
            return Double.NaN;
        }

        @Override
        public ConicStraightLine2D reverse() {
            return new ConicStraightLine2D(super.reverse());
        }

        @Override
        public ConicStraightLine2D transform(GJAffineTransform2D trans) {
            return new ConicStraightLine2D(super.transform(trans));
        }
    }


    static class ConicTwoLines2D extends GJContourArray2D<GJStraightLine2D>
            implements GJConic2D {

        double xc = 0, yc = 0, d = 1, theta = 0;

        public ConicTwoLines2D(GJPoint2D point, double d, double theta) {
            this(point.x(), point.y(), d, theta);
        }

        public ConicTwoLines2D(double xc, double yc, double d, double theta) {
            super();

            this.xc = xc;
            this.yc = yc;
            this.d = d;
            this.theta = theta;

            GJStraightLine2D baseLine = new GJStraightLine2D(
                    new GJPoint2D(xc, yc), theta);
            this.add(baseLine.parallel(d));
            this.add(baseLine.parallel(-d).reverse());
        }

        public double[] conicCoefficients() {
            double[] coefs = { 0, 0, 1, 0, 0, -1 };
            GJAffineTransform2D sca = GJAffineTransform2D.createScaling(0, d);
            GJAffineTransform2D rot = GJAffineTransform2D.createRotation(theta);
            GJAffineTransform2D tra = GJAffineTransform2D.createTranslation(xc, yc);

            // GJAffineTransform2D trans = tra.compose(rot).compose(sca);
            GJAffineTransform2D trans = sca.chain(rot).chain(tra);
            return GJConics2D.transform(coefs, trans);
        }

        public Type conicType() {
            return GJConic2D.Type.TWO_LINES;
        }

        public double eccentricity() {
            return Double.NaN;
        }

        @Override
        public ConicTwoLines2D transform(GJAffineTransform2D trans) {
            GJPoint2D center = new GJPoint2D(xc, yc).transform(trans);
            GJStraightLine2D line = this.firstCurve().transform(trans);

            double dist = line.distance(center);
            double angle = line.horizontalAngle();
            return new ConicTwoLines2D(center, dist, angle);
        }

        @Override
        public ConicTwoLines2D reverse() {
            return new ConicTwoLines2D(xc, yc, -d, theta);
        }
    }

    // TODO: add CrossConic2D
}
