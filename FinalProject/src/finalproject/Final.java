package finalproject;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;



public class Final extends JFrame implements ActionListener{
	PrinterJob pj;
	MyPanel panel;
	public static void main(String[] args) {
		JFrame frame = new Final();
		frame.setTitle("Benchmark");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setVisible(true);
	}
	
	public Final() {
		JMenuBar mb = new JMenuBar();    //Creation of menuBra
		setJMenuBar(mb);
		//FILE Menu
		JMenu menu = new JMenu("File");
		JMenuItem mi = new JMenuItem("Print");    //Option to print
		mi.addActionListener(this);
		menu.add(mi);
		
		
		
		mb.add(menu);
		
		panel = new MyPanel();   // panel created here (cause it needs to be created in a dinamic class)
		getContentPane().add(panel);
		
		pj = PrinterJob.getPrinterJob();   
		pj.setPrintable(panel);  // make panel printable
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();  //string with commands from the terminal
		
		if("Print".equals(cmd)) {     //if Print selected
			if(pj.printDialog()) {    
				
				try {
					pj.print();       // use class print
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
		} else if("Exit".equals(cmd)) {
			System.exit(0); 
		}
		
	}
		
	}


class MyPanel extends JPanel implements Runnable, KeyListener, Printable{

	Shape obj1 = null;  //rectangle horizontal creation
	Shape obj2 = null;	//rectangle vertical creation
	Shape obj3 = null;	//rectangle vertical creation
	Shape player = null;   // player of game
	Shape square = null;   //square to show gradients
	Shape square2 = null;  //square to show gradients
	Shape squareInt = null;   //square to show intersections
	Shape squareInt2 = null;   //other square to show intersections
	
	
	// image to show in the background of panel
	Image img = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Serus\\git\\repository\\FinalProject\\src\\finalproject\\Brick_Wall.jpg");
	//image to show on background of clipping
	Image img2 = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Serus\\git\\repository\\FinalProject\\src\\finalproject\\external-content.duckduckgo.com.jpg");
	
	private static final String Shape  = null;
	
	int ang = 0;  //angle to constatly rotate cross
	int r = 15;
	int tx= 30;   //x position of player
	int ty = 300;  //y position of player
	int vx= 0;     //x speed of player
	int vy = 0;    //y speed of player

	AffineTransform at = new AffineTransform();   // created affineTransform to transform objects
	
	public MyPanel() {
		setPreferredSize(new Dimension(400,400));  //dimension of panel
		
		this.addKeyListener(this);   
		this.setFocusable(true);   //make program focusable
		
		// Create a new thread to make the program animated
		Thread thread = new Thread(this); 
		thread.start();
		
	}
	
	//***********************
	// PAINT COMPONENT - to paint everything in the scene
	//***********************
	public void  paintComponent (Graphics g) {  
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//Draw game state
		g2.fillRect(0,0,400,400);
		g2.drawImage(img, 0, 0, null);
		drawAll(g2);
	}
	
	//***********************
	// KEY EVENTS - making stuff happen when keys are clicked
	//***********************
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		switch(keyCode) {
		case KeyEvent.VK_LEFT:
			vx = -5;
			vy = 0;
			break;
		case KeyEvent.VK_RIGHT:
			vx = 5;
			vy = 0;
			break;
		case KeyEvent.VK_UP:
			vx = 0;
			vy = -5;
			break;
		case KeyEvent.VK_DOWN:
			vx = 0;
			vy = 5;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		vx = 0;		
		vy = 0;
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//***********************
	// ANIMS & COLLISIONS
	//***********************
	@Override
	public void run() {
		while(true) {
			//Update game state
			ang = (ang+1)%360;

			tx = tx + vx;    // turning with variable from the keyPressed Class
			ty = ty + vy;    // turning with variable from the keyPressed Class
			
			
			//***********************
			// COLLISION WITH CROSS
			//***********************
			if(obj1 != null)//obj1 & obj2 arent active in the use of this class (they are created before main)
			if(obj1.contains(tx+30,ty+30) || obj2.contains(tx+30,ty+30) ||obj1.contains(tx-30,ty-30) || obj2.contains(tx-30,ty-30) ||
					obj1.contains(tx+30,ty-30) || obj2.contains(tx+30,ty-30) ||obj1.contains(tx-30,ty+30) || obj2.contains(tx-30,ty+30)) { //if the coords of the player are inside of any of the objects
				tx=60;  //spawn here
				ty = 200;
			}
			
			//***********************
			//BORDER COLLISION
			//***********************
			
			if(tx - r < -35) 	tx = r-35;    
			else if(tx+r>335) tx = 335-r;
			
			if(ty - r < -50) ty = r-50;
			else if(ty + r >335) ty = 335-r;
			
			repaint();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	//****************************
	//enables class that has the result image;
	//****************************
	private void drawAll(Graphics2D g2) {
		obj1 = new Ellipse2D.Double(-100,-10,200,20);  //made eclipses for the cross
		obj2 = new Ellipse2D.Double(-10,-100,20,200);
	    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD); // Creation path for the clipping
	    
	    path.moveTo(0,10);
	    path.quadTo(200, 50, 400, 10);
	    path.lineTo(400,390);
	    path.quadTo(200,350,0,400);
	    path.closePath();
	    GeneralPath path2 = new GeneralPath(GeneralPath.WIND_EVEN_ODD); // Creation path for the strokes
	    path2.moveTo(0,120);
	    path2.lineTo(80,0);
	    path2.lineTo(160,120);
	    path2.quadTo(80, 80, 0, 120);
	    path2.closePath();
	    g2.clip(path);   //clipping with path
	    
	    g2.drawImage(img2, -300, -300, null); // making img2 as the background for inside the clip
		
			
		square = new Rectangle2D.Double(-15,-15,30,30);   
		square2 = new Rectangle2D.Double(-20,-20,40,40);
		squareInt = square;
		squareInt2 = square2;
		
		player = new CustShape(-r,-r,2*r,2*r); //created player with custom shpae from the class CustShape          
		

		//***********************
		//SETTING UP STROKE
		//***********************
		BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	    g2.setStroke(stroke);
	    g2.translate(50,50);
	    g2.draw(path2);
		//***********************
		//SETTING UP PLAYER
		//***********************
		g2.setColor(Color.YELLOW);
		at.setToTranslation(tx,ty);
		at.scale(2, 2);
		player = at.createTransformedShape(player);
		g2.setColor(Color.RED);
		g2.fill(player);

		//***********************
		//SETTING UP Text
		//***********************
	    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	    AlphaComposite af = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
	    AlphaComposite ab = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
	    g2.setComposite(ac);
	    g2.setColor(Color.BLACK);
	    
	    Font font2 = new Font("Sans", Font.BOLD, 30);
	    g2.setFont(font2);
	    g2.drawString("Strokes", -40, 10);
	    
	    
	    // set transparency back
	    g2.setComposite(ab);
	    
		//***********************
		//SETTING UP RED CROSS
		//***********************
		at.setToTranslation(250,120);  
			
		at.rotate(Math.toRadians(ang)); 
			
		obj1 = at.createTransformedShape(obj1); 
		obj2 = at.createTransformedShape(obj2);
		
		g2.setColor(Color.BLUE);
		g2.fill(obj1);
		g2.fill(obj2);

		//***********************
		//SETTING UP Text
		//***********************
		g2.setColor(Color.BLACK);
		g2.setComposite(af);
		
		Font font3 = new Font("Sans", Font.BOLD, 30);
	    g2.setFont(font3);
	    g2.drawString("Anims & shapes", 100, 160);
	    
	    // set transparency back
	    g2.setComposite(ab);
		
		//***********************
		//SETTING UP squares
		//***********************
		at.setToTranslation(300,300);
		square = at.createTransformedShape(square);
		GradientPaint gp = new GradientPaint(290, 300, Color.GREEN, 310, 300 , Color.BLACK);
		g2.setPaint(gp);
		g2.fill(square);
		
		at.setToTranslation(300,260);
		square2 = at.createTransformedShape(square2);
		GradientPaint gp2 = new GradientPaint(290, 300, Color.WHITE, 310, 300 , Color.GRAY);
		g2.setPaint(gp2);
		g2.fill(square2);
		
		
		//***********************
		//SETTING UP Intersections
		//***********************
		at.setToTranslation(240,300);
		squareInt = at.createTransformedShape(squareInt);
		at.setToTranslation(240,280);
		squareInt2 = at.createTransformedShape(squareInt2);
		Area a1 = new Area(squareInt);  // criado uma area e associar a elipse s1 a essa area
		Area a2 = new Area(squareInt2);
		a1.exclusiveOr(a2);
		g2.setPaint(gp2);
		g2.fill(a1);
		
	}
	
	
	//***********************
	//SETTING UP Printing
	//***********************
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		switch(pageIndex) {
		case 0: 
			drawAll((Graphics2D) g); 
			break;
		default:
			return NO_SUCH_PAGE;
		}
			
			
		return 0;
		
	}
		
		
		
	//***********************
	//SETTING UP Custom Shape
	//***********************
	public class CustShape implements Shape {
		GeneralPath path;
		public CustShape(float x, float y, float w, float h) {
			path = new GeneralPath();
			float x0 = x + 0.2f*w;
			float y0 = y + 1f*h;
		    float x1 = x + 0.2f*w;
		    float y1 = y + -0.6f * h;
		    float x2 = x + 1.1f * w;
		    float y2 = y + 2.4f * h;
		    float x3 = x + 1f * w;
		    float y3 = y + 0f * h;
		    float x4 = x +0.8f * w;
		    float y4 = y + 0f * h;
		    float x5 = x + 0.8f * w;
		    float y5 = y + 1.8f * h;
		    float x6 = x + -0.1f * w;
		    float y6 = y + -1.5f * h;
		    float x7 = x + 0f * w;
		    float y7 = y + 1f * h;
		    float x8 = x + 0.2f * w;
		    float y8 = y + 1f * h;
		    path.moveTo(x0, y0);
		    path.curveTo(x1, y1, x2, y2, x3, y3);
		    path.lineTo(x4, y4);
		    path.curveTo(x5, y5, x6, y6, x7,y7);
			path.clone();
		}
		public boolean contains(Rectangle2D rect) {
			return path.contains(rect);
		}

	    public boolean contains(Point2D point) {
		    return path.contains(point);
	    }

		public boolean contains(double x, double y) {
		   return path.contains(x, y);
	    }

		public boolean contains(double x, double y, double w, double h) {
			return path.contains(x, y, w, h);
		}

		public Rectangle getBounds() {
			return path.getBounds();
		}

		public Rectangle2D getBounds2D() {
			return path.getBounds2D();
		}

		public PathIterator getPathIterator(AffineTransform at) {
			return path.getPathIterator(at);
		}

		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return path.getPathIterator(at, flatness);
		}

		public boolean intersects(Rectangle2D rect) {
			return path.intersects(rect);
		}
		
		public boolean intersects(double x, double y, double w, double h) {
			return path.intersects(x, y, w, h);
		}
	}
		

}