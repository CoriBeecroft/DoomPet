package com.cyntaks.sgf.utils;

public class PrecisionTimer 
{
  private static long ticksPerSecond;
  
  static
  {
    ticksPerSecond = 1000000000;
  }
  
  ////
  //  public interface
  ////
  public static long getTime()
  {
    return System.nanoTime();
  }
  
  ////
  // accessors and mutators
  ////
  public static long getTicksPerSecond()
  {
    return ticksPerSecond;
  }
}