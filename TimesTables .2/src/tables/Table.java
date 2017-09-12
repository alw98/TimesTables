package tables;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import menus.Helper;
import menus.Screen;

public abstract class Table extends Canvas implements Editable{

	private Screen s;
	private ArrayList<Circle> circles;
	private ArrayList<Frame> frames;
	private ListView<String> circleView;
	private ComboBox<String> editableList;
	private ComboBox<String> propBox;
	private Accordion frameControl;
	private Node curShown;
	private StringProperty name;
	private GridPane settings;
	private GridPane timelineSettings;
	private GridPane timelineFields;
	private Point center;
	private Timeline t;
	private Map<String, Interpolator> interpMap;
	public Table(double width, double height, Screen s){

		super(width, height);
		this.s = s;
		name = new SimpleStringProperty();
		name.set(type());
		t = new Timeline();
		t.setAutoReverse(true);
		t.setCycleCount(Timeline.INDEFINITE);
		frames = new ArrayList<>();
		timelineSettings = null;
		
		interpMap = new HashMap<>();
		interpMap.put("Discrete", Interpolator.DISCRETE);
		interpMap.put("Ease in", Interpolator.EASE_IN);
		interpMap.put("Ease out", Interpolator.EASE_OUT);
		interpMap.put("Ease both", Interpolator.EASE_BOTH);
		interpMap.put("Linear", Interpolator.LINEAR);
		
		this.circles = new ArrayList<>();
		this.center = new Point(width / 2, height / 2);
		initSettings();
		refreshCurrentItems();


	}

	private void initSettings() {
		settings = new GridPane();
		settings.setHgap(10);
		settings.setVgap(10);

		Label title = new Label("Table settings");
		title.setTextFill(Color.ANTIQUEWHITE);
		settings.add(title, 0, 0);

		initCircleView();
		settings.add(circleView, 2, 0, 4, 5);

		frameControl = new Accordion();
		settings.add(frameControl, 6, 6, 2, 10);
		settings.add(add(), 2, 5);
		settings.add(remove(), 3, 5);
		settings.add(up(), 4, 5);
		settings.add(down(), 5, 5);
		TextField name = new TextField();
		name.setPromptText("Name");
		name.setOnAction((e) -> {
			this.getName().set(name.getText());
			name.setText("");
			this.refreshCurrentItems();
		});
		settings.add(name, 0, 1);


		InvalidationListener l = (e) -> attemptRefresh();
		center.getXProp().addListener(l);
		center.getYProp().addListener(l);
		Helper.addField(settings, 2, 0, "X", center.getXProp());
		Helper.addField(settings, 3, 0, "Y", center.getYProp());


	}

	private Node down() {
		Button result = new Button("v");
		result.setOnAction((e) -> {
			int index = circleView.getSelectionModel().getSelectedIndex();
			if(index < circles.size() - 1 && index != -1){
				System.out.println("Moving table at index " + index + " down");
				Helper.shift(circles, index, true);
				fullRefresh();
				refreshCurrentItems();
				circleView.getSelectionModel().select(index + 1);
			} else
				System.out.println("No table to move");
		});			
		return result;	
	}

	private Node up() {
		Button result = new Button("^");
		result.setOnAction((e) -> {
			int index = circleView.getSelectionModel().getSelectedIndex();
			if(index > 0){
				System.out.println("Moving table in index " + index + " up");
				Helper.shift(circles, index, false);
				fullRefresh();
				refreshCurrentItems();
				circleView.getSelectionModel().select(index - 1);

			} else 
				System.out.println("No table to move");
		});
		return result;
	}

	private Node remove() {
		Button result = new Button("Remove");
		result.setOnAction((e) -> {
			int index = circleView.getSelectionModel().getSelectedIndex();
			if(index != -1){
				System.out.println("Removing circle at index " + index);
				circles.get(index).delete();
				circles.remove(index);
				refreshCurrentItems();
				fullRefresh();
				if(index != 0) circleView.getSelectionModel().select(index - 1);
				else circleView.getSelectionModel().select(0);
			} else 
				System.out.println("No item selected");

		});
		return result;
	}

	private Button add() {
		Button result = new Button("New");
		result.setOnAction((e) -> newCircle());
		return result;
	}

	private void initCircleView() {
		circleView = new ListView<>();
		circleView.setPrefSize(this.getWidth() * .2, this.getHeight() * .2);
		circleView.setOnMouseClicked((e) -> {
			int index = circleView.getSelectionModel().getSelectedIndex();
			if(index != -1){
				this.settings().getChildren().remove(curShown);
				curShown = circles.get(index).settings();
				curShown.relocate(settings.getLayoutX(), settings.getLayoutY() + settings.getHeight() + 10);
				circles.get(index).getName().addListener((f) -> refreshCurrentItems());
				settings.add(curShown, 0, 6, 5, 1);
			}
		});		
	}

