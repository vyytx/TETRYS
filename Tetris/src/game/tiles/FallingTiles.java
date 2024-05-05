package game.tiles;

import game.References;

public class FallingTiles {
	protected final int GH = References.HEIGHT;
	protected final int GW = References.GAMEWIDTH;

	public int[][] data;
	
	protected int y;
	protected int x;
	
	public FallingTiles(int[][] t) {
		this.data = t.clone();
		
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setPos(int py, int px) {
		this.y = py;
		this.x = px;
	}
	
	public int getHeight() {
		return this.data.length;
	}
	
	public int getWidth() {
		return this.data[0].length;
	}

	public int valueOf(int ty, int tx) {
		return (y <= ty && ty < y+getHeight() && x <= tx && tx < x+getWidth()) ? data[ty-y][tx-x] : -1;
	}
	
	protected boolean testFit(int[][] map, int ny, int nx) {
		return testFit(map, ny, nx, data);
	} 
	
	protected boolean testFit(int[][] map, int ny, int nx, int[][] newData) {
		for(int i = 0; i < newData.length; i++) {
			for(int j = 0; j < newData[0].length; j++) {
				int ry = ny+i, rx = nx+j;
				if(0 <= ry && ry < GH && 0 <= rx && rx < GW) {					
					if(map[ry][rx] > 0 && newData[i][j] > 0)
						return false;
				}else if(newData[i][j] > 0)
					return false;
			}
		}
		return true;
	}
	
	public boolean fall(int[][] map) {
		int newY = y + 1;
		if(testFit(map, newY, x)) {
			y = newY;
			return true;
		}
		return false;
	}

}
