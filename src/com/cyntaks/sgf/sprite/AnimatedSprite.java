package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.core.ResourceManager;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Used to rapidly display a series of Images, generally to represent a Sprite.
 * Delays for each frame may be specified, and frames of uniform or unique
 * cell sizes may be used.
 * 
 * @author Weston Beecroft
 */

public class AnimatedSprite extends ImageSprite implements Cloneable
{ 
  public static final long serialVersionUID = 1;
  private int[][] frameDelays;
  
  private int[][] xStart;
  private int[][] xEnd;
  private int[][] yStart;
  private int[][] yEnd;
  
  private int curXStart;
  private int curXEnd;
  private int curYStart;
  private int curYEnd;
  
  private boolean reverse;
  
  private int rows;
  private int cols;
  
  private int row;
  private int col;
  
  private int imgWidth; //this may be for a sub-image
  private int imgHeight; //this may be for a sub-image
  
  private float timePassed;
  private int nextFrameTime;
  
  private int frameDirection;
  private ArrayList animationListeners;
  
  private boolean initialized;
  
  /**
   * constructs a new Animator with uniform cell size and frame delay specified for each frame.
   */
  public AnimatedSprite(Entity entity, String imageResourceName, 
                        int cellWidth, int cellHeight,
                        int[][] frameDelays, boolean reverseDirection)
  {
    this(entity, imageResourceName, cellWidth, cellHeight, 0, 0,
    	 ResourceManager.getImage(imageResourceName).getWidth(), 
    	 ResourceManager.getImage(imageResourceName).getHeight(), 
    	 frameDelays, reverseDirection);
    
  }
  
  //cell width and height unnecessary, can be calculated from other parameters
  public AnimatedSprite(Entity entity, String imageResourceName, 
          int cellWidth, int cellHeight,
          int xOffset, int yOffset, int subImgWidth, int subImgHeight,
          int[][] frameDelays, boolean reverseDirection)
  {
	  	super(entity, imageResourceName);
	  
	  	this.imgWidth = subImgWidth;
	  	this.imgHeight = subImgHeight;
	  	this.reverse = reverseDirection;
	    this.frameDelays = frameDelays;
	    
	    animationListeners = new ArrayList();
	    
	    if(entity != null)
	      getEntity().setOriginalSize(cellWidth, cellHeight);
	    
	    rows = subImgHeight/cellHeight;
	    cols = subImgWidth/cellWidth;
	    
	    xStart = new int[rows][cols];
	    xEnd = new int[rows][cols];
	    yStart = new int[rows][cols];
	    yEnd = new int[rows][cols];
	    for (int row = 0; row < rows; row++) 
	    {
	      for (int col = 0; col < cols; col++) 
	      {
	        int curXStart = col*(cellWidth) + xOffset;
	        int curYStart = row*(cellHeight) + yOffset;
	        
	        xStart[row][col] = curXStart;
	        xEnd[row][col] = curXStart+cellWidth;
	        yStart[row][col] = curYStart;
	        yEnd[row][col] = curYStart+cellHeight;
	      }
	    }
	    
	    row = 0;
	    col = 0;
	    curXEnd = xEnd[0][0];
	    curYEnd = yEnd[0][0];
	    frameDirection = 1;
	    
	    nextFrameTime = frameDelays[0][0];
	    
	    initialized = true;
  }
  
  public AnimatedSprite(String imageResourceName,
                        int cellWidth, int cellHeight,
                        int[][] frameDelays, boolean reverseDirection)
  {
    this(null, imageResourceName, cellWidth, cellHeight, frameDelays, reverseDirection);
  }
  
  public AnimatedSprite(String imageResourceName,
          int cellWidth, int cellHeight,
          int xOffset, int yOffset, int subImgWidth, int subImgHeight,
          int[][] frameDelays, boolean reverseDirection)
{
	  this(null, imageResourceName, cellWidth, cellHeight, 
		   xOffset, yOffset, subImgWidth, subImgHeight, 
		   frameDelays, reverseDirection);
}
  
  /**
   * constructs a new Animator with uniform cell size and frame delay specified for each frame.
   * 
   */
  public AnimatedSprite(Entity entity, String imageResourceName,
                        int cellWidth, int cellHeight,
                        int frameDelay, boolean reverseDirection)
  {
    this(entity, imageResourceName, cellWidth, cellHeight,
         new int[][]{new int[]{frameDelay}}, reverseDirection);
  }
  
  	public AnimatedSprite(Entity entity, String imageResourceName,
          int cellWidth, int cellHeight, 
          int xOffset, int yOffset, int subImgWidth, int subImgHeight,
          int frameDelay, boolean reverseDirection)
	{
		this(entity, imageResourceName, cellWidth, cellHeight,
			 xOffset, yOffset, subImgWidth, subImgHeight,
		new int[][]{new int[]{frameDelay}}, reverseDirection);
	}
  
