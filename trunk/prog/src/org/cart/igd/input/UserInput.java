package org.cart.igd.input;

import java.awt.Component;
import java.awt.event.*;
import java.awt.Robot;
import javax.swing.SwingUtilities;

import org.cart.igd.Display;

public class UserInput implements KeyListener, MouseListener, MouseMotionListener
{
	public static boolean[] keys = new boolean[256];
	public static int[] mousePos = new int[] { 0, 0 };
	public static int[] mousePress = new int[]{0,0	};
	public static boolean typingEnabled = false;
	
	public static Robot robot;

	public UserInput(Component comp)
	{
		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		comp.setFocusTraversalKeysEnabled(false);
		
		try
		{
			robot = new Robot();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static  boolean isSquareButtonPressed(int bx, int by, int r,int mx, int my)
	{
		if( bx<mx && bx+(r*2)>mx && by<my && (by+(r*2))>my )
		{
			return true;
		}
		return false;
	}	
	
	/** test whether a circular button is pressed */
	public static boolean isRoundButtonPressed(int xb, int yb, int br,int xm, int ym)
	{
		if( br > Math.sqrt( ((double)((xm-xb)*(xm-xb))+ ((ym-yb)*(ym-yb)))) )
		{
			return true;
		}
		return false;
	}
	
	public static boolean isKeyPressed(int vk) { return keys[vk]; }
	
	public void keyPressed(KeyEvent e)
	{
		keys[e.getKeyCode()] = true;
		e.consume();
	}
	
	public void keyReleased(KeyEvent e)
	{
		keys[e.getKeyCode()] = false;
		e.consume();
	}
	
	public void keyTyped(KeyEvent e)
	{
		e.consume();
	}
	
	public void mousePressed(MouseEvent e)
	{
		updateMousePress(e.getX(), e.getY());
	}
	
	public void mouseReleased(MouseEvent e)
	{
		updateMousePos(e.getX(), e.getY());
	}
	
	public void mouseClicked(MouseEvent e)
	{
		updateMousePos(e.getX(), e.getY());
	}
	
	public void mouseMoved(MouseEvent e)
	{
		updateMousePos(e.getX(), e.getY());
	}
	
	public void mouseDragged(MouseEvent e)
	{
		updateMousePos(e.getX(), e.getY());
	}
	
	public void mouseEntered(MouseEvent e)
	{
		updateMousePos(e.getX(), e.getY());
	}
	
	public void mouseExited(MouseEvent e)
	{
		updateMousePos(e.getX(), e.getY());
	}
	
	private void updateMousePress(int x,int y){
		mousePress[0] = x;
		mousePress[1] = Display.getScreenHeight()-y;
	}
	
	private void updateMousePos(int x, int y)
	{
		mousePos[0] = x;
		mousePos[1] = Display.getScreenHeight()-y;
	}
}