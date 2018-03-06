package com.bonhomi.main;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class SpriteOccurence extends Rectangle
{
	private static final long serialVersionUID = 4097364124390311395L;

	protected BufferedImage image = null;
	
	//transformations matricielles
	protected AffineTransform transf = new AffineTransform();
	protected double rotation = 0;
	
	/**
	 * types d'ancre de rotation et de suivi possible parmis:
	 * ORIGIN : point au coin superieur gauche
	 * CENTRE : point au centre du rectangle
	 * CUSTOM : le point n'est mis a jour que par l'utilisateur
	 */
	public enum TYPE_ANCRE
	{
		ORIGIN,
		CENTRE,
		CUSTOM
	}
	//decalage du centre de rotation:
	protected TYPE_ANCRE type_ancre = TYPE_ANCRE.CENTRE;
	protected Point ancre = new Point(0, 0);
    
    //mirroir x et y
    protected byte[] flip = {1, 1};
	
    
	/**
	 * Construit une occurence d'une image et garde l'objet en memoire.
	 * 
	 * 
	 * @param g Graphics2D de l'afficheur
	 * @param image Image à afficher
	 * @param coord_x
	 * @param coord_y
	 * @param rotation en radians autour du centre
	 * @param ech_x
	 * @param ech_y
	 */
	public SpriteOccurence(BufferedImage image, 
			int coord_x, int coord_y, 
			double rotation,
			double ech_x, double ech_y)
	{
		this(image, 
			new AffineTransform(ech_x, 0, 0, ech_y, coord_x, coord_y));
		this.rotation = rotation;
		comRect();
	}
	
	/**
	 * Construit une occurence d'une image et garde l'objet en memoire.
	 * Constructeur par defaut.
	 * 
	 * 
	 * @param g       Graphics2D de l'afficheur.
	 * @param image   Image.
	 * @param transf  AffineTransform de l'image.
	 */
	public SpriteOccurence(BufferedImage image, 
			AffineTransform transf)
	{
		this.image = image;
		this.transf = transf;;
		this.comRect();
	}

	/**
	 * assigne une nouvelle ancre a l'occurence
	 * 
	 * @param nvx_ancre  Valeur d'enum du type de centre
	 * 
	 * @see com.bonhomi.main.SpriteOccurence#TYPE_ANCRE
	 */
	public void setAncre(TYPE_ANCRE nvx_ancre)
	{
		type_ancre = nvx_ancre;
		comRect();
	}
	
	/**
	 * assigne une nouvelle ancre a l'occurence
	 * 
	 * @param nvx_ancre  Point ou se trouve l'ancre sur l'image
	 * 
	 * @see com.bonhomi.main.SpriteOccurence#TYPE_ANCRE
	 */
	public void setAncre(Point nvx_ancre)
	{
		ancre = nvx_ancre;
		type_ancre = TYPE_ANCRE.CUSTOM;
		comRect();
	}
	
	
	/**
	 * Change l'image affichee par l'occurence de Sprite.
	 * 
	 * @param newImage <code>Image</code> a afficher
	 */
	public void setImage(BufferedImage newImage)
	{
		image = newImage;
		comRect();
	}
	
	/**
	 * Permet de manipuler l'AffineTransforme de l'occurence sans la redefinir.
	 * On y entre les meme parametre qu'a la creation.
	 * 
	 * 
	 * @param coord_x int ; correspond au graphics2D utilise.
	 * @param coord_y int ; correspond au graphics2D utilise.
	 * @param rotation double ; en radians dans le sens trigonmetrique
	 * @param ech_x double ; 1.0 pour ne pas appliquer de transformation
	 * @param ech_y double ; 1.0 pour ne pas appliquer de transformation
	 */
	public void newTransforms(
			int coord_x, int coord_y,
			double rotation,
			double ech_x, double ech_y)
	{
		transf.setTransform(ech_x, 0, 0, ech_y, coord_x, coord_y);
		this.rotation = rotation;
		comRect();
	}
	
	/**
	 * Permet de manipuler l'AffineTransforme de l'occurence sans la redefinir.
	 * 
	 * 
	 * @return AffineTransform du sprite
	 */
	public AffineTransform getTransforms()
	{
		return transf;
	}
	
	/**
	 * Retourne les coordonnes de l'ancre transposees dans le repere de l'occurence.
	 * 
	 * 
	 * @return Point
	 */
	public Point getTrackingPoint()
	{
		Point TrackingPoint = new Point(ancre);
		TrackingPoint.translate(x, y);
		
		return TrackingPoint;
	}
	
	public void setFlipX(boolean flip)
	{
		this.flip[0] = (byte) (flip ? -1 : 1);
	}
	
	public void setFlipY(boolean flip)
	{
		this.flip[1] = (byte) (flip ? -1 : 1);
	}
	
	private Image flipImage()
	{
		//on inverse: fait un mirroir x y sur l'image et on la deplace
		AffineTransform temptransform = AffineTransform.getScaleInstance(
				(int) flip[0], 
				(int) flip[1]);
		temptransform.translate(
				(flip[0] < 0 ? -image.getWidth(null)  : 0), 
				(flip[1] < 0 ? -image.getHeight(null) : 0));
		rotateAt(temptransform);
		
		//on fabrique l'operation a effectuer sur l'image
		AffineTransformOp op = new AffineTransformOp(temptransform, 
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		//on cree une nouvelle image retournee
		return op.filter(image, null);
	}
	
	
	public void draw(Graphics2D g)
	{
		if(image == null)
			return;
		
		//fait des trous la ou l'image est transparente:
		Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		
		//permet de recuperer la composition initiale:
		Composite restore_composite = g.getComposite();
		
		g.setComposite(comp);
		
		//on cree une nouvelle image retournee avec flipImage() que l'on affiche
		g.drawImage(flipImage(), transf, null);
		
		//on restaure la composition des graphics2D
		g.setComposite(restore_composite);
		
		if(MainClass.getDebugLvl() > 2)
			g.draw(this);
	}
	
	/**
	 * procédure tournant un affine transform si nécessaire
	 */
	private void rotateAt(AffineTransform at)
	{
		at.rotate(-rotation, ancre.getX(), ancre.getY());
	}
	
	protected void comRect()
	{
		if(image != null)
		{
			Image img = flipImage();
			
			setRect(transf.getTranslateX(), 
					transf.getTranslateY(), 
					img.getWidth(null)*transf.getScaleX(), 
					img.getHeight(null)*transf.getScaleY());
		}
		else
		{
			setRect(transf.getTranslateX(), 
					transf.getTranslateY(), 
					0, 
					0);
		}
		
		switch(type_ancre)
		{
		case ORIGIN :
			ancre.setLocation(0, 0);
			break;
		
		case CUSTOM :
			//ne rien faire car l'utilsateur veut avoir un controle total
			break;
			
		case CENTRE :
			
		default :
			ancre.setLocation((width/2), (height/2));
			break;
		}
	}
}
