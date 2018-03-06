package com.bonhomi.game;

import java.awt.Graphics2D;
import com.bonhomi.main.Core;
import com.bonhomi.main.Loopable;

public class Niveau implements DoorsPosition, Loopable
{
	static protected Room[][] map;
	static private Room actualRoom;
	
	private Thread tNivGen;
	
	
	Niveau()
	{
		map = new Room[Core.MAP_WIDTH][Core.MAP_HEIGHT];
		
		for (int x = 0; x < Core.MAP_WIDTH; x++)
		{
			for (int y = 0; y < Core.MAP_HEIGHT; y++)
			{
				map[x][y] = null;
			}
		}
		
		NivGen ng = new NivGen();
		tNivGen = new Thread(ng, "level generator");
	}
	
	@Override
	public void init() {
		for (int x = 0; x < Core.MAP_WIDTH; x++)
		{
			for (int y = 0; y < Core.MAP_HEIGHT; y++)
			{
				map[x][y].init();
			}
		}
	}
	
	@Override
	public void update()
	{
		if (actualRoom != null)
			actualRoom.update();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		//Core.out.println("actual room: " + actualRoom);
		if (actualRoom != null)
			actualRoom.draw(g);
	}
	
	@Override
	public void terminate() {

		
		
		for (int x = 0; x < Core.MAP_WIDTH; x++)
		{
			for (int y = 0; y < Core.MAP_HEIGHT; y++)
			{
				try 
				{
					map[x][y].terminate();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				map[x][y] = null;
			}
		}
	}
	
	
	
	
	public Room getActualRoom() {
		return actualRoom;
	}
	public void setActualRoom(int x, int y, DoorSide appearFrom)
	{
		actualRoom = map[x][y];
		
		if (actualRoom != null)
			map[x][y].setEntityAtDoor(GameManager.player1, appearFrom);
		else
			throw new IllegalArgumentException("Room (" + x + ";" + y + ") doesn't exist! ");
	}
	
	
	public void printMap()
	{
		for (int x = 0; x < Core.MAP_WIDTH; x++)
		{
			for (int y = 0; y < Core.MAP_HEIGHT; y++)
			{
				if (map[x][y] == null)
				{
					System.out.print("x");
				}
				else
				{
					Room room = map[x][y];
					if (room.isDoorOpened(DoorSide.TOP) == OPENED)
					{
						System.out.print("t");
					}
					if (room.isDoorOpened(DoorSide.BOT) == OPENED)
					{
						System.out.print("b");
					}
					if (room.isDoorOpened(DoorSide.LEFT) == OPENED)
					{
						System.out.print("l");
					}
					if (room.isDoorOpened(DoorSide.RIGHT) == OPENED)
					{
						System.out.print("r");
					}
				}
				System.out.print(" ");
			}
			
			System.out.print("\n\n");
		}
	}
	
	static DoorSide invertWall(DoorSide wall)
	{
		switch(wall)
		{
			case TOP:
				return DoorSide.BOT;
				
			case BOT:
				return DoorSide.TOP;
				
			case LEFT:
				return DoorSide.RIGHT;
				
			case RIGHT:
				return DoorSide.LEFT;
				
			default:
				throw new IllegalArgumentException(wall + " is not a valid wall index!");
		}
	}

//compatibilité avec "Niveau2"
	public boolean isLoaded() 
	{
		//la carte est-elle chargée?
		boolean unloadedRooms = false;
		for (int x = 0; x < Core.MAP_WIDTH; x++)
		{
			for (int y = 0; y < Core.MAP_HEIGHT; y++)
			{
				//on passe unloadedRooms à "vrai" si au moins une salle n'est pas chargée
				unloadedRooms = (map[x][y] == null ? true : unloadedRooms);
			}
		}
		
		return (!tNivGen.isAlive() && !unloadedRooms);
	}

	public void startLoading() 
	{
		tNivGen.start();
	}
}
