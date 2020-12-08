package finalproject;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
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
		frame.setTitle("Final");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//JPanel panel = new MyPanel();
		//frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	public Final() {
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);
		//FILE Menu
		JMenu menu = new JMenu("File");
		JMenuItem mi = new JMenuItem("Print");
		mi.addActionListener(this);
		menu.add(mi);
		
		
		
		mb.add(menu);
		
		panel = new MyPanel();   // criar painel aqui (pq o main é estatico e tem que ser criado numa classe dinamica)
		getContentPane().add(panel);
		
		pj = PrinterJob.getPrinterJob();
		pj.setPrintable(panel);  // painel que tem o metodo print é este
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		//Creating Condition
		if("Print".equals(cmd)) {
			if(pj.printDialog()) {
				
				try {
					pj.print();
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
		} else if("Exit".equals(cmd)) {
			System.exit(0); // To work, we need to add actionlisteners to... (for each menu item -> 'mi.addActionListener(this);')
		}
		
	}
		
	}


class MyPanel extends JPanel implements Runnable, KeyListener, Printable{

	Shape obj1 = null;  //rectangle horizontal creation
	Shape obj2 = null;	//rectangle vertical creation
	Shape player = null;   // player of game~
	Shape exit1 = null;
	
	private static final String Shape  = null;
	
	int ang = 0;
	int r = 15;
	int tx= 60;
	int ty = 340;
	int vx= 0;
	int vy = 0;

	AffineTransform at = new AffineTransform();   // created affineTransform to transform objects
	
	public MyPanel() {
		setPreferredSize(new Dimension(400,400));
		setBackground(Color.WHITE);
		
		this.addKeyListener(this);   //adicionado a class dinamica key listener para as classes de ouvir teclas serem usadas
		this.setFocusable(true);   //por o programa em modo focado
		
		// Create a new thread
		Thread thread = new Thread(this); //esta classe faz o programa ser continuo e não estatico  
		thread.start();
		
	}
	
	public void  paintComponent (Graphics g) {   // Metodo existente em panel que vamos reescrever 
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		//Draw game state
		g2.setColor(Color.BLACK);  //setted background color
		g2.fillRect(0,0,400,400);
		
		drawAll(g2);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		switch(keyCode) {
		case KeyEvent.VK_LEFT:
			vx = -10;
			vy = 0;
			break;
		case KeyEvent.VK_RIGHT:
			vx = 10;
			vy = 0;
			break;
		case KeyEvent.VK_UP:
			vx = 0;
			vy = -10;
			break;
		case KeyEvent.VK_DOWN:
			vx = 0;
			vy = 10;
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
			if(obj1 != null)//pois os obj1 e obj2 não estao ativos no uso desta classe (é criada antes do main)
			if(obj1.contains(tx,ty) || obj2.contains(tx,ty)) {
				tx=60;
				ty = 340;
			}
			
			//***********************
			//BORDER COLLISION
			//***********************
			
			if(tx - r < 0) 	tx = r;
			else if(tx+r>400) tx = 400 -r;
			
			if(ty - r < 0) ty = r;
			else if(ty + r >400) ty = 400-r;
			
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
		obj1 = new Rectangle2D.Double(-100,-10,200,20);  //made the shape of rectangles
		obj2 = new Rectangle2D.Double(-10,-100,20,200);
			
		exit1 = new Rectangle2D.Double(-15,-15,30,30);
			
		player = new Heart(-r,-r,2*r,2*r);
		
		//***********************
		//SETTING UP PLAYER
		//***********************
		g2.setColor(Color.YELLOW);
		at.setToTranslation(tx,ty);
		at.scale(2, 2);
		player = at.createTransformedShape(player);
		g2.setColor(Color.YELLOW);
		g2.fill(player);
		
		// set transparency
	    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	    AlphaComposite ab = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
	    g2.setComposite(ac);
	    g2.setColor(Color.blue);
	    
	    // draw transparent text
	    Font font = new Font("Serif", Font.BOLD, 120);
	    g2.setFont(font);
	    g2.drawString("Java", 120, 200);
	    
	    // set transparency back
	    g2.setComposite(ab);
	    
		//***********************
		//SETTING UP RED CROSS
		//***********************
		at.setToTranslation(120,120);   // moved at
			
		at.rotate(Math.toRadians(ang));  // turned at
			
		obj1 = at.createTransformedShape(obj1);  //at became obj1 and equaled to obj1 
		obj2 = at.createTransformedShape(obj2);
		g2.setColor(Color.RED);
		g2.fill(obj1);
		g2.fill(obj2);
		
		//***********************
		//SETTING UP Exit
		//***********************
		g2.setColor(Color.GREEN);
		at.setToTranslation(380,380);
		exit1 = at.createTransformedShape(exit1);
		g2.setColor(Color.GREEN);
		g2.fill(exit1);
			
		
		
	}

		@Override
		public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
			switch(pageIndex) {
			case 0: //page 0, if there is another page is case 1:
				drawAll((Graphics2D) g); //drawing for printer to print, uses graphics from class
				break;
			default:
				return NO_SUCH_PAGE;
			}
			
			
			return 0;//If there are more pages substitute the 0 with PAGE_EXISTS
		
		}
		
		public class Heart implements Shape {
			GeneralPath path;
			public Heart(float x, float y, float w, float h) {
			    path = new GeneralPath();
			    float x0 = x + 0.5f*w;
			    float y0 = y + 0.3f*h;
			    float x1 = x + 0.1f*w;
			    float y1 = y + 0f * h;
			    float x2 = x + 0f * w;
			    float y2 = y + 0.6f * h;
			    float x3 = x + 0.5f * w;
			    float y3 = y + 0.9f * h;
			    float x4 = x + 1f * w;
			    float y4 = y + 0.6f * h;
			    float x5 = x + 0.9f * w;
			    float y5 = y + 0f * h;
			    path.moveTo(x0, y0);
			    path.curveTo(x1, y1, x2, y2, x3, y3);
			    path.curveTo(x4, y4, x5, y5, x0, y0);
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