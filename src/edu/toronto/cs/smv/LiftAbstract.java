package edu.toronto.cs.smv;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;
import edu.toronto.cs.algebra.*;

import java.io.*;

public class LiftAbstract extends SMVModule
{
  LiftCabin lift;
  Button[] landingButtons;
  int floors;

  AlgebraDefine noCall;
  IntDefine landingCall;

  MvSet maybe;
  
  
  // -- floors -- number of floors the lift is servicing
  public LiftAbstract ()
  {
    super ("");
    floors = 2;
    super.setAlgebra (AlgebraCatalog.getAlgebra ("Kleene"));
  }
  
  public int getFloors ()
  {
    return floors;
  }
  public void setFloors (int v)
  {
    floors = v;
  }

  public void setAlgebra (IAlgebra algebra)
  {
    System.out.println ("Ignoring algebra setting");
  }
  

  public MvSet computeTrans ()
  {
    initLift ();
    initLandingButtons ();    

    MvSet result = lift.computeTrans ();
    for (int i = 0; i < landingButtons.length; i++)
      result = result.and (landingButtons [i].computeTrans ());

    return result;
  }

  public MvSet computeInit ()
  {
    MvSet result = lift.computeInit ();
    for (int i = 0; i < landingButtons.length; i++)
      result = result.and (landingButtons [i].computeInit ());
    return result;
  }


  MvSet computeNoCall ()
  {
    MvSet result = top;
    
    for (int i = 0; i < landingButtons.length; i++)
      result = result.and (landingButtons [i].getRequest ().eq (bot));
    
    noCall.setMvSet (result);
    return result;
  }
  
  MvSet computeLandingCall ()
  {

    MvSet result = computeCall (landingButtons);

    landingCall.setMvSet (result);
    return result;
  }

    
  public MvSet computeCall (Button[] buttons)
  {
    
    CaseStatement downStmt = new CaseStatement ();
    CaseStatement upStmt = new CaseStatement ();

    

    for (int i = floors - 1; i >= 1; i--)
      downStmt.addCase (buttons [i].getRequest ().eq (top).
			and (lift.getFloor ().gt (i)), 
			intConstant (i + 1));
    downStmt.
      addCase (buttons [0].getRequest ().eq (top), intConstant (1)).
      addDefault (intConstant (0));
    
    for (int i = 0; i < floors - 1; i++)
      upStmt.addCase (buttons [i].getRequest ().eq (top).
		  and (lift.getFloor ().lt (i + 2)), 
		      intConstant (i + 1));
    upStmt.
      addCase (buttons [floors - 1].getRequest ().eq (top), 
	       intConstant (floors)).
      addDefault (intConstant (0));
    
    MvSet result = new CaseStatement ().
      addCase (lift.getDir ().eq (bot), downStmt.compute ()).
      addCase (lift.getDir ().eq (top), upStmt.compute ()).compute ();

    return result;
    
  }
  

  void initLandingButtons ()
  {
    for (int i = 0; i < landingButtons.length; i++)
      landingButtons [i].setReset (lift.getFloor ().eq (i + 1).
				   and (lift.getDoor ().eq (top)));
    
  }
  
  void initLift ()
  {
    computeLandingCall ();
    computeNoCall ();
    
    lift.setLandingCall (landingCall);
    lift.setNoCall (noCall);
  }
  

  void seal ()
  {
    noCall = declareAlgebraDefine ("noCall");
    landingCall = declareIntDefine ("landingCall");

    landingButtons = new Button [getFloors ()];

    for (int i = 0; i < floors; i++)
      landingButtons [i] = new Button ("landingButton" + (i + 1));
    lift = new LiftCabin ("lift");


    super.seal ();
    maybe = getMvSetFactory ().createConstant (getAlgebra ().getValue ("M"));
  }

  public class Button
  {
    String name;
    
    MvSet reset;
    
    AlgebraVariable request;
    AlgebraVariable pressed;
    
    public Button (String _name)
    {
      name = _name;
      request = declareAlgebraVariable (name + "." + "request");
      pressed = declareAlgebraVariable (name + "." + "pressed");
    }
    
    public void setReset (MvSet v)
    {
      reset = v;
    }
    

    public MvSet computeTrans ()
    {
      MvSet result = computeRequest ().and (computePressed ());
      return result;
    }
    
    public MvSet computeInit ()
    {
      return request.eq (bot).and (pressed.eq (bot));
    }

    public AlgebraVariable getRequest ()
    {
      return request;
    }
    

    MvSet computePressed ()
    {
      
      /***
	  -- if there is an outstanding request or if there is a reset signal
	  -- ignore pressed completely
	  button := case
	              pressed : maybe;
		      reset   : maybe;
		      1       : {true, false}
      ***/
      MvSet pressedMvSet;

      pressedMvSet = new CaseStatement ().
	addCase (request.eq (top), pressed.eq (maybe)).
	addCase (reset.eq (top), pressed.eq (maybe)).
	addDefault (pressed.eq (top).or (pressed.eq (bot))).
	compute ();

      return invariant (pressedMvSet);
    }
    

