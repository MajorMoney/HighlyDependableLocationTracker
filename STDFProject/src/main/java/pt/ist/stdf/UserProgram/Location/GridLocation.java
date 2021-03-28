package pt.ist.stdf.UserProgram.Location;

import java.awt.Point;

public class GridLocation implements Location{
	
	int x;
	int y;
	
	public GridLocation(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public String getCurrentLocation() {
		return null;
	}

}
