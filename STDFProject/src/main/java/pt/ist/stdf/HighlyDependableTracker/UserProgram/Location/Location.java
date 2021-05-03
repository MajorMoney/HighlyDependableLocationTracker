package pt.ist.stdf.HighlyDependableTracker.UserProgram.Location;

import com.google.gson.JsonArray;

public interface Location {
	
	public String getCurrentLocation();
	public JsonArray getCurrentLocationAsJsonArray();

}
