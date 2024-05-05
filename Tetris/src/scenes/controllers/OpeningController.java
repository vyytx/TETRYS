package scenes.controllers;

import boot.Main;
import scenes.Scenes;

public class OpeningController {
	
	public void singlePlay() {
		Main.changeScene(Scenes.SINGLEPLAYER);
	}

	public void controls() {
		Main.changeScene(Scenes.CONTORLS);
	}

	public void quit() {
		Main.quit();
	}

}
