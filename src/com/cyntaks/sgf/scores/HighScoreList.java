package com.cyntaks.sgf.scores;

import com.cyntaks.sgf.core.SGFGame;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JOptionPane;

public class HighScoreList
{
  private String[][] values;
  private int maxScores;
  private int numFields;
  private boolean connectionFailure;
  
  public static final int NAME = 0;
  public static final int SCORE = 1;
  
  public HighScoreList(int maxScores)
  {
    values = new String[maxScores][2];
    
    for (int i = 0; i < values.length; i++) 
    {
      values[i][NAME] = "blank";
      values[i][SCORE] = "0";
    }
    
    this.maxScores = maxScores;
    this.numFields = 2;
  }
  
  public HighScoreList(int numFields, int maxScores)
  {
    this.maxScores = maxScores;
    values = new String[maxScores][numFields];
    this.numFields = numFields;
  }
  
  public void setRow(int row, String[] values)
  {
    for (int i = 0; i < values.length; i++) 
    {
      this.values[row][i] = values[i];
    }
  }
  
  public String[] getRow(int row)
  {
    return values[row];
  }
  
  public boolean addScore(String name, int score)
  {
    int row = -1;
    for (int i = 0; i < values.length; i++) 
    {
      int rowScore = Integer.parseInt(values[i][SCORE]);
      if(score > rowScore)
      {
        row = i;
        i = values.length;
      }
    }
    
    if(row != -1)
    {
      insertScore(row, name, score);
      return true;
    }
    
    return false;
  }
  
  public void shiftDownFrom(int row)
  {
    for (int i = values.length-1; i > row; i--) 
    {
      for (int j = 0; j < values[i].length; j++) 
      {
        values[i][j] = values[i-1][j]; 
      }
    }
  }
  
  private void insertScore(int row, String name, int score)
  {
    shiftDownFrom(row);
    values[row][NAME] = name;
    values[row][SCORE] = ""+score;
  }
  
  public boolean getScoresFromServer(URL getScoresScript)
  {
    /*try 
    {
      connectionFailure = false;
      HttpURLConnection con = (HttpURLConnection)getScoresScript.openConnection();
      
      con.setDoInput(true);
      con.setUseCaches(false);
      
      InputStream in = con.getInputStream();
      byte[] b = new byte[in.available()];
      in.read(b);
      String str = new String(b);
      boolean error = isError(str);
      
      if(!error)
      {
        setToData(str);
      }
      
      return !error;
    } catch (IOException ex) 
    {
      ex.printStackTrace();
      connectionFailure = true;
      JOptionPane.showMessageDialog(SGFGame.getInstance().getDrawingSurface(), "Could not connect to high score server");
      return false;
    }*///commented due to security restrictions
    return false;
  }
  
  private void setToData(String str)
  {
    while(str.charAt(str.length()-1) == ' ')
    {
        str = str.substring(0, str.length()-1);
    }
      
    String[] lines = str.split("\\*");
      
    for (int row = 0; row < lines.length; row++) 
    {
      String[] fields = lines[row].split("\\|");
      for (int field = 0; field < fields.length; field++) 
      {
        values[row][field] = fields[field];
      }
    }
  }
  
  private String getDataString()
  {
    StringBuffer buff = new StringBuffer();
    for (int row = 0; row < values.length; row++) 
    {
      for (int field = 0; field < values[row].length; field++) 
      {
        buff.append(values[row][field]);
        
        if(field != values[row].length-1)
          buff.append("|");
      }
      
      if(row != values.length-1)
        buff.append("*");
    }
    
    return buff.toString();
  }
  
  private boolean isError(String message)
  {
    return (message.indexOf("Error") != -1 || message.indexOf("Warning") != -1) ? true : false;
  }
  
  public boolean saveScoresToServer(URL writeScoresScript)
  {
    String newScores = getDataString();
    try 
    {
      HttpURLConnection con = (HttpURLConnection)writeScoresScript.openConnection();
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false);
      
      newScores = URLEncoder.encode(newScores, "UTF-8");
      
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write("data="+newScores);
      out.close();
      
      InputStream in = con.getInputStream();
      byte[] b = new byte[in.available()];
      in.read(b);
      String str = new String(b);
      
      return !isError(str);
    } catch (Exception ex) 
    {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean connectionFailed()
  {
    return connectionFailure;
  }
}