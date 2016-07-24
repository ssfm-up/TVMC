package edu.toronto.cs.proof2;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.algebra.*;

public class ProofStepListModel implements ListModel
{
  java.util.List listeners;
  StatePresenter statePresenter;
  String name;
  CTLNode[] ctlState = null;

  public ProofStepListModel (StatePresenter _statePresenter)
  {
    listeners = new ArrayList ();
    statePresenter = _statePresenter;
  }


  public void addListDataListener (ListDataListener l)
  {
    listeners.add (l);
  }

  public void removeListDataListener (ListDataListener l)
  {
    listeners.remove (l);
  }

  public Object getElementAt (int index)
  {
    if (index == 0) return name;
    else return ctlState [index - 1];
  }

  public int getSize ()
  {
    if (ctlState == null) return 0;

    return ctlState.length + 1;
  }


  public void setProofStep (ProofStep step)
  {
    if (!(step ==null)){
      
    MvSet state = step.getFormula ().getState ();
    
    name = step.getFormula ().getStateName ();

    ctlState = 
      statePresenter.toCTL ((AlgebraValue[])state.cubeIterator ().next ());

    // -- fire changed event
    fireContentsChangedEvent (new ListDataEvent (this, 
						 ListDataEvent.CONTENTS_CHANGED,
						 0, ctlState.length + 1));
    }
    
  }


  void fireContentsChangedEvent (ListDataEvent evt)
  {
    for (Iterator it = listeners.iterator (); it.hasNext ();)
      ((ListDataListener)it.next ()).contentsChanged (evt);
  }
}
