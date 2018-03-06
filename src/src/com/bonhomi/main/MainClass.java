package com.bonhomi.main;

import com.bonhomi.main.Core.GameState;

public class MainClass {
	protected static Fenetre fenetre;
	
	private static int debug_lvl = 2;
	private static boolean has_sound = true;
	private static boolean display_fps = false;
	
	public static int getDebugLvl() {
		return debug_lvl;
	}
	public static boolean getHasSound() {
		return has_sound;
	}
	public static boolean getDisplayFps() {
		return display_fps;
	}
	
	
	public static void main(String... args) {
		parseCmdArgs(args);
		Core.gameState = GameState.MENU;
		fenetre = new Fenetre();
	}
	
	
	private final static void parseCmdArgs(String[] args) {
		if(args.length > 0) {
			for(String arg : args) {
				
				String[] str = arg.split(":");
				
				switch(str[0]) {
				
				case "-debug":
					debug_lvl = Integer.parseInt(str[1]);
					Core.debugPrint(0, "debug level set: "+debug_lvl);
					
					//check proper debug mode by checking assertions
					boolean assertsEnabled = false;
					assert assertsEnabled = true;
					if (!assertsEnabled) {
							throw new RuntimeException("Asserts must be enabled in debug mode!!! (-ea)");
					}
					break;
					
				case "-noSound":
					has_sound = false;
					Core.debugPrint(1, "Sound system inactive");
					break;
					
				case "-fps":
					display_fps = true;
					Core.debugPrint(0, "FPS display active");
					break;
				}
			}
		}
	}
}
