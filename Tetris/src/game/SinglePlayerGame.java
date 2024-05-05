package game;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;

import game.References.SpecialScoring;
import game.tiles.FallingTiles;
import game.tiles.Tetermino;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import scenes.controllers.SinglePlayerController;

class TeterminoHelper {
	private static Random random = new Random();
	private static OfInt oi = random.ints().iterator();
	private static int chosenType;
	
	private static LinkedList<Tetermino> following;
	
	public static void init() {
		chosenType = 0;
		following = new LinkedList<Tetermino>();
		for(int i = 0; i < 2; i++)
			following.push(nextTetermino());
	}
	
	public static Tetermino get(int i) {
		return following.get(i);
	}
	
	private static Tetermino nextTetermino() {
		int u = 7-Integer.bitCount(chosenType);
		if(u == 0) {
			chosenType = 0;
			u = 7;
		}
		
		int n = (oi.nextInt()%u + u)%u; // 0-6
		int t, c = 0;
		for(t = 0; t < 7; t++) {
			int tc = 1 << t;
			if((chosenType & tc) == 0)
				if(c++ == n) {
					chosenType |= tc;
					break;
				}
		}
		
		return new Tetermino(t, random.nextInt(4));
	}

	public static Tetermino next() {
		following.add(nextTetermino());
		return following.pop();
	}
}

public class SinglePlayerGame {
	Canvas gameCanvas;
	GraphicsContext ctx;
	
	private final int GH = References.HEIGHT;
	private final int GW = References.GAMEWIDTH;
	private final int TW = References.TOOLWIDTH;
	private final int TS = References.TILESIZE;
	
	private int[][] map = new int[GH][GW];
	private Tetermino falling;
	private Tetermino shiftTetermino = null;
	private boolean hasShifted = false;
	
	private BitSet specialScoring = new BitSet(4);
	
	private FallingTiles shadow;
	
	private final int fps = 24;
	private final long frameNs = 1_000_000_000L / fps;
	private int fallingDuration = References.levelFallTick(1);
	private int fallingTicker = 0;
	private boolean isTicking = true;
	private int strictFallStartY = -1;
	
	private AnimationTimer loop;
	
	private SinglePlayerController SPC;
	
	public boolean isPaused = false;
	public boolean isFailed = false;
	
	private SimpleIntegerProperty clearLines = new SimpleIntegerProperty(0);
	private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
	private SimpleIntegerProperty level = new SimpleIntegerProperty(1);
	
