package com.cyntaks.sgf.core;

/**
 * SGFGame embodies all the functions that belong to a game as a program running
 * on a computer. In this case, those functions include managing the game's window,
 * updating the game's state, rendering the game's graphics, hiding the cursor,
 * and cleaning up when the game exits.
 * 
 * SGFGame also manages any SGF Modules, simply add them to this SGFGame and they
 * will be updated/rendered. You can also obtain a list of active modules and remove
 * modules.
 */

import com.cyntaks.sgf.input.InputHandler;
import com.cyntaks.sgf.event.EventManager;
import com.cyntaks.sgf.sprite.SpriteManager;
import com.cyntaks.sgf.sprite.SpriteStore;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.*;
import java.awt.Canvas;
import com.cyntaks.sgf.core.WindowConfiguration;
import com.cyntaks.sgf.utils.FrameCounter;
import com.cyntaks.sgf.utils.PrecisionTimer;

public final class SGFGame 
{
	private float tickValue;
	
  private static SGFGame INSTANCE = new SGFGame(true); //singleton
  private boolean debugMode; //various features may be enable or disabled
                                    //depending on the state of this variable
  private boolean applet; //whether this SGFGame should be an applet or application
  
  private boolean fullscreen; //whether this SGFGame is in fullscreen mode
  private DisplayMode originalDisplayMode; //the display mode before switching to fullscreen
  private WindowConfiguration windowConfiguration; //the chosen WindowConfiguration
  private String windowTitle; //the title of the JFrame
  private JFrame mainFrame; //the JFrame used for applications, rendering
                            //surface used in full screen mode
  
  private Canvas drawingSurface; //rendering done here for non full screen
  private BufferStrategy strategy; //manages offscreen surfaces
  
  private boolean running; //game loop continues while this is true
  private int frameRate; //desired number of frames per second
  private long timeNow, timeThen, timeLate; //used for syncing to the desired framerate
  private FrameCounter frameCounter; //used to track the framerate
  private boolean useSimpleFrameSyncing; //works better for on low res timers
  private boolean timeBased;
  
  private ArrayList<UpdateListener> updateListeners;
  
  InputHandler lastInputHandler; //last used input handler, we keep track
                                         //of it so we can remove it if necessary
  private int transX;
  private int transY;
  private int camZoom;
  private LinkedList<CameraState> cameraStateStack;
  
  private boolean frameCap;
  private boolean clearBG;
  
  boolean loading;
  
  private class CameraState
  {
    int transX;
    int transY;
    int camZoom;
    
    public CameraState(int transX, int transY, int camZoom)
    {
      this.transX = transX;
      this.transY = transY;
      this.camZoom = camZoom;
    }
  }
  
  ////
  // constructors
  ////
  public static SGFGame getInstance()
  {
    if(INSTANCE == null)
      INSTANCE = new SGFGame(true);
    return INSTANCE;
  }
  
  public static SGFGame getInstance(boolean initialize)
  {
    if(INSTANCE == null)
      INSTANCE = new SGFGame(initialize);
    return INSTANCE;
  }
  
