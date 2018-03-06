package com.bonhomi.main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

public class InputManager implements KeyListener, MouseListener, MouseMotionListener
{
	protected static Set<Integer> keysDown = new HashSet<Integer>();
	private static boolean leftClicked, rightClicked;
	
	private static int mouseX, mouseY;
    
	public static String getMouseSetAsString(){
		StringBuilder str = new StringBuilder();
		str.append("Mouse: LC ");
		str.append(String.valueOf(leftClicked));
		str.append(" ; RC ");
		str.append(String.valueOf(rightClicked));
		str.append(" ; x : ");
		str.append(String.valueOf(mouseX));
		str.append(" ; y : ");
		str.append(String.valueOf(mouseY));
		
		String final_s = str.toString();
		return final_s;
	}
	
	public static String getKeySetAsString(){
		StringBuilder str = new StringBuilder();
		
		for(int key : new HashSet<Integer>(keysDown)){
			
			str.append(KeyEvent.getKeyText(key));
			str.append(" ; ");
		}
		
		String final_s = str.toString();
		return final_s;
	}
	
	public static boolean isKeyDown(int k)
	{
		return keysDown.contains(k);
	}
	
	public static boolean isKeyUp(int k)
	{
		return (!isKeyDown(k));
	}
	
	public static boolean mouseLeftCliked()
	{
		return leftClicked;
	}

	public static boolean mouseRightCliked()
	{
		return rightClicked;
	}
	
	public static Point getMouseXY()
	{
		Point mXY = new Point(mouseX, mouseY);
		return mXY;
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		keysDown.add(e.getKeyCode());
	}
	
	@Override
	public void keyReleased(KeyEvent e) 
	{
		keysDown.remove(e.getKeyCode());
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) 
	{
		// Non utilise
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			leftClicked = true;
		}
		else if (e.getButton() == MouseEvent.BUTTON3)
		{
			rightClicked = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			leftClicked = false;
		}
		else if (e.getButton() == MouseEvent.BUTTON3)
		{
			rightClicked = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}
}