	public SinglePlayerGame(Canvas canvas, SinglePlayerController SPC) {
		gameCanvas = canvas;
		this.SPC = SPC;
		ctx = gameCanvas.getGraphicsContext2D();
		
		gameCanvas.setOnKeyReleased(e -> {
			if(e.getCode().equals(KeyCode.DOWN))
				strictFallStartY = -1;
		});
		
		
		gameCanvas.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.ESCAPE) && !isFailed) {
				if(!isPaused)
					pause();
				else 
					SPC.resumeButtonClick();
			}else if(isPaused || isFailed)
				return;
			
			if(e.getCode().equals(KeyCode.UP) || e.getCode().equals(KeyCode.PERIOD)) {
				if(strictFallStartY == -1)
					strictFallStartY = falling.getY();
				specialScoring.clear(SpecialScoring.TSPIN);
				specialScoring.clear(SpecialScoring.MINI_TSPIN);
				int rotateResult = falling.rotate(map, e.getCode().equals(KeyCode.PERIOD));
				if(rotateResult == 0)
					SoundHandler.playEffect(Sounds.inVain);
				else if(falling.getType() == 5) {
					int c = 0, ty = falling.getY(), tx = falling.getX();
					for(int dy = 0; dy < 2; dy++)
						for(int dx = 0; dx < 2; dx++) {
							if(ty + dy*2 < GH && tx + dx*2 < GW)
								c += map[ty + dy*2][tx + dx*2] != 0 ? 1: 0;						
						}
					
					if(shadow.getY() - falling.getY() == 0) {						
						if(c >= 3) {
							specialScoring.set(SpecialScoring.TSPIN);
						}else
							specialScoring.set(SpecialScoring.MINI_TSPIN);
					}
				}
				shadowUpdate();
			}
			
			else if(e.getCode().equals(KeyCode.RIGHT)) {
				if(!falling.moveRight(map, 1))
					SoundHandler.playEffect(Sounds.inVain);
				shadowUpdate();
			}else if(e.getCode().equals(KeyCode.LEFT)) {
				if(!falling.moveRight(map, -1))
					SoundHandler.playEffect(Sounds.inVain);
				shadowUpdate();
			}else if(e.getCode().equals(KeyCode.DOWN)) {
				specialScoring.set(SpecialScoring.SOFTFALL);
				fallingTicker = 0;
				fall(1);
			}else if(e.getCode().equals(KeyCode.SPACE)) {
				falling.setPos(shadow);
				specialScoring.set(SpecialScoring.HARDFALL);
				fallingTicker = 0;
				fall(2);
			}else if(e.getCode().equals(KeyCode.SLASH)) {
				if(hasShifted)
					return;
				if(shiftTetermino == null) {					
					shiftTetermino = falling;
					falling = TeterminoHelper.next();
				}else {
					Tetermino tmp = falling;
					falling = new Tetermino(shiftTetermino.getType(), shiftTetermino.getRotateState());
					shiftTetermino = tmp;
				}
				hasShifted = true;
				shadowUpdate();
				render();
			}
		});
		
		loop = new AnimationTimer() {
			long last = -1;

			@Override
			public void handle(long now) {
				if(last == -1) {					
					last = now;
					falling = TeterminoHelper.next();
					
					shadowUpdate();
					SoundHandler.playBackgroundMusic();
					return;
				}
				
				if (now <= last)
				      return;
			    
				if(now > last + frameNs) {
					last = now;
					
					if(isTicking)
						if(fallingTicker++ / fallingDuration > 0) {
							fallingTicker = 0;
							fall(0);
						}
					
					render();
				}
			}
			
		};

		
		SPC.scoreLabel.textProperty().bind(score.asString());
		SPC.levelLabel.textProperty().bind(level.asString());
		SPC.clearLabel.textProperty().bind(clearLines.asString());
	}
	
	/**
	 * fallType
	 * - 0 nature
	 * - 1 soft
	 * - 2 hard
	 *  */
	private boolean fall(int fallType) {
		boolean r = falling.fall(map);
		if(!r) {
			isTicking = false;
			fallingTicker = 0;
			SoundHandler.playEffect(fallType == 2 ? Sounds.fastDrop : Sounds.ground);
			for(int i = 0; i < falling.getHeight(); i++)
				for(int j = 0; j < falling.getWidth(); j++) {
					int ry = i+falling.getY(), rx = j+falling.getX();
					if(0 <= ry && ry < GH && 0 <= rx && rx < GW && falling.data[i][j] > 0)
						this.map[ry][rx] = falling.data[i][j];
				}
			int lastClr = clearLines.get();
			int cleared = clearLines();
			scoring(cleared, falling.getY() - strictFallStartY);
			if((cleared + lastClr) / 10 - (lastClr)/10 > 0) {
				level.set(level.get() + 1);
				SoundHandler.playEffect(Sounds.levelUp);
			}
			fallingDuration = References.levelFallTick(level.get());
			strictFallStartY = -1;
			hasShifted = false;
			isTicking = true;
			falling = TeterminoHelper.next();
			if(!falling.setInitialPos(map)) {
				falling = null;
				render();
				fail();
				return false;
			}
		}
		specialScoring.clear(0, 4);
		shadowUpdate();
		return r;
	}
	
	private int clearLines() {
		int cleared = 0;
		for(int k = 0; k < falling.getHeight() ; k++) {
			int ry = falling.getY() + k;
			boolean isFull = true;
			
			if(ry >= GH || ry < 0)
				break;
			
			for(int j = 0; j < GW; j++)
				if(map[ry][j] == 0) {
					isFull = false;
					break;
				}
			
			if(!isFull)
				continue;
			
			cleared++;
			for(int i = ry; i > 0; i--)
					map[i] = map[i-1];
			map[0] = new int[GW];
			
		}
		clearLines.set(clearLines.get() + cleared);
		return cleared;
	}
	
	
	private final int[] BaseLineScore = new int[] {0, 100, 300, 500, 800};
	private final int[] TSpinLineScore = new int[] {400, 800, 1200, 1600};
	private final int[] MiniTSpinLineScore = new int[] {100, 200, 400, 800};
	private boolean B2B = false;
	
	private void scoring(int cleared, int dy) {
		int baseScore = 0;
		boolean TS = specialScoring.get(SpecialScoring.TSPIN), MTS = specialScoring.get(SpecialScoring.MINI_TSPIN);
		boolean HF = specialScoring.get(SpecialScoring.HARDFALL), SF = specialScoring.get(SpecialScoring.SOFTFALL);
		if(TS)
			baseScore = this.TSpinLineScore[cleared];
		else if(MTS)
			baseScore = this.MiniTSpinLineScore[cleared];
		else
			baseScore = this.BaseLineScore[cleared];
		
		String[] multis = {"", " Double", " Triple"};
		String specScoreLore = "";
		if(TS)
			specScoreLore += "T-Spin" + multis[cleared-1];
		else if(MTS)
			specScoreLore += "Mini T-Spin" + ((cleared > 1) ? multis[cleared-1] : "");
		else if(cleared == 4)
			specScoreLore += "Tetrys";
		
		if(B2B && !specScoreLore.equals(""))
			specScoreLore += " B2B";
		
		SPC.specScore.setText(specScoreLore);
		
		baseScore *= level.get();
		baseScore = (int) (B2B ? Math.floor(1.5 * baseScore) : baseScore);
		
		dy *= cleared > 0 ? (HF ? 2 : (SF ? 1 : 0)) : 0;
		
		B2B = cleared > 0 ? (TS || MTS || cleared == 4) : !(TS && MTS);
		score.set(score.get() + baseScore + dy);
	}
	
	private void shadowUpdate() {
		shadow = new FallingTiles(falling.data);
		shadow.setPos(falling.getY(), falling.getX());
		while(shadow.fall(map));
	}
	
	private void renderText() {
		ctx.setFont(new Font(30));
		ctx.setFill(Color.WHITE);
		ctx.setTextBaseline(VPos.TOP);
		ctx.fillText("⬆ Hold", (GW+1)*TS, 5*TS);
		ctx.setTextBaseline(VPos.BOTTOM);
		ctx.fillText("⬇ Next", (GW+1)*TS, 10*TS);
	}
	
	private void render() {
		for(int i = 0; i < GH; i++)
			for(int j = 0; j < GW+TW; j++) {
				if(j < GW) {
					int t = falling == null ? -1 : falling.valueOf(i, j);
					int s = shadow == null ? -1 : shadow.valueOf(i, j);
					
					int c = (t > 0) ? t : ( (s > 0) ? s : map[i][j] );
					Color color = References.colorMap[c];
					
					if(s > 0 && t <= 0)
						color = color.darker();
					ctx.setFill(color);
				}else if(j > GW){
					int k = i / 5;
					Color color = Color.GRAY;
					if(k == 0 && i%5 < 4 && shiftTetermino != null) {
						int ti = i - k*5, tj = j - GW - 1;
						if(ti < shiftTetermino.data.length && tj < shiftTetermino.data[0].length) {
							int c = shiftTetermino.data[ti][tj];
							c = (c == 0) ? 8 : c;
							color = References.colorMap[c];
						}
					}else if(1 < k && k <= 3 && i%5 > 0) {
						Tetermino t = TeterminoHelper.get(k - 2);
						int ti = (i - 1)%5, tj = j - GW - 1;
						if (ti < t.getHeight() && tj < t.getWidth()) {
							int c = t.data[ti][tj];
							c = (c == 0) ? 8 : c;
							color = References.colorMap[c];
						}
					}else if(i == 4 || i > 9 && i%5 == 0) {
						color = Color.DIMGRAY;
					}
					
					ctx.setFill(color);
				}else
					ctx.setFill(Color.LIGHTGRAY);
				ctx.fillRect(j*TS, i*TS, TS, TS);
			}
		renderText();
	}
	
	public void start() {
		TeterminoHelper.init();
		SoundHandler.resetBackgroundMusic();
		loop.start();
	}
	
	public void pause() {
		SoundHandler.pauseBackgroundMusic();
		isPaused = true;
		loop.stop();
		SPC.showPauseGroup();
	}

	public void fail() {
		SoundHandler.pauseBackgroundMusic();
		SoundHandler.playEffect(Sounds.lose);
		isPaused = false;
		isFailed = true;
		loop.stop();
		SPC.showFailGroup();
	}
	
	public void resume() {
		SoundHandler.playBackgroundMusic();
		isPaused = false;
		loop.start();
	}
	
	public void init() {
		TeterminoHelper.init();
		SoundHandler.resetBackgroundMusic();
		
		map = new int[GH][GW];
		falling = null;
		shiftTetermino = null;
		hasShifted = false;
		
		specialScoring = new BitSet(4);
		
		fallingTicker = 0;
		isTicking = true;
		strictFallStartY = -1;
		
		isFailed = false;
		isPaused = false;
				
		clearLines.set(0);
		score.set(0);
		level.set(1);
		fallingDuration = References.levelFallTick(1);
		
		falling = TeterminoHelper.next();
		falling.setInitialPos(map);
	}
	
	public void restart() {
		init();
		shadowUpdate();
		SoundHandler.resetBackgroundMusic();
		SoundHandler.playBackgroundMusic();
		loop.start();
	}

	public void clearCanvas() {
		ctx.clearRect(0, 0, (GW+TW)*TS, GH*TS);
	}
}
