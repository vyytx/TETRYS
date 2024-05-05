package scenes.controllers;

import boot.Main;
import game.SinglePlayerGame;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import scenes.Scenes;

public class SinglePlayerController {
	
	boolean hasStarted = false;
	
	@FXML
	Canvas gameCanvas;
	
	@FXML
	public VBox startGroup;
	@FXML
	VBox pauseGroup;
	@FXML
	VBox failGroup;
	
	@FXML
	public Label specScore;
	
	@FXML
	public Label scoreLabel;
	@FXML
	public Label levelLabel;
	@FXML
	public Label clearLabel;
	
	Scene scene;
	SinglePlayerGame game;
	
	private SinglePlayerController instance;
    
	@FXML
	protected void initialize() {
		instance = this;
	}
	
	public void startButtonClick() {
		startGroup.setVisible(false);
    	gameCanvas.requestFocus();
    	hasStarted = true;
    	game = new SinglePlayerGame(gameCanvas, instance);
    	game.start();
    }
	
	public void resumeButtonClick() {
		pauseGroup.setVisible(false);
    	gameCanvas.requestFocus();
    	game.resume();
    }
	
	public void quitButtonClick() {
		Main.changeScene(Scenes.OPENING);
		hasStarted = false;
		pauseGroup.setVisible(false);
		failGroup.setVisible(false);
		game.init();
		game.clearCanvas();
		startGroup.setVisible(true);
	}
	
	public void restartButtonClick() {
		failGroup.setVisible(false);
		gameCanvas.requestFocus();
		game.restart();
	}
	
	public void showPauseGroup() {
		pauseGroup.setVisible(true);
	}
	
	public void showFailGroup() {
		failGroup.setVisible(true);
	}
	
}
