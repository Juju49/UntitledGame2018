package com.bonhomi.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import com.bonhomi.main.MainClass;

class NavMesh implements Shape {
	private ArrayList<Shape> nav_shape;
	private ArrayList<Shape> obstacle_shape;
	
	private Area aire;
	
	
	public NavMesh() {
		nav_shape = new ArrayList<Shape>();
		obstacle_shape = new ArrayList<Shape>();
		
		aire = new Area();
	}

	public static Point getNavPoint(RectangularShape sprite)
	{
		return new Point( (int) sprite.getCenterX(), (int) sprite.getMaxY());
	}
	
	/**
	 * Vide le nav_mesh de toutes ses données.
	 */
	public void purge()
	{
		nav_shape.clear();
		obstacle_shape.clear();
		aire.reset();
	}
	
	/**
	 * Calcul si un déplacement produira une collision.
	 * 
	 * 
	 * 
	 * @param s <code>Shape</code> utilisé pour calculer les collisions
	 * @param vector Déplacement prévu du <code>Shape</code> sur l'axe x puis y
	 * @return tableau booléen etablissant la véractité d'une collision sur l'axe X puis Y. 
	 */
	public boolean[] getCollision(Shape s, Point vector)
	{
		boolean[] collision_xy = new boolean[2];
		//on considere que la forme entre en collision d'abord
		collision_xy[0] = true;//axe x
		collision_xy[1] = true;//axe y
		
		//test si le mouvement selon x est autorise:
			//on translate sur la forme sur l'axe x:
		Shape s_shifted = new AffineTransform(1, 0, 0, 1, vector.getX(), 0).createTransformedShape(s);
		
		if( this.contains(s_shifted.getBounds2D()))
			collision_xy[0] = false; /*la forme est toujours dans le nav_mesh, 
			le mouvement est autorisé*/
		
			//on translate sur la forme sur l'axe y:
		s_shifted = new AffineTransform(1, 0, 0, 1, 0, vector.getY()).createTransformedShape(s);
		
		if( this.contains(s_shifted.getBounds2D()))
			collision_xy[1] = false; /*la forme est toujours dans le nav_mesh, 
		le mouvement est autorisé*/
		
		//Core.out.println("collision state: x:" + collision_xy[0] + "; y:" + collision_xy[1]);
		
		return collision_xy;
	}
	
	
	/**
	 * Ajoute une <code>Shape</code> à l'aire de navigation des ennemis et du joueur.
	 * Si la forme est deja presente, ne fait rien.
	 * 
	 * 
	 * @param rect
	 */
	void addNav(Shape rect)
	{
		if (!nav_shape.contains(rect))
		{
			nav_shape.add(rect);
			comAire();
		}
	}
	
	/**
	 * Ajoute des <code>Shape</code> de navigation.
	 * Si des formes sont deja presentes, ne fait rien.
	 * 
	 * 
	 * @param rects Liste des <code>Shape</code> a ajouter
	 */
	void addNav(Shape[] rects)
	{
		for( int i = 0; i < rects.length; i++)
		{
			addNav(rects[i]);
		}
		comAire();
	}

	/**
	 * Supprime une <code>Shape</code> de navigation.
	 * L'aire de navigation est modifiée si le rectangle existe
	 */
	void removeNav(Shape rect)
	{
		if(nav_shape.contains(rect))
		{
			nav_shape.remove(nav_shape.indexOf(rect));
			comAire();
		}
	}

	/**
	 * Ajoute une <code>Shape</code> a la liste d'obstacles des ennemis et du joueur.
	 * Si la forme est deja presente, ne fait rien.
	 * 
	 * 
	 * @param rect
	 */
	void addObs(Shape rect)
	{
		if (!obstacle_shape.contains(rect))
		{
			obstacle_shape.add(rect);
			comAire();
		}
	}
	
	/**
	 * Ajoute des <code>Shape</code> d'obstacle.
	 * Si des formes sont deja presentes, ne fait rien.
	 * 
	 * 
	 * @param rects Liste des <code>Shape</code> a ajouter
	 */
	void addObs(Shape[] rects)
	{
		for( int i = 0; i < rects.length; i++)
		{
			addObs(rects[i]);
		}
		comAire();
	}

	/**
	 * Supprime une <code>Shape</code> d'obstacle.
	 * L'aire de navigation est modifiée si le rectangle existe
	 */
	void removeObs(Shape rect)
	{
		if(obstacle_shape.contains(rect))
		{
			obstacle_shape.remove(obstacle_shape.indexOf(rect));
			comAire();
		}
	}
	
	
	@Override
	public boolean contains(Point2D p)
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.contains(p);
	}
	
	@Override
	public boolean contains(double x, double y)
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.contains(x, y);
	}
	
	@Override
	public boolean contains(Rectangle2D r)
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.contains(r);
	}
	
	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.contains(x, y, w, h);
	}
	
	@Override
	public Rectangle getBounds()
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.getBounds();
	}
	
	@Override
	public Rectangle2D getBounds2D()
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.getBounds2D();
	}
	
	@Override
	public boolean intersects(Rectangle2D r)
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.intersects(r);
	}
	
	@Override
	public boolean intersects(double x, double y, double w, double h)
	{
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.intersects(x, y, w, h);
	}
	
	public boolean isEmpty() {
		comAire();
		return (nav_shape.isEmpty() && aire.isEmpty());
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		//on verifie que l'aire est bien generee
		if (aire.isEmpty() && !nav_shape.isEmpty())
			comAire();
		
		return aire.getPathIterator(at, flatness);
	}
	
	/**
	 * Calcule l'aire de navigation des personnages.
	 */
	private void comAire()
	{
		try 
		{
		//vidange de l'aire
		aire.reset();
		
		synchronized (nav_shape) 
		{
			//dessin de l'aire
			for (Shape s : nav_shape)
			{
				if (s == null)
					continue;
				
					aire.add(new Area(s));
			}
		}

		synchronized (obstacle_shape) 
		{
			//poinconnage des obstacle sur le nav_mesh
			for (Shape s : obstacle_shape)
			{
				if (s == null)
					continue;
					aire.subtract(new Area(s));
				}
			}
		}
		catch (ConcurrentModificationException e) 
		{
				
		}
	}

	public void draw(Graphics2D g) 
	{
		if( MainClass.getDebugLvl() > 2)
		{	
			//semi-transparent:
			Composite comp = 
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
			
			//permet de recuperer la composition initiale:
			Composite restore_composite = g.getComposite();
			
			g.setComposite(comp);
			
			//on dessine le nav_mesh
			g.setColor(Color.yellow);
			g.fill(this);

			//on restaure la composition des graphics2D
			g.setComposite(restore_composite);
			
			//on fait de même pour les contours
			Stroke restore_stroke = g.getStroke();

			g.setStroke(new BasicStroke(2.0f));
			g.setColor(Color.orange);
			g.draw(this);
			
			g.setStroke(restore_stroke);
		}
		
	}

}
