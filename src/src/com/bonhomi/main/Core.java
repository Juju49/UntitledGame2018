package com.bonhomi.main;

import java.io.PrintStream;

/**
 * Cette classe possede des fonctions
 * et variables statiques utilisables
 * dans l'ensemble des fichiers du jeu
 */
public final class Core 
{
	//public static DebugOutput DebOut;//sortie des appels a print
	
	public static double deltaTime = 0d; //temps depuis dernier update
	public static GameState gameState = GameState.GAME; //phase du jeu en cours
	
	//taille de la fenetre:
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 576;
	//taux de raffrraichissement max:
	public static final double WANTED_FPS = 60d;
	
	public static final int MAP_WIDTH = 4;
	public static final int MAP_HEIGHT = 3;

	
	//reglage du gameplay:
	/**
	 * Points de vie max du joueur.
	 * Cela sert de parametre d'entree pour les ennemis aussi.
	 * 
	 * 
	 * @see com.bonhomi.main.Core#DIFFICULTE
	 */
	public static final int MAX_VIE = 5; //vie du joueur
	
	/**
	 * Multiplicateur des vies et attaques ennemies.
	 * C'est un ajustement par rapport aux statistiques du joueur:
	 * avec difficulte de 2.0 , les ennemis sont x2 plus forts que le joueur
	 * en terme de degats et de vie.
	 */
	public static final double DIFFICULTE = 1.5; //
	public static final int DELAIS_INVULNERABILITE = 600; //en millisecondes
	
	public enum GameState
	{
		MENU,
		GAME,
		PAUSE
	}
	
	public static PrintStream out = System.out; //DebugOutput.debugOutputPS;
	
	/**
	 * Prints output following severity settings.
	 * 
	 * 
	 * @param severity int specifies priority of the message 0=info; 1=warning; 2=error.
	 */
	public static void debugPrint(int severity, Object... printed )
	{
		if(severity >= MainClass.getDebugLvl()) {
			out.println(printed.toString());
		}
	}
	
	
}
