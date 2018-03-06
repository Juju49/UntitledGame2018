/**
 * 
 */
package com.bonhomi.game;

import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import com.bonhomi.main.Core;
import com.bonhomi.main.SpriteLoader;

/**
 * @author Julian
 *
 */
public class BadGuys extends Entity {
	private static final long serialVersionUID = 1L;
	
	/**
	 * le centre du npc va se deplacer jusqu'a ce point de l'ecran si il le peut
	 */
	protected Point Cible;
	
	/**
	 * 
	 */
	BadGuys(int x, int y, double scale)
	{
		super(x, y, scale);
		this.life = ThreadLocalRandom.current().nextInt(
				Core.MAX_VIE, //vie mini
				(int) (Core.MAX_VIE * Core.DIFFICULTE)); /*vie maxi possible
				(DIFFICULTE est un double)*/
		
		/**
		 * En pixels par secondes, genere aleatoirement au lancement en 
		 * fonction de la difficulte. Entre 1 et 1+DIFFICULTE
		 */
		this.speed = ThreadLocalRandom.current().nextInt(1, 1 + (int) Core.DIFFICULTE);
	}

	public boolean isEnnemy()
	{
		return true;
	}
	
	/**
	 * le perso bouge si il n'a pas atteint sa cible et
	 *  que cette derniere existe
	 */
	@Override
	protected Point characterTranslation()
	{
		Point delta_translate = super.characterTranslation();
		
		if(!entity_sprite.contains(Cible) && (Cible != null))
		{
			//on identifie le déplacement à effectuer:
			if(entity_sprite.getTrackingPoint().getX() < Cible.getX())
			{
				delta_translate.x += speed;
			}
			
			if(entity_sprite.getTrackingPoint().getX() > Cible.getX())
			{
				delta_translate.x -= speed;
			}
			
			if(entity_sprite.getTrackingPoint().getY() < Cible.getY())
			{
				delta_translate.y += speed;
			}
			
			if(entity_sprite.getTrackingPoint().getY() > Cible.getY())
			{
				delta_translate.y -= speed;
			}
		}
		return delta_translate;
	}
	/* (non-Javadoc)
	 * @see com.npc.main.Loopable#init()
	 */
	@Override
	public void init() {
		super.init();
		
		anims.replace(null, new SpriteLoader("Characters/badguys/", "avant", 
				true, true, 100));
		
		//on charge les sprites utilises pour les méchants
		anims.put("avant", new SpriteLoader("Characters/badguys/", "avant", 
				true, true, 100));
		anims.put("gauche", new SpriteLoader("Characters/badguys/", "gauche", 
				true, true, 100));
		anims.put("mort", new SpriteLoader("Characters/badguys/", "mort"));
	}
	
	@Override
	protected void perdreVie() {
		super.perdreVie();
		/*if(life > 0)
			SoundSystemMaster.getInstance().ennemyHit();*/
	}
	
}