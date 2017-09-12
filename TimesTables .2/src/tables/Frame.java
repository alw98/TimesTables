package tables;

import javafx.animation.Interpolator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.scene.control.Label;

public class Frame extends Label implements Comparable<Frame>{
	WritableValue<Number> prop;
	Object owner;
	DoubleProperty dur = new SimpleDoubleProperty();
	DoubleProperty val = new SimpleDoubleProperty();
	Interpolator interp;
	String name;
	public Frame(WritableValue<Number> prop, String name, Object owner){
		this.owner = owner;
		this.prop = prop;
		this.name = name;
		interp = Interpolator.DISCRETE;
		InvalidationListener l = (e) -> refreshText();
		this.val.addListener(l);
		this.dur.addListener(l);
		refreshText();
	}
	
	private void refreshText() {
		this.setText(this.toString());
	}

	public String toString(){ return String.format("%s: %.3f, ends at %.0f seconds, owned by " + owner, 
			this.name, this.val.get(), this.dur.get()); }
	public String name(){ return name; }
	public DoubleProperty getDur(){ return dur; }
	
	public DoubleProperty getVal(){ return this.val; }
	
	public WritableValue<Number> getProp(){ return prop; }
	public void setProp(WritableValue<Number> prop){ this.prop = prop; }
	@Override
	public int compareTo(Frame f) {
		return (int) (this.getDur().subtract(f.getDur())).doubleValue();
	}
	
	public Object getOwner(){ return owner; }

	public Interpolator interpolator() {
		return interp;
	}
	
	public void setInterpolator(Interpolator interp){ this.interp = interp; }


	
}
