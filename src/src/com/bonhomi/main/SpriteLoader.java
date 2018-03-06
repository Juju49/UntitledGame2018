package com.bonhomi.main;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class SpriteLoader 
{
	protected String spritePath;
	protected String name;
	protected boolean animated;
	protected boolean repeat;
	protected long delay;
	
	protected ArrayList<BufferedImage> images;
	protected int actualIndex;
	protected Timer animationTimer;
	
	/**
	 * Charge des images et les transforme en animation a partir du system de fichiers.
	 * <p>
	 * Les chemins commencent à "src/Sprites/" et le nom doit etre precise sans extension.
	 * Le format *.png est supporte.
	 * <p>
	 * lorsque repeat est active, l'animation est jouee en boucle. Le nombre d'images 
	 * est calculé automatiquement. Delay est le temps d'affichage de chaque image a 
	 * l'écran lors de l'animation en milllisecondes.
	 * <p>
	 * Les animations doivent etre au format <code>nom_index.png</code> 
	 * dans les hierarchies.
	 * 
	 * 
	 * @param spritePath chemin du dossier avec un slash a la fin.
	 * @param name       nom sans extension de l'image.
	 * @param animated   rechercher les animations ?
	 * @param repeat     boucler les animations ?
	 * @param delay      temps d'affichage par image.
	 */
	public SpriteLoader(
			String spritePath, 
			String name, 
			boolean animated, 
			boolean repeat,
			long delay)
	{
		this.spritePath = "/Sprites/" + spritePath; //tous les sprites sont dans src/sprites/
		this.name = name;
		this.animated = animated;
		this.repeat = repeat;
		this.delay = delay;
		this.actualIndex = 0;
		
		images = new ArrayList<BufferedImage>();
		
		load();
	}
	
	/**
	 * Constructeur sans animation.
	 * 
	 *
	 * @param spritePath
	 * @param name
	 */
	public SpriteLoader(
			String spritePath, 
			String name)
	{
		this(spritePath, name, false, false, 1);
	}
	
	
	protected void load()
	{
		BufferedImage img = null;
		
		if (animated)
		{

			ArrayList<String> files;
			try {
				files = searchAnimation(name, walkFiles(spritePath));
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
				throw new Error("Cannot walk files");
			}
			
			for (String fileName : files)
			{
				try 
				{
					img = ImageIO.read(getClass().getResourceAsStream(fileName));
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				images.add(img);
			}
		}
		else //cas sans animation
		{
			try 
			{
				img = ImageIO.read(getClass().getResourceAsStream(spritePath + name + ".png"));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			images.add(img);
		}
	}
	
	public void start()
	{
		if (animated == true)
		{
			animationTimer = new Timer();
			
			animationTimer.scheduleAtFixedRate(
				new TimerTask() 
				{
					@Override
					public void run() 
					{
						//Core.out.println(actualIndex + "   " + (images.size() - 1));
						if (actualIndex == images.size() - 1)
						{
							actualIndex = 0;
							if (repeat == false)
								stop(true);
						}
						else
							actualIndex++;
					}
				},
				0, 
				this.delay
			);
		}
	}
	
	/**
	 * Stop anim execution.
	 * 
	 * @param reset resets anim to frame 0.
	 */
	public void stop(boolean reset)
	{
		animationTimer.cancel();
		animationTimer = null;
		actualIndex = (reset ? 0 : actualIndex);
	}
	
	public boolean isPlaying()
	{
		return (animationTimer == null ? false : true);
	}
	
	public BufferedImage getActualImage()
	{
		return images.get(actualIndex);
	}
	
	protected ArrayList<String> searchAnimation(
			String aName, 
			ArrayList<Path> files) throws FileNotFoundException
	{
		ArrayList<String> newFiles = new ArrayList<String>();
		
		for (Path file : files)
		{
			String actualFile = file.toString();
			String[] tab2 = actualFile.split("_");
			if (tab2[0].equals(aName))
			{
				newFiles.add(spritePath + file);
			}
		}
		
		if (newFiles.isEmpty())
			throw new FileNotFoundException("AUCUNE ANIMATION TROUVEE");
		
		return newFiles;
	}
	
	
	protected ArrayList<Path> walkFiles(String directoryPath) throws URISyntaxException, IOException 
	{
		ArrayList<Path> filesName = new ArrayList<Path>();

		URI directoryUri = getClass().getResource(directoryPath).toURI();
		Path myPath;

		if (directoryUri.getScheme().equals("jar")) {
			
			//on vérifie que le FS n'est pas déjà ouvert sinon on le crée
			FileSystem fileSystem;
			try
			{
				fileSystem = FileSystems.getFileSystem(directoryUri);
			} 
			catch (FileSystemNotFoundException e) 
			{
				fileSystem = FileSystems.newFileSystem(directoryUri, Collections.<String, Object>emptyMap());
			}
			
			//on récupère le chemin que l'on voulait explorer
			myPath = fileSystem.getPath(directoryPath);
			
		} else {
			myPath = Paths.get(directoryUri);
		}

		if(!Files.isDirectory(myPath))
		{
			throw new IllegalArgumentException("Le chemin ne conduit pas à un répertoire!");
		}

		//on walk le dossier et on ajoute tout ce que l'on trouve
		Stream<Path> walk = Files.walk(myPath, 1);
		for (Iterator<Path> it = walk.iterator(); it.hasNext();)
			filesName.add(it.next().getFileName());
		walk.close();

		if(filesName.isEmpty())
		{
			throw new FileNotFoundException("No files found under: " + directoryPath);
		}
		return filesName;
	}
}