	private void initTimelineSettings(){
		this.settings.getChildren().remove(timelineSettings);
		timelineSettings = new GridPane();
		timelineSettings.setHgap(10);
		timelineSettings.setVgap(10);
		Label label = new Label("Timeline settings");
		label.setTextFill(Color.ANTIQUEWHITE);
		timelineSettings.add(label, 0, 0);

		Label oLabel = new Label("Object:");
		oLabel.setTextFill(Color.ANTIQUEWHITE);
		timelineSettings.add(oLabel, 0, 1);

		editableList = editableList();
		timelineSettings.add(editableList, 1, 1);


		Label pLabel = new Label("Property:");
		pLabel.setTextFill(Color.ANTIQUEWHITE);
		timelineSettings.add(pLabel, 2, 1);
		Button pause = new Button("Pause/Play");
		pause.setOnAction((e) -> {
			if(t.getCurrentRate() == 0){
				t.play();
			} else t.pause();
		});
		this.timelineSettings.add(pause, 4, 1);
		
		Button restart = new Button("Restart");
		restart.setOnAction((e) -> {
			t.stop();
			t.jumpTo(Duration.ZERO);
			t.play();
			t.stop();
		});
		this.timelineSettings.add(restart, 4, 2);


		this.settings.add(timelineSettings, 0, 8, 5, 1);
	}

	private ComboBox<String> editableList() {
		editableList = new ComboBox<>();
		editableList.getItems().add(this.getName().get());
		propBox = propBox();
		timelineSettings.add(propBox, 3, 1);
		editableList.setOnMouseClicked((e) -> refreshTimelineSettings());

		editableList.setOnAction((e) -> {
			propBox.getSelectionModel().clearSelection();
			int index = editableList.getSelectionModel().getSelectedIndex();
			if(index != -1 ){
				if(index == 0){
					propBox.getItems().setAll(this.getPropertyMap().keySet());
				} else{
					propBox.getItems().setAll(circles.get(index - 1).getPropertyMap().keySet());
				}
			}
		});
		return editableList;
	}

	private ComboBox<String> propBox() {
		ComboBox<String> propBox = new ComboBox<>();
		propBox.setOnMouseClicked((e) -> refreshTimelineSettings());
		propBox.setOnAction((e1) -> {
			int index = editableList.getSelectionModel().getSelectedIndex();
			String name = propBox.getItems().get(propBox.getSelectionModel().getSelectedIndex() == -1 ? 0 : propBox.getSelectionModel().getSelectedIndex());
			WritableValue<Number> val;
			Frame f;
			if(index != -1){
				if(index == 0){
					val = this.getPropertyMap().get(name);
					f = new Frame(val, name, this);
				} else {
					Circle c =  circles.get(index - 1);
					val = c.getPropertyMap().get(name);
					f = new Frame(val, name, c);
				}
				selectFrame(f);
			}	
		});
		return propBox;
	}

	private void selectFrame(Frame f) {
		refreshTimelineSettings();
		f.setOnMouseClicked((e) -> {
			selectFrame(f);
		});
		timelineFields = new GridPane();
		timelineFields.setVgap(10);
		timelineFields.setHgap(10);
		int index;
		if(f.getOwner() == this){
			index = 0;
		}else{
			index = circles.indexOf(f.getOwner()) + 1;
		}
		editableList.getSelectionModel().select(index);

		index = propBox.getItems().indexOf(f.name);
		if(index != -1){
			propBox.getSelectionModel().select(index);
		}
		Helper.addField(timelineFields, 0, 0, "Duration:", f.getDur());
		Helper.addField(timelineFields, 0, 2, "Value:", f.getVal());
		ComboBox<String> interps = new ComboBox<>();
		interps.getItems().addAll(interpMap.keySet());
		interps.setOnAction((e) -> f.setInterpolator(interpMap.get(interps.getItems().get(interps.getSelectionModel().getSelectedIndex()))));
		for(Entry<String, Interpolator> e : interpMap.entrySet()){
			if(e.getValue().equals(f.interpolator()))
				interps.getSelectionModel().select(interps.getItems().indexOf(e.getKey()));
		}
		
		timelineFields.add(interps, 0, 2);
		
		Button add = new Button("Add keyframe");
		add.setOnAction((e) -> {
			addFrame(f);
			selectFrame(new Frame(f.getProp(), f.name(), f.getOwner()));
		});
		timelineFields.add(add, 0, 1);
		Button remove = new Button("Remove keyframe");
		remove.setOnAction((e) -> removeFrame(f));
		timelineFields.add(remove, 1, 1);
		
		System.out.println(timelineSettings.getChildren().contains(timelineFields));
		refreshTimelineSettings();
		timelineSettings.add(timelineFields, 0, 2, 4, 3);

	}
	
