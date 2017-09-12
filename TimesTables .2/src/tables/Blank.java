package tables;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.value.WritableValue;
import menus.Screen;

public class Blank extends Table {

	public Blank(Dimension d, Screen s) {
		super(d, s);
		// TODO Auto-generated constructor stub
	}
	
	public Blank(double width, double height, Screen s){
		super(width, height, s);
		
	}

	@Override
	public String type() { return "Blank"; }

	@Override
	public Map<String, WritableValue<Number>> getPropertyMap() {
		Map<String, WritableValue<Number>> result = new TreeMap<>();
		result.put("X", this.getCenter().getXProp());
		result.put("Y", this.getCenter().getYProp());
		return result;
	}

}
