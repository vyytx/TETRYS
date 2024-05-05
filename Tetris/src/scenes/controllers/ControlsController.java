package scenes.controllers;

import java.util.ArrayList;

import boot.Main;
import game.References;
import game.tiles.Tetermino;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import scenes.Scenes;

public class ControlsController {

	@FXML
	Rectangle rightSpinKey;
	@FXML
	Rectangle leftSpinKey;
	@FXML
	Rectangle softFallKey;
	@FXML
	Rectangle hardFallKey;
	
	@FXML
	Canvas rightSpinCanvas;
	@FXML
	Canvas leftSpinCanvas;
	@FXML
	Canvas softFallCanvas;
	@FXML
	Canvas hardFallCanvas;
	
	@FXML
	public void initialize() {
		spinAnimation(false);
		spinAnimation(true);
		softFallAnimation();
		hardFallAnimation();
		
		for(Timeline t: timelines)
			t.play();
	}
	
	private ArrayList<Timeline> timelines = new ArrayList<Timeline>();
	
	private int TS = References.TILESIZE;
	
	private void spinAnimation(boolean counter) {
		Rectangle key = counter ? leftSpinKey : rightSpinKey;
		Canvas canvas = counter ? leftSpinCanvas : rightSpinCanvas;
		
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		Tetermino t = new Tetermino(5, 0);
		
		Timeline timeline = new Timeline(
			new KeyFrame(
				Duration.ZERO,
				new KeyValue(key.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(0.7),
				new KeyValue(key.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(0.8),
				new KeyValue(key.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(1),
				e -> {
					t.rotate(null, counter);
					for(int i = 0; i < 3; i++)
						for(int j = 0; j < 3; j++) {
							ctx.setFill(References.colorMap[t.data[i][j]]);
							ctx.fillRect(j*TS, i*TS, TS, TS);
						}
				}
			),
			new KeyFrame(
				Duration.seconds(1.2),
				new KeyValue(key.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(1.3),
				new KeyValue(key.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(2),
				new KeyValue(key.strokeProperty(), Paint.valueOf("000000"))
			)
		);
		
		timeline.setCycleCount(Animation.INDEFINITE);
		timelines.add(timeline);
	} 
	
	private void softFallAnimation() {
		GraphicsContext ctx = softFallCanvas.getGraphicsContext2D();
		Tetermino t = new Tetermino(5, 0);
		
		Runnable render = () -> {
			for(int i = 0; i < 20; i++)
				for(int j = 0; j < 3; j++) {
					if(t.getY() <= i && i < t.getY() + t.getHeight()) {
						int dy = i - t.getY();
						ctx.setFill(References.colorMap[t.data[dy][j]]);
					}else
						ctx.setFill(References.colorMap[0]);
					ctx.fillRect(j*TS, i*TS, TS, TS);
				}
		};
		
		Timeline timeline1 = new Timeline(
			new KeyFrame(
				Duration.ZERO,
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(0.7),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(0.8),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(1),
				e -> {
					render.run();
					t.setPos(t.getY()+1, 0);
				}
			),
			new KeyFrame(
				Duration.seconds(1.2),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(1.3),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(2),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			)
		);
		
		Timeline timeline2 = new Timeline(
			new KeyFrame(
				Duration.ZERO,
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(1.5),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(1.8),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("FF0000"))
			)
		);
		
		for(int sec = 0; sec < 9; sec++) {
			timeline2.getKeyFrames().add(new KeyFrame(
				Duration.seconds(sec / 2.0 + 2),
				e -> {
					render.run();
					t.setPos(t.getY()+1, 0);
				}
			));
		}
		
		timeline2.getKeyFrames().addAll(
			new KeyFrame(
				Duration.seconds(6.2),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(6.5),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(7),
				e -> {
					t.setPos(0, 0);
				},
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(8),
				new KeyValue(softFallKey.strokeProperty(), Paint.valueOf("000000"))
			)
		);
		
		
		timeline1.setCycleCount(9);
		timeline2.setOnFinished(e -> {
			timeline1.playFromStart();
		});
		
		timeline1.setOnFinished(e -> {
			t.setPos(0, 0);
			timeline2.playFromStart();
		});
		timelines.add(timeline1);
	}
	
	private void hardFallAnimation() {
		GraphicsContext ctx = hardFallCanvas.getGraphicsContext2D();
		Tetermino t = new Tetermino(5, 0);
		
		Runnable render = () -> {
			for(int i = 0; i < 10; i++)
				for(int j = 0; j < 3; j++) {
					ctx.setFill(
						(t.getY() <= i && i < t.getY() + t.getHeight() && i >= 0)
						? References.colorMap[t.data[i - t.getY()][j]]
						: References.colorMap[0]
					);
					ctx.fillRect(j*TS, i*TS, TS, TS);
				}
		};
		
		Timeline timeline = new Timeline(
			new KeyFrame(
				Duration.ZERO,
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(0.7),
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(0.8),
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(1),
				e -> {
					t.setPos(8, 0);
					render.run();
				}
			),
			new KeyFrame(
				Duration.seconds(1.2),
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("FF0000"))
			),
			new KeyFrame(
				Duration.seconds(1.3),
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(3),
				e -> {
					t.setPos(0, 0);
					render.run();
				},
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("000000"))
			),
			new KeyFrame(
				Duration.seconds(4),
				new KeyValue(hardFallKey.strokeProperty(), Paint.valueOf("000000"))
			)
		);
		
		timeline.setCycleCount(Animation.INDEFINITE);
		timelines.add(timeline);
	}
	
	public void backButtonClick() {
		Main.changeScene(Scenes.OPENING);
	}
}
