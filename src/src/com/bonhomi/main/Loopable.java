package com.bonhomi.main;

import java.awt.Graphics2D;

public interface Loopable
{
	/**
	 * Must be the called before the first update.
	 * It is advised to use a {@code private boolean intialized}
	 * variable to keep track class' inner state.
	*/
	public void init();

	/**
	 * used for script updates.
	 * Graphical updates of this class or its children should go in {@link #draw(Graphics2D)}.
	 */
	public void update();
	
	/**
	 * render components on frame update.
	 * 
	 * 
	 * @param g 2D Graphical context to manipulate.
	 */
	public void draw(Graphics2D g);
	
	/**
	 * used when loop ends to unload resources.
	 */
	public void terminate();

}
