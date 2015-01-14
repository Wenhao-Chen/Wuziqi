package snake;

import java.awt.Point;
import java.util.ArrayList;

public class SnakeState {

	static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN = 4;
	// tail at 0, head at last
	ArrayList<Point> body = new ArrayList<Point>();
	boolean stillAlive = true;
	
	public SnakeState(int x, int y) {
		body = new ArrayList<Point>();
		body.add(new Point(x, y));
	}
	
	public void move(int direction, ArrayList<Point> food, ArrayList<Point> walls) {

		Point oldHead = body.get(body.size()-1);
		Point newHead = new Point(oldHead.x, oldHead.y);
		switch (direction) {
		case LEFT:
			newHead.x--;
			break;
		case RIGHT:
			newHead.x++;
			break;
		case UP:
			newHead.y--;
			break;
		case DOWN:
			newHead.y++;
			break;
		}
		// if newHead is in a place of walls
		
		// if newHead is in a place of food
		
		// if newHead is not in a place of food or walls
		body.add(newHead);
		body.remove(0);
	}
	
	
	
}
