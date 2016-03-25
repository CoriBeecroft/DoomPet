package com.cyntaks.sgf.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.*;
import java.io.*;
import java.awt.geom.Area;

public class CollisionBounds implements Serializable, Cloneable
{
  public static final long serialVersionUID = 1L;
  
  private transient Area originalArea;
  private transient Area area;
  private static transient AffineTransform transform = new AffineTransform();
  
  private int originalWidth;
  private int originalHeight;
  private transient int width;
  private transient int height;
  
  private int originalX;
  private int originalY;
  private transient int x;
  private transient int y;
  
  private int originalRadius;
  private transient int radius;
  
  private int[] xPoints;
  private int[] yPoints;
  
  private transient float xScale;
  private transient float yScale;
  private transient float rotation;
  private transient boolean needsTransform;
  
  private static transient float[] pointBuffer = new float[2];
  
  private transient int type;
  public static transient final int CIRCULAR = 0;
  public static transient final int POLYGON = 1;
  
  public CollisionBounds(int[] xPoints, int[] yPoints, int width, int height, int radius, int x, int y)
  {
    this(radius);
    this.xPoints = xPoints;
    this.yPoints = yPoints;
    
    this.originalX = x;
    this.originalY = y;
    this.originalWidth = width;
    this.originalHeight = height;
    this.width = originalWidth;
    this.height = originalHeight;
    
    Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);
    originalArea = new Area(poly);
    area = originalArea.createTransformedArea(transform);
    
