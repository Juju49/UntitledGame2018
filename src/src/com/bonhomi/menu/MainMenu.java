package com.bonhomi.menu;

import java.awt.Graphics2D;
import com.bonhomi.main.Core;
import com.bonhomi.main.Core.GameState;
import com.bonhomi.main.InputManager;
import com.bonhomi.main.Loopable;
import com.bonhomi.main.SpriteLoader;
import com.bonhomi.main.SpriteOccurence;

public class MainMenu implements Loopable {

	private boolean initialized = false;
	private SpriteOccurence mainImg;
	private SpriteLoader backgroundAnim;
	
	public MainMenu() 
	{
		init();
	}
	
	@Override
	public void init() 
	{
		backgroundAnim = new SpriteLoader("UI/bonhomiTitle/", "background", true, true, 500);
		backgroundAnim.start();
		mainImg = new SpriteOccurence(
				backgroundAnim.getActualImage(),
				0, 0,
				0d,
				1, 1);
		
		initialized = true;
	}

	@Override
	public void update() {
		if(!initialized) throw new IllegalStateException("Class Updated before Init!");
		
		if(InputManager.mouseLeftCliked())
		{
			Core.gameState = GameState.GAME;
		}
		
		mainImg.setImage(backgroundAnim.getActualImage());
	}

	@Override
	public void draw(Graphics2D g) {
		mainImg.draw(g);
	}

	@Override
	public void terminate() {
		initialized = false;
	}
}
