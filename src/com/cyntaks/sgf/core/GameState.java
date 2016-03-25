package com.cyntaks.sgf.core;

import com.cyntaks.sgf.input.InputHandler;
import com.cyntaks.sgf.sprite.SpriteManager;
import java.awt.*;

public abstract class GameState 
{
  private String name;
  private boolean initialized;
  private ProgressBar progressBar;
  private boolean paused;
  private String pauseMessage;
  private static final String FOCUS_PAUSE_MESSAGE = "Paused -- click to resume.";
  private static final Font bigFont = new Font("Impact", Font.PLAIN, 16);
  
  ////
  // constructors
  ////
  public GameState(String name)
  {
    paused = false;
    this.name = name;
    progressBar = new ProgressBar();
  }
  
  void auxUpdate(float delta)
  { 
    if(!paused)
    {
      update(delta);
    }
  }
  
  void auxRender(Graphics2D g2)
  {
    if(!paused)
    {
      render(g2);
    }
    else
    {
      render(g2);
      renderPaused(g2);
    }
  }
  
  ////
  // abstract methods
  ////
  public abstract void loadResources();
  public abstract void init();
  public abstract void preRender(Graphics2D g2);
  public abstract void render(Graphics2D g2);
  public abstract void update(float delta);
  public abstract void cleanup();
  
  ////
  // public interface
  ////
  public void renderLoadingScreen(Graphics2D g2)
  {
    int width = SGFGame.getInstance().getWidth();
    int height = SGFGame.getInstance().getHeight();
    String loadString = "Loading...";
    
    int centerWidth = 100+progressBar.getWidth();
    int centerHeight = 100+progressBar.getHeight();
    
    if(centerWidth > width-8)
      centerWidth = width-8;
    
    if(centerHeight > height-8)
      centerHeight = height-8;
    
    Composite oldComp = g2.getComposite();
    AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
    g2.setComposite(alphaComp);
    g2.setColor(Color.GRAY);
    g2.fillRect(width/2-centerWidth/2, height/2-centerHeight/2, centerWidth, centerHeight);
    g2.setComposite(oldComp);
    g2.setColor(Color.WHITE);
    g2.drawRect(width/2-centerWidth/2, height/2-centerHeight/2, centerWidth, centerHeight);
    
    Font oldFont = g2.getFont();
    g2.setFont(bigFont);
    FontMetrics metrics = g2.getFontMetrics(bigFont);
    int stringWidth = (int)metrics.getStringBounds(loadString, g2).getWidth();
    int stringHeight = metrics.getHeight();
    
    
    g2.setColor(Color.WHITE);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.drawString(loadString, width/2-stringWidth/2, height/2-stringHeight/2-progressBar.getHeight()/2);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    g2.setFont(oldFont);
    
    progressBar.render(g2,
                       width/2-progressBar.getWidth()/2,
                       height/2-progressBar.getHeight()/2);
  }
  
  public void lostFocus()
  {
    pause(FOCUS_PAUSE_MESSAGE);
  }
  
  public void gainedFocus()
  {
    unpause();
  }
  
  public void updateLoadingScreen(float delta)
  {
    
  }
  
  public void setInputHandler(InputHandler inputHandler)
  {
    SGFGame.getInstance().registerInputHandler(inputHandler);
  }
  
  ////
  // package only methods
  ////
  final void activate()
  {
    new Thread(){
      public void run()
      {
        SGFGame.getInstance().setLoading(true);
        loadResources();
        ResourceManager.loadQueuedResources(progressBar, true);
        //System.out.println("init after this");
        init();
        if(!SGFGame.getInstance().hasFocus() && false)//(Cori) fix this unreachable code
          paused = true;
        initialized = true;
        SpriteManager.getInstance().requestSort();
        SGFGame.getInstance().setLoading(false);
      }
    }.start();
  }
  
  ////
  // private methods
  ////
  public void renderPaused(Graphics2D g2)
  {
    int width = SGFGame.getInstance().getWidth();
    int height = SGFGame.getInstance().getHeight();
      
    int centerWidth = 100+progressBar.getWidth();
    int centerHeight = 50+progressBar.getHeight();
    
    if(centerWidth > width-8)
      centerWidth = width-8;
    
    if(centerHeight > height-8)
      centerHeight = height-8;
    
    Composite oldComp = g2.getComposite();
    AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75f);
    g2.setComposite(alphaComp);
    g2.setColor(Color.GRAY);
    g2.fillRect(width/2-centerWidth/2, height/2-centerHeight/2, centerWidth, centerHeight);
    g2.setComposite(oldComp);
    g2.setColor(Color.WHITE);
    g2.drawRect(width/2-centerWidth/2, height/2-centerHeight/2, centerWidth, centerHeight);
    
    if(pauseMessage == null)
    	pauseMessage = FOCUS_PAUSE_MESSAGE;
    
    Font oldFont = g2.getFont();
    g2.setFont(bigFont);
    FontMetrics metrics = g2.getFontMetrics(bigFont);
    int stringWidth = (int)metrics.getStringBounds(pauseMessage, g2).getWidth();
    int stringHeight = metrics.getHeight();
      
    g2.setColor(Color.WHITE);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.drawString(pauseMessage, width/2-stringWidth/2, height/2-stringHeight/2+metrics.getAscent());
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    g2.setFont(oldFont);
  }
  
  ////
  // accessors and mutators
  ////
  public final String getName()
  {
    return name;
  }
  
  public final boolean isInitialized()
  {
    return initialized;
  }
  
  void setInitialized(boolean initialized)
  {
    this.initialized = initialized;
  }

  public ProgressBar getProgressBar()
  {
    return progressBar;
  }

  public void setProgressBar(ProgressBar progressBar)
  {
    this.progressBar = progressBar;
  }
  
  public void pause(String message) {
	  paused = true;
	  pauseMessage = message;
	  SGFGame.getInstance().pushCameraState();
	  SGFGame.getInstance().setCameraTo(0, 0);
  }

  public void unpause() {
	  paused = false;
	  SGFGame.getInstance().popCameraState();
  }
  
  public boolean isPaused()
  {
    return paused;
  }
}