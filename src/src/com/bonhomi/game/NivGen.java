package com.bonhomi.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.bonhomi.main.Core;

/**
 * General level generator.
 * 
 * 
 * @author Sami C., Julian M.
 *
 */
class NivGen implements Runnable, DoorsPosition
{
	private boolean finished = false;
	private Random rand = ThreadLocalRandom.current();
	
	private int roomsCount = Core.MAP_HEIGHT * Core.MAP_WIDTH;
	private ArrayList<Room> rooms = new ArrayList<Room>(roomsCount);
	private Room[][] genMap = new Room[Core.MAP_WIDTH][Core.MAP_HEIGHT];
	private Room actualRoom;
	
	@Override
	public void run()
	{
		genMap = new Room[Core.MAP_WIDTH][Core.MAP_HEIGHT];
		
		for (int x = 0; x < Core.MAP_WIDTH; x++)
		{
			for (int y = 0; y < Core.MAP_HEIGHT; y++)
			{
				genMap[x][y] = null;
			}
		}
		
		creerNiveau();
		setMap();
		Core.out.println("----------------< MAP LOADED >----------------\n");
		return;
	}
	
	private void setMap() {
		Niveau.map = genMap;
		
		for(Room r : rooms)
		{
			//on fait apparaitre le joueur dans une salle contenant au moins 2 portes
			if(r.getNDoorsOpened() >= 2)
			{
				actualRoom = genMap[r.getLocation().x][r.getLocation().y];
				break;
			}
		}
		GameManager.niveau1.setActualRoom(actualRoom.getLocation().x, actualRoom.getLocation().y, null);
	}

	private void creerNiveau()
	{
		finished = false;
		
		while (!finished)
		{
			creerSalle();
		}
		
		actualRoom = genMap[rand.nextInt(Core.MAP_WIDTH-1)][rand.nextInt(Core.MAP_HEIGHT-1)];
		
		placerItemsVictoire();
	}
	
	private void creerSalle()
	{	
		if (rooms.size() >= roomsCount)
		{
			finished = true;
			assert rooms.size() == roomsCount;
			
			Core.out.println("map ------------ FINISHED ----------------\n");
			return;
		}

		Room room = new Room();
		int x;
		int y;
		
		if (rooms.size() > 0)
		{
			int choice = 0;
			choice = rand.nextInt(rooms.size());
			room = rooms.get(choice);
			
			x = room.getLocation().x;
			y = room.getLocation().y;
		}
		else
		{
			x = rand.nextInt(Core.MAP_WIDTH);
			y = rand.nextInt(Core.MAP_HEIGHT);
			
			room.setLocation(x, y);
			
			//première salle
			rooms.add(room);
			genMap[x][y] = room;
			
			Core.out.println("room n°0");
		}
		
		boolean canCreate = false;
		DoorSide wall = DoorSide.TOP;
		
		if (room.getNDoorsOpened() >= 4)
			return;
		
		while (!canCreate)
		{
			wall = DoorSide.values()[rand.nextInt(4)];
			
			if (room.isDoorOpened(wall) == CLOSED)
				canCreate = true;
		}
		
		Core.out.println("Original room y: " + x + "  x: " + y);
		
		
		switch(wall)
		{
			case TOP:
				y--;
				break;
			case BOT:
				y++;
				break;
			case LEFT:
				x--;
				break;
			case RIGHT:
				x++;
				break;
			default:
		}
		
		//nouvelle salle
		if (canCreateRoom(x, y))
		{
			Room newRoom = new Room();
			newRoom.setLocation(x, y);
			
			room.setDoorOpened(wall, OPENED);
			newRoom.setDoorOpened(Niveau.invertWall(wall), OPENED);
			
			rooms.add(newRoom);
			genMap[x][y] = newRoom;
			Core.out.println("--------------> Created room x: " + x + "  y: " + y);
		}
	}
	
	public boolean canCreateRoom(int x, int y)
	{
		boolean can = false;
		
		if ((x < 0 || x >= Core.MAP_WIDTH )||
			(y < 0 || y >= Core.MAP_HEIGHT))
		{
			can = false;
			return can;
		}

		if (genMap[x][y] == null)
			can = true;
		
		return can;
	}
	
	private void placerItemsVictoire()
	{
		int nbItems = 0;
		Room room;

		//on vérifie que au moins 4 salles n'ont qu'une seule porte
		final ArrayList<Point> oneDoorRoomCoords = new ArrayList<Point>();
		for(Room r : rooms)
		{
			//on ajoute les salles qui n'ont qu'une seule porte au registre des salles intéresssantes
			if(r.getNDoorsOpened() == 1)
			{
				oneDoorRoomCoords.add(r.getLocation());
			}
		}
		Core.out.println("items victoire: rooms w 1door: " + oneDoorRoomCoords.size());
		
		if(oneDoorRoomCoords.size() <= VictoryItem.getMaxItems())
		{
			for (int x = 0; x < Core.MAP_WIDTH; x++)
			{
				for (int y = 0; y < Core.MAP_HEIGHT; y++)
				{
					if((genMap[x][y].getNDoorsOpened() == 1) || nbItems > oneDoorRoomCoords.size())
					{
						final Entity e = new VictoryItem(nbItems);
						genMap[x][y].newEntity(e);
						
						nbItems++;
						
						Core.out.println("item de victoire placé: n°" + nbItems+ "\n" 
								+ "                 room x: " + x + "  y: " + y);
					}
				}
			}
		}
		else
		{
			//on veut placer 4 items
			for(int k=0;(nbItems < VictoryItem.getMaxItems()); k++)
			{
				/* On prend une salle au pif de celles intéressantes dans la
				 * limite ou il reste assez de salles pour placer les items restants.
				 */
				if(rand.nextBoolean() 
						&& ((oneDoorRoomCoords.size() - k) > (VictoryItem.getMaxItems() - nbItems)))
					continue;
					
				//on choisi une salle qui a l'air valide
				room = genMap[oneDoorRoomCoords.get(k).x]
						[oneDoorRoomCoords.get(k).y];

				final Entity e = new VictoryItem(nbItems);
				room.newEntity(e);
				
				nbItems++;
				
				Core.out.println("item de victoire placé: n°" + nbItems + "\n" 
						+ "                 room x: "
						+ room.getLocation().x + "  y: " + room.getLocation().y);
			}
		}
		
		assert nbItems == VictoryItem.getMaxItems();
		Core.out.println("item de victoire - FINISHED ---------------- \n");
	}
}