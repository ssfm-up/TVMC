package edu.toronto.cs.util;

import java.sql.*;

/**
 ** This class implements a stop watch.
 **/
public class StopWatch
{
  long started;
  long finished;
  long timeElapsed;

  /**
   ** Creates a new StopWatch which starts ticking immediately.
   **/
  public StopWatch ()
    {
      reset ();
    }

  /**
   ** Resets the time counter and restarts the StopWatch.
   **/
  public void reset ()
    {
      started = System.currentTimeMillis ();
      finished = 0;
      timeElapsed = 0;
    }
  
  /**
   ** Stops the time counter recording the elapsed time. The counting
   ** can be resumed.
   **/
  public void pause ()
    {
      if (finished < started) // if the StopWatch is not already paused
	finished = System.currentTimeMillis ();
    }
  public void stop () 
    {
      pause ();
    }

  /**
   ** Resumes the time counting if the watch was stopped. Does nothing
   ** otherwise.
   **/
  public void resume ()
    {
      if (finished >= started) // if the StopWatch was paused
	{
	  timeElapsed += finished - started;
	  started = System.currentTimeMillis ();
	}
    }

  /**
   ** Gets the time elapsed in milliseconds.
   **/
  public long getTimeElapsed ()
    {
      if (finished < started) // the StopWatch is running
	return timeElapsed + System.currentTimeMillis () - started;
      else // the StopWatch is paused
	return timeElapsed + finished - started;
    }

  /**
   ** Gets the time elapsed in hh:mm:ss.sss format as a String.
   **/
  public String toString ()
    {
      long time = getTimeElapsed ();
      long h = time/3600000;
      long m = time/60000 - h*60;
      float s = ((float)time/1000) - m*60 - h*3600;

      String result = "";
      
      if (h > 0) result += h + "h ";
      if (m > 0) result += m + "m ";
      result += s + "s";
      return result;
    }
  
}


