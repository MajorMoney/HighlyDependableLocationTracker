package pt.ist.stdf.HighlyDependableTracker.ServerProgram;

import com.google.gson.JsonArray;

public class Position {

	public Integer[] position = new Integer[2];
	public int x;
	public int y;
	
	public Position(int x,int y) {
		this.x=x;
		this.y=y;
		position[0]=x;
		position[1]=y;
	}
	
	public Position(JsonArray jsonArray)
	{
		this.x=jsonArray.get(0).getAsInt();
		this.y=jsonArray.get(1).getAsInt();
		position[0]=x;
		position[1]=y;
	}
	
	public Integer[] getArray()
	{
		return position;
	}
	
	public boolean equals(Position pos)
	{
		if(pos.x == this.x)
			if(pos.y==this.y)
				return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "{ "+x+" , "+y+"}";
	}
}
