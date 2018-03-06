package com.bonhomi.sounds;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.ArrayList;

import com.bonhomi.main.MainClass;
 

public class SoundSystemMaster  {

	static ArrayList<AudioClip> loaded_sounds;
	
	private SoundSystemMaster()
	{
		if( MainClass.getHasSound())
			loaded_sounds = new ArrayList<AudioClip>();
	}
	
	//gestion de la classe comme singleton
	private static class soundSysHolder
	{
		private static final SoundSystemMaster sono = new SoundSystemMaster();
	}
	static SoundSystemMaster getInstance()
	{
		return soundSysHolder.sono;
	}
	
	int addSound(String name)
	{
		if(!MainClass.getHasSound())
			return -1;
		
		URL url = getClass().getResource("/Sounds/" + name + ".wav" );
		
		AudioClip ac = Applet.newAudioClip(url);
		
		
		if(!loaded_sounds.contains(ac))
			loaded_sounds.add(ac);
		
		//jamais '-1' car le son est forcement charge a cet endroit
		return loaded_sounds.indexOf(ac);
	}
	
	void terminate()
	{
		if(!MainClass.getHasSound())
			return;
		
		loaded_sounds.clear();
	}
}
