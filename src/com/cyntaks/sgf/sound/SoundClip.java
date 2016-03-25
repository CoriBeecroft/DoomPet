package com.cyntaks.sgf.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundClip
{
	private int size;
	
	private AudioFormat format;
	private DataLine.Info info;
	private byte[] audio;	
  private boolean looping;
  
  private static boolean soundEnabled = true;

	public SoundClip(int size, AudioFormat format, DataLine.Info info, byte[] audio)
	{
		this.size = size;
		this.format = format;
		this.info = info;
		this.audio = audio;
	}
  
  public void play()
  {
    if(soundEnabled)
    {
      new Thread(){
        public void run()
        {
          int bufferSize = format.getFrameSize() * (int)(format.getSampleRate()/10);
          byte[] buffer = new byte[bufferSize];
          
          SourceDataLine line = null;
          try 
          {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format, bufferSize);
          } catch (LineUnavailableException ex) 
          {
            ex.printStackTrace();
            return;
          }
          
          line.start();
          
          int numBytesRead = 0;
          ByteArrayInputStream bytesIn = new ByteArrayInputStream(audio);
          while(numBytesRead != -1)
          {
            numBytesRead = bytesIn.read(buffer, 0, bufferSize);
            if(numBytesRead != -1)
              line.write(buffer, 0, numBytesRead);
          }
          
          line.drain();
          line.close();
        }
      }.start();
    }
  }
  
  public void loop()
  {
    if(soundEnabled)
    {
      new Thread(){
        public void run()
        {
          looping = true;
          int bufferSize = format.getFrameSize() * (int)(format.getSampleRate()/10);
          byte[] buffer = new byte[bufferSize];
          
          SourceDataLine line = null;
          try 
          {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format, bufferSize);
          } catch (LineUnavailableException ex) 
          {
            ex.printStackTrace();
            return;
          }
          
          line.start();
          
          try 
          {
            int numBytesRead = 0;
            LoopingByteArrayInputStream bytesIn = new LoopingByteArrayInputStream(audio);
            while(numBytesRead != -1)
            {
              if(!looping)
                bytesIn.close();
              numBytesRead = bytesIn.read(buffer, 0, bufferSize);
              if(numBytesRead != -1)
                line.write(buffer, 0, numBytesRead);
            }
          } catch (IOException ex) 
          {
            ex.printStackTrace();
          } 
          finally
          {
            line.drain();
            line.close();
          }
        }
      }.start();
    }
  }
  
  public void stop()
  {
    looping = false;
  }
  
  public static void setSoundEnabled(boolean isSoundEnabled)
  {
    soundEnabled = isSoundEnabled;
  }
  
  public static boolean isSoundEnabled()
  {
    return soundEnabled;
  }
  
  public String toString()
  {
    return "SoundClip: size: " + size + ", looping: " + looping;
  }
}