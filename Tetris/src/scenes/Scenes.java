package scenes;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class Scenes {
	
	public static Scene newScene(String name) {		
		FXMLLoader loader = new FXMLLoader(Scenes.class.getResource(name + "Scene.fxml"));
		try {
			Scene s = new Scene(loader.load());
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scene OPENING = newScene("Opening");
	public static Scene SINGLEPLAYER = newScene("SinglePlayer");
	public static Scene CONTORLS = newScene("Controls");
}
