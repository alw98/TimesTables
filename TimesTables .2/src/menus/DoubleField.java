package menus;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import com.sun.javafx.binding.StringFormatter;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class DoubleField extends TextField {

	final DecimalFormat format = new DecimalFormat("00.###E0");

	public DoubleField(){
		this("Enter a double.");
	}

	public DoubleField(String string) {
		this.setPromptText(string);
//		this.setOnAction((e) -> {
//			try {
//				System.out.println(format.parse(this.getText()));
//			
//			} catch (ParseException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		});
		//		this.setTextFormatter(new TextFormatter<>(c -> {
		//			if(c.getControlNewText().isEmpty())
		//				return c;
		//			ParsePosition pos = new ParsePosition(0);
		//			Object o = format.parse(c.getControlNewText(), pos);
		//			if(o == null || pos.getIndex() < c.getControlNewText().length())
		//				return null;
		//			else
		//				return c;
		//		}));	
	}


}
