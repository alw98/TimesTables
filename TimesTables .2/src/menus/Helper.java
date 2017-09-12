package menus;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Helper {
	
	public static void addField(GridPane result, int row, int col, String tag, DoubleProperty prop) {
		Label label = new Label(String.format("%.3f", prop.get()));
		label.setTextFill(Color.ANTIQUEWHITE);
		prop.addListener((e) -> label.setText(String.format("%.3f", prop.get())));
		DoubleField field = new DoubleField(tag);
		connect(field, prop);
		result.add(field, col, row);
		result.add(label, col + 1, row);
	}
	
	public static void addField(GridPane result, int row, int col, String tag, IntegerProperty prop) {
		Label label = new Label(prop.get() + "");
		label.setTextFill(Color.ANTIQUEWHITE);
		prop.addListener((e) -> label.setText(String.format("%d", prop.get())));
		DoubleField field = new DoubleField(tag);
		connect(field, prop);
		result.add(field, col, row);
		result.add(label, col + 1, row);		
	}

	public static void connect(DoubleField field, IntegerProperty prop) {
		field.setOnKeyPressed((KeyEvent e) -> {
			if(e.getCode().equals(KeyCode.ENTER)){
				try{
					int val = (int) (double) Double.parseDouble(field.getText());
					prop.set(val);
					field.clear();
				} catch(Exception e1){
					System.out.println("Cannot parse.");
				}
			}
		});		
	}
	
	public static void connect(DoubleField field, DoubleProperty prop) {
		field.setOnKeyPressed((KeyEvent e) -> {
			if(e.getCode().equals(KeyCode.ENTER)){
				if(!field.getText().isEmpty()){
					try{
					double val = Double.parseDouble(field.getText());
					prop.set(val);
					field.clear();
					} catch(Exception e1){
						System.out.println("Cannot parse.");
					}
				}
			}
		});		
	}
	
	public static <T> void shift(List<T> list, int index, boolean up){
		if(up){
			if(index >= list.size() - 1 || index < 0) throw new IllegalArgumentException("Invalid index");
			T tmp = list.remove(index);
			list.add(index + 1, tmp);
		} else{
			if(index < 1 || index >= list.size()) throw new IllegalArgumentException("Invalid index");
			T tmp = list.remove(index - 1);
			list.add(index, tmp);
		}
	}
	
	public static Color randomColor(){
		return Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
	}
}
