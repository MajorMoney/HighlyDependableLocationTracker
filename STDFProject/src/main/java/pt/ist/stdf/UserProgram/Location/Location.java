package pt.ist.stdf.UserProgram.Location;

import com.google.gson.JsonArray;

public interface Location {
	
	public String getCurrentLocation();
	public JsonArray getCurrentLocationAsJsonArray();

}
