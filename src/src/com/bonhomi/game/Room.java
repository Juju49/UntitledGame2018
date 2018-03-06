package com.bonhomi.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.bonhomi.main.Core;
import com.bonhomi.main.Loopable;
import com.bonhomi.main.SpriteLoader;
import com.bonhomi.main.SpriteOccurence;

public class Room implements DoorsPosition, Loopable
{
	private final boolean[] doors;
	private final Point location;
	
	private SpriteOccurence salleOccurence;
	private final SpriteLoader salleSprite;
	
	private SpriteOccurence[] doorOccurences; 
	private final SpriteLoader doorSprite;
	
	private final ArrayList<Entity> entites;
	private ThreadLocalRandom randGen = ThreadLocalRandom.current();
	
	Room()
	{
		location = new Point(-1,-1);
		
		doorOccurences = new SpriteOccurence[4];
		doors = new boolean[4];
		
		salleSprite = new SpriteLoader("Rooms/", "background");
		salleOccurence = new SpriteOccurence(salleSprite.getActualImage(), 
				0, 0, 0, 1.0, 1.0);
		
		doorSprite = new SpriteLoader("Rooms/", "door");
		//init des portes
		for (int i = 0; i < 4; i++)
		{
			doors[i] = CLOSED;
			
			doorOccurences[i] = new SpriteOccurence(doorSprite.getActualImage(), 
					0, 0, Math.toRadians(i*90), 1.0, 1.0);
		}
		
		entites = new ArrayList<Entity>(4);
		
		init();
	}
	
	@Override
	public void init() {
		//positionnement des portes
		final int widthPortes = (int) doorSprite.getActualImage().getWidth();
		final int heightPortes = (int) doorSprite.getActualImage().getHeight();
		
		//top door
		doorOccurences[DoorSide.TOP.ordinal()].newTransforms(Core.WIDTH/2 - widthPortes/2, OFFSET_MURS - heightPortes,
				0, 1.0, 1.0);
		//left door
		doorOccurences[DoorSide.LEFT.ordinal()].newTransforms(OFFSET_MURS - heightPortes, Core.HEIGHT/2 - widthPortes/2,
				Math.PI/2, 1.0, 1.0);
		
		//bottom door
		doorOccurences[DoorSide.BOT.ordinal()].newTransforms(Core.WIDTH/2 - widthPortes/2, Core.HEIGHT - OFFSET_MURS,
				0, 1.0, 1.0);
		doorOccurences[DoorSide.BOT.ordinal()].setFlipY(true);
		
		//right door
		doorOccurences[DoorSide.RIGHT.ordinal()].newTransforms(Core.WIDTH - OFFSET_MURS, Core.HEIGHT/2 - widthPortes/2,
				Math.PI/2, 1.0, 1.0);
		doorOccurences[DoorSide.RIGHT.ordinal()].setFlipX(true);
		
		//création des ennemis
		int maxEnnemies = randGen.nextInt(1, (int) (Core.DIFFICULTE) +1 );
		
		for (int i = 0; i < maxEnnemies; i++) 
		{
			int x = randGen.nextInt( OFFSET_MURS, Core.WIDTH  - OFFSET_MURS*2 - 128);
			int y =	randGen.nextInt( OFFSET_MURS, Core.HEIGHT - OFFSET_MURS*2 - 128);
			
			entites.add(new BadGuys(x, y, 1));
		}
		
		
		for (Entity e : entites)
		{
			e.init();
		}
	}
	
	@Override
	public void update()
	{
		//navigation sol
		final Shape[] compo_navigation = new Shape[5];
		compo_navigation[0] = new Rectangle(
				OFFSET_MURS, OFFSET_MURS, 
				Core.WIDTH  - OFFSET_MURS*2, Core.HEIGHT - OFFSET_MURS*2)
				.getBounds2D();
		
		//navigation portes
		for (int i=0; i < doors.length; i++)
		{
			if (doors[i] == OPENED)
			{
				compo_navigation[i+1] = (doorOccurences[i].getBounds2D());
			}
		}
		
		//on ajoute le tout au nav_mesh
		GameManager.nav_mesh.addNav(compo_navigation);
		
		//on récupère la position du joueur pour usage ultérieur
		Point posJoueur = new Point(
				(int) GameManager.player1.getCenterX(), 
				(int) GameManager.player1.getCenterY());
		
		
		
		//gestion des ennemis et obstacles
		for (Entity e : entites)
		{
			GameManager.PlayerAttack(e);
			
			//on ajoute les ponts ou autres navigations supplémentaires
			GameManager.nav_mesh.addNav(e.NavComp());
			
			//poursuite du joueur par des ennemis
			if(e.isEnnemy())
			{
				BadGuys b_g = (BadGuys) e;
				
				if (b_g.Cible != null)
					b_g.Cible.setLocation(posJoueur);
				else
					b_g.Cible = posJoueur;
			}
			else
				//colisions des entités
				GameManager.nav_mesh.addObs(e.ObsComp());
			
			e.update();
		}
		
		//passage des portes
		playerPassDoor(posJoueur);
		
	}