  private SGFGame(boolean initialize)
  {
    if(initialize)
    {
      updateListeners = new ArrayList<UpdateListener>();
      cameraStateStack = new LinkedList<CameraState>();
      
      clearBG = true;
      timeBased = true;
      frameCap = true;
      frameRate = 60;
      transX = 0;
      transY = 0;
      camZoom = 0;
      frameCounter = new FrameCounter();

      updateListeners.add(GameStateManager.getInstance());
      updateListeners.add(EventManager.getInstance());
      updateListeners.add(SpriteManager.getInstance());
      updateListeners.add(EntityManager.getInstance());
    }
  }
  /**
   * creates a new SGFGame with several possible window configurations, this 
   * constructor is always called.
   */
  private void initDisplay(WindowConfiguration[] windowConfigurations, String title, boolean applet)
  {
    this.applet = applet;
    this.windowTitle = title;
    drawingSurface = new Canvas();
    drawingSurface.setBackground(Color.BLACK);
    
    if(applet)
      windowConfiguration = new WindowConfiguration(windowConfigurations[0]);
    else
    {
      initFrame(title);
      showWindow(windowConfigurations);
      createBufferStrategy();
    }
  }
  /**
   * creates a new SGFGame suitable for a fullscreen application.
   * The WindowConfigurations should be ordered so that the first configuration
   * is the one you would most like to use.
   */
  public void initDisplay(WindowConfiguration[] windowConfigurations, String title)
  {
    initDisplay(windowConfigurations, title, false);
  }
  /**
   * creates a new SGFGame suitable for an applet
   */
  public void initDisplay(WindowConfiguration windowConfig, JApplet applet)
  {
    initDisplay(new WindowConfiguration[]{windowConfig}, "", true);
    
    drawingSurface.addFocusListener(new FocusListener(){
      public void focusGained(FocusEvent e)
      {
        gainedFocus();
      }
      public void focusLost(FocusEvent e)
      {
        lostFocus();
      }
    });
    System.out.println("got to this other one");
    drawingSurface.setSize(windowConfig.getWidth(), windowConfig.getHeight());
    applet.getContentPane().add(drawingSurface);
    createBufferStrategy();
  }
  /**
   * creates a new SGFGame suitable for a windowed application
   */
  public void initDisplay(WindowConfiguration windowConfig, String title)
  {
    initDisplay(new WindowConfiguration[]{windowConfig}, title, false);
  }
  
  ////
  // public interface
  ////
  /**
   * starts the game loop which will run until exit is called
   */
  public void start()
  {
    new Thread()
    {
      public void run()
      {
        running = true;
        long lastTime = PrecisionTimer.getTime();
        long delta = 0;
        float deltaSeconds = 0;
        
        while(running)
        {
          delta = PrecisionTimer.getTime()-lastTime;
          deltaSeconds = (float)delta/PrecisionTimer.getTicksPerSecond();
          lastTime = PrecisionTimer.getTime();
          tickValue = ((1000f/PrecisionTimer.getTicksPerSecond())
                  *(PrecisionTimer.getTicksPerSecond()/(float)frameRate))
                  /1000f;         
          
          if(timeBased)
            update(deltaSeconds);
          else
            update(tickValue);
            
          doRendering();
          
          if(frameCap)
          {
            if(useSimpleFrameSyncing)
              sync2(frameRate);
            else
              sync(frameRate);
          }
            
          frameCounter.tick();
        }
        if(!applet)
          doExit();
      }
    }.start();
  }
  
  /**
   * allows the game loop to finish then exits the program
   */
  public void exit()
  {
    if(applet)
      doExit();
    running = false;
  }
  
