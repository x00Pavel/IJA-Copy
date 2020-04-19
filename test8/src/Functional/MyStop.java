package Functional;

public class MyStop implements Stop {

	private String myStopName;
	private Coordinate myStopCoords;
	private Street myStreet;
	
	public MyStop(String stopName) {
		this.myStopName = stopName;
	}
	
	public MyStop(String stopName, Coordinate stopCoords) {
		this.myStopName = stopName;
		this.myStopCoords = stopCoords;
	}

	@Override
	public String getId() {
		return this.myStopName;
	}

	@Override
	public Coordinate getCoordinate() {
		return this.myStopCoords;
	}

	@Override
	public void setStreet(Street s) {
		this.myStreet = s;
	}

	@Override
	public Street getStreet() {
		return this.myStreet;
	}
	
	@Override
	public boolean equals(Object stop) {
		if(this.getId().equals(((Stop) stop).getId())) {
			return true;
		}else {
			return false;
		}	
	}
	
	@Override
	public String toString() {
		return ("stop(" + this.getId() + ")");
	}
}
