/**
 * 
 */
package com.bonhomi.game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import com.bonhomi.main.Loopable;
import com.bonhomi.main.SpriteLoader;
import com.bonhomi.main.SpriteOccurence;

/**
 * @author Julian
 *
 */
abstract class Entity extends Rectangle implements Loopable {
	private static final long serialVersionUID = 1L;

	protected boolean initialized = false;
	
	protected int life;
	protected double speed = 0;
	
	protected SpriteOccurence entity_sprite; //sprite du perso
	protected final HashMap<String, SpriteLoader> anims = new HashMap<String, SpriteLoader>(); //dictionnaire des annimations
	protected double scale = 1.0; //taille de l'occurence par rapport a la taille de l'image d'origine
	private String actual_anim = null;	
	
	private final Rectangle2D ground_collide; //rectangle de collision avec le sol

	
	/**
	 * 
	 */
	Entity(int x, int y, double scale)
	{
		this.scale = scale;
		this.x = x;
		this.y = y;
		//on crée le rectangle de collision au sol
		this.ground_collide = new Rectangle2D.Double(
					x, getMaxY() - 0.15*height, 
					width, 0.15*height);
	}

	/**
	 * permet aux sous-classes d'assigner une chaine de caractère sélectionnant
	 * l'annimation à jouer.
	 * Une sécurité contre les valeurs interdite est prévue.
	 * 
	 * @param anim nom de l'animation voulue
	 */
	void setActualAnim(String anim)
	{
		actual_anim = defaultToLoadedAnim(anim);
	}
	
	/**
	 * Charge l'anim par défaut proposée en cas d'abscence de la principale.
	 * En cas de défaut de l'anim par défaut, l'anim "null" est chargée.
	 * 
	 * @param wanted_anim
	 * @param default_anim
	 * @return String de l'anim finale
	 */
	protected String defaultToLoadedAnim(String wanted_anim, String default_anim)
	{
		return (anims.containsKey(wanted_anim)? 
				wanted_anim : defaultToLoadedAnim(default_anim));
		
	}
	
	/**
	 * Charge l'anim par défaut (null) en cas d'abscence de la principale.
	 * 
	 * @param wanted_anim
	 * @return String de l'anim finale
	 */
	protected String defaultToLoadedAnim(String wanted_anim)
	{
		return (anims.containsKey(wanted_anim)? wanted_anim : null);
		
	}
	
	protected int getVie()
	{
		return life;
	}
	
	protected void perdreVie()
	{
		if (life > 0) 
		{
			this.life--;
		}

	}
	
	protected void extraVie()
	{
		this.life++;
	}
	
	/**
	 * Retourne les coordonnes de l'ancre de l'occurence.
	 * 
	 * 
	 * @return Point
	 */
	protected Point getTrackingPoint()
	{
		return entity_sprite.getTrackingPoint();
	}
		
	/**
	 * façon dont le perso doit bouger à chaque update
	 * 
	 * @return Point vecteur vitesse par update
	 */
	protected Point characterTranslation()
	{
		Point delta_translate = new Point(0, 0);

		
		return delta_translate;
	}
	
	
	/**
	 * Choisit l'animation à jouer en fonction du déplacement et des anims
	 * disponibles.
	 * 
	 * @param delta_translate vecteur vitesse de l'entité
	 */
	protected void comActualAnim(Point delta_translate)
	{
		//déducton de l'animation à afficher:
		if (delta_translate.x > 0) 
		{
			if (!anims.containsKey("droite"))
			{
				actual_anim = defaultToLoadedAnim("gauche");
				entity_sprite.setFlipX(true);
			}
			else
			{
				actual_anim = defaultToLoadedAnim("droite");
				entity_sprite.setFlipX(false);
			}
		}
		if (delta_translate.x < 0) 
		{
			actual_anim = defaultToLoadedAnim("gauche");
			entity_sprite.setFlipX(false);
		}
		if (delta_translate.y > 0) 
		{
			actual_anim = defaultToLoadedAnim("arriere", "avant");
		}
		if (delta_translate.y < 0) 
		{
			actual_anim = defaultToLoadedAnim("avant");
		}
	}
	
