package menus;

import tables.*;

import java.awt.Dimension;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Screen extends Pane {

	ArrayList<Table> tables;
	Settings settings;
	Dimension d;
	
	public Screen(double width, double height){
		System.out.println("Creating screen");
		super.setPrefSize(width, height);
		getPrefDimension(width, height);
		start();
		System.out.println("Done initializing.\n");
	}

	private void getPrefDimension(double width, double height) {
		//TODO make an screen for user to choose these
		width *= .99;
		height *= .96;
		d = new Dimension((int) width, (int) height);
		System.out.printf("Dimensions got. %.0fx%.0f\n", width, height);
	}

	private void start() {
		instantiateVars();
		this.getChildren().addAll(0, tables);
		
	}

	public void remove(Table t){
		if(this.tables.contains(t)){
			t.delete();
			
		}
	}
	private void instantiateVars() {
		System.out.println("Instantiating variables");
		tables = new ArrayList<>();
		
		double radius = 25;
		
		Region r = new Region();
		Label txt = new Label("Settings");
		txt.relocate(d.getWidth() - radius * 2, 0);
		r.relocate(d.getWidth() - radius * 2, 0);
		r.setOnMouseClicked((obs) -> settings.open());
		r.setPrefSize(radius * 2, radius * 2);
		this.getChildren().add(txt);
		this.getChildren().add(r);
		
		this.settings = new Settings(this);
	}

	public Dimension getDimension(){ return d; }
}
