package com.cyntaks.sgf.core;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Entity implements Serializable, Cloneable
{
  public static final long serialVersionUID = 1L;
  
  private float xPosition;
  private float yPosition;
  private boolean xFlipped;
  private boolean yFlipped;
  
  private float xVol;
  private float yVol;
  
  private float xAccel;
  private float yAccel;
  
  private float xScale;
  private float yScale;
  private float rotation;
  
  private boolean visible;
  private boolean alive;
  
  private CollisionBounds collisionBounds;
  private float collisionXOffset;
  private float collisionYOffset;
  private CollisionGroup collisionGroup;
  private static final CollisionGroup DEFAULT_COLLISION_GROUP = new CollisionGroup(-1, new int[]{-2});
  
  private transient ArrayList<Connector> connectors;
  
  private transient int originalWidth;
  private transient int originalHeight;
  
  private Behavior behavior;
  private Connector connection;
  
  private int followerKey;
  private ArrayList<Integer> connectedKeys;
  
  ////
  // constructors
  ////
  public Entity(CollisionBounds collisionBounds)
  {
    //if(collisionBounds == null)
      //collisionBounds = new CollisionBounds(0);
    this.collisionBounds = collisionBounds;
    
    visible = true;
    alive = true;
    xScale = 1;
    yScale = 1;
    xVol = 0;
    yVol = 0;
    xAccel = 0;
    yAccel = 0;
    collisionGroup = DEFAULT_COLLISION_GROUP;
    //connectors = new ArrayList<Connector>();
    //connectedKeys = new ArrayList<Integer>();
    
    EntityManager.getInstance().addEntity(this);
  }
  
  public Entity()
  {
    this(null);
  }
  
  public Entity(CollisionBounds collisionBounds, CollisionGroup collisionGroup)
  {
    this(collisionBounds);
    this.collisionGroup = collisionGroup;
  }
  
  ////
  // public interface
  ////
  
  public Object clone()
  {
    try 
    {
      Entity entity =  (Entity)super.clone();
      entity.collisionBounds = (CollisionBounds)collisionBounds.clone();
      entity.collisionGroup = (CollisionGroup)collisionGroup.clone();
      
      ArrayList<Connector> connectorList = new ArrayList<Connector>();
      for (int i = 0; i < connectedKeys.size(); i++) 
      {
        int key = ((Integer)connectedKeys.get(i)).intValue();
        Entity follower = Connector.getFollowerWithID(key);
        if(follower != null)
        {
          Connector connector = follower.getConnection();
          connector.setOwner(entity);
          connectorList.add(connector);
        }
      }
      entity.connectors = connectorList;
      
      if(connection != null)
      {
        Connector clone = (Connector)connection.clone();
        clone.setFollower(entity);
        entity.setConnection(clone);
        Connector.registerFollower(entity);
      }
      
      if(behavior != null)
      {
        entity.behavior = (Behavior)behavior.clone();
        entity.behavior.setOwner(entity);
      }
      
      return entity;
    } catch (CloneNotSupportedException ex) 
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  public void connect(Entity entity, int distance)
  {
    entity.followerKey = entity.hashCode();
    Connector connector = new Connector(this, entity, distance, 0, 0, 0);
    connector.setSyncRotation(true);
  }
  
  public void connect(Entity entity, int distance, int xOff, int yOff, int thetaOff)
  {
    entity.followerKey = entity.hashCode();
    Connector connector = new Connector(this, entity, distance, xOff, yOff, thetaOff);
    connector.setSyncRotation(true);
  }
  
  public void connect(Entity entity, int distance, int xOff, int yOff, int thetaOff,
                      float followStrength, float rotationStrength)
  {
    entity.followerKey = entity.hashCode();
    Connector connector = new Connector(this, entity, distance, xOff, yOff, thetaOff);
    connector.setSyncRotation(true);
    connector.setStrength(followStrength);
    connector.setRotationStrength(rotationStrength);
  }
  
  public void removeConnection(Entity connected)
  {
    for (int i = 0; i < connectors.size(); i++) 
    {
      Connector connector = (Connector)connectors.get(i);
      int key = -1;
      if(connectedKeys.size() > i)
        key = ((Integer)connectedKeys.get(i)).intValue();
        
      if(connector.getFollower() == connected)
      {
        connectors.remove(connector);
      }
      if(key == connected.followerKey)
        connectedKeys.remove(connectedKeys.get(i));
    }
  }
  
  ////
  // package only methods
  ////
  void update(float delta)
  {
/*    if(connectedKeys.size() > connectors.size())
    {
      for (int i = 0; i < connectedKeys.size(); i++) 
      {
        int key = ((Integer)connectedKeys.get(i)).intValue();
        Entity follower = Connector.getFollowerWithID(key);
        if(follower != null)
        {
          Connector connector = follower.getConnection();
          connector.setOwner(this);
          connectors.add(connector);
        }
      }
    }*/
    setVelocity(xVol + xAccel, yVol + yAccel);
    
    float newX = xPosition + xVol*delta;
    float newY = yPosition + yVol*delta;
    
    setPosition(newX + getXScaleOffset(), newY + getYScaleOffset());
    if(collisionBounds != null) {
    	collisionBounds.setTransformTo(getXPosition()+collisionXOffset, getYPosition()+collisionYOffset,
                rotation,
                xScale, yScale);
    }
    setPosition(getXPosition() - getXScaleOffset(), getYPosition() - getYScaleOffset());
    
    /*for (int i = 0; i < connectors.size(); i++) 
    {
      Connector connector = (Connector)connectors.get(i);
      connector.update(delta);
    }*/

    
    if(behavior != null) {
    	if(!behavior.isInitialized()) {
    		behavior.init();
    		behavior.setInitialized(true);
    	}
    	behavior.update(delta);
    }
  }
  
  protected void render(Graphics2D g2)
  {
	  setPosition(getXPosition() + getXScaleOffset(), getYPosition() + getYScaleOffset());
    if(SGFGame.getInstance().isDebugEnabled())
    {
      /*for (int i = 0; i < connectors.size(); i++) 
      {
        ((Connector)connectors.get(i)).render(g2);
      }*/
      
      if(collisionBounds != null)
      {
        collisionBounds.renderCircularBounds(g2);
        collisionBounds.renderPolygonBounds(g2);
      }
    }
    setPosition(getXPosition() - getXScaleOffset(), getYPosition() - getYScaleOffset());
  }
  
  void addConnector(Connector connector)
  {
    connectors.add(connector);
    connectedKeys.add(new Integer(connector.getFollower().followerKey));
  }
  
  boolean removeConnector(Connector connector)
  {
    for (int i = 0; i < connectedKeys.size(); i++) 
    {
      int key = ((Integer)connectedKeys.get(i)).intValue();
      if(key == connector.getFollower().followerKey)
        connectedKeys.remove(connectedKeys.get(i));
    }
    
    return connectors.remove(connector);
  }
  
  ////
  // accessors and mutators
  ////
  ////
  // position
  ////
  public void setPosition(float x, float y)
  {
    this.xPosition = x;
    this.yPosition = y;
  }
  public float getXPosition()
  {
    return xPosition;
  }
  public float getYPosition()
  {
    return yPosition;
  }
  public float getXMidpoint()
  {
	  return xPosition + getCurrentWidth()/2;
  }
  public float getYMidpoint()
  {
	  return yPosition + getCurrentHeight()/2;
  }
  
  ////
  // velocity
  ////
  public void setVelocity(float xVol, float yVol)
  {
    this.xVol = xVol;
    this.yVol = yVol;
  }
  public void setVelocity(float speed, float xDirection, float yDirection)
  {
	  this.xVol = speed*xDirection;
	  this.yVol = speed*yDirection;
  }
  public float getXVelocity()
  {
    return xVol;
  }
  public float getYVelocity()
  {
    return yVol;
  }
  
  ////
  // acceleration
  ////
  public void setAcceleration(float xAccel, float yAccel)
  {
    this.xAccel = xAccel;
    this.yAccel = yAccel;
  }
  public void setAcceleration(float rate, float xDirection, float yDirection)
  {
	  System.out.println("don't use this setAccel method!");
	//needs to be written
  }
  public float getXAcceleration() {
	  return this.xAccel;
  }
  public float getYAcceleration() {
	  return this.yAccel;
  }
  
  ////
  // scale
  ////
  public void setScale(float horizontalScale, float verticalScale)
  {
    this.xScale = horizontalScale;
    this.yScale = verticalScale;
  }
  public void setScaleSize(float newWidth, float newHeight)
  {
	  setScale(newWidth/(this.originalWidth*xScale), newHeight/(this.originalHeight*yScale));
  }
  public float getXScale()
  {
    return xScale;
  }
  public float getYScale()
  {
    return yScale;
  }
  public void flip(boolean horizontal, boolean vertical)
  {
	  if(horizontal)
	  {
		  xFlipped = !xFlipped;
		  setScale(-xScale, yScale);
		  //setPosition((xPosition - originalWidth*xScale), yPosition);
	  }
	  if(vertical)
	  {
		  yFlipped = !yFlipped;
		  setScale(xScale, -yScale);
		  //setPosition(xPosition, (yPosition - originalHeight*yScale));
		  
	  }
  }
  public int getXScaleOffset() {
	  if(xFlipped)
		  return getCurrentWidth();
	  else
		  return 0;
  }
  public int getYScaleOffset() {
	  if(yFlipped)
		  return getCurrentHeight();
	  else
		  return 0;
  }
  
  //(Cori) I have added the following four methods, make sure that's ok
  	public boolean isXFlipped() 
  	{
  		return xFlipped;
  	}
  	public void setXFlipped(boolean xFlipped) 
  	{
		this.xFlipped = xFlipped;
	}

	public boolean isYFlipped() 
	{
		return yFlipped;
	}

	public void setYFlipped(boolean yFlipped) 
	{
		this.yFlipped = yFlipped;
	} 
  
  
  
  ////
  // rotation
  ////
  public void setRotation(float degrees)
  {
    this.rotation = degrees;
  }

  public float getRotation()
  {
    return rotation;
  }
  
  ////
  // bounds
  ////
  public CollisionBounds getCollisionBounds()
  {
    return collisionBounds;
  }
  public void setOriginalSize(int originalWidth, int originalHeight)
  {
    this.originalWidth = originalWidth;
    this.originalHeight = originalHeight;
  }
  public int getOriginalWidth()
  {
    if(originalWidth != 0)
      return originalWidth;
    else
      return collisionBounds.getWidth();
  }
  public int getOriginalHeight()
  {
    if(originalHeight != 0)
      return originalHeight;
    else
      return collisionBounds.getHeight();
  }
  public int getCurrentWidth()
  {
	  if(originalWidth != 0)
	      return Math.abs((int)(originalWidth*xScale));
	    else
	      return Math.abs((int)(collisionBounds.getWidth()*xScale));
  }
  public int getCurrentHeight()
  {
	  if(originalHeight != 0)
		  return Math.abs((int)(originalHeight*yScale));
	  else
	      return Math.abs((int)(collisionBounds.getHeight()*yScale));
  }
  public void setAlive(boolean alive)
  {
    this.alive = alive;
    /*if(!alive && connection != null)
      connection.breakConnection();
    for (int i = 0; i < connectors.size(); i++) 
    {
      ((Connector)connectors.get(i)).breakConnection();
    }
    connectors.clear();*/
  }
  public boolean isAlive()
  {
    return alive;
  }
  
  public void setVisible(boolean visible)
  {
    this.visible = visible;
  }
  public boolean isVisible()
  {
    return visible;
  }

  public CollisionGroup getCollisionGroup()
  {
    return collisionGroup;
  }

  public void setCollisionGroup(CollisionGroup collisionGroup)
  {
    this.collisionGroup = collisionGroup;
  }

  public Behavior getBehavior()
  {
    return behavior;
  }

  public void setBehavior(Behavior behavior)
  {
    this.behavior = behavior;
    if(this.behavior != null)
    {
    	this.behavior.setOwner(this);
    }
  }

  public void setCollisionBounds(CollisionBounds collisionBounds)
  {
    this.collisionBounds = collisionBounds;
  }

  public Connector getConnection()
  {
    return connection;
  }
  
  void setConnection(Connector connector)
  {
    this.connection = connector;  
  }

  public ArrayList<Connector> getConnectors()
  {
    return connectors;
  }

  public int getFollowerKey()
  {
    return followerKey;
  }

	public void setCollisionOffset(float xOffset, float yOffset) {
		this.collisionXOffset = xOffset;
		this.collisionYOffset = yOffset;
	}
	
	public float getCollisionXOffset() {
		return collisionXOffset;
	}
	
	public float getCollisionYOffset() {
		return collisionYOffset;
	}
}