  public AnimatedSprite(String imageResourceName,
                        int cellWidth, int cellHeight,
                        int frameDelay, boolean reverseDirection)
  {
    this(null, imageResourceName, cellWidth, cellHeight,
         new int[][]{new int[]{frameDelay}}, reverseDirection);
  }
  
  	public AnimatedSprite(String imageResourceName,
          int cellWidth, int cellHeight,
          int xOffset, int yOffset, int subImgWidth, int subImgHeight,
          int frameDelay, boolean reverseDirection)
	{
		this(null, imageResourceName, cellWidth, cellHeight,
			 xOffset, yOffset, subImgWidth, subImgHeight,
		new int[][]{new int[]{frameDelay}}, reverseDirection);
	}
  
  public Object clone()
  {
    return super.clone();
  }
  
  public void setEntity(Entity entity)
  {
    super.setEntity(entity);
    refresh();
    entity.setOriginalSize(getWidth(), getHeight());
  }
  
  /**
   * updates the animator based on the given time delta
   * @param delta the amount of time that passed during the last frame
   * @return whether the animation finished or not
   */
  public void update(float delta)
  {
    if(initialized)
    {
      boolean finished = false;
    
      timePassed += delta*1000;
      if(timePassed >= nextFrameTime)
      {
        timePassed = 0;
        if(reverse)
          finished = updateThenReverse(delta);
        else
          finished = updateWithoutReverse(delta);
        changeFrame();
      }
      
      if(finished)
      {
        for (int i = 0; i < animationListeners.size(); i++) 
        {
          ((AnimationListener)animationListeners.get(i)).animationComplete(this);
        }
      }
    }
  }
  
  public void render(Graphics2D g2)
  {
    if(super.isVisible() && super.getEntity().isVisible())
    	g2.drawImage(getImage(), 0, 0, getWidth(), getHeight(),
                curXStart, curYStart, curXEnd, curYEnd, null);
  }
  
  public void addAnimationListener(AnimationListener listener)
  {
    animationListeners.add(listener);
  }
  
  public boolean removeAnimationListener(AnimationListener listener)
  {
    return animationListeners.remove(listener);
  }
  
  private boolean updateWithoutReverse(float delta)
  {
    boolean finished = false;
    
    if(col < cols-1)
      col++;
    else
    {
      col = 0;
      if(row < rows-1)
        row++;
      else
      {
        row = 0;
        finished = true;
      }
     }
     return finished;
  }
  
  private boolean updateThenReverse(float delta)
  {
    boolean changedDirection = false;
    
    if(frameDirection == 1 && col < cols-1)
      col++;
    else if(frameDirection == -1 && col > 0)
      col--;
    else
    {
      if(frameDirection == 1 && row < rows-1)
        row++;
      else if(frameDirection == -1 && row > 0)
        row--;
      else
      {
        if(frameDirection == 1)
          col = cols-2;
        else
          col = 1;
        frameDirection *= -1;
        changedDirection = true;
      }
    }
    return changedDirection;
  }
  
  /**
   * changes the current frame to the one specified
   * @param row row of the desired frame
   * @param col column of the desired frame
   */
  public void setCurrentFrame(int row, int col)
  {
    this.row = row;
    this.col = col;
    timePassed = 0;
    changeFrame();
  }
  
  /**
   * changes the animator's current row
   * @param row the desired row
   */
  public void setRow(int row)
  {
    this.row = row;
    timePassed = 0;
    changeFrame();
  }
  
  /**
   * changes the animator's current column
   * @param col the desired column
   */
  public void setCol(int col)
  {
    this.col = col;
    timePassed = 0;
    changeFrame();
  }
  
  private void changeFrame()
  {
    if(frameDelays != null)
    {
      if(frameDelays[0].length > 1)
        nextFrameTime = frameDelays[row][col];
      else
        nextFrameTime = frameDelays[0][0];
    }
    
    refresh();
  }
  
  private void refresh() {
	  curXStart = xStart[row][col];
      curXEnd = xEnd[row][col];
      curYStart = yStart[row][col];
      curYEnd = yEnd[row][col];
  }
  
  public boolean doesChangeDirection()
  {
    return reverse;
  }

  public int getRow()
  {
    return row;
  }

  public int getCol()
  {
    return col;
  }
  
  /**
   * returns a copy of this AnimatedSprite
   * @return a copy of this AnimatedSprite
   */
  public AnimatedSprite getCopy()
  {
    try 
    {
      return (AnimatedSprite)clone();
    } catch (Exception ex) 
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  /**
   * there will be a random delay before switching to the next frame
   * @param min the minimum length the delay should have
   * @param max the maximum length the delay should have
   */
  public void randomDelay(int min, int max)
  {
    nextFrameTime = (int)(Math.random()*(max-min) + min);
  }
  
  public int getWidth()
  {
    return curXEnd - curXStart;
  }
  
  public int getHeight()
  {
    return curYEnd - curYStart;
  }
}