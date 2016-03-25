package com.cyntaks.sgf.utils;

public class FrameCounter 
{
  private long lastTime;
	private StringBuffer fpsStr;
	private long frameTime;
	private long lastFrameTime;
	private long timerRes;
	private long frameCount;
  private long currentTime;
  private int fps;

	public FrameCounter()
	{
    fpsStr = new StringBuffer();
		timerRes = 1000;
	}

	public void tick()
	{
		frameCount++;
		currentTime = System.currentTimeMillis();
		frameTime = currentTime - lastFrameTime;
		lastFrameTime = currentTime;
		if(currentTime - lastTime > timerRes)
		{
      fps = (int)((float)frameCount/(float)(currentTime - lastTime)*timerRes);
      fpsStr.replace(0, fpsStr.length(), fps+"fps");
			frameCount=0;
			lastTime=currentTime;
		}
	}

	public String getStr()
	{
		return fpsStr.toString();
	}
  
  public int getLastFPS()
  {
    return fps;
  }

	public long getFrameTime()
	{
		return frameTime;
	}

  public int getFps()
  {
    return fps;
  }

  public long getTimerRes()
  {
    return timerRes;
  }
}