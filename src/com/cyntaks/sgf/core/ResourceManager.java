package com.cyntaks.sgf.core;

import com.cyntaks.sgf.sound.*;
import com.cyntaks.sgf.utils.ImageLoader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import com.cyntaks.sgf.core.ProgressBar;

public final class ResourceManager 
{
  private static HashMap<String, Object> resources; //all loaded resources
  private static HashMap<String, String> resourcePaths; //resource paths linked to names
                                        //from the 'resources' map
  private static LinkedList<ResourceManager.ResourceParameters> resourceQueue;
  
  private static final int IMAGE = 0;
  private static final int SOUND = 1;
  private static final int OBJECT = 2;
  
  static
  {
    resources = new HashMap<String, Object>();
    resourcePaths = new HashMap<String, String>();
    resourceQueue = new LinkedList<ResourceManager.ResourceParameters>();
  }
  
  ////
  // public interface
  ////
  public static void queueImage(String path, int transparency, String name)
  {
    ResourceManager.ResourceParameters parameters = new ResourceManager.ResourceParameters(path, name, transparency);
    resourceQueue.addFirst(parameters);
  }
  
  public static void loadImage(String path, int transparency, String name)
  {
    Object image = getResourceFromPath(path);
    
    if(image != null)
    {
      resources.put("image_"+name, image);
    }
    else
    {
      image = ImageLoader.loadImage(path, transparency);
      resources.put("image_"+name, image);
      resourcePaths.put(path, "image_"+name);
    }
  }
  
  public static void queueSound(String path, String name)
  {
    ResourceManager.ResourceParameters parameters = new ResourceManager.ResourceParameters(SOUND, path, name);
    resourceQueue.addFirst(parameters);
  }
  
  public static void loadSound(String path, String name)
  {
    Object clip = getResourceFromPath(path);
    
    if(clip != null)
      resources.put("sound_"+name, clip);
    else
    {
      clip = SoundLoader.loadSound(path);
      resources.put("sound_"+name, clip);
      resourcePaths.put(path, "sound_"+name);
    }
  }
  
  public static void queueObject(String path, String name)
  {
    ResourceManager.ResourceParameters parameters = new ResourceManager.ResourceParameters(OBJECT, path, name);
    resourceQueue.addFirst(parameters);
  }
  
  public static void loadObject(String path, String name)
  {
    Object object = getResourceFromPath(path);
    
    if(object != null)
    {
      object = copyObject(object);
      resources.put("object_"+name, object);
    }
    else
    {
        InputStream stream = com.cyntaks.sgf.core.ResourceManager.class.getResourceAsStream("/"+path);
        BufferedInputStream bi = new BufferedInputStream(stream);
        try 
        {
          ObjectInputStream oi = new ObjectInputStream(bi);
          try 
          {
            object = oi.readObject();
            resources.put("object_"+name, object);
            resourcePaths.put(path, "object_"+name);
          } catch (Exception ex) 
          {
            ex.printStackTrace();
          } finally 
          {
            oi.close();
          }
        } catch (IOException ex) 
        {
          ex.printStackTrace();
        }
    }
  }
  
  public static void loadQueuedResources(ProgressBar progressBar, boolean showFileNames)
  {
    if(progressBar != null)
    {
      if(showFileNames)
        progressBar.init(getResourceNames());
      else
        progressBar.init(resourceQueue.size());
    }
    while(!resourceQueue.isEmpty())
    {
      ResourceManager.ResourceParameters parameters = (ResourceParameters)resourceQueue.removeLast();
      switch (parameters.getResourceType()) 
      {
        case IMAGE:
          loadImage(parameters.getPath(), parameters.getTransparency(), parameters.getName());
        break;
        case SOUND:
          loadSound(parameters.getPath(), parameters.getName());
        break;
        case OBJECT:
          loadObject(parameters.getPath(), parameters.getName());
        break;
      }
      
      if(progressBar != null)
        progressBar.taskCompleted();
    }
  }
  
  public static void loadQueuedResources()
  {
    loadQueuedResources(null, false);
  }
  
  public static BufferedImage getImage(String name)
  {
    return (BufferedImage)resources.get("image_"+name);
  }
  
  public static SoundClip getSound(String name)
  {
    return (SoundClip)resources.get("sound_"+name);
  }
  
  public static Object getObject(String name)
  {
    Object object = copyObject(resources.get("object_"+name));
    
    return object;
  }
  
  private static Object copyObject(Object object)
  {
    try 
    {
      //cloning with serialization
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream oo = new ObjectOutputStream(bo);
      oo.writeObject(object);
      oo.close();
      ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
      ObjectInputStream oi = new ObjectInputStream(bi);
      object = oi.readObject();
      oi.close();
    } catch (IOException ex) 
    {
      ex.printStackTrace();
      return null;
    } catch (ClassNotFoundException ex) 
    {
      ex.printStackTrace();
      return null;
    }
    
    return object;
  }
  
  public static Object getResource(String name)
  {
    return resources.get(name);
  }
  
  public static BufferedImage releaseImage(String name)
  {
    return (BufferedImage)resources.remove("image_"+name);
  }
  
  public static SoundClip releaseSound(String name)
  {
    return (SoundClip)resources.remove("sound_"+name);
  }
  
  public static Object releaseObject(String name)
  {
    return resources.remove("object_"+name);
  }
  
  public static Object releaseResource(String name)
  {
    return resources.remove(name);
  }
  
  public static void releaseAllResources()
  {
    resources.clear();
  }
  
  public String[] getResourceList()
  {
    ArrayList list = new ArrayList();
    
    Iterator iter = resources.keySet().iterator();
    while(iter.hasNext())
      list.add(iter.next());
      
    String[] names = new String[list.size()];
    for (int i = 0; i < names.length; i++) 
    {
      names[i] = list.get(i).toString();
    }
    
    return names;
  }
  
  ////
  // private methods
  ////
  private static String[] getResourceNames()
  {
    String[] names = new String[resourceQueue.size()];
    for (int i = 0; i < names.length; i++) 
    {
      names[i] = ((ResourceParameters)resourceQueue.get(names.length-1-i)).getPath(); 
    }
    
    return names;
  }
  
  private static Object getResourceFromPath(String path)
  {
    String name = (String)resourcePaths.get(path);
    if(name != null)
      return resources.get(name);
    else
      return null;
  }
  
  private static class ResourceParameters
  {
    private int resourceType;
    private int transparency;
    private String path;
    private String name;
    
    ResourceParameters(int resourceType, String path, String name)
    {
      this.resourceType = resourceType;
      this.path = path;
      this.name = name;
      this.transparency = -1;
    }
    ResourceParameters(String path, String name, int transparency)
    {
      this(IMAGE, path, name);
      this.transparency = transparency;
    }
    
    int getResourceType()
    {
      return resourceType;
    }
    
    int getTransparency()
    {
      return transparency;
    }
    
    String getPath()
    {
      return path;
    }
    
    String getName()
    {
      return name;
    }
  }
  
  public static void destroy()
  {
    resources.clear();
    resourcePaths.clear();
    resourceQueue.clear();
  }
}