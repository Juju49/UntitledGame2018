package com.bonhomi.game;

interface DoorsPosition 
{
	static enum DoorSide
	{
		TOP,
		LEFT,
		BOT,
		RIGHT;
	}
	
	static final boolean OPENED = true;
	static final boolean CLOSED = false;
	
	static final int OFFSET_MURS = 100;
}