  public void setMouseVisible(boolean visible)
  {
    Component component = null;
    if(fullscreen)
      component = mainFrame;
    else
      component = drawingSurface;
    
    if(visible)
      component.setCursor(Cursor.getDefaultCursor());
    else
    {
      component.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                               new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR),
                              new Point(0, 0), "blank"));
    }
  }
  
  public void addUpdateListener(UpdateListener listener)
  {
    updateListeners.add(listener);
  }
  public boolean removeUpdateListener(UpdateListener listener)
  {
    return updateListeners.remove(listener);
  }
  
  public void pushCameraState()
  {
    cameraStateStack.addFirst(new CameraState(transX, transY, camZoom));
  }
  
  public void popCameraState()
  {
    if(!cameraStateStack.isEmpty())
    {
      CameraState state = cameraStateStack.getFirst();
      this.transX = state.transX;
      this.transY = state.transY;
      this.camZoom = state.camZoom;
    }
  }
  
  ////
  // private methods
  ////
  /**
   * acquires a Graphics2D object from the BufferStrategy, calls render
   * using the Graphics2D object, assures that the BufferStrategy's contents
   * is not lost, and displays the buffer.
   */
  private void doRendering()
  {
    while(true) 
    {
      Graphics2D g2 = (Graphics2D)strategy.getDrawGraphics();
      render(g2);
      g2.dispose();
      if(strategy.contentsLost())
        while(!strategy.contentsRestored());
      else
        break;
    }
    if(running)
    {
      strategy.show();
    }
  }
  
  /**
   * clears the buffer and renders all modules
   * @param g2 graphics object obtained from the BufferStrategy
   */
  private void render(Graphics2D g2)
  {
    if(clearBG)
    {
      g2.setColor(Color.BLACK);
      g2.fillRect(0, 0, windowConfiguration.getWidth(), windowConfiguration.getHeight());
    }
    
    if(!loading)
    {
      AffineTransform transform = g2.getTransform();
      g2.translate(transX, transY);
      g2.scale((camZoom+100)/100f, (camZoom+100)/100f);
      
      GameStateManager.getInstance().preRender(g2);
      SpriteManager.getInstance().render(g2);
      EntityManager.getInstance().render(g2);
      GameStateManager.getInstance().render(g2);
      
      g2.setTransform(transform);
    }
    else
      GameStateManager.getInstance().render(g2);
      
    if(debugMode || !frameCap)
    {
      g2.setColor(Color.GREEN);
      g2.drawString(frameCounter.getStr(), 10, 20);
    }
  }
  
  /**
   * updates all modules
   * @param delta duration in seconds for the last frame
   */
  private void update(float delta)
  {
    GameState state = GameStateManager.getInstance().getTopGameState();
    if(state != null && !state.isPaused() && !loading)
    {
      for (int i = 0; i < updateListeners.size(); i++) 
      {
        ((UpdateListener)updateListeners.get(i)).updateOccurred(delta);
      }
    }
  }
  
  /**
   * used to pause the gameloop, catches up when frames take too long
   * @param fps desired frames per second
   */
  private void sync(int fps)
  {
		long gapTo = PrecisionTimer.getTicksPerSecond() / fps + timeThen;
    
		timeNow = PrecisionTimer.getTime();
		while (gapTo > timeNow + timeLate)
    {
			try 
      {
				Thread.yield();
			} catch (Exception ex)
      {
        ex.printStackTrace();
			}
			timeNow = PrecisionTimer.getTime();
		}

		if (gapTo < timeNow)
			timeLate = timeNow - gapTo;
		else
			timeLate = 0;

		timeThen = timeNow;
	}
  
  /**
   * simple pause mechanism for syncing to a desired framerate
   * @param fps desired frames per second
   */
  private void sync2(int fps)
  {
    long pauseTime = (long)((1000.0/PrecisionTimer.getTicksPerSecond())*
                    ((double)PrecisionTimer.getTicksPerSecond()/fps));
    
    try 
    {
      Thread.sleep(pauseTime);
    } catch (Exception ex) 
    {
      ex.printStackTrace();
    }
  }
  
  /**
   * restores the original display mode, disposes the window and exits the program
   */
  private void doExit()
  {
    GameStateManager.getInstance().destroy();
    EventManager.getInstance().destroy();
    SpriteManager.getInstance().destroy();
    EntityManager.getInstance().destroy();
    ResourceManager.destroy();
    SpriteStore.getInstance().destroy();
    updateListeners.clear();
    lastInputHandler = null;
    
    INSTANCE = null;
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    
    if(originalDisplayMode != null)
    {
      if(!fullscreen)
        gd.setFullScreenWindow(mainFrame);
        
      gd.setDisplayMode(originalDisplayMode);
    }
    
    if(!applet)
    {
      mainFrame.dispose();
      System.exit(0);
    }
  }
  /**
   * creates the BufferStrategy
   */
  private void createBufferStrategy()
  {
    if(fullscreen)
    {
      mainFrame.createBufferStrategy(2);
      strategy = mainFrame.getBufferStrategy();
    }
    else
    {
      drawingSurface.createBufferStrategy(2);
      strategy = drawingSurface.getBufferStrategy();
    }
  }
  /**
   * initializes the frame when running as an application
   */
  private void initFrame(String title)
  {
    mainFrame = new JFrame(title);
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    mainFrame.setBackground(Color.BLACK);
    mainFrame.setIgnoreRepaint(true);
    mainFrame.setResizable(false);
    
    mainFrame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e)
      {
        exit();
      }
    });
    
    mainFrame.addWindowFocusListener(new WindowFocusListener(){
      public void windowLostFocus(WindowEvent e)
      {
        lostFocus();
      }
      public void windowGainedFocus(WindowEvent e)
      {
        gainedFocus();
      }
    });
  }
  /**
   * Finds a suitable window configuration, sets up the drawing surface, and makes
   * the frame visible if this SGFGame is an application
   * 
   * @param windowConfigs the desired window configurations
   */
  private void showWindow(WindowConfiguration[] windowConfigs)
  {
    for (int i = 0; i < windowConfigs.length; i++) 
    {
      boolean foundOne = true;
      if(windowConfigs[i].isFullscreen())
      {
        if(!goFullscreen(windowConfigs[i])) //test the config
          foundOne = false;
      }
      
      if(foundOne)
      {
        this.windowConfiguration = windowConfigs[i];
        i = windowConfigs.length; //exit loop
      }
    }
    
    if(!windowConfiguration.isFullscreen())
    {
      drawingSurface.setPreferredSize(new Dimension(windowConfiguration.getWidth(), windowConfiguration.getHeight()));
      mainFrame.getContentPane().add(drawingSurface);
      mainFrame.pack();
    
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      int screenWidth = (int)screenSize.getWidth();
      int screenHeight = (int)screenSize.getHeight();
      mainFrame.setLocation(screenWidth/2-mainFrame.getWidth()/2, 
                            screenHeight/2-mainFrame.getHeight()/2);
      mainFrame.setVisible(true);
    }
    
    if(debugMode)
    	System.out.println("window configuration: " + windowConfiguration);
  }
  
  /**
   * finds a compatible display mode (if available) and switches into fullscreen
   * if supported by the default screen device.
   */
  private boolean goFullscreen(WindowConfiguration windowConfig)
  {
    boolean worked = false;
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    DisplayMode[] displayModes = gd.getDisplayModes();
    for (int i = 0; i < displayModes.length; i++) 
    {
      if(displayModes[i].getWidth() == windowConfig.getWidth() &&
         displayModes[i].getHeight() == windowConfig.getHeight())
      {
        if(windowConfig.getRefreshRate() == 0)
        {
          windowConfig.setRefreshRate(displayModes[i].getRefreshRate());
          worked = true;
        }
        else if(windowConfig.getRefreshRate() == displayModes[i].getRefreshRate())
          worked = true;
        
        if(worked)
        	windowConfig.setBitDepth(displayModes[i].getBitDepth());
       }
    }
    
    if(worked)
    {
      if(gd.isFullScreenSupported())
      {
    	  
        mainFrame.setUndecorated(true);
        originalDisplayMode = gd.getDisplayMode();
        gd.setFullScreenWindow(mainFrame);
        fullscreen = true;
        
        if(gd.isDisplayChangeSupported())
        {
        	gd.setDisplayMode(new DisplayMode(windowConfig.getWidth(),
                            windowConfig.getHeight(),
                            windowConfig.getBitDepth(), windowConfig.getRefreshRate()));
        }
        //display change not supported and needs to change
        else if(windowConfig.getWidth() != originalDisplayMode.getWidth() ||
                windowConfig.getHeight() != originalDisplayMode.getHeight() ||
                windowConfig.getRefreshRate() != originalDisplayMode.getRefreshRate())
        {
          System.err.println("display mode change not supported");
          
          fullscreen = false;
          originalDisplayMode = null;
          gd.getFullScreenWindow().dispose();
          gd.setFullScreenWindow(null);
          mainFrame.setUndecorated(false);
          worked = false;
        }
      }
      else //display change not supported, but doesn't need to change
      {
        System.err.println("full screen mode not supported");
        worked = false;
      }
    }
    else if(debugMode)
      System.err.println("incompatible window configuration");
      
    return worked;
  }
  
  /**
   * called when the top level container loses focus
   */
  private void lostFocus()
  {
    GameState state = GameStateManager.getInstance().getTopGameState();
    if(state != null)
      state.lostFocus();
  }
  
  /**
   * called when the top level container gains focus
   */
  private void gainedFocus()
  {
    GameState state = GameStateManager.getInstance().getTopGameState();
    if(state != null)
      state.gainedFocus();
    
    if(lastInputHandler != null)
      lastInputHandler.clearPressedButtons();
  }
  
  public boolean hasFocus()
  {
    if(mainFrame != null && mainFrame.hasFocus() ||
       drawingSurface.hasFocus())
      return true;
    else
      return false;
  }
  
  public void setCameraTo(int x, int y)
  {
    transX = x;
    transY = y;
  }
  
  public void setCameraZoom(int zoom)
  {
    this.camZoom = zoom;
  }
  
  void registerInputHandler(InputHandler inputHandler)
  {
    if(mainFrame != null)
    {
      mainFrame.removeMouseListener(lastInputHandler);
      mainFrame.removeMouseMotionListener(lastInputHandler);
      mainFrame.removeKeyListener(lastInputHandler);
      
      mainFrame.addMouseListener(inputHandler);
      mainFrame.addMouseMotionListener(inputHandler);
      mainFrame.addKeyListener(inputHandler);
    }
    
    drawingSurface.removeMouseListener(lastInputHandler);
    drawingSurface.removeMouseMotionListener(lastInputHandler);
    drawingSurface.removeKeyListener(lastInputHandler);
    
    drawingSurface.addMouseListener(inputHandler);
    drawingSurface.addMouseMotionListener(inputHandler);
    drawingSurface.addKeyListener(inputHandler);
    
    this.lastInputHandler = inputHandler;
  }
  
  ////
  // accessors and mutators
  ////
  /**
   * various undefined features may be enabled or disabled depending on whether
   * this SGFGame is in debug mode or not.
   */
  public void setDebugEnabled(boolean debug)
  {
    debugMode = debug;
  }
  
  public boolean isDebugEnabled()
  {
    return debugMode;
  }
  
  /**
   * returns the Component that is rendered to. Applications and Applets will
   * render to a Canvas unless the Application runs in full screen mode, the 
   * frame is the drawing surface in this case.
   */
  public Component getDrawingSurface()
  {
    if(fullscreen)
      return mainFrame;
    else
      return drawingSurface;
  }
  
  /**
   * returns the JFrame being used by this application, null if this is an applet
   */
  public JFrame getFrame()
  {
    return mainFrame;
  }

  public void setFrameRate(int frameRate)
  {
    this.frameRate = frameRate;
  }

  public boolean isFullscreen()
  {
    return fullscreen;
  }

  public void setUseSimpleFrameSyncing(boolean useSimpleFrameSyncing)
  {
    this.useSimpleFrameSyncing = useSimpleFrameSyncing;
    if(!useSimpleFrameSyncing && frameRate >= PrecisionTimer.getTicksPerSecond())
    {
      System.err.println("Timer Resolution is too low. Switching to simple frame syncing, try decreasing framerate.");
      this.useSimpleFrameSyncing = true;
    }
  }
  
  public int getWidth()
  {
    return windowConfiguration.getWidth();
  }
  
  public int getHeight()
  {
    return windowConfiguration.getHeight();
  }

  public void setTimeBased(boolean timeBased)
  {
    this.timeBased = timeBased;
    if(!timeBased && frameRate >= PrecisionTimer.getTicksPerSecond())
    {
      System.err.println("Timer Resolution is too low. Switching to time based, try decreasing framerate.");
      this.timeBased = true;
    }
  }

  public void setLoading(boolean loading)
  {
    this.loading = loading;
  }

  public void setUseFrameCap(boolean frameCap)
  {
    this.frameCap = frameCap;
  }
  
  public boolean isUsingFrameCap()
  {
    return frameCap;
  }
  
  public void setClearBG(boolean clear)
  {
    this.clearBG = clear;  
  }
  
  public boolean getClearBG()
  {
    return clearBG;  
  }

  public InputHandler getLastInputHandler()
  {
    return lastInputHandler;
  }

  public void setWindowConfiguration(WindowConfiguration windowConfiguration)
  {
    this.windowConfiguration = windowConfiguration;
  }
  
  public float getTickValue()
  {
	  return tickValue;
  }
}