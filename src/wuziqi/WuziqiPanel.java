package wuziqi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WuziqiPanel extends JPanel implements MouseListener, MouseMotionListener{

	private int boardSize;
	private double margin = 5;
	private final double piece_frac = 0.9;
	private PieceState[][] pieces;
	private int currentPlayer = PieceState.black;
	private JLabel label, undoLabel;
	private double boardWidth, squareWidth, pieceRadius, gridWidth;
	private boolean hasWinner = false;
	private ArrayList<String> gameLog = new ArrayList<String>();
	private int mouseX, mouseY;
	
	public WuziqiPanel() {
		this(10);
	}
	
	public WuziqiPanel(int i) {
		this.boardSize = i;
		addMouseListener(this);
		addMouseMotionListener(this);
		pieces = new PieceState[boardSize][boardSize];
		initWidgets();
	}
	
	private void initWidgets() {
		//BorderLayout layout = new BorderLayout();
		this.setLayout(null);
		
		label = new JLabel("Current Player: ");
		label.setSize(130, 15);
		label.setLocation(3, 3);
		
		undoLabel = new JLabel("Click Here To Undo");
		undoLabel.setSize(150, 15);
		undoLabel.setLocation(250, 3);
		undoLabel.setForeground(Color.gray);
		
		this.add(label);
		this.add(undoLabel);
	}


	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw board
		
		boardWidth = Math.min(getWidth(), getHeight()) - 2 * margin;
		squareWidth = boardWidth / boardSize;
		pieceRadius = squareWidth * piece_frac / 2;
		gridWidth = squareWidth * (boardSize - 1);
		margin = Math.max(margin, this.label.getHeight() + this.label.getY() + pieceRadius);
		
		Color bgColor = new Color(0.925f, 0.670f, 0.34f);
		g2d.setColor(bgColor);
		g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
		
		// draw current player color and hovering piece and follows mouse cursor
		g2d.setColor(currentPlayer == PieceState.black? Color.black : Color.white);
		g2d.fill(new Ellipse2D.Double(label.getX() + label.getWidth() + 2, label.getY(), label.getHeight(), label.getHeight()));
		//g2d.fill(new Ellipse2D.Double(Math.max(0, mouseX - pieceRadius), Math.max(0, mouseY - pieceRadius), pieceRadius*2, pieceRadius*2));
		// draw hovering piece that follows mouse cursor
		
		// draw grid
		g2d.setColor(Color.BLACK);
		double gridTopleftX = margin + squareWidth/2;
		double gridTopleftY = gridTopleftX;
		for (int i = 0; i < boardSize; i++) {
			double leftX = gridTopleftX;
			double leftY = gridTopleftY + squareWidth*i;
			g2d.draw(new Line2D.Double(leftX, leftY, leftX + gridWidth, leftY));
			double topX = gridTopleftX + squareWidth*i;
			double topY = gridTopleftY;
			g2d.draw(new Line2D.Double(topX, topY, topX, topY + gridWidth));
		}
		
		// draw pieces
		for (int x = 0; x < boardSize; x++)
		for (int y = 0; y < boardSize; y++) {
			if (pieces[x][y] == null)
				continue;
			double centerX = gridTopleftX + x*squareWidth;
			double centerY = gridTopleftY + y*squareWidth;
			g2d.setColor( pieces[x][y].color == PieceState.black? Color.black : Color.white);
			g2d.fill(new Ellipse2D.Double(centerX - pieceRadius, centerY - pieceRadius, pieceRadius*2, pieceRadius*2));
		}
		
		if (mouseX >= gridTopleftX-pieceRadius && mouseX <= gridTopleftX+boardWidth+pieceRadius &&
			mouseY >= gridTopleftY-pieceRadius && mouseY <= gridTopleftY+boardWidth+pieceRadius)
		{
			g2d.setColor(currentPlayer == PieceState.black? Color.black : Color.white);
			g2d.fill(new Ellipse2D.Double(Math.max(0, mouseX - pieceRadius), Math.max(0, mouseY - pieceRadius), pieceRadius*2, pieceRadius*2));
		}
		
	}
	


	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}

	
	@Override
	public void mouseReleased(MouseEvent e) {
		// restart game
		if (hasWinner && label.getBounds().contains(e.getX(), e.getY())) {
			hasWinner = false;
			pieces = new PieceState[boardSize][boardSize];
			currentPlayer = PieceState.black;
			label.setText("Current Player: ");
			gameLog.clear();
			undoLabel.setForeground(Color.gray);
			repaint();
			return;
		}
		
		// undo
		if (undoLabel.getBounds().contains(e.getX(), e.getY())) {
			if (gameLog.size() > 0) {
				String lastAction = gameLog.get(gameLog.size()-1);
				int lastX = Integer.parseInt(lastAction.split(",")[0]);
				int lastY = Integer.parseInt(lastAction.split(",")[1]);
				int playedBy = Integer.parseInt(lastAction.split(",")[2]);
				pieces[lastX][lastY].color = PieceState.empty;
				currentPlayer = playedBy;
				repaint();
				gameLog.remove(gameLog.size()-1);
				hasWinner = false;
				label.setText("Current Player: ");
			}
			if (gameLog.size() == 0)
				undoLabel.setForeground(Color.gray);
			return;
		}
		
		// play game
		if (hasWinner)
			return;
		double x = e.getX() - margin;
		double y = e.getY() - margin;
		if (x <= 0 || y <= 0 || x >= boardWidth || y >= boardWidth)
			return;
		int row = (int) Math.floor(x/squareWidth);
		int column = (int) Math.floor(y/squareWidth);
		double pieceCenterX = pieceRadius + row * squareWidth;
		double pieceCenterY = pieceRadius + column * squareWidth;
		double distance = Math.sqrt((pieceCenterX - x)*(pieceCenterX - x) + (pieceCenterY - y)*(pieceCenterY - y));
		if (distance > pieceRadius)
			return;
		if (pieces[row][column] == null || pieces[row][column].color == PieceState.empty) {
			pieces[row][column] = new PieceState();
			pieces[row][column].color = currentPlayer;
			gameLog.add(row + "," + column + "," + currentPlayer);
			if (gameLog.size() >= 1)
				undoLabel.setForeground(Color.black);
			
			repaint();
			
			int winner = checkWinner(row, column);
			if (winner == PieceState.black) {
				JOptionPane.showMessageDialog(null, "Black Wins!");
				hasWinner = true;
				currentPlayer = PieceState.empty;
				repaint();
				label.setText("Click Here To Restart");
			}
			else if (winner == PieceState.white) {
				JOptionPane.showMessageDialog(null, "White Wins!");
				hasWinner = true;
				currentPlayer = PieceState.empty;
				repaint();
				label.setText("Click Here To Restart");
			}
			else
				changePlayer();
		}
	}

	private int checkWinner(int row, int col) {
		
		int x0 = Math.max(row-4, 0);
		int y0 = Math.max(col-4, 0);
		int xn = Math.min(row+5, boardSize);
		int yn = Math.min(col+5, boardSize);
		
		// check left-to-right
		int counter = 1;
		int lastColor = PieceState.empty;
		if (pieces[x0][col] != null)
			lastColor = pieces[x0][col].color;
		for (int i = x0+1; i < xn; i++) {
			if (pieces[i][col] == null || pieces[i][col].color == PieceState.empty) {	counter = 1; lastColor = PieceState.empty; continue; }
			int currentColor = pieces[i][col].color;
			if (currentColor == lastColor) {
				counter++;
				if (counter == 5)	return currentColor;
			}
			else {
				lastColor = currentColor;
				counter = 1;
			}
		}
		
		// check top-to-bottom
		counter = 1;
		lastColor = PieceState.empty;
		if (pieces[row][y0] != null)
			lastColor = pieces[row][y0].color;
		for (int j = y0+1; j < yn; j++) {
			if (pieces[row][j] == null || pieces[row][j].color == PieceState.empty)	{	counter = 1; lastColor = PieceState.empty; continue;}
			int currentColor = pieces[row][j].color;
			if (currentColor == lastColor) {
				counter++;
				if (counter == 5)	return currentColor;
			}
			else {
				lastColor = currentColor;
				counter = 1;
			}
		}
		
		// check topleft-to-bottomright
		int xi = row, yi = col;
		while (xi >0 && yi>0) {	xi--;	yi--;}
		counter = 1;
		lastColor = PieceState.empty;
		if (pieces[xi][yi] != null)
			lastColor = pieces[xi][yi].color;
		xi++; yi++;
		while (xi < boardSize && yi < boardSize && xi < row+5 && yi < col+5) {
			if (pieces[xi][yi] == null || pieces[xi][yi].color == PieceState.empty)	{	counter = 1; lastColor = PieceState.empty; xi++; yi++; continue;}
			int currentColor = pieces[xi][yi].color;
			if (currentColor == lastColor) {
				counter++;
				if (counter == 5) return currentColor;
			}
			else {
				counter = 1;
				lastColor = currentColor;
			}
			xi++; yi++;
		}

		// check bottomleft-to-topright
		xi = row; yi = col;
		while (xi > 0 && yi < boardSize-1)	{xi--; yi++;}
		counter = 1;
		lastColor = PieceState.empty;
		if (pieces[xi][yi] != null)
			lastColor = pieces[xi][yi].color;
		xi++; yi--;
		while (xi < boardSize && xi < row+5 && yi > 0 && yi > col - 5) {
			if (pieces[xi][yi] == null || pieces[xi][yi].color == PieceState.empty)	{	counter = 1; lastColor = PieceState.empty; xi++; yi--; continue;}
			int currentColor = pieces[xi][yi].color;
			if (currentColor == lastColor) {
				counter++;
				if (counter == 5) return currentColor;
			}
			else {
				counter = 1;
				lastColor = currentColor;
			}
			xi++; yi--;
		}
		return PieceState.empty;
	}

	private void changePlayer() {
		if (currentPlayer == PieceState.black)
			currentPlayer = PieceState.white;
		else currentPlayer = PieceState.black;
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
		//System.out.printf("x/y = %d/%d\n", mouseX, mouseY);
	}

}
