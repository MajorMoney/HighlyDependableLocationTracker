package pt.ist.stdf.UserProgram.Location;

import com.google.gson.JsonArray;

public class GridLocation implements Location{
	
	private int x;
	private int y;
	private StringBuilder outputLocation;
	
	public GridLocation(int x, int y) {
		this.x=x;
		this.y=y;
		this.outputLocation = new StringBuilder();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setPosition(int x, int y) {
		this.x=x;
		this.y=y;
	}

	public String getCurrentLocation() {
		outputLocation.setLength(0);
		outputLocation.append("X:");
		outputLocation.append(this.x);
		outputLocation.append(" Y:");
		outputLocation.append(this.y);
		return outputLocation.toString();
		
	}
	public JsonArray getCurrentLocationAsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(this.x);
		jsonArray.add(this.y);
		return jsonArray;
		
	}
	
//	public static void main (String args[]) {
//		GridLocation teste = new GridLocation(3,3);
//		System.out.println(teste.getCurrentLocation());
//	}

}