	/**
	 * calcule la boite de collision avec le sol
	 */
	protected void comGroundCollide()
	{
		//on crée le rectangle de collision au sol
		ground_collide.setRect(
					x, getMaxY() - 0.15*height, 
					width, 0.15*height);
	}
	
	/**
	 * Returns a shape which adds to navigation mesh.
	 * 
	 * @return Shape
	 */
	public Shape NavComp()
	{
		return null;
	}
	
	/**
	 * Returns a shape which adds to obstacle mesh.
	 * 
	 * @return Shape
	 */
	public Shape ObsComp()
	{
		/*
		 * on fait en sorte qu'il reste une marge de sécu plus grande que
		 * speed*1.1 pour être sûr que les fonctions de collision ne 
		 * fasse pas entrer les entités mouvantes avec elles-même.
		 */
		double hauteur = height - 2 * ((0.15*height) + (speed*1.1));
		
		return new Rectangle2D.Double(
				getCenterX() - 0.7*width/2, getCenterY() - hauteur/2,
				0.7*width, hauteur);
	}
	
	public boolean isEnnemy()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.npc.main.Loopable#init()
	 */
	@Override
	public void init() {
		//on charge le sprite utilises pour le joueur par défaut
		anims.clear();
		
		anims.put(null, new SpriteLoader("", "empty"));
		
		//on charge un gestionnaire d'affichage pour les sprites
		entity_sprite = new SpriteOccurence(anims.get(null).getActualImage(), x, y, 0, scale, scale);
		setBounds(entity_sprite); //colecte des dimensions et coordonnees
		
		//recalcul du rectangle au sol
		comGroundCollide();
		
		//l'initialisation est termine si le gestionnaire de sprites est charge
		initialized = (entity_sprite != null ? true : false);
	}
	
	/* (non-Javadoc)
	 * @see com.npc.main.Loopable#update()
	 */
	@Override
	public void update() {
		if(!initialized) 
			throw new IllegalStateException("Class Updated before Init!");
		
		//index de l'animation a jouer
		actual_anim = null;//anim par defaut
		
		//deplacement en pixels du personnage et redef de actual_anim
		Point delta_translate = characterTranslation();
		boolean immobile = (delta_translate.x == 0) && (delta_translate.y == 0);
		
		//on calcule les animations à afficher
		comActualAnim(delta_translate);
		
		//on calcule les collisions si le perso est en mouvement
		if (!immobile)
		{
			//recalcul du rectangle au sol
			comGroundCollide();
			
			//on vérifie les collision et supprime le mouvment non-permis
			boolean[] collide_with = GameManager.nav_mesh.getCollision(
					ground_collide.getBounds2D(), delta_translate);	
			delta_translate.x = collide_with[0] ? 0 : delta_translate.x;
			delta_translate.y = collide_with[1] ? 0 : delta_translate.y;
			
			//peut-être que les collision on rendu le perso immobile
			immobile = (delta_translate.x == 0) && (delta_translate.y == 0);
		}
		
		
		//on arrete les animatiuon si le npc ne bouge pas on :
		if (immobile)
		{
			if(anims.get(actual_anim).isPlaying())
				anims.get(actual_anim).stop(true);
		}
		else
		{
			if(!anims.get(actual_anim).isPlaying())
				anims.get(actual_anim).start();
		}
		
		//on vérifie si le perso est mort pour savoir si il peut encore bouger
		if(this.life <= 0)
		{
			delta_translate.x = 0;
			delta_translate.y = 0;
			entity_sprite.setFlipX(false);
			entity_sprite.setFlipY(false);
			actual_anim = defaultToLoadedAnim("mort");
		}
		
		//tout est validé préparation au rendu:
		entity_sprite.newTransforms(x + delta_translate.x, y + delta_translate.y, 0, scale, scale);
		entity_sprite.setImage(anims.get(actual_anim).getActualImage());
		this.setBounds(this.entity_sprite);
	}

	/* (non-Javadoc)
	 * @see com.npc.main.Loopable#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		entity_sprite.draw(g);
	}

	/* (non-Javadoc)
	 * @see com.npc.main.Loopable#terminate()
	 */
	@Override
	public void terminate() {
		entity_sprite = null;
		anims.clear();
		
		initialized = false;
	}
}