    MvSet computeRequest ()
    {
      /***
	  next (request) := case
	                     reset  : false;
			     pressed : true;
			     1      : request;
      ***/			   
      return new CaseStatement ().
	addCase (reset.eq (top), next (request).eq (bot)).
	addCase (pressed.eq (top), next (request).eq (top)).
	addDefault (next (request).eq (request)).
	compute ();
    }
    
  }
  
  public class LiftCabin 
  {
    String name;

    IntDefine landingCall;
    AlgebraDefine noCall;
    
    AlgebraVariable door;
    AlgebraVariable dir;
    IntVariable floor;
    
    AlgebraDefine idle;
    IntDefine carCall;
    
    Button[] liftButtons;
    
    public LiftCabin (String _name)
    {
      name = _name;
      
      door = declareAlgebraVariable (name + "." + "door");
      dir = declareAlgebraVariable (name + "." + "dir");
      floor = declareIntVariable (name + "." + "floor", 1, floors);

      idle = declareAlgebraDefine (name + "." + "idle");
      carCall = declareIntDefine (name + "." + "carCall");  

      liftButtons = new Button [floors];
      for (int i = 0; i < floors; i++)
	liftButtons [i] = new Button (name + "." + "btn" + (i + 1));
      
    }

    public IntVariable getFloor ()
    {
      return floor;
    }
    public AlgebraVariable getDoor ()
    {
      return door;
    }
    public AlgebraVariable getDir ()
    {
      return dir;
    }
    
    
    void initLiftButtons ()
    {
      for (int i = 0; i < liftButtons.length; i++)
	liftButtons [i].setReset (floor.eq (i + 1).and (door.eq (top)));
    }
    

    public void setLandingCall (IntDefine v)
    {
      landingCall = v;
    }
    public void setNoCall (AlgebraDefine v)
    {
      noCall = v;
    }
    
    public MvSet computeInit ()
    {
      MvSet result = floor.eq (1).and (dir.eq (top)).and (door.eq (bot));
      for (int i = 0; i < liftButtons.length; i++)
	result = result.and (liftButtons [i].computeInit ());
      return result;
    }
    
    public MvSet computeTrans ()
    {
      computeIdle ();
      computeCarCall ();
      initLiftButtons ();
      
      MvSet result = computeDoor ().and (computeFloor ()).and (computeDir ());
      for (int i = 0; i < liftButtons.length; i++)
	result = result.and (liftButtons [i].computeTrans ());

      return result;
    }


    MvSet computeDoor ()
    {
      CaseStatement curDoor = new CaseStatement ().
	addCase (floor.eq (carCall), door.eq (top)).
	addCase (floor.eq (landingCall), door.eq (top)).
	addDefault (door.eq (bot));

      return invariant (curDoor.compute ());
    }
    
    
    MvSet computeDir ()
    {
      return new CaseStatement ().
	addCase (idle, next (dir).eq (dir)).
	addCase (floor.eq (floors), next(dir).eq (bot)).
	addCase (floor.eq (1), next (dir).eq (top)).
	addCase (carCall.eq (0).and (landingCall.eq (0)).and (dir.eq (bot)),
		 next (dir).eq (top)).
	addCase (carCall.eq (0).and (landingCall.eq (0).and (dir.eq (top))),
		 next (dir).eq (bot)).
	addDefault (next (dir).eq (dir)).compute ();
    }
    
    MvSet computeFloor ()
    {
      return new CaseStatement ().
	addCase (door.eq (top), next (floor).eq (floor)).
	addCase (carCall.eq (0).and (landingCall.eq (0)),
		 next (floor).eq (floor)).
	addCase (dir.eq (top).and (floor.lt (floors)),
		 next (floor).eq (floor.plus (1))).
	addCase (dir.eq (bot).and (floor.gt (1)),
		 next (floor).eq (floor.minus (1))).
	addDefault (next (floor).eq (floor)).compute ();
    }
    
    MvSet  computeIdle ()
    {
      MvSet idleMvSet = noCall.mvSet ();
      
      for (int i = 0; i < liftButtons.length; i++)
	idleMvSet = idleMvSet.and (liftButtons [i].getRequest ().eq (bot));
      
      idle.setMvSet (idleMvSet);
      return idle.mvSet ();
    }

    MvSet computeCarCall ()
    {
      MvSet result = computeCall (liftButtons);
      carCall.setMvSet (result);

      return result;
    }
    
    
  }

  public static void main (String[] args)
  {
    new Lift ();
  }
  
}

