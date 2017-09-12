package application;


import menus.Screen;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	Stage stage;
	Screen screen;
	@Override
	public void start(Stage primaryStage) {
		System.out.println("Initializing");
		stage = primaryStage;
		primaryStage.show();
		screen = new Screen(stage.getWidth(), stage.getHeight());

		Scene scene = new Scene(screen);
	    primaryStage.setScene(scene);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
