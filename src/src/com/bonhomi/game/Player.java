/**
 * 
 */
package com.bonhomi.game;

import java.awt.Point;
import java.awt.event.KeyEvent;

import com.bonhomi.main.Core;
import com.bonhomi.main.InputManager;
import com.bonhomi.main.SpriteLoader;

/**
 * @author Julian
 *
 */
public final class Player extends Entity {
	private static final long serialVersionUID = 1L;

	Player(int x, int y, double scale)
	{
		super(x, y, scale);
		this.life = Core.MAX_VIE;
		this.speed = 5;
	}
	
	
	@Override
	protected void perdreVie()
	{
		super.perdreVie();
		if (life <= 0)
		{
			Core.out.println("Bonhomi est mort!");
		}

	}
	
	@Override
	protected void extraVie() {
		super.extraVie();
		life = Core.MAX_VIE;
	}
	

	/* (non-Javadoc)
	 * @see com.bonhomi.main.Loopable#init()
	 */
	@Override
	public void init() {
		super.init();
		
		//on change l'anim de base:
		anims.replace(null, new SpriteLoader("Characters/bonhomi/", "avant", 
				true, true, 100));
		
		//on charge les sprites utilises pour le joueur
		anims.put("avant", new SpriteLoader("Characters/bonhomi/", "avant", 
				true, true, 100));
		anims.put("gauche", new SpriteLoader("Characters/bonhomi/", "gauche", 
				true, true, 100));
		anims.put("mort", new SpriteLoader("Characters/bonhomi/", "mort"));

	}

	@Override
	protected Point characterTranslation()
	{
		Point delta_translate = super.characterTranslation();
		
		if(InputManager.isKeyDown(KeyEvent.VK_S))
		{
			delta_translate.y += 5;
		}
		
		if(InputManager.isKeyDown(KeyEvent.VK_Z))
		{
			delta_translate.y -= 5;
		}
		
		if(InputManager.isKeyDown(KeyEvent.VK_D))
		{
			delta_translate.x += 5;
		}
		
		if(InputManager.isKeyDown(KeyEvent.VK_Q))
		{
			delta_translate.x -= 5;
		}
		return delta_translate;

	}
}
