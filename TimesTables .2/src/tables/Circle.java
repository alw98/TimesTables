package tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import menus.Helper;

public class Circle implements ObservableValue<Circle>, Editable {

	Table table;
	Timeline t;
	ArrayList<InvalidationListener> listeners;
	Map<String, WritableValue<Number>> propertyMap;
	BooleanProperty radians;
	DoubleProperty multiplier;
	DoubleProperty ticks;
	DoubleProperty resolution;
	DoubleProperty opacity;
	DoubleProperty rotation;
	DoubleProperty radius;
	DoubleProperty refRotation;
	DoubleProperty distance;
	IntegerProperty red;
	IntegerProperty blue;
	IntegerProperty green;
	BooleanProperty drawCircle;
	BooleanProperty drawTicks;
	Point center;
	private StringProperty name;
	private GridPane settings;
	public Circle(Table t){
		this.table = t;
		name = new SimpleStringProperty();
		name.set("Circle");
		initVars();
		initSettings();
		this.refresh();
	}

	private void initVars() {
		t = table.getTimeline();
		listeners = new ArrayList<>();
		
		radians = new SimpleBooleanProperty();
		this.multiplier = new SimpleDoubleProperty();
		this.ticks = new SimpleDoubleProperty();
		this.resolution = new SimpleDoubleProperty();
		this.rotation = new SimpleDoubleProperty();
		this.opacity = new SimpleDoubleProperty();
		this.radius = new SimpleDoubleProperty();
		this.distance = new SimpleDoubleProperty();
		this.refRotation = new SimpleDoubleProperty();
		this.red = new SimpleIntegerProperty();
		this.green = new SimpleIntegerProperty();
		this.blue = new SimpleIntegerProperty();
		this.drawTicks = new SimpleBooleanProperty();
		this.drawCircle = new SimpleBooleanProperty();
		
		this.propertyMap = new TreeMap<>();
		this.propertyMap.put("Multiplier", multiplier);
		this.propertyMap.put("Number of ticks", ticks);
		this.propertyMap.put("Resolution of ticks", resolution);
		this.propertyMap.put("Absolute rotation", rotation);
		this.propertyMap.put("Relative rotation", refRotation);
		this.propertyMap.put("Distance from reference", distance);
		this.propertyMap.put("Opacity", opacity);
		this.propertyMap.put("Radius", radius);
		this.propertyMap.put("Red", red);
		this.propertyMap.put("Green", green);
		this.propertyMap.put("Blue", blue);
		
		
		d = new Duration(t.getCurrentTime().toMillis());

		this.ticks.set(200);
		this.resolution.set(1);
		this.rotation.set(0);
		this.radius.set(Math.min(table.getHeight(), table.getWidth()) / 2);
		this.opacity.set(1);
		this.red.set(0);
		this.green.set(0);
		this.blue.set(0);
		this.drawTicks.set(false);
		this.drawCircle.set(false);		

		InvalidationListener l = (e) -> {
			attemptRefresh();
			e.toString();//You have to do something with the variable?? it freezes otherwise
		};
		radius.addListener(l);
		distance.addListener(l);
		refRotation.addListener(l);
		

		
		this.ticks.addListener(l);
		this.resolution.addListener(l);
		this.multiplier.addListener(l);
		this.rotation.addListener(l);
		this.opacity.addListener(l);
		this.drawTicks.addListener(l);
		this.drawCircle.addListener(l);

		red.addListener((obs, oldNum, newNum) -> {
			if((int) newNum > 255)
				red.set((int)(double)oldNum);
			else attemptRefresh();
		});
		
		green.addListener((obs, oldNum, newNum) -> {
			if((int) newNum > 255)
				green.set((int)(double)oldNum);
			else attemptRefresh();
		});
		
		blue.addListener((obs, oldNum, newNum) -> {
			if((int) newNum > 255){
				blue.set((int)(double)oldNum);
			}
			else attemptRefresh();
		});

		center = this.getCenter();
	}

	private void initSettings() {
		settings = new GridPane();
		settings.setVgap(10);
		settings.setHgap(10);
		Label title = new Label("Circle settings");
		title.setTextFill(Color.ANTIQUEWHITE);
		settings.add(title, 0, 0);
	
		TextField name = new TextField();
		name.setPromptText("Name");
		name.setOnAction((e) -> {
			this.getName().set(name.getText());
			name.setText("");
		});
		settings.add(name, 0, 1);
		Helper.addField(settings, 2, 0, "Multiplier", multiplier);
		Helper.addField(settings, 3, 0, "Number of ticks", ticks);
		Helper.addField(settings, 4, 0, "Resolution of ticks", resolution);
		Helper.addField(settings, 5, 0, "Radius", radius);
		Helper.addField(settings, 6, 0, "Rotation", rotation);
		Helper.addField(settings, 7, 0, "Distance from reference", distance);
		Helper.addField(settings, 8, 0, "Angle from reference", refRotation);
		CheckBox radians = new CheckBox("Angle in radians");
		radians.setTextFill(Color.ANTIQUEWHITE);
		radians.selectedProperty().bindBidirectional(this.radians);
		settings.add(radians, 0, 9);
		CheckBox drawCircle = new CheckBox("Draw outer circle");
		drawCircle.setTextFill(Color.ANTIQUEWHITE);
		drawCircle.selectedProperty().bindBidirectional(this.drawCircle);
		settings.add(drawCircle, 0, 10);
		CheckBox drawTicks = new CheckBox("Draw tick marks");
		drawTicks.setTextFill(Color.ANTIQUEWHITE);
		drawTicks.selectedProperty().bindBidirectional(this.drawTicks);
		settings.add(drawTicks, 0, 11);
		Helper.addField(settings, 1, 2, "Red", this.red);
		Helper.addField(settings, 2, 2, "Green", this.green);
		Helper.addField(settings, 3, 2, "Blue", this.blue);
		Helper.addField(settings, 4, 2, "Opacity", this.opacity);
	}

