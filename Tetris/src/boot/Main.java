package boot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scenes.Scenes;

public class Main extends Application {
	
	private static Stage currStage;
	private static Scene currScene;
	
	public static Scene getCurrScene() {
		return currScene;
	}
	
    public static void changeScene(Scene newScene) {
    	currStage.hide();
    	
    	currStage.setScene(newScene);
    	currScene = newScene;
    	
    	currStage.sizeToScene();
    	currStage.show();
    	
    	currScene.getRoot().requestFocus();
    }
	
	public static void main(String args[]) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		currStage = primaryStage;
		
		currStage.setForceIntegerRenderScale(true);
		currStage.setResizable(false);
		currStage.setTitle("TETRYS");
		
		currStage.setScene(Scenes.OPENING);
		Scenes.OPENING.getRoot().requestFocus();
		
		currStage.show();
	}
	
	public static void quit() {
		currStage.close();
	}
	
	
	
}
