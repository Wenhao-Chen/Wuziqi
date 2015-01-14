package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Snake extends JPanel implements KeyListener{

	int topMargin = 5, bottomMargin = 20;
	int moveInterval = 200;
	
	int direction;
	private int mapSize = 30;
	private SnakeState snake;
	private ArrayList<Point> food;
	private ArrayList<Point> walls;

	public void startGame() {
		
		direction = SnakeState.RIGHT;
		initSnakeState();
		
		while (true) {
			
			snake.move(direction, food, walls);
			repaint();
			
			try {
				Thread.sleep(moveInterval);
			} catch (InterruptedException e) {e.printStackTrace();}
			
		}
	}
	
	
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		calculateSizes();
		
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		drawWalls(g2d);
		
		drawSnake(g2d);
		
		drawFood(g2d);
		
	}
	
	/////////////////////////////////////////////////////
	


	private void initSnakeState() {
		snake = new SnakeState(3,3);
		snake.body.add(new Point(4,3));
		snake.body.add(new Point(5,3));
	}
	
	private void drawFood(Graphics2D g2d) {
		
	}
	
	private void drawSnake(Graphics2D g2d) {
		int x0 = 30, y0 = 30;
		int unitSize = 10;
		g2d.setColor(Color.black);
		for (int i = 0, len = snake.body.size(); i < len; i++) {
			int row = snake.body.get(i).x;
			int col = snake.body.get(i).y;
			int x = x0 + row*unitSize;
			int y = y0 + col*unitSize;
			g2d.fill(new Rectangle2D.Double(x, y, unitSize, unitSize));
		}
	}

	private void drawWalls(Graphics2D g2d) {
		
	}

	private void calculateSizes() {
		
	}

	//////////////////////////////////////////////////////
	
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case 87:
		case 38:
			direction = SnakeState.UP;
			System.out.println("up");
			break;
		case 65:
		case 37:
			direction = SnakeState.LEFT;
			System.out.println("left");
			break;
		case 83:
		case 40:
			direction = SnakeState.DOWN;
			System.out.println("down");
			break;
		case 68:
		case 39:
			direction = SnakeState.RIGHT;
			System.out.println("right");
			break;
		}
	}

	//TODO main
	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(500, 500));
		
		
		Snake panel = new Snake();
		panel.setOpaque(true);
		panel.addKeyListener(panel);
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		
		frame.add(panel);
		frame.setVisible(true);
		
		panel.startGame();
	}
	
}
