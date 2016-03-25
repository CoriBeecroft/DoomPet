package com.cyntaks.sgf.event;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Events are used to keep track of time passage so that action can be taken
 * during its duration or when it completes. Events can be used with
 * Interpolators to report intermediate values to an EventListener depending
 * on the length of the Event and the amount of time passed. If an Interpolator
 * is not specified then the ratio of time passed to total time will be reported
 * to the EventListener.
 * 
 * @author Weston Beecroft
 */

public class Event implements Serializable
{
  public static final long serialVersionUID = 1L;
  
  public static final int INFINITE = -1;
  public static final int EVERY_FRAME = -1;
  public final int ID;
  private final float TOTAL_TIME;
  private float timePassed;
  private int updateFrequency; //how often the listeners should be updated
  private int lastUpdate;
  private ArrayList eventListeners;
  private boolean repeat; //whether the event should repeat or not
  private boolean alive;
  private Interpolator interpolator;
  
  /**
   * Constructs a new Event with no initial listener and no interpolator
   * @param ID the Event's ID
   * @param eventLength the total event length
   * @param updateFrequency the frequency at which listener's should be updated
   * @param repeat whether the event should repeat or not when it finishes
   */
  public Event(int ID, int eventLength, int updateFrequency, boolean repeat)
  {
    this.ID = ID;
    if(eventLength == INFINITE)
      this.TOTAL_TIME = eventLength;
    else
      this.TOTAL_TIME = eventLength/1000f;
    
    this.updateFrequency = updateFrequency;
    this.repeat = repeat;
    alive = true;
    eventListeners = new ArrayList();
    EventManager.getInstance().addEvent(this);
  }
  
  /**
   * constructs a new Event with no Interpolator and an initial EventListener
   * @param ID the Event's ID
   * @param eventLength the total event length
   * @param updateFrequency the frequency at which listener's should be updated
   * @param repeat whether the event should repeat or not when it finishes
   * @param listener initial EventListener
   */
  public Event(int ID, int eventLength, int updateFrequency, boolean repeat, EventListener listener)
  {
    this(ID, eventLength, updateFrequency, repeat);
    addEventListener(listener);
  }
  
  /**
   * constructs a new Event with the given Interpolator and no initial EventListener
   * @param ID the Event's ID
   * @param eventLength the total event length
   * @param updateFrequency the frequency at which listener's should be updated
   * @param repeat whether the event should repeat or not when it finishes
   * @param interpolator the Event's Interpolator
   */
  public Event(int ID, int eventLength, int updateFrequency, boolean repeat, Interpolator interpolator)
  {
    this(ID, eventLength, updateFrequency, repeat);
    this.interpolator = interpolator;
  }
  
  /**
   * constructs a new Event with the given Interpolator and initial EventListener
   * @param ID the Event's ID
   * @param eventLength the total event length
   * @param updateFrequency the frequency at which listener's should be updated
   * @param repeat whether the event should repeat or not when it finishes
   * @param interpolator the Event's Interpolator
   * @param listener initial EventListener
   */
  public Event(int ID, int eventLength, int updateFrequency, boolean repeat, Interpolator interpolator, EventListener listener)
  {
    this(ID, eventLength, updateFrequency, repeat, interpolator);
    addEventListener(listener);
  }
  
  /**
   * updates the Event based on the given time delta. All listeners will be updated
   * if it is time according to this Event's update frequency. If the Event has ended,
   * then all EventListeners will be notified and this Event will be removed from the
   * EventManager during the next frame.
   * @param delta the amount of time that passed during the last frame
   */
  void update(float delta)
  {
    if(alive)
    {
      timePassed += delta;
      if(interpolator != null)
        interpolator.update(timePassed/TOTAL_TIME);
      if(TOTAL_TIME != INFINITE && timePassed >= TOTAL_TIME) //event is over
      {
        timePassed = 0; //reset
        if(interpolator != null)
          interpolator.reset();
        for (int i = 0, n = eventListeners.size(); i < n; i++) 
        {
            ((EventListener)eventListeners.get(i)).finish(ID); //notify each listener that the event is finished
        }
        if(!repeat)
          alive = false; //event can be killed now
      }
      else //event isn't over so update as usual
      {
        lastUpdate += delta;
        if(updateFrequency == EVERY_FRAME || lastUpdate > updateFrequency) //time for to give a new update
        {
          lastUpdate = lastUpdate%updateFrequency; //reset
          float value = (float)timePassed/TOTAL_TIME; //the percentange of time passed is the value if we don't have an interpolator
          if(interpolator != null)
            value = interpolator.getValue(); //get the interpolator's value if available
          for (int i = 0, n = eventListeners.size(); i < n; i++) 
          {
             ((EventListener)eventListeners.get(i)).update(ID, value); //update each listener
          }
        }
      }
    }
  }
  
  public void addEventListener(EventListener listener)
  {
    eventListeners.add(listener);
  }
  
  public boolean removeEventListener(EventListener listener)
  {
    return eventListeners.remove(listener);
  }
  
  public void kill()
  {
    alive = false;
  }

  public boolean isRepeat()
  {
    return repeat;
  }

  public void setRepeat(boolean repeat)
  {
    this.repeat = repeat;
  }

  public int getEventLength()
  {
    return (int)(TOTAL_TIME*1000);
  }

  public int getUpdateFrequency()
  {
    return updateFrequency;
  }

  public void setUpdateFrequency(int updateFrequency)
  {
    this.updateFrequency = updateFrequency;
  }
  
  public Interpolator getInterpolator()
  {
    return interpolator;
  }
  
  public void setInterpolator(Interpolator newInterpolator)
  {
    this.interpolator = newInterpolator;
  }

  public boolean isAlive()
  {
    return alive;
  }
}