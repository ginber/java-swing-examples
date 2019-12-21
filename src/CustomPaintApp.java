import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CustomPaintApp extends JFrame
{
	
	// Buttons
	JButton strokeButton, ellipseButton, rectButton, lineButton, fillButton, clearButton;
	
	// A board where the drawing actions will occur
	DrawingBoard board;
	
	// ArrayLists to save the informations about shapes
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	ArrayList<Color> strokeColors = new ArrayList<Color>();
	ArrayList<Color> fillColors = new ArrayList<Color>();
	
	// An integer variable to indicate which action is going to be performed
	// A rectangle is going to be drawn as default
	int actionID = 1;
	
	// Default colors for strokes and fillings of the shapes
	Color strokeColor = Color.BLACK;
	Color fillColor = Color.BLACK;
	
	public CustomPaintApp()
	{
		
		// Creating frame
		this.setSize(500, 500); // in pixels
		this.setTitle("Paint App");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// A panel to add components
		JPanel buttonPanel = new JPanel();
		board = new DrawingBoard();
		
		// A box to put buttons in order
		Box buttonBox = Box.createHorizontalBox();
		
		lineButton = createButton("Line", 3);
		ellipseButton = createButton("Ellipse", 2);
		strokeButton = createButton("Stroke", "stroke");
		rectButton = createButton("Rectangular", 1);
		fillButton = createButton("Fill", "fill");
		clearButton = createButton("Clear", "clear");
		
		buttonBox.add(rectButton);
		buttonBox.add(ellipseButton);
		buttonBox.add(lineButton);
		buttonBox.add(strokeButton);
		buttonBox.add(fillButton);
		buttonBox.add(clearButton);
		
		buttonPanel.add(buttonBox);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(board, BorderLayout.CENTER);
		
		this.setVisible(true);
		
	}

	public static void main(String[] args)
	{
		
		new CustomPaintApp();
		
	}
	
	/* 
	 * I will overload the "createButton" method because of the followings:
	 * 
	 * - If a drawing action is going to happen, then I need another ID number 
	 * for that action
	 * 
	 * - If I will change properties of shapes such as color or clean the board,
	 * then it's better to use some Strings as keys to indicate which properties
	 * are going to be changed 
	 * 
	 * So, I will create the same function twice with different parameters according
	 * to the aims that I will use them for
	 */
	
	// Firstly for actions
	public JButton createButton(String buttonName, final int thisActionID)
	{
		
		JButton button = new JButton(buttonName);
		
		button.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				
				// Deciding which action is going to be performed
				actionID = thisActionID;
				
			}
			
		}); // END OF addActionListener()
		
		return button;
		
	}
	
	// Secondly for properties & cleaning the board
	public JButton createButton(String buttonName, final String type)
	{
		
		JButton button = new JButton(buttonName);
		
		button.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				
				// I will use a simple if-else statement to decide which property I am
				// going to change.
				if(type == "stroke")
				{
					
					strokeColor = JColorChooser.showDialog(null, "Select stroke color", Color.BLACK);
					
				}
				else if(type == "fill")
				{
					
					fillColor = JColorChooser.showDialog(null, "Select fill color", Color.BLACK);
					
				}
				else if(type == "clear")
				{
					
					shapes.clear();
					strokeColors.clear();
					fillColors.clear();
					
					board.repaint();
					
				}
				
			}
			
		}); // END OF addActionListener()
		
		return button;
	}
	
	// -----------------------------------------------------------------------------------
	// So I created my buttons with these two functions I wrote above, I used both of them
	// for the purposes I mentioned above in the comment section
	// -----------------------------------------------------------------------------------
	
	// Creating the board
	private class DrawingBoard extends JComponent
	{
		
		// Points to indicate which areas the shapes will cover
		Point startPoint, endPoint;
		
		// Constructor for the board, handles of mouse actions should be done here,
		// because I am only interested in the mouse actions on the board, not anywhere
		// else.
		public DrawingBoard()
		{
			
			this.addMouseListener(new MouseAdapter()
			{
				
				// When the user presses the mouse
				public void mousePressed(MouseEvent e)
				{
					
					// Take the coordinates of the point where the mouse is pressed,
					// and that should be my starting point to draw
					startPoint = new Point(e.getX(), e.getY());
					repaint();
					
				}
				
				// When the user releases the mouse
				public void mouseReleased(MouseEvent eRelease)
				{
					
					// Take the coordinates of the point where the mouse is released,
					// and that should be the ending point of the shape
					endPoint = new Point(eRelease.getX(), eRelease.getY());
					
					// Initializing the shape I'm going to draw
					Shape drawShape = null;
					
					// Performing the drawing actions according to the IDs I gave them
					// Drawing methods will be defined in the later parts of the code.
					switch(actionID) 
					{
					
						case 1:
							drawShape = drawRectangle(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
							break;
							
						case 2:
							drawShape = drawEllipse(startPoint, endPoint);
							break;
							
						case 3:
							drawShape = drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
							break;
							
						default:
							break;
					
					}
					
					// Saving informations about the shape
					shapes.add(drawShape);
					fillColors.add(fillColor);
					strokeColors.add(strokeColor);
					
					// Nullifying the points to start again after the shape is drawn
					startPoint = null;
					endPoint = null;
					
					// Updating the board to show the changes that are made on it
					repaint();
					
				}
				
			}); // END OF addMouseListener()
			
			// My ending point will change simultaneously as the user drags the mouse,
			// so I need to handle it by "listening" the motions of the mouse
			this.addMouseMotionListener(new MouseMotionAdapter()
					{
				
						// When the mouse is dragged
						public void mouseDragged(MouseEvent e)
						{
							
							endPoint = new Point(e.getX(), e.getY());
							repaint();
							
						}
				
					}); // END OF addMouseMotionListener
			
		} // END OF DrawingBoard()
		
		
		/*
		 * Auto-triggered paint function; this function triggers when a change in a visible
		 * component has to be made. That's why "DrawingBoard" class inherits "JComponent".
		 * I'm trying to draw something on this component, so I'm trying to make a change in
		 * this component. That's why the "paint" function will be triggered when I start
		 * the program. Think it like the "main" function, but this is triggered when you
		 * try to make a change in visible things in your program while the main function
		 * is triggered always. 
		 */
		public void paint(Graphics g)
		{
			
			// Defining a Graphics2D object to handle drawing events and to control
			// the view of the board. Graphics2D class inherits Graphics class to
			// provide more sophisticated control over geometry. This class is
			// fundamental for rendering 2-dimensional shapes.
			Graphics2D graphics2D = (Graphics2D) g;
			
			// Anti-aliasing, especially important when drawing lines to make them
			// "straight"
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			// Setting a default stroke
			graphics2D.setStroke(new BasicStroke(2));
			
			// Creating iterators for the ArrayLists that contain the info of the 
			// properties for my shapes, I will use them to "iterate" in a for loop
			// so that I will be able to
			Iterator<Color> strokeIterator = strokeColors.iterator();
			Iterator<Color> fillIterator = fillColors.iterator();
			
			// Making sure that the board has no transparency
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			// Drawing the shapes that were added to ArrayList called "shapes" by
			// using an enhanced for loop. That's why I needed to "clear" the ArrayLists
			// to clear the board.
			for(Shape s : shapes)
			{
				
				// I'm using iterators to get the data that I saved in ArrayLists
				graphics2D.setPaint(strokeIterator.next());
				graphics2D.draw(s);
				
				graphics2D.setPaint(fillIterator.next());
				graphics2D.fill(s);
				
			}
			
		} // END OF paint
		
		// Defining drawing methods
		
		private Rectangle2D.Float drawRectangle(int x1, int y1, int x2, int y2)
		{
			
			// Getting the x value which is closer to the left side of the frame
			int x = Math.min(x1, x2); 
			// Getting the y value which is closer to the upper part of the frame
			int y = Math.min(y1, y2);
			
			int width = Math.abs(x1 - x2);
			int height = Math.abs(y1 - y2);
			
			// To draw a rectangle: 
			// Rectangle2D.Float(starting x position, starting y position, how much you will 
			// go forward in x, how much you will go forward in y);
			
			return new Rectangle2D.Float(x, y, width, height);
			
		}
		
		// Drawing an ellipse in Java is almost identical to drawing a rectangle,
		// because in fact you're drawing a rectangle and filling the interrior tangent
		// eliptic part.
		// I just arbitrarily used Points here instead of integers - nothing different
		private Ellipse2D.Float drawEllipse(Point startPoint, Point endPoint)
		{
			
			int x = Math.min(startPoint.x, endPoint.x);
			int y = Math.min(startPoint.y, endPoint.y);
			
			int width = Math.abs(startPoint.x - endPoint.x);
			int height = Math.abs(startPoint.y - endPoint.y);
			
			return new Ellipse2D.Float(x, y, width, height);
			
		}
		
		// You only need the coordinates of starting an ending points to draw a line
		private Line2D.Float drawLine(int x1, int y1, int x2, int y2)
		{
			
			return new Line2D.Float(x1, y1, x2, y2);
			
		}
		
	} // END OF DrawingBoard

} // END OF CustomPaintApp
