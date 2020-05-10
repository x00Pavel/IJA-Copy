/**
 * File: ija/src/functional/Coordinate.java
 * 
 * Author: Pavel Yadlouski (xyadlo00)
 *         Oleksii Korniienko (xkorni02)
 * 
 * Date: 04.2020
 * 
 * Description: Implementation of Coordinate object and its functionality
 */

package src.functional;

public class Coordinate implements CoordinateInterface {
	
	private int X;
	private int Y;
	
	public Coordinate(int i, int j) {
		this.X = i;
		this.Y = j;
	}
	
	public static Coordinate create(int i, int j) {
		Coordinate coordinates = new Coordinate(i, j);
		if ((i >= 0) && (j >= 0)) {		
			return coordinates;
		}else {
			return null;
		}
	}
	
	@Override
	public int getX() {
		return this.X;
	}
	
	@Override
	public int getY() {
		return this.Y;
	}
	
	@Override
	public int diffX(Coordinate c) {
		return this.getX() - c.getX();
	}

	@Override
	public int diffY(Coordinate c) {
		return this.getY() - c.getY();
	}
	
	@Override
	public boolean equals(Object coord) {
		if((this.getX() == ((Coordinate) coord).getX()) && (this.getY() == ((Coordinate) coord).getY())) {
			return true;
		}else {
			return false;
		}
	}

	@Override 
	public int hashCode() { 
		final int prime = 21;
		int result = prime * this.X * this.Y;
		return result;
	}
}
