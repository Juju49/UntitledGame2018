package com.bonhomi.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import com.bonhomi.game.GameManager;
import com.bonhomi.menu.MainMenu;
import com.bonhomi.menu.PauseMenu;

/**
 * Cette classe gere l'affichage et 
 * la boucle de jeu
 */
public class Afficheur extends JPanel implements Runnable 
{
	private static final long serialVersionUID = 11109134671002595L;
	
	private Graphics2D graphics;
	private boolean running = false;
	private Thread thread;
	private float frames = 0;
	
	// "ecrans" a charger
	private GameManager gameManager;
	private MainMenu mainMenu;
	private PauseMenu pauseMenu;

	// Constructeur
	public Afficheur()
	{
		setPreferredSize(new Dimension(Core.WIDTH, Core.HEIGHT));
		setDoubleBuffered(true);
	}
	
	// Lancement du thread
	public synchronized void start()
	{
		if (running)
			return;
		
		running = true;
		thread = new Thread(this, "Bonhomi main Win");
		thread.start();
	}
	

	/**
	 * Updates game according to states.
	 * Fired at each frame, it updates children too.
	 */
	public void update()
	{
		switch (Core.gameState)
		{
			case MENU:
				// Chargement mainMenu
				if(mainMenu == null) 
				{
					mainMenu = new MainMenu();
				} 
				else if ((gameManager != null)) 
				{
					gameManager.terminate();
					gameManager = null;
				}
				if ((pauseMenu != null)) 
				{
					pauseMenu.terminate();
					pauseMenu = null;
				}
				// Update mainMenu
				else 
				{
					mainMenu.update();
				}
				break;
				
			case PAUSE:
				/*can only be invoked from GAME state,
				nothing must unload now.*/
				if(pauseMenu == null) {
					pauseMenu = new PauseMenu();
					
				} else {
					pauseMenu.update();
				}
				break;
				
			case GAME:
				
				/* closing mainMenu;
				 * pauseMenu is not unloaded,
				 * it might serve under short notice*/
				if(mainMenu != null) 
				{
					mainMenu.terminate();
					mainMenu = null;
				} 
				else if(gameManager == null)
				{
					gameManager = new GameManager();
				} 
				else 
				{
					gameManager.update();
				}
				break;
				
			default:
				throw new Error("invalid gameState");
		}
	}
	
	// Affichage
	public void draw(Graphics g)
	{
		graphics = (Graphics2D) g;
		switch (Core.gameState)
		{
			case MENU:
				if (mainMenu != null)
				mainMenu.draw(graphics);
				break;
			case PAUSE:
				if (pauseMenu != null)
				pauseMenu.draw(graphics);
				break;
			case GAME:
				if (gameManager != null)
				gameManager.draw(graphics);
				break;
			default:
				throw new Error("invalid gameState");
		}
	}
	
	// Permet de "dessiner" sur le JPanel
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
		
		if(MainClass.getDebugLvl() >= 3) 
		{
			g.setColor(Color.blue);
			g.drawString(("Keys : [" + InputManager.getKeySetAsString() + "]"), 4, 20);
			g.drawString(("Mouse : [" + InputManager.getMouseSetAsString() + "]"), 4, 30);
		}
		if( MainClass.getDisplayFps())
			g.drawString("FPS  : " + String.valueOf(1000/frames), 4, 10);
	}

	// Boucle de jeu
	@Override
	public void run() 
	{
		// Variable de temps
		double lastTime = (double) System.currentTimeMillis();
		double nowTime = (double) System.currentTimeMillis();
		double timeElapsed = 0d;
		
		// Horloge du jeu
		while (running)
		{
			nowTime = System.currentTimeMillis();
			Core.deltaTime = nowTime - lastTime;
			lastTime = nowTime;
			
			// Mise a jour + affichage
			update();
			repaint();
			
			timeElapsed = (double) System.currentTimeMillis() - nowTime;
			
			frames = 
					(float) ((float) (1000 / Core.WANTED_FPS) - 
							(timeElapsed));
			
			if ((1000 / Core.WANTED_FPS) > (timeElapsed))
			{
				try 
				{
					Thread.sleep((long) (frames));
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}