	private Duration d;
	private void attemptRefresh() {
		for(InvalidationListener li : listeners)
			li.invalidated(this);
//		Duration curTime = t.getCurrentTime();
//
//		//		refresh();
//		if(curTime.toMillis() == 0 || t.currentRateProperty().get() == 0 //anim is paused
//				|| (curTime.greaterThan(d) && t.currentRateProperty().get() < 0) //we started reversing
//				|| (curTime.lessThan(d) && t.currentRateProperty().get() > 0)//we stopped reversing
//				|| Math.abs(curTime.subtract(d).toMillis()) > t.getTargetFramerate() / 2){//we've elapsed our frame rate duration
//			d = new Duration(curTime.toMillis());
//			
//		}		
	}

	public void refresh() {
		//System.out.println("Refreshing");
		GraphicsContext gc = table.getGraphicsContext2D();
		gc.setGlobalAlpha(opacity.get());
		gc.setStroke(this.getColor());
		this.center = this.getCenter();
		//gc.clearRect(center.getX() - radius.get(), center.getY() - radius.get(), 2 * radius.get(), 2 * radius.get());
		drawCircle(gc);
		drawLines(gc);
	}

	public void delete() {
		t.stop();
		t = null;
		listeners.clear();
		radians.unbind();
		multiplier.unbind();
		ticks.unbind();
		resolution.unbind();
		opacity.unbind();
		rotation.unbind();
		radius.unbind();
		refRotation.unbind();
		distance.unbind();
		red.unbind();
		blue.unbind();
		green.unbind();
		drawCircle.unbind();
		drawTicks.unbind();
		
	}

	private void drawLines(GraphicsContext gc) {
		double x;
		double oldX;
		double y;
		double oldY;
		double radPer = 2 * Math.PI / ticks.get();
		double rotation = radians.get() ? this.rotation.get() : Math.toRadians(this.rotation.get());
		for(double i = 0; i < ticks.get(); i += resolution.get()){
			oldX = Math.cos(i * radPer + rotation) * radius.get() + center.getX();
			oldY = Math.sin(i * radPer + rotation) * radius.get() + center.getY();
			x = Math.cos(i * multiplier.get() * radPer + rotation) * radius.get() + center.getX();
			y = Math.sin(i * multiplier.get() * radPer + rotation) * radius.get() + center.getY();
			gc.strokeLine(oldX, oldY, x, y);
		}
	}

	private void drawCircle(GraphicsContext gc) {
		double radPer = 2 * Math.PI / ticks.get();
		if(drawCircle.get())
			gc.strokeOval(center.getX() - radius.get(), center.getY() - radius.get(),
					2 * radius.get(), 2 * radius.get());
		double x;
		double y;
		double rotation = radians.get() ? this.rotation.get() : Math.toRadians(this.rotation.get());
		if(drawTicks.get())
			for(double i = 0; i < ticks.get(); i += resolution.get()){
				x = Math.cos(i * radPer + rotation) * radius.get() + center.getX();
				y = Math.sin(i * radPer + rotation) * radius.get() + center.getY();
				drawTick(x, y, gc);
			}			
	}

	private void drawTick(double x, double y, GraphicsContext gc) {
		double size = 8.0;
		gc.strokeOval(x - size / 2, y - size / 2, size, size);		
	}

	private Paint getColor() { return Color.rgb(red.get(), green.get(), blue.get()); }

	public Point getCenter() {
		double rotation = radians.get() ? this.refRotation.get() : Math.toRadians(this.refRotation.get());
		double y = Math.sin(rotation) * distance.get() + table.getCenter().getY();
		double x = Math.cos(rotation) * distance.get() + table.getCenter().getX();
		return new Point(x, y);
	}

	public Node settings() { return settings; }

	public StringProperty getName(){ return this.name; }

	@Override
	public void addListener(InvalidationListener listener) { this.listeners.add(listener); }

	@Override
	public void removeListener(InvalidationListener listener) { this.listeners.remove(listener); }

	@Override
	public void addListener(ChangeListener<? super Circle> listener) { throw new UnsupportedOperationException("Change listener not implemented"); }

	@Override
	public void removeListener(ChangeListener<? super Circle> listener) { throw new UnsupportedOperationException("Change listener not implemented"); }

	@Override
	public Circle getValue() { return this; }

	@Override
	public Map<String, WritableValue<Number>> getPropertyMap() { return propertyMap; }

	public String toString(){ return this.getName().get(); }

}
