package game;

import javafx.scene.paint.Color;

public class References {
	
	public static final int GAMEWIDTH = 10;
	public static final int TOOLWIDTH = 5;
	public static final int HEIGHT = 20;
	
	public static final int TILESIZE = 32;
	
	public static final Color[] colorMap = {Color.BLACK, Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.LIMEGREEN, Color.DARKMAGENTA, Color.RED, Color.GRAY};
	
	public static final class TeterminoData {
		public static int[][] I = {
			{0, 0, 0, 0},
			{1, 1, 1, 1},
			{0, 0, 0, 0},
			{0, 0, 0, 0}
		};
		
		public static int[][] J = {
			{2, 0, 0},
			{2, 2, 2},
			{0, 0, 0},
		};
		
		public static int[][] L = {
			{0, 0, 3},
			{3, 3, 3},
			{0, 0, 0},
		};
		
		public static int[][] O = {
			{0, 4, 4, 0},
			{0, 4, 4, 0},
			{0, 0, 0, 0},
		};
		
		public static int[][] S = {
			{0, 5, 5},
			{5, 5, 0},
			{0, 0, 0},
		};
		
		public static int[][] T = {
			{0, 6, 0},
			{6, 6, 6},
			{0, 0, 0},
		};
		
		public static int[][] Z = {
			{7, 7, 0},
			{0, 7, 7},
			{0, 0, 0},
		};
		
		public static int[][] FromInt(int n) {
			return (new int[][][] {I, J, L, O, S, T, Z})[n];
		}
	};
	
	// [nowRotateState][testCaseNum] => {y, x}
	public static final int[][][] wallKickDataGenral = new int[][][] {
		{ { 0, -1}, { 1, -1}, {-2,  0}, {-2, -1} },
		{ { 0,  1}, {-1,  1}, { 2,  0}, { 2,  1} },
		{ { 0,  1}, { 1,  1}, {-2,  0}, {-2,  1} },
		{ { 0, -1}, {-1, -1}, { 2,  0}, { 2, -1} },
	};
	
	public static final int[][][] wallKickDataI = new int [][][] {
		{ { 0, -2}, { 0,  1}, {-1, -2}, { 2,  1} },
		{ { 0, -1}, { 0,  2}, { 2, -1}, {-1,  2} },
		{ { 0,  2}, { 0, -1}, { 1,  2}, {-2, -1} },
		{ { 0,  1}, { 0, -2}, {-2,  1}, { 1, -2} },
	};
	
	public static final int[][] rotate(int[][] data, boolean counter) {
		int[][] t = new int[data[0].length][data.length];
		for(int i = 0; i < t.length; i++)
			for(int j = 0; j < t[0].length; j++)
				if(!counter)
					t[i][data.length - j - 1] = data[j][i];
				else
					t[t.length - i - 1][j] = data[j][i];
		return t;
	}
	
	public static final class SpecialScoring {
		public static final int SOFTFALL	= 0;
		public static final int HARDFALL	= 1;
		public static final int TSPIN		= 2;
		public static final int MINI_TSPIN	= 3;
	}
	
	private final static int[] LevelFallTickData = new int[] {-1, 48, 43, 38, 33, 28, 23, 18, 13, 8, 6};
	public static final int levelFallTick(int newLevel) {
		if(newLevel <= 10)
			return LevelFallTickData[newLevel];
		if(newLevel <= 19)
			return 5 - (newLevel-11)/3;
		if(newLevel <= 28)
			return 2;
		return 1;
	}
}
