package game;

import java.util.HashMap;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class SoundHandler {
	private final static int soundEffectDuration = 100;
	
	private static HashMap<AudioClip, Long> effectPlaying = new HashMap<AudioClip, Long>();
	static long lastTime = 0;
	public static void playEffect(AudioClip ac) {
		long currTime = System.currentTimeMillis();
		if(!effectPlaying.containsKey(ac) || currTime - effectPlaying.get(ac) > soundEffectDuration) {
			effectPlaying.put(ac, currTime);
			ac.play();
		}
	}
	
	private static Media Korobeiniki = new Media(SoundHandler.class.getClassLoader().getResource("resources/audio/Korobeiniki.wav").toExternalForm());
	private static MediaPlayer bgmPlayer = new MediaPlayer(Korobeiniki);
	
	public static void playBackgroundMusic() {
		bgmPlayer.setVolume(0.5);
		bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		bgmPlayer.play();
	}
	
	public static void pauseBackgroundMusic() {
		bgmPlayer.pause();
	}

	public static void resetBackgroundMusic() {
		bgmPlayer.stop();
	}
}

class Sounds {
	public static AudioClip inVain = new AudioClip(Sounds.class.getClassLoader().getResource("resources/audio/movementFail.mp3").toExternalForm());
	public static AudioClip ground = new AudioClip(Sounds.class.getClassLoader().getResource("resources/audio/ground.mp3").toExternalForm());
	public static AudioClip fastDrop = new AudioClip(Sounds.class.getClassLoader().getResource("resources/audio/fastDrop.mp3").toExternalForm());
	public static AudioClip levelUp = new AudioClip(Sounds.class.getClassLoader().getResource("resources/audio/levelUp.mp3").toExternalForm());
	public static AudioClip lose = new AudioClip(Sounds.class.getClassLoader().getResource("resources/audio/lose.mp3").toExternalForm());
}