	private void refreshTimelineSettings(){
		System.out.println("Refreshing");
		if(timelineSettings.getChildren().contains(timelineFields))
			timelineSettings.getChildren().remove(timelineFields);
	}

	private void removeFrame(Frame f) {
		this.frames.remove(f);
		updateTimeline();
	}

	private void addFrame(Frame f) {
		if(!this.frames.contains(f))
			this.frames.add(f);
		updateTimeline();
	}

	private void updateTimeline() {
		Collections.sort(frames);
		frameControl.getPanes().clear();
		t.stop();
		t.getKeyFrames().clear();
		ListView<Frame> cur = new ListView<>();
		TitledPane pane = new TitledPane("Initial", cur);
		frameControl.getPanes().add(pane);
		int index = 0;
		double last = 0;
		List<KeyValue> vals = new ArrayList<>();
		while(index < frames.size() && frames.get(index).getDur().get() == last){
			Frame f = frames.get(index++);
			vals.add(new KeyValue(f.prop, f.val.get(), f.interpolator()));
			cur.getItems().add(f);
		}
		
		while(index < frames.size()){
			Frame f = frames.get(index++);
			if(f.getDur().get() != last){
				KeyFrame kf = new KeyFrame(Duration.millis(last), null, null, vals);
				t.getKeyFrames().add(kf);
				vals.clear();
				cur = new ListView<>();
				pane = new TitledPane(String.format("%fms - %fms", last, f.getDur().get()), cur);
				frameControl.getPanes().add(pane);
				last = f.getDur().get();
			}
			vals.add(new KeyValue(f.prop, f.val.get(), f.interpolator()));
			cur.getItems().add(f);
		}
		KeyFrame kf = new KeyFrame(Duration.millis(last), null, null, vals);
		t.getKeyFrames().add(kf);
		System.out.println(t.getKeyFrames());
		
	}

	public Table(Dimension d, Screen s){
		this(d.getWidth(), d.getHeight(), s);
	}

	public abstract String type();

	public Screen getScreen(){ return s;}

	public ArrayList<Circle> getCircles(){ return circles; }
	public void newCircle(){ 
		Circle c = new Circle(this);
		c.addListener((e1) -> this.attemptRefresh());
		this.circles.add(c);
		this.refreshCurrentItems();

		circleView.getSelectionModel().select(circles.size() - 1);	
		circleView.onMouseClickedProperty().getValue().handle(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 0, false, false, false, false, false, false, false, false, false, false, null));
	}
	public void delete() {
		for(Circle c : circles)
			c.delete();
	}

	private void refreshCurrentItems() {
		initTimelineSettings();
		circleView.getItems().clear();
		this.editableList.getItems().clear();
		this.editableList.getItems().add(this.getName().get());
		for(Circle c : this.circles){
			circleView.getItems().add(c.getName().get());
			this.editableList.getItems().add(c.getName().get());
		}
	}

	private Duration d = Duration.millis(0);
	private void attemptRefresh() {
		Duration curTime = t.getCurrentTime();

		//fullRefresh();
		if(curTime.toMillis() == 0 || t.currentRateProperty().get() == 0 //anim is paused
				|| (curTime.greaterThan(d) && t.currentRateProperty().get() < 0) //we started reversing
				|| (curTime.lessThan(d) && t.currentRateProperty().get() > 0)//we stopped reversing
				|| Math.abs(curTime.subtract(d).toMillis()) > t.getTargetFramerate() / 2){//we've elapsed our frame rate duration
			d = new Duration(curTime.toMillis());
			fullRefresh();
		}		
	}

	private void fullRefresh() {
		this.getGraphicsContext2D().clearRect(0, 0, this.getWidth(), this.getHeight());
		for(int i = circles.size() - 1; i >= 0; i--){
			circles.get(i).refresh();
		}
	}

	public StringProperty getName(){ return name; }

	public String toString(){ return getName().get(); }
	
	public Point getCenter(){ return center; }

	public Pane settings() { return settings; }

	public Timeline getTimeline() { return t; }
}
