/**
 * 
 */
package com.bonhomi.game;

import java.awt.Graphics2D;
import java.awt.Shape;

import com.bonhomi.main.Core;
import com.bonhomi.main.SpriteLoader;

/**
 * @author BORDECIEL
 *
 */
public class VictoryItem extends Entity {
	
	private int bonusType;
	private static final String[] items = {
			"chocolat",
			"compote",
			"lait",
			"orange",
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	VictoryItem(int numItem)
	{
		super(Core.WIDTH/2, Core.HEIGHT/2, 1.0);
		
		bonusType = numItem;
		life = 1;
		speed = 0;
	}

	@Override
	protected void perdreVie() 
	{
		
	}
	
	@Override
	protected void extraVie() 
	{
		
	}
	
	static public int getMaxItems()
	{
		return items.length;
	}
	
	public int getBonusType()
	{
		return bonusType;
	}
	
	/* (non-Javadoc)
	 * @see com.bonhomi.main.Loopable#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
		//on change l'anim de base:
		anims.replace(null, new SpriteLoader("items/", items[bonusType], 
				true, true, 100));
		
		entity_sprite.setImage(anims.get(null).getActualImage());
		
		setLocation(x - entity_sprite.width/2, y - entity_sprite.height/2);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(GameManager.player1.intersects(entity_sprite) && (life > 0))
		{
			GameManager.victoryList.add(this);
			GameManager.player1.extraVie();
			life = 0;
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(life > 0)
			super.draw(g);
	}
	
	@Override
	public Shape ObsComp() {
		super.ObsComp();
		
		return null;
	}
	
	
}
