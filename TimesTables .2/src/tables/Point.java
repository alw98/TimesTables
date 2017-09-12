package tables;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Point {
	DoubleProperty x;
	DoubleProperty y;
	DoubleProperty value;

	public Point(double x, double y) { this(x, y, -1); }
	public Point(double x, double y, double value){
		this.x = new SimpleDoubleProperty();
		this.y = new SimpleDoubleProperty();
		this.value = new SimpleDoubleProperty();

		this.x.set(x);
		this.y.set(y);
		this.value.set(value);
	}

	public double getX(){ return x.doubleValue(); }
	public double getY(){ return y.doubleValue(); }
	public double getValue(){ return value.doubleValue(); }
	public DoubleProperty getXProp(){ return x; }
	public DoubleProperty getYProp(){ return y; }
	public DoubleProperty getValueProp(){ return value; }
	
	//Returns the value being replaced
	public double setX(double value){ 
		double result = x.doubleValue();
		x.set(value);
		return result;
	}

	//Returns the value being replaced
	public double setY(double value){ 
		double result = y.doubleValue();
		y.set(value);
		return result;
	}

	//Returns the value being replaced
	public double setValue(double value){ 
		double result = this.value.doubleValue();
		this.value.set(value);
		return result;
	}
	
	public String toString(){
		String result = String.format("Point[X: %f, Y: %f, VALUE: %f]", x.get(), y.get(), value.get());
		return result;
		
	}


}
