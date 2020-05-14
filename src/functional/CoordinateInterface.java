
package src.functional;

/**
 * Represents the position (coordinates) on the map. 
 * The coordinates are a pair (x, y), the beginning of the map is always at position (0,0). 
 * You cannot have a position with a negative coordinate.
 * 
 * @author koci
 */
public interface CoordinateInterface {

    /**
	 * Get X coordinate
	 * 
	 * @return			X coordinate
	 */
    public int getX();
    
    /**
	 * Get Y coordinate
	 * 
	 * @return			Y coordinate
	 */
    public int getY();
    
    /**
	 * Difference of two X coordinates
	 * 
	 * @return			Difference of two X coordinates
	 */
    public int diffX(Coordinate c);
    
    /**
	 * Difference of two Y coordinates
	 * 
	 * @return			Difference of two Y coordinates
	 */
    public int diffY(Coordinate c);
    
}
