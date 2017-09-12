package menus;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import tables.Blank;
import tables.Table;

public class Settings extends Pane{

	private Screen s;
	private ListView<String> tables;
	private ListView<String> avail;
	private Map<String, Class<? extends Table>> availMap;
	
	private Node curShown;
	public Settings(Screen s) {
		System.out.println("Instantiating settings screen");
		this.s = s;
		Dimension d = s.getDimension();
		super.setPrefSize(d.getWidth(), d.getHeight());
		
		initAvailable();
		addBackground(d);
		addSelectors(d);
		
		
		
		
		
		
		Button close = close();
		close.relocate(d.getWidth() - 25, 0);
		this.getChildren().add(close);
		
	}

	private void initAvailable() {
		System.out.println("Filling table choices");
		avail = new ListView<>();
		availMap = new HashMap<>();
		availMap.put("Blank", Blank.class);
		
		for(String s : availMap.keySet())
			avail.getItems().add(s);
	}

	private void addSelectors(Dimension d) {
		GridPane selectors = new GridPane();
		tables = new ListView<>();
		tables.setPadding(new Insets(10));
		tables.setPrefSize(d.getWidth() * .3, d.getHeight() * .4);
		tables.setOnMouseClicked((e) -> {
			int index = tables.getSelectionModel().getSelectedIndex();
			if(index != -1){
				this.getChildren().remove(curShown);
				curShown = s.tables.get(index).settings();
				curShown.relocate(10 + tables.getParent().getLayoutX() + tables.getWidth(), tables.getParent().getLayoutY());
				s.tables.get(index).getName().addListener((f) -> refreshCurrentItems());
				this.getChildren().add(curShown);
			}
		});
		selectors.add(tables, 0, 0, 2, 5);
		
		selectors.add(remove(), 0, 6);
		selectors.add(add(), 0, 7);
		selectors.add(up(), 1, 6);
		selectors.add(down(), 1, 7);
		avail.setPadding(new Insets(10));
		avail.setPrefSize(d.getWidth() * .2, d.getHeight() * .4);
		selectors.add(avail, 0, 8, 2, 5);
		
		selectors.setHgap(10);
		selectors.setVgap(10);
		selectors.relocate(10, 10);
		this.getChildren().add(selectors);		
	}

	private Button add() {
		Button result = new Button("Add the selected table");
		result.setOnAction((e) -> {
			int index = avail.getSelectionModel().getSelectedIndex();
			if(index != -1){
				Class<? extends Table> type = availMap.get(avail.getItems().get(index));
				boolean added = false;
				int i = 0;
				while(!added && i < type.getConstructors().length){
					try {
						Table t = (Table) type.getConstructors()[i].newInstance(s.getDimension(), s);
						s.tables.add(t);
						s.getChildren().add(0, t);
						added = true;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | SecurityException e1) {
						System.out.println("Failed attempt " + ++i + " to instantiate type " + type);
						e1.printStackTrace();
					}
				}
				if(added)
					System.out.println("Succesfully added " + type);
				else
					System.out.println("Unable to add " + type);
				
			} else System.out.println("No item selected");
			refreshCurrentItems();
			tables.getSelectionModel().select(tables.getItems().size() - 1);
			tables.onMouseClickedProperty().getValue().handle(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 0, false, false, false, false, false, false, false, false, false, false, null));
			
		});
		return result;
	}

	private Button remove() {
		Button result = new Button("Remove the selected table");
		result.setOnAction((e) -> {
			int index = tables.getSelectionModel().getSelectedIndex();
			if(index != -1){
				System.out.println("Removing item at index " + index);
				s.tables.get(index).delete();
				s.getChildren().remove(s.tables.get(index));
				s.tables.remove(index);
				this.getChildren().remove(curShown);
				refreshCurrentItems();
				if(index != 0) tables.getSelectionModel().select(index - 1);
				else tables.getSelectionModel().select(0);
			} else 
				System.out.println("No item selected");
		
		});
		return result;
	}

	private Button up() {
		Button result = new Button("Move table up");
		result.setOnAction((e) -> {
			int index = tables.getSelectionModel().getSelectedIndex();
			if(index > 0 && index != -1){
				System.out.println("Moving table in index " + index + " up");
				Helper.shift(s.tables, index, false);
				refreshCurrentItems();
				tables.getSelectionModel().select(index - 1);
				index = s.tables.size() - index - 1;
				Helper.shift(s.getChildren(), index, true);
			} else 
				System.out.println("No table to move");
		});
		return result;
	}
	
	private Button down() {
		Button result = new Button("Move table down");
		result.setOnAction((e) -> {
			int index = tables.getSelectionModel().getSelectedIndex();
			if(index < s.tables.size() - 1 && index != -1){
				System.out.println("Moving table in index " + index + " down");
				Helper.shift(s.tables, index, true);
				refreshCurrentItems();
				tables.getSelectionModel().select(index + 1);
				index = s.tables.size() - index - 1;
				Helper.shift(s.getChildren(), index, false);
			} else
				System.out.println("No table to move");
		});			
		return result;			
	}

	private void addBackground(Dimension d) {
		Rectangle bg = new Rectangle(d.getWidth(), d.getHeight());
		bg.opacityProperty().set(.6);
		bg.setStyle("-fx-background-color: black;");
		this.getChildren().add(bg);		
	}

	private Button close() {
		Button result = new Button("X");
		result.setOnAction((e) -> {
			System.out.println("Closing settings");
			s.getChildren().remove(this);
			this.getChildren().remove(curShown);
			curShown = null;
		});
		return result;
	}

	public void open() {
		System.out.println("Opening Settings");
		s.getChildren().add(this);
		refreshCurrentItems();
	}

	private void refreshCurrentItems(){
		tables.getItems().clear();
		for(Table t : s.tables){
			tables.getItems().add(t.getName().get());
		}	
	}
}
