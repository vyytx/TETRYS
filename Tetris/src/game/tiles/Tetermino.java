package game.tiles;

import java.util.BitSet;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;

import game.References;

public class Tetermino extends FallingTiles {
	private int type;
	private int rotateState = 0;
	
	public int getType() {
		return type;
	}
	
	public int getRotateState() {
		return rotateState;
	}
	
	public Tetermino(int type, int rotateState) {
		super(References.TeterminoData.FromInt(type).clone());
		this.rotateState = rotateState;
		for(int i = 0; i < rotateState; i++)
			this.data = References.rotate(data, false).clone();
		this.type = type;
	}
	
	public Tetermino() {
		super(new int[][] {});
	} 
	
	public void setPos(FallingTiles ref) {
		this.y = ref.y;
	}
		
	public boolean moveRight(int[][] map, int step) {
		int newX = this.x + step;
		if(testFit(map, y, newX)) {
			x = newX;
			return true;
		}
		return false;
	}
	
	
	/**
	 * 0 - fail
	 * 1 - normal
	 * 2 - wall kicks
	 *  */
	public int rotate(int[][] map, boolean counter) {
		if(this.type == 3)
			return 1;
		int t[][] = References.rotate(this.data, counter);
		int newRotateState = (rotateState + (counter ? 3 : 1))%4;
		int[][] wallKickData = this.type > 0
				? References.wallKickDataGenral[counter ? newRotateState : rotateState].clone()
				: References.wallKickDataI[counter ? newRotateState : rotateState].clone();
		int dy = 0, dx = 0;
		
		if(map == null) {
			this.data = t.clone();
			rotateState = newRotateState;
			return 1;
		}
			
		
		for(int i = -1; i < 4; i++) {
			if(i >= 0) {
				dy = wallKickData[i][0];
				dx = wallKickData[i][1];
			}
			if(counter) {
				dy *= -1;
				dx *= -1;
			}
			
			if(testFit(map, y+dy, x+dx, t)) {
				this.data = t.clone();
				this.y += dy;
				this.x += dx;
				rotateState = newRotateState;
				return i >= 0 ? 2 : 1;
			}
		}
		return 0;
	}
	
	private static Random random = new Random();
	private static OfInt oi = random.ints().iterator();
	
	public boolean setInitialPos(int[][] map) {
		int iy = -1;
		boolean flag = true;
		while(flag && iy < 4) {
			for(int k : this.data[++iy])
				if(k > 0) {
					flag = false;
					break;
				}
		}
		this.y = -iy;
		
		
		BitSet possCases = new BitSet(GW + 4);
		for(int j = -2; j <= GW+2; j++) { // for I
			if(testFit(map, 0, j))
				possCases.set(j+2);
		}
		
		int possCaseNum = possCases.cardinality();
		if(possCaseNum == 0)
			return false;
		int dx = (oi.nextInt()%possCaseNum + possCaseNum)%possCaseNum;
		
		int t, c = 0;
		for(t = -2; t <= GW+2; t++) {
			if(possCases.get(t+2))
				if(c++ == dx) {
					this.x = t;
					return true;
				}
		}
		return false;
	}
};