	/**
	 * place le joueur devant une porte de la salle
	 * 
	 * @param entityToMove
	 * @param door
	 */
	public void setEntityAtDoor(Entity entityToMove, DoorSide door)
	{
		if(door == null || entityToMove == null)
			return;
		
		
		Rectangle rectEntity = entityToMove.getBounds();
		Point locEntity = new Point(rectEntity.getLocation());
	
		locEntity = doorOccurences[door.ordinal()].getLocation();

		switch(door)
		{
			case TOP:
				locEntity.y += doorOccurences[door.ordinal()].height;
				break;
				
			case BOT:
				locEntity.y -= rectEntity.height;
				break;
				
			case LEFT:
				locEntity.x += doorOccurences[door.ordinal()].width;
				break;
				
			case RIGHT:
				locEntity.x -= rectEntity.width;
				break;
				
			default:
				throw new IllegalArgumentException("setPlayerAtDoor: Invalid door index!");
		}
		entityToMove.setLocation(locEntity);
	}
	
	/**
	 * Détection du passage d'une porte par une entité se trouvant en <code>posJour</code>.
	 * 
	 * 
	 * @param posJoueur
	 */
	public void playerPassDoor(Point posJoueur)
	{
		for (int i=0; i < doors.length; i++)
		{
			if (doors[i] == OPENED && doorOccurences[i].contains(posJoueur))
			{
				int new_x = location.x;
				int new_y = location.y;
				
				switch(DoorSide.values()[i])
				{
					case TOP:
						new_y--;
						break;
						
					case BOT:
						new_y++;
						break;
						
					case LEFT:
						new_x--;
						break;
						
					case RIGHT:
						new_x++;
						break;
						
					default:
						break;
				}
				
				GameManager.niveau1.setActualRoom(new_x, new_y, 
						Niveau.invertWall(DoorSide.values()[i]));
			}
		}
	}
	
	
	@Override
	public void draw(Graphics2D g) 
	{
		g.setColor(Color.white);
		salleOccurence.draw(g);
		
		g.setColor(Color.white);
		for (int i=0; i < doors.length; i++)
		{
			if (doors[i] == OPENED)
				doorOccurences[i].draw(g);
		}
		
		g.setColor(Color.red);
		for (Entity e : entites)
		{
			e.draw(g);
		}
	}
	
	
	/**
	 * Définition des portes ouvertes de la salle d'après leurs positions.
	 * 
	 * @param top
	 * @param bot
	 * @param left
	 * @param right
	 */
	synchronized void setDoorsOpened(boolean top, boolean bot, boolean left, boolean right)
	{
		doors[DoorSide.TOP.ordinal()] = top;
		doors[DoorSide.BOT.ordinal()] = bot;
		doors[DoorSide.LEFT.ordinal()] = left;
		doors[DoorSide.RIGHT.ordinal()] = right;
	}
	
	boolean isDoorOpened(DoorSide door)
	{
		return doors[door.ordinal()];
	}
	
	/**
	 * retourne la liste des portes ouvertesde : TOP à RIGHT
	 * 
	 * 
	 * @return liste booléenne
	 */
	public boolean[] getDoorsOpened()
	{
		return doors;
	}
	
	/**
	 * Ouverture / fermeture d'une porte
	 * 
	 * @param door
	 * @param value
	 */
	synchronized void setDoorOpened(DoorSide door, boolean value)
	{
		doors[door.ordinal()] = value;
	}
	
	/**
	 * Obtention de la localisation x y de la salle dans la map
	 * 
	 * @return
	 */
	Point getLocation()
	{
		return location;
	}
	synchronized void setLocation(int x, int y)
	{
		this.location.setLocation(x, y);
	}	
	synchronized void setLocation(Point p)
	{
		this.location.setLocation(p);
	}
	
	
	synchronized void newEntity(Entity e)
	{
		entites.add(e);
	}
	synchronized void delEntity(Entity e)
	{
		entites.remove(e);
	}
	Entity[] getEntityList()
	{
		Entity[] a = new Entity[entites.size()];
		entites.toArray(a);
		return a;
	}

	
	public int getNDoorsOpened()
	{
		int n = 0;
		
		for (int i = 0; i < doors.length; i++)
		{
			if (doors[i] == OPENED) n++;
		}
		
		return n;
	}


	@Override
	public void terminate() {
		for (int i = 0; i < 4; i++)
		{
			doors[i] = CLOSED;
			
			doorOccurences[i] = new SpriteOccurence(doorSprite.getActualImage(), 
					0, 0, Math.toRadians(i*90), 1.0, 1.0);
		}
		
		for (Entity e : entites)
		{
			e.terminate();
		}
		
		entites.clear();
	}


}
