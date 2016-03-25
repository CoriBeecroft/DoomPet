package com.cyntaks.sgf.core;

import java.io.Serializable;

/**
 * WindowConfiguration objects are used to specify desired parameters for
 * a window created by an SGFGame.
 */

public class WindowConfiguration implements Serializable
{
  public static final long serialVersionUID = 1L;
  
  private int width; //window width
  private int height; //window height
  private int refreshRate; //window refresh rate
  private int bitDepth; //window bit depth
  private boolean fullscreen; //whether fullscreen mode should be used
  
  ////
  // constructors
  ////
  /**
   * creates a basic WindowConfiguration 
   * @param height desired app height
   * @param width desired app width
   */
  public WindowConfiguration(int width, int height)
  {
    this.width = width;
    this.height = height;
  }
  /**
   * creates a WindowConfiguration suitable for an application
   * @param fullscreen whether to use fullscreen or not
   * @param height desired window height
   * @param width desired widnow widht
   */
  public WindowConfiguration(int width, int height, boolean fullscreen)
  {
    this(width, height);
    this.fullscreen = fullscreen;
  }
  /**
   * creates a WindowConfiguration suitable for a full screen application
   * @param refreshRate desired refresh rate
   * @param fullscreen whether to use fullscreen or not
   * @param height desired screen height
   * @param width desired screen width
   */
  public WindowConfiguration(int width, int height, int refreshRate, boolean fullscreen)
  {
    this(width, height, fullscreen);
    this.refreshRate = refreshRate;
  }
  
  /**
   * copy constructor
   * @param windowConfiguration the WindowConfiguration to copy
   */
  public WindowConfiguration(WindowConfiguration windowConfiguration)
  {
    this.width = windowConfiguration.width;
    this.height = windowConfiguration.height;
    this.fullscreen = windowConfiguration.fullscreen;
    this.refreshRate = windowConfiguration.refreshRate;
  }

  ////
  // accessors and mutators
  ////
  public boolean isFullscreen()
  {
    return fullscreen;
  }

  public int getHeight()
  {
    return height;
  }

  public int getWidth()
  {
    return width;
  }

  public int getRefreshRate()
  {
    return refreshRate;
  }
  
  public void setBitDepth(int bitDepth) {
	this.bitDepth = bitDepth;
  }
  
  public int getBitDepth() {
	return bitDepth;
  }
  
  void setRefreshRate(int refreshRate)
  {
    this.refreshRate = refreshRate;
  }
  
  public String toString()
  {
	  return "fullscreen: " + fullscreen + ", width: " + width + ", height: " + height + ", refreshRate: " + refreshRate + ", bitDepth: " + bitDepth;
  }
}