    type = POLYGON;
  }
  
  public CollisionBounds(int radius)
  {
    xScale = 1;
    yScale = 1;
    rotation = 0;
    this.originalRadius = radius;
    this.radius = originalRadius;
    originalWidth = radius*2;
    originalHeight = radius*2;
    type = CIRCULAR;
  }
  
  public Object clone()
  {
    try 
    {
      return super.clone();
    } catch (CloneNotSupportedException ex) 
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    
    transform = new AffineTransform();
    x = originalX;
    y = originalY;
    width = originalWidth;
    height = originalHeight;
    radius = originalRadius;
    transform.setToTranslation(x, y);
    if(xPoints != null)
    {
      Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);
      originalArea = new Area(poly);
      area = originalArea.createTransformedArea(transform);
    }
    type = POLYGON;
  }
  
  public void setTransformTo(float xPos, float yPos, float rotation, float xScale, float yScale)
  {
    this.x = (int)(xPos)+originalX;
    this.y = (int)(yPos)+originalY;
    this.width = (int)(originalWidth*xScale);
    this.height = (int)(originalHeight*yScale);
    this.xScale = xScale;
    this.yScale = yScale;
    this.rotation = rotation;
    needsTransform = true;
  }
  
  public void transform()
  {
    if(needsTransform)
    {
      if(originalRadius == 0)
        originalRadius = radius;
      float radiusScale = (width > height ? xScale : yScale);
      radius = (int)(originalRadius*radiusScale); //not positive this works correctly
      transform.setToTranslation(x, y);
      transform.scale(xScale, yScale);
      transform.rotate(Math.toRadians(rotation), width/2/xScale, height/2/yScale);
      
      if(originalArea != null)
        area = originalArea.createTransformedArea(transform);
        
      needsTransform = false;
    }
  }
  
  public void translate(float xDistance, float yDistance)
  {
    x += xDistance;
    y += yDistance;
    transform.translate(xDistance+originalX, yDistance+originalY);
  }
  
  public void scale(float xScale, float yScale)
  {
    width = (int)(originalWidth*xScale);
    height = (int)(originalHeight*yScale);
    
    transform.scale(xScale, yScale);
  }
  
  public void rotate(float degrees, float pivotX, float pivotY)
  {
    transform.rotate(Math.toRadians(degrees), pivotX/(transform.getScaleX()), pivotY/(transform.getScaleY()));
  }
  
  public void resetTransform()
  {
    xScale = 1;
    yScale = 1;
    rotation = 0;
    x = originalX;
    y = originalY;
    width = originalWidth;
    height = originalHeight;
    radius = originalRadius;
    transform.setToIdentity();
  }
  
  public boolean collidesWith(CollisionBounds bounds)
  {
    return checkForCollision(this, bounds);
  }
  
  private boolean checkForCollision(CollisionBounds collider, CollisionBounds collider2)
  {
    float distance = getDistanceSquared(collider.getX()+collider.getRadius(), collider.getY()+collider.getRadius(),
                                        collider2.getX()+collider2.getRadius(), collider2.getY()+collider2.getRadius());
    float minDistance = (collider.getRadius()+collider2.getRadius())*(collider.getRadius()+collider2.getRadius());
    if(distance <= minDistance)
    {
      if(collider.type == CIRCULAR && collider2.type == CIRCULAR)
        return true;
      else
        return checkForPolygonCollision(collider, collider2);
    }
    else
    {
      return false;
    }
  }
  
  private boolean checkForPolygonCollision(CollisionBounds collider, CollisionBounds collider2)
  {
    collider.transform();
    collider2.transform();
    
    Area area = collider.getArea();
    Area area2 = collider2.getArea();
    
    PathIterator iterator = null;
    
    if(collider.type == POLYGON)
    {
      iterator = area.getPathIterator(null);
      while(!iterator.isDone())
      {
        iterator.currentSegment(pointBuffer);
        if(collider2.contains(pointBuffer[0], pointBuffer[1]))
          return true;
        iterator.next();
      }
    }
    else
    {
      if(collider2.contains(collider.getX()+collider.getRadius(), collider.getY()+collider.getRadius()))
        return true;
        
      int smallRadius = collider.getRadius()>>8;
      if(collider2.contains(collider.getX()-smallRadius, collider.getY()+smallRadius))
        return true;
      if(collider2.contains(collider.getX()-smallRadius, collider.getY()-smallRadius))
        return true;
      if(collider2.contains(collider.getX()+smallRadius, collider.getY()-smallRadius))
        return true;
      if(collider2.contains(collider.getX()+smallRadius, collider.getY()+smallRadius))
        return true;
    }
    
    if(collider2.type == POLYGON)
    {
      iterator = area2.getPathIterator(null);
      while(!iterator.isDone())
      {
        iterator.currentSegment(pointBuffer);
        if(collider.contains(pointBuffer[0], pointBuffer[1]))
          return true;
        iterator.next();
      }
    }
    else
    {
      if(collider.contains(collider2.getX()+collider2.getRadius(), collider2.getY()+collider2.getRadius()))
        return true;
      
      int smallRadius = collider2.getRadius()>>8;
      if(collider.contains(collider2.getX()-smallRadius, collider2.getY()+smallRadius))
        return true;
      if(collider.contains(collider2.getX()-smallRadius, collider2.getY()-smallRadius))
        return true;
      if(collider.contains(collider2.getX()+smallRadius, collider2.getY()-smallRadius))
        return true;
      if(collider.contains(collider2.getX()+smallRadius, collider2.getY()+smallRadius))
        return true;
    }
    
    return false;
  }
  
  public boolean contains(float x, float y)
  {
    if(type == CIRCULAR)
    {
      float distance = getDistanceSquared(this.x+radius, this.y+radius, x, y);
      
      if(distance <= (radius*radius))
        return true;
      else
        return false;
    }
    else
    {
      return area.contains(x, y);
    }
  }
  
  private float getDistanceSquared(float x1, float y1, float x2, float y2)
  {
    return (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
  }
  
  public void renderPolygonBounds(Graphics2D g2)
  {
    needsTransform = true;
    transform();
    g2.setColor(Color.RED);
    if(xPoints != null && xPoints.length > 0 && type == POLYGON)
    {
      AffineTransform oldTransform = g2.getTransform();
      g2.setTransform(transform);
      g2.drawPolygon(xPoints, yPoints, xPoints.length);
      g2.setTransform(oldTransform);
    }
  }
  
  public void renderCircularBounds(Graphics2D g2)
  {
    needsTransform = true;
    transform();
    g2.setColor(Color.RED);
    //this could be completely wonky -- edited while sleepy, but it was broken before anyhow.
    //Actual collision positioning seems intact.
    int xShift = (radius*2 - width)/2;
    int yShift = (radius*2 - height)/2;
    if(radius > 0)
      g2.drawOval(x-xShift, y-yShift, radius*2, radius*2);
  }
  
  public String toString()
  {
    String string = "";
    if(xPoints != null)
    {
      for (int i = 0; i < xPoints.length; i++) 
      {
        string = string+"x: " + xPoints[i] + ", y: " + yPoints[i] + "\n";
      }
    }
    else
      string = "x: " + x + ", y: " + y + ", radius: " + radius;
      
    return string;
  }

  public Area getArea()
  {
    return area;
  }

  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }

  public int getRadius()
  {
    return radius;
  }

  public int getWidth()
  {
    return width;
  }

  public int getHeight()
  {
    return height;
  }
  
  public void setCollisionType(int type)
  {
    this.type = type;
  }
  
  public int getCollisionType()
  {
    return type;
  }
}