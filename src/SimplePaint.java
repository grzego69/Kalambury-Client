
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SimplePaint extends Applet implements MouseListener, MouseMotionListener, ActionListener 
{        
	private static final long serialVersionUID = 1L;
	JTextField output;
	JTextField pointsInput;
	JTextField  input;
	private int prevX, prevY;     // Poprzednia lokalizacj myszki
	private boolean dragging;      // true kiedy user przeciaga myszke
	private Graphics graphicsForDrawing;  // Graphics na którym rysujemy
	ArrayList<LineContainer> lcList = new ArrayList<LineContainer>();
	KalamburyClient kc; //obs³uga po³¹czenia z serwerem
	boolean canIDraw = false;
	JButton button;
	int points = 0;
   
   public void init() //inicjalizacja apletu od razu po uruchomieniu
   {
	   addMouseListener(this);
	   addMouseMotionListener(this);
	   try 
	   {
		kc = new KalamburyClient(this);
      	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
      
      	this.setLayout(null);
      	Insets insets = this.getInsets();
		//przycisk wyœlij
	  	button = new JButton("Wyœlij");
	  	this.add(button);
	    Dimension size = button.getPreferredSize();
	    button.setBounds(476 + insets.left, 374 + insets.top, 70, 23);
	    button.setVisible(true);
	    button.setActionCommand("Close");
	    button.addActionListener(this);
	    //pola wejscia/wyjscia gry
	    input  = new JTextField();  add(input,3,0);
	    input.setVisible(true);
	    input.setBounds(3 + insets.left, 373 + insets.top, 475, 25); 
		output  = new JTextField();  add(output,3,0);
	    output.setVisible(true);
	    output.setEditable(false);
	    output.setBounds(3 + insets.left, 351 + insets.top, 475, 23);
		output.setText("Czekaj na now¹ rundê");
		pointsInput  = new JTextField();  add(pointsInput);
		pointsInput.setVisible(true);
		pointsInput.setEditable(false);
		pointsInput.setBounds(480 + insets.left, 351 + insets.top, 62, 23);
		pointsInput.setText("Punkty: " + Integer.toString(points));
   }
      
   public void update(Graphics g) 
   {
	   	//tu przy repaint'cie
	   	paint(g);
		remove(input);
		remove(output);
		remove(button);
		Insets insets = this.getInsets();
		//przycisk wyœlij
		button = new JButton("Wyœlij");
		this.add(button);
		Dimension size = button.getPreferredSize();
		button.setBounds(476 + insets.left, 374 + insets.top, 70, 23);
		button.setVisible(true);
		button.addActionListener(this);
		//pola wejscia/wyjscia gry + pkt
		input  = new JTextField();  add(input,3,0);
		input.setVisible(true);
		input.setBounds(3 + insets.left, 373 + insets.top, 475, 25); 
		output  = new JTextField();  add(output,3,0);
		output.setVisible(true);
		output.setEditable(false);
		output.setBounds(3 + insets.left, 351 + insets.top, 475, 23);
		output.setText("Czekaj na now¹ rundê");
		pointsInput  = new JTextField();  add(pointsInput);
		pointsInput.setVisible(true);
		pointsInput.setEditable(false);
		pointsInput.setBounds(480 + insets.left, 351 + insets.top, 62, 23);
		pointsInput.setText("Punkty: " + Integer.toString(points));
   }
   
   public void paint(Graphics g) {

      int width = getSize().width;  
      int height = getSize().height;                              
      int colorSpacing = (height - 56) / 7;

      // bia³e t³o
      g.setColor(Color.white);
      g.fillRect(3, 3, width - 59, height - 6);


      g.setColor(Color.gray);
      g.drawRect(0, 0, width-1, height-1);
      g.drawRect(1, 1, width-3, height-3);
      g.drawRect(2, 2, width-5, height-5);
      g.drawRect(2, 2, width-5, height-29);
      g.drawRect(2, 2, width-5, height-52);

      g.fillRect(width - 56, 0, 56, height);

      /* przycisk Clear*/
      g.setColor(Color.white);
      g.fillRect(width-53,  height-53, 50, 50);
      g.setColor(Color.black);
      g.drawRect(width-53, height-53, 49, 49);
      g.drawString("CLEAR", width-48, height-23); 
      output.setText("Czekaj na now¹ rundê");
      pointsInput.setText("Punkty: " + Integer.toString(points));
      Insets insets = this.getInsets();
	  //przycisk wyœlij
	  button = new JButton("Wyœlij");
	  this.add(button);
	  Dimension size = button.getPreferredSize();
	  button.setBounds(476 + insets.left, 374 + insets.top, 70, 23);
	  button.setVisible(true);
	  button.addActionListener(this);
   } // end paint()
   
   private void setUpDrawingGraphics()
   {
      graphicsForDrawing = getGraphics();
      graphicsForDrawing.setColor(Color.black);
      
   } 

   public void mousePressed(MouseEvent evt) 
   {
           
      int x = evt.getX();   // x-coordinate where the user clicked.
      int y = evt.getY();   // y-coordinate where the user clicked.
      
      int width = getSize().width;    // Width of the applet.
      int height = getSize().height;  // Height of the applet.
      
      if (dragging == true)  // Ignore mouse presses that occur
          return;            //    when user is already drawing a curve.
                             //    (This can happen if the user presses
                             //    two mouse buttons at the same time.)
      
      
  //  Clicked on "CLEAR button".
      if (x > width - 53) 
      {
         if (y > height - 53)
         {
        	 if(canIDraw)
        	 kc.sendClearRequest();     
         }

      }
      else if (x > 3 && x < width - 56 && y > 3 && y < height - 57) 
      {
              // The user has clicked on the white drawing area.
              // Start drawing a curve from the point (x,y).
         prevX = x;
         prevY = y;
         dragging = true;
         setUpDrawingGraphics();
      }
      
   } // end mousePressed()
   

   public void mouseReleased(MouseEvent evt) 
   {
       // Przy puszczeniu przycisku myszy:
       // If the user was drawing a curve, the curve is done,
       // so we should set drawing to false and get rid of
       // the graphics context that we created to use during
       // the drawing.
       if (dragging == false)
          return;
       dragging = false;
       graphicsForDrawing.dispose();
       graphicsForDrawing = null;
   }
   
   public void mouseDragged(MouseEvent evt) 
   {
        
       if (dragging == false)
          return;  
          
       int x = evt.getX();   // x-coordinate of mouse.
       int y = evt.getY();   // y=coordinate of mouse.
       
       
       //Sprawdzanie czy x i y s¹ w polu przeznaczonym do rysowania
       if (x < 3)                         
          x = 3;                          
       if (x > getSize().width - 57)       
          x = getSize().width - 57;
          
       if (y < 3)                          
          y = 3;                           
       if (y > getSize().height - 52)       
          y = getSize().height - 52;
       if(canIDraw==true)
       {
	       graphicsForDrawing.drawLine(prevX, prevY, x, y);  // rysowanie lini z pkt
	       kc.strWrite = prevX +" " + prevY +" " + x +" " + y;
	       try {
			kc.sendData();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
       LineContainer tmp = new LineContainer(prevX, prevY, x, y);
       lcList.add(tmp);
       prevX = x; //poprzednie wspó³rzeden segmentu lini
       prevY = y;
       
   } // end mouseDragged.
   

   public void mouseEntered(MouseEvent evt) { }   // Some empty routines.
   public void mouseExited(MouseEvent evt) { }    //    (Required by the MouseListener
   public void mouseClicked(MouseEvent evt) { }   //    and MouseMotionListener
   public void mouseMoved(MouseEvent evt) { }     //    interfaces).
   
   public void stop()
   {	
	   kc.connectionClose();
	   destroy();
   }
   
   public void drawLine(String coordinates)
   {
	   String[] pXpYeXeY = coordinates.split(" ");
	   System.out.println("Wspó³rzêdne: " + Integer.valueOf(pXpYeXeY[0]) + " " + Integer.valueOf(pXpYeXeY[1]) + " " + Integer.valueOf(pXpYeXeY[2]) + " " +Integer.valueOf(pXpYeXeY[3]));
	   dragging=true;
	   setUpDrawingGraphics();
	   graphicsForDrawing.drawLine(Integer.valueOf(pXpYeXeY[0]), Integer.valueOf(pXpYeXeY[1]), Integer.valueOf(pXpYeXeY[2]), Integer.valueOf(pXpYeXeY[3]));
	   dragging=false;
   }
   
   @Override
	public void actionPerformed(ActionEvent ae)
	{
		String msg = input.getText();
		kc.sendAnserw(msg);
//		System.out.println("Wys³ane: " + msg);
		input.setText("");
	} 
   
//   public void setQuestionWord(String qw)
//   {
//	   qw=qw.toUpperCase();
//	   output.setText(qw);
//	   System.out.println("TEKST: " + qw);
//   }
   
   public class LineContainer
   {
   	int xStart, yStart, xEnd,yEnd;
   	
   	public LineContainer(int x1,int y1, int x2, int y2)
   	{
   		this.xStart = x1;
   		this.yStart = y1;
   		this.xEnd = x2;
   		this.yEnd = y2;
   		System.out.println(" x1: " + xStart + "y1: " + yStart + "x2: " + xEnd+ "y2: "+yEnd);
   	}
   	
   }
   
}

