package com.cyntaks.sgf.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Connector implements Serializable, Cloneable
{
  public static final long serialVersionUID = 1L;
	
  private transient Entity owner;
  private transient Entity follower;
  
  private float theta;
  private float distance;
  private float velocity;
  private float acceleration;
  private int xOff;
  private int yOff;
  private float connectX;
  private float connectY;
  private float thetaOff;
  
  private float lastFollowerX;
  private float lastFollowerY;
  private static final float MAX_PIXELS_PER_FRAME = 10f;
  private static final float MAX_DEGREES_PER_FRAME = 1.5f;
  private float strength;
  private float rotationStrength;
  
  transient Entity tempConnectee;
  
  private boolean syncRotation;
  
  private static ArrayList<Entity> registeredFollowers = new ArrayList<Entity>();
  
  public Connector(Entity owner, Entity follower, float distance,
                   int xOff, int yOff, float thetaOff)
  {
    this.follower = follower;
    this.owner = owner;
    this.distance = distance;
    this.xOff = xOff;
    this.yOff = yOff;
    this.thetaOff = thetaOff;
    init();
  }
  
  public Connector(Entity owner, Entity follower, float distance)
  {
    this(owner, follower, distance, 0, 0, 0);
  }
  
  public Connector(float connectX, float connectY, Entity follower, float distance,
                   int xOff, int yOff, float thetaOff)
  {
    this.follower = follower;
    this.connectX = connectX;
    this.connectY = connectY;
    this.distance = distance;
    this.xOff = xOff;
    this.yOff = yOff;
    this.thetaOff = thetaOff;
    init();
  }
  
  public Connector(float connectX, float connectY, Entity follower, int distance)
  {
    this(connectX, connectY, follower, distance, 0, 0, 0);
  }
  
  public static void registerFollower(Entity follower)
  {
    registeredFollowers.add(follower);
  }
  
  public static void removeRegisteredFollower(Entity follower)
  {
    registeredFollowers.remove(follower);
  }
  
  public static Entity getFollowerWithID(int id)
  {
    for (int i = 0; i < registeredFollowers.size(); i++) 
    {
      Entity follower = (Entity)registeredFollowers.get(i);
      if(follower.getFollowerKey() == id)
      {
        registeredFollowers.remove(follower);
        return follower;
      }
    }
    return null;
  }
  
  private void init()
  {
    lastFollowerX = follower.getXPosition();
    lastFollowerY = follower.getYPosition();
    
    strength = 1;
    rotationStrength = 1;
    if(owner != null)
      owner.addConnector(this);
    follower.setConnection(this);
  }
  
  public Object clone()
  {
    try 
    {
      Connector connector = (Connector)super.clone();
      
      return connector;
    } catch (CloneNotSupportedException ex) 
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  public void update(float delta)
  {
    if(follower != null)
    {
      lastFollowerX = follower.getXPosition()+follower.getCurrentWidth()/2;
      lastFollowerY = follower.getYPosition()+follower.getCurrentHeight()/2;
    }
    
    velocity += acceleration*delta;
    float change = velocity*delta;
    
    if(syncRotation)
    {
      float maxRotation = MAX_DEGREES_PER_FRAME*rotationStrength;
      
      float destTheta = owner.getRotation();
      
      if(rotationStrength != 1 && Math.abs(destTheta-theta) > maxRotation)
      {
        if(destTheta > theta)
          theta += maxRotation;
        else
          theta -= maxRotation;
      }
      else
        theta = destTheta;
    }
    else
      theta += change;
      
    if(owner != null)
    {
      connectX = owner.getXPosition();
      connectY = owner.getYPosition();
    }
    
    int destX = (int)(connectX+xOff + (Math.cos(Math.toRadians(theta+thetaOff))*distance));
    int destY = (int)(connectY+yOff + (Math.sin(Math.toRadians(theta+thetaOff))*distance));
    
    float maxDistance = strength*MAX_PIXELS_PER_FRAME;
    
    float newX = 0;
    float newY = 0;
    
    if(strength < 1 && Math.abs(destX-lastFollowerX) > maxDistance)
    {
      if(destX > lastFollowerX)
        newX = lastFollowerX+maxDistance;
      else
        newX = lastFollowerX-maxDistance;
    }
    else
      newX = destX;
      
    if(strength < 1 && Math.abs(destY-lastFollowerY) > maxDistance)
    {
      if(destY > lastFollowerY)
        newY = lastFollowerY+maxDistance;
      else
        newY = lastFollowerY-maxDistance;
    }
    else
      newY = destY;
  
    if(follower != null)
      follower.setPosition(newX-follower.getCurrentWidth()/2, newY-follower.getCurrentHeight()/2);
  }
  
  public void snapTo()
  {
    int newX = (int)(connectX+xOff + (Math.cos(Math.toRadians(theta+thetaOff))*distance));
    int newY = (int)(connectY+yOff + (Math.sin(Math.toRadians(theta+thetaOff))*distance));
    
    follower.setPosition(newX-follower.getCurrentWidth()/2, newY-follower.getCurrentHeight()/2);
  }
  
  public void render(Graphics2D g2)
  {
    int x1 = 0;
    int y1 = 0;
    int x2 = 0;
    int y2 = 0;
    
    /*if(owner != null)
    {
      x1 = (int)owner.getXPosition()+xOff;
      y1 = (int)owner.getYPosition()+yOff;
      
      if(owner instanceof EntityGroup)
      {
        EntityGroup group = (EntityGroup)owner;
        x1 = (int)group.getXPosition();
        y1 = (int)group.getYPosition();
      }
    }
    else
      System.out.println("owner null");*/
    
    if(follower != null)
    {
      x2 = (int)follower.getXPosition()+follower.getCurrentWidth()/2;
      y2 = (int)follower.getYPosition()+follower.getCurrentHeight()/2;
    }
    else
      System.out.println("follower null");
    
    g2.setColor(Color.GREEN);
    g2.drawLine(x1, y1, x2, y2);
    
    //g2.setColor(Color.RED);
    //g2.fillRect(x1-2, y1-2, 4, 4);
    //g2.fillRect(x2-2, y2-2, 4, 4);
  }
  
  public void breakConnection()
  {
    if(owner != null)
      owner.removeConnection(follower);
    syncRotation = false;
  }
  
  public float getTheta()
  {
    return theta;
  }
  
  public void setTheta(float theta)
  {
    this.theta = theta;
  }

  public void setSyncRotation(boolean syncRotation)
  {
    this.syncRotation = syncRotation;
  }

  public int getXOff()
  {
    return xOff;
  }

  public void setXOff(int xOff)
  {
    this.xOff = xOff;
  }

  public int getYOff()
  {
    return yOff;
  }

  public void setYOff(int yOff)
  {
    this.yOff = yOff;
  }

  public float getVelocity()
  {
    return velocity;
  }
  
  public void setVelocity(float velocity)
  {
    this.velocity = velocity;
  }

  public float getDistance()
  {
    return distance;
  }

  public void setDistance(float distance)
  {
    this.distance = distance;
  }

  public float getAcceleration()
  {
    return acceleration;
  }

  public void setAcceleration(float acceleration)
  {
    this.acceleration = acceleration;
  }

  public float getConnectX()
  {
    return connectX;
  }

  public void setConnectX(float connectX)
  {
    this.connectX = connectX;
  }

  public float getConnectY()
  {
    return connectY;
  }

  public void setConnectY(float connectY)
  {
    this.connectY = connectY;
  }

  public float getStrength()
  {
    return strength;
  }

  public void setStrength(float strength)
  {
    this.strength = strength;
  }

  public float getRotationStrength()
  {
    return rotationStrength;
  }

  public void setRotationStrength(float rotationStrength)
  {
    this.rotationStrength = rotationStrength;
  }

  public Entity getFollower()
  {
    return follower;
  }

  public void setFollower(Entity follower)
  {
    this.follower = follower;
    snapTo();
  }

  public Entity getOwner()
  {
    return owner;
  }

  public void setOwner(Entity owner)
  {
    this.owner = owner;
  }
}