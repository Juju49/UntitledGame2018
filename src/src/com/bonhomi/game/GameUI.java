/**
 * 
 */
package com.bonhomi.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.bonhomi.main.Core;
import com.bonhomi.main.Loopable;
import com.bonhomi.main.SpriteLoader;
import com.bonhomi.main.SpriteOccurence;

/**place une interface utilisateur sur le jeu.
 * 
 * 
 * @author Julian
 *
 */
class GameUI implements Loopable {
	private boolean initialized = false;
	
	protected Player focused_player;
	
	private SpriteLoader[] sante_sprites = new SpriteLoader[3];
	private SpriteLoader[] face_sprites = new SpriteLoader[1];
	
	private SpriteOccurence[] sante_display = new SpriteOccurence[Core.MAX_VIE];
	private SpriteOccurence face_display;
	
	/* (non-Javadoc)
	 * @see com.bonhomi.main.Loopable#init()
	 */
	@Override
	public void init() {
		//chargement des images:
		sante_sprites[0] = new SpriteLoader("UI/game/", "nolife_0");//coeur vide
		sante_sprites[1] = new SpriteLoader("UI/game/", "life_0");//coeur plein
		sante_sprites[2] = new SpriteLoader("UI/game/", "life", 
				true, true, 90);//dernier coeur clignotant
		face_sprites[0] = new SpriteLoader("UI/game/", "faceOk_0");//visage souriant
		
		
		//on dispose les coeurs sur l'ecran
		int coord_x_coeurs = 830; int coord_y_coeurs = 20;
		for(int i = 0; i < Core.MAX_VIE; i++)
		{
			sante_display[i] = new SpriteOccurence(sante_sprites[1].getActualImage(), 
					coord_x_coeurs, coord_y_coeurs, 0, 2.8125, 2.8125);
			coord_x_coeurs -= 58;
			
			//on verifie que les coeurs ne sortent pas de l'ecran a gauche
			if(coord_x_coeurs < 0)
			{
				coord_x_coeurs = 830; coord_y_coeurs += 58;
			}
		}
		
		//on dispose le visage de bonhomi sur l'ecran
		face_display = new SpriteOccurence(face_sprites[0].getActualImage(), 
				898, 20, 0, 2.71875, 2.71875);
		
		//on demarre les animations
		for(SpriteLoader sprites : sante_sprites) sprites.start();
		for(SpriteLoader sprites : face_sprites)  sprites.start();
		
		initialized = true;
	}

	/**
	 * Lie le joueur qui sera surveille a l'UI.
	 * 
	 * 
	 * @param player <code>Player</code>
	 */
	void setPlayerFocus(Player player)
	{
		focused_player = player;
	}
	
	/* (non-Javadoc)
	 * @see com.bonhomi.main.Loopable#update()
	 */
	@Override
	public void update() {
		if(!initialized) 
			throw new IllegalStateException("Class Updated before Init!");
		if(focused_player == null) 
			throw new IllegalStateException("UI: pas de joueur defini!");
		
		/* on recupere la vie du joueur, 
		 * puis on remplis le nombre de coeurs en fonction de la vie actuuelle
		 * le dernier coeur de la vie actuelle clignote
		 * les autres coeurs sont gris
		 */
		int vie = focused_player.getVie();
		
		for(int i = 0; i < Core.MAX_VIE; i++)
		{
			if(i < (vie - 1))
				sante_display[i].setImage(sante_sprites[1].getActualImage());
			else if(i == (vie - 1))
				sante_display[i].setImage(sante_sprites[2].getActualImage());
			else 
				sante_display[i].setImage(sante_sprites[0].getActualImage());
		}
	}

	/* (non-Javadoc)
	 * @see com.bonhomi.main.Loopable#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		//on dessine les occurences
		face_display.draw(g);
		
		for(SpriteOccurence coeur : sante_display)
		{
			coeur.draw(g);
		}
		
		//on enregistre l'ancienne fonte pour la replacer après
		final Font recup_font = g.getFont();
		
		//nouvelle fonte à afficher
		final int fontSize = 32;
		final Font font = new Font("Arial", Font.PLAIN, fontSize);
		g.setFont(font);
		g.setColor(Color.green);

		//dessin du texte
		final String afficher = "Gouter: " + GameManager.victoryList.size() + 
				" / " +  VictoryItem.getMaxItems();
		
		//on obtient la largeur du texte afin de le positionner dans la fenetre
		final int textWidth = g.getFontMetrics().stringWidth(afficher);
		
		g.drawString(afficher, 
				(Core.WIDTH - (textWidth + 5)), 
				(Core.HEIGHT));
		
		//on remet l'ancienne fonte en place
		g.setFont(recup_font);
	}

	/* (non-Javadoc)
	 * @see com.bonhomi.main.Loopable#terminate()
	 */
	@Override
	public void terminate() {
		face_display = null;
		
		initialized = false;
	}

}
