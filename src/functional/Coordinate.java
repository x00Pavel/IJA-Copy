/*
    Author: Oleksii Korniienko (xkorni02)

    File: src/functional/Coorinate.java
    Date: 04.2020
 */


package src.functional;

/**
 * Representation of coordinates on street map
 */
public class Coordinate {
	
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
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public int diffX(Coordinate c) {
		return this.getX() - c.getX();
	}

	public int diffY(Coordinate c) {
		return this.getY() - c.getY();
	}
	
	@Override
	public boolean equals(Object coord) {
		return (this.getX() == ((Coordinate) coord).getX()) && (this.getY() == ((Coordinate) coord).getY());
	}

	@Override 
	public int hashCode() { 
		final int prime = 21;
		return prime * this.X * this.Y;
	}
}
