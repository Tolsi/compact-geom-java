import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;


/**
 * Represent the union of a finite number of GJPoint2D.
 * 
 * @author dlegland
 */
public class GJPointArray2D
implements GJPointSet2D, GJCirculinearShape2D, Cloneable {

    // ===================================================================
    // static constructors

	public static <T extends GJPoint2D> GJPointArray2D create(Collection<T> points) {
		return new GJPointArray2D(points);
	}

	public static <T extends GJPoint2D> GJPointArray2D create(T... points) {
		return new GJPointArray2D(points);
    }
    
    /**
     * Allocate memory for the specified number of points.
     */
    public static GJPointArray2D create(int size) {
    	return new GJPointArray2D(size);
    }
    
    // ===================================================================
    // inner variables

	/**
     * The inner collection of points composing the set.
     */
    protected ArrayList<GJPoint2D> points = null;

    // ===================================================================
    // constructors
    
    /**
     * Creates a new GJPointArray2D without any points.
     */
    public GJPointArray2D() {
        this(0);
    }

    /**
     * Creates a new empty GJPointArray2D, but preallocates the memory for storing a
     * given amount of points.
     * 
     * @param n the expected number of points in the GJPointArray2D.
     */
    public GJPointArray2D(int n) {
        points = new ArrayList<GJPoint2D>();
    }

    /**
     * Instances of GJPoint2D are directly added, other Point are converted to
     * GJPoint2D with the same location.
     */
    public GJPointArray2D(GJPoint2D... points) {
        this(points.length);
        for (GJPoint2D element : points)
            this.points.add(element);
    }

    /**
     * Copy constructor
     */
    public GJPointArray2D(GJPointSet2D set) {
        this(set.size());
        for (GJPoint2D element : set)
            this.points.add(element);
    }

    /**
     * Points must be a collection of java.awt.Point. Instances of GJPoint2D are
     * directly added, other Point are converted to GJPoint2D with the same
     * location.
     * 
     * @param points
     */
    public GJPointArray2D(Collection<? extends GJPoint2D> points) {
        this(points.size());

        for (GJPoint2D point : points) {
            this.points.add(point);
        }
    }

    // ===================================================================
    // methods implementing the GJPointSet2D interface
    
    /**
     * Add a new point to the set of point. If point is not an instance of
     * GJPoint2D, a GJPoint2D with same location is added instead of point.
     * 
     * @param point
     */
    public boolean add(GJPoint2D point) {
        return this.points.add(point);
    }

	public void add(int index, GJPoint2D point) {
		this.points.add(index, point);
	}

    /**
     * Add a series of points
     * 
     * @param points an array of points
     */
    public void addAll(GJPoint2D[] points) {
        for (GJPoint2D element : points)
            this.add(element);
    }

    public void addAll(Collection<? extends GJPoint2D> points) {
        this.points.addAll(points);
    }

    public GJPoint2D get(int index) {
    	return this.points.get(index);
    }
    
	public boolean remove(GJPoint2D point) {
		return this.points.remove(point);
	}
	
	public GJPoint2D remove(int index) {
		return this.points.remove(index);
	}

	public int indexOf(GJPoint2D point) {
		return this.points.indexOf(point);
	}

	/**
     * return an iterator on the internal point collection.
     * 
     * @return the collection of points
     */
    public Collection<GJPoint2D> points() {
        return Collections.unmodifiableList(points);
    }

    /**
     * remove all points of the set.
     */
    public void clear() {
        this.points.clear();
    }

    /**
     * Returns the number of points in the set.
     * 
     * @return the number of points
     */
    public int size() {
        return points.size();
    }


    // ===================================================================
    // Methods implementing GJCirculinearShape2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.circulinear.GJCirculinearShape2D#buffer(double)
	 */
	public GJCirculinearDomain2D buffer(double dist) {
		GJBufferCalculator bc = GJBufferCalculator.getDefaultInstance();
		return bc.computeBuffer(this, dist);
	}

	public GJPointArray2D transform(GJCircleInversion2D inv) {
    	
    	GJPointArray2D array = new GJPointArray2D(points.size());
    	
    	for (GJPoint2D point : points)
    		array.add(point.transform(inv));
    	
    	return array;
    }
   
   /**
     * Return distance to the closest point of the collection
     */
    public double distance(GJPoint2D p) {
        return distance(p.x(), p.y());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#distance(double, double)
     */
    public double distance(double x, double y) {
    	// basic checkup
        if (points.isEmpty())
            return Double.NaN;
        
        // find smallest distance
        double dist = Double.MAX_VALUE;
        for (GJPoint2D point : points)
            dist = Math.min(dist, point.distance(x, y));
        
        // return distance to closest point
        return dist;
    }

    /**
     * Always return true.
     */
    public boolean isBounded() {
        return true;
    }
    
    /** 
     * Returns true if the point set is empty, i.e. the number of points is 0.
     */
    public boolean isEmpty() {
        return points.size() == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#clip(java.awt.geom.GJRectangle2D)
     */
    public GJPointArray2D clip(GJBox2D box) {
    	// allocate memory for result
        GJPointArray2D res = new GJPointArray2D(points.size());

        // select only points inside of box
        for (GJPoint2D point : points) {
        	if (box.contains(point)) {
        		res.add(point);
        	}
        }
        
        // use array the right size
        res.points.trimToSize();
        
        // return result
        return res;
    }

    public GJBox2D boundingBox() {
    	// init with max values in each direction
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        // update max values with each point
        for (GJPoint2D point : points) {
            xmin = Math.min(xmin, point.x());
            ymin = Math.min(ymin, point.y());
            xmax = Math.max(xmax, point.x());
            ymax = Math.max(ymax, point.y());
        }
        
        // createFromCollection the bounding box
        return new GJBox2D(xmin, xmax, ymin, ymax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.GJShape2D#transform(math.geom2d.GJAffineTransform2D)
     */
    public GJPointArray2D transform(GJAffineTransform2D trans) {
        GJPointArray2D res = new GJPointArray2D(points.size());

        for (GJPoint2D point : points)
            res.add(point.transform(trans));

        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(double, double)
     */
    public boolean contains(double x, double y) {
        for (GJPoint2D point : points)
            if (point.distance(x, y) < GJShape2D.ACCURACY)
                return true;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(java.awt.geom.GJPoint2D)
     */
    public boolean contains(GJPoint2D point) {
        return contains(point.x(), point.y());
    }

    /**
     * Draws the point set on the specified Graphics2D, using default radius
     * equal to 1.
     * 
     * @param g2 the graphics to draw the point set
     */
    public void draw(Graphics2D g2) {
        this.draw(g2, 1);
    }

    /**
     * Draws the point set on the specified Graphics2D, by filling a disc with a
     * given radius.
     * 
     * @param g2 the graphics to draw the point set
     */
    public void draw(Graphics2D g2, double r) {
    	double x, y;
    	double w = 2 * r;
        for (GJPoint2D point : points) {
        	x = point.x();
        	y = point.y();
            g2.fill(new java.awt.geom.Ellipse2D.Double(x-r, y-r, w, w));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<GJPoint2D> iterator() {
        return points.iterator();
    }
    
    // ===================================================================
    // methods implementing GJGeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GJGeometricObject2D#almostEquals(math.geom2d.GJGeometricObject2D, double)
	 */
	public boolean almostEquals(GJGeometricObject2D obj, double eps) {
		if (this == obj)
			return true;
		
        if (!(obj instanceof GJPointSet2D))
            return false;
        
        GJPointSet2D set = (GJPointSet2D) obj;
        if (this.points.size() != set.size())
        	return false;

        Iterator<GJPoint2D> iter = set.iterator();
        for (GJPoint2D point : points) {
        	if (!point.almostEquals(iter.next(), eps))
        		return false;
        }
        
        return true;
	}
	
    // ===================================================================
    // methods overriding Object methods


    /**
     * Returns true if the given object is an instance of GJPointSet2D that
     * contains the same number of points, such that iteration on each set
     * returns equal points.
     */
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	
        if (!(obj instanceof GJPointSet2D))
            return false;
        
        GJPointSet2D set = (GJPointSet2D) obj;
        if (this.points.size() != set.size())
        	return false;
        
        Iterator<GJPoint2D> iter = set.iterator();
        for (GJPoint2D point : points) {
        	if (!point.equals(iter.next()))
        		return false;
        }
        
        return true;
    }
    
	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public GJPointArray2D clone() {
        GJPointArray2D set = new GJPointArray2D(this.size());
        for (GJPoint2D point : this)
            set.add(point);
        return set;
    }

}
