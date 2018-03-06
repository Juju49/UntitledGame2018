package com.bonhomi.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.bonhomi.main.Core;
import com.bonhomi.main.Core.GameState;
import com.bonhomi.main.InputManager;
import com.bonhomi.main.Loopable;
import com.bonhomi.main.SpriteLoader;
import com.bonhomi.sounds.SoundOccurence;


/**
 * Cette classe gere le jeu en lui meme, le joueur,
 * les enemis, les niveaux...
 */
public class GameManager implements Loopable {

	static private boolean initialized = false;
	static final Rectangle window = new Rectangle(0, 0, Core.WIDTH, Core.HEIGHT);
	
	//écrans fixes
	static private final BufferedImage loadingImg = (new SpriteLoader("UI/game/", "Loading"))
			.getActualImage();
	static private final BufferedImage gamefinishedImg = (new SpriteLoader("UI/game/", "gameFinished"))
			.getActualImage();
	static private final BufferedImage gameOverImg = (new SpriteLoader("UI/game/", "gameOver"))
			.getActualImage();
	

	
	static Player player1;
	static NavMesh nav_mesh;
	
	static private Timer damageTimer;
	static ArrayList<VictoryItem> victoryList;
	
	
	static GameUI GUI;
	
	static protected Niveau niveau1;
	static protected GameMode gameMode = GameMode.RUN;
	
	private float time;
	
	// Constructeur
	public GameManager()
	{
		GUI = new GameUI();
		damageTimer = new Timer("GestionDegats");
		nav_mesh = new NavMesh();
		niveau1 = new Niveau();
		victoryList = new ArrayList<VictoryItem>();
		
		init();
	}
	
	/**
	 * Procedure gerant les degats sur le joueur avec un chronometre et des 
	 * frames d'invincibilite.
	 */
	private void playerDamage()
	{
		SoundOccurence ouille = new SoundOccurence("sfx/ouille_0","sfx/ouille_1");
		//gestion des degats avec un chronometre qui inflige toutes les 0.3s
		damageTimer.scheduleAtFixedRate(
			new TimerTask()
			{
				@Override
				public void run() 
				{
					if (player1.getVie() <= 0)
						this.cancel();
					
					Entity[] eList = niveau1.getActualRoom().getEntityList();
					for (Entity e : eList)
					{
						/* on retire des point de vie au joueur si il touche un
						 * ennmis et qu'il lui reste des vies
						 */
						if (player1.intersects(e) && e.isEnnemy() && (e.getVie() > 0))
						{
							player1.perdreVie();
							ouille.Play(false);
						}
					}
				}
			},
			0,
			Core.DELAIS_INVULNERABILITE
		);
	}
	
	public static void PlayerAttack(Entity e)
	{
		if(InputManager.mouseLeftCliked())
		{
			Point mXY = InputManager.getMouseXY();
			if( e.contains(mXY) )
				if( e.getVie() > 0 )
					e.perdreVie();
		}
	}
	
	@Override
	public void init()
	{
		if (niveau1.isLoaded())
		{
			player1 = new Player(200, 300, 1);
			
			//initialisations...
			player1.init();
			GUI.init();
			
			damageTimer.purge();
			nav_mesh.purge();
			
			niveau1.init();
			
			//on selectionne le joueur 1 en tant qu'entité a surveiller avec l'UI
			GUI.setPlayerFocus(player1);
			
			//on lance le fil d'execution gerant les degats:
			playerDamage();
			
			initialized = true;
		}
		else
			niveau1.startLoading();
	}

	// Affichage
	@Override
	public void draw(Graphics2D g) 
	{
		//on dessine l'écran de chargement lorsque le niveau n'est pas chargé
		if (!niveau1.isLoaded())
		{
			//dessin de l'ecran de chargement
			g.drawImage(loadingImg, 0, 0, null);
			
			//on enregistre l'ancienne fonte pour la replacer après
			final Font recup_font = g.getFont();
			
			//nouvelle fonte à afficher
			final int fontSize = 100;
			final Font font = new Font("Monospaced", Font.BOLD, fontSize);
			g.setFont(font);
			
			//clignotement du texte chargement avec la fonction Math.cos(a)
			g.setColor(new Color( 0.95f, 0.95f, 0.5f, (float) ( Math.abs(Math.sin(time)))));
			if( time >= Math.PI*2)
				time = 0;
			else
				time += Math.PI/75;
			
			//dessin du texte
			g.drawString("Chargement",
					(int) (Core.WIDTH/2 - g.getFontMetrics().stringWidth("Chargement")/2),
					Core.HEIGHT/2);
			
			g.setFont(recup_font);
			
			return;
		}
		
		//boucle des gagnants
		if(victoryList.size() >= VictoryItem.getMaxItems())
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g.drawImage(gamefinishedImg, 0, 0, null);
			return;
		}
		
		if(player1.getVie() == 0)
		{
			g.drawImage(gameOverImg, 0, 0, null);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
			
		g.setColor(Color.white);
		niveau1.draw(g);
		
		//un dessin ne ressort de là que si le debug est activé
		nav_mesh.draw(g);
		
		
		g.setColor(Color.green);
		player1.draw(g);
		
			
		g.setColor(Color.blue);
		GUI.draw(g);
	}
	
	/**
	 * mise a jour.
	 */
	public void update() 
	{
		if (!niveau1.isLoaded())
			return;
		else if (!initialized)
			init();

		if(!initialized) 
			throw new IllegalStateException("Class Updated before Init!");
		
		//boucle des gagnants et des perdants
		if((victoryList.size() >= VictoryItem.getMaxItems()) || (player1.getVie() == 0))
		{
			//on arrete la gestion des degats
			damageTimer.cancel();
			
			//on quitte le jeu au clic gauche
			if(InputManager.mouseRightCliked())
				Core.gameState = GameState.MENU;
			return;
		}
		
		//on nettoie le nav_mesh pour les entités dynamiques
		nav_mesh.purge();
		
		//le niveau régénère le nav_mesh par la methode statique
		niveau1.update();
		
		//mise à journ du joueur
		player1.update();
		nav_mesh.addObs(player1.ObsComp());
		
		GUI.update();
		

	}

	@Override
	public void terminate() 
	{
		if(!initialized)
			return;
		
		niveau1.terminate();
		GUI.terminate();
		player1.terminate();
		
		initialized = false;
	}

}
