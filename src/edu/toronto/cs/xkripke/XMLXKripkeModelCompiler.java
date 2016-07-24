package edu.toronto.cs.xkripke;

import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.xkripke.XKripke.*;
import edu.toronto.cs.algebra.*;
import edu.toronto.cs.mdd.*;
import edu.toronto.cs.mvset.*;
import edu.toronto.cs.util.*;

import edu.toronto.cs.smv.VariableTable;

import java.util.Iterator;
import java.util.Map;
import java.io.File;

public class XMLXKripkeModelCompiler implements ModelCompiler {
	File xmlFile;

	public XMLXKripkeModelCompiler() {
	}

	public void setXmlFile(File v) {
		xmlFile = v;
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public XKripkeStructure compile() {
		try {
			XKripke kripke = XKripkeFactory.parse(xmlFile.toString());
			MvSetFactory mvSetFactory = MDDMvSetFactory.newMvSetFactory(kripke.getAlgebra(), kripke.getSymbolTable().getNumDDVars());
			VariableTable vt = kripke.getSymbolTable();
			vt.setMvSetFactory(mvSetFactory);

			MvRelation trans = new MvSetMvRelation(computeTrans(mvSetFactory, kripke), mvSetFactory.buildCube(vt.getUnPrimedVariablesIds()), mvSetFactory.buildCube(vt.getPrimedVariablesIds()), vt.getPrimeMap(), vt.getUnPrimeMap());

			return new XKripkeStructure(trans, computeInit(mvSetFactory, kripke), vt.getPrimeMap(), mvSetFactory.buildCube(vt.getPrimedVariablesIds()), mvSetFactory.buildCube(vt.getUnPrimedVariablesIds()), vt.getVarNames(), kripke.getAlgebra(), vt.getNumVars(), vt.getNumVars(), vt.getCtlReWriter(), vt.getStatePresenter());

		} catch (Exception ex) {
			assert false : ex;
		}
		return null;
	}

	private MvSet computeTrans(MvSetFactory mvSetFactory, XKripke kripke) {
		// -- we build each transition individually and then 
		// -- join all of them together

		IAlgebra algebra = kripke.getAlgebra();
		// -- start with the identity on JOIN
		MvSet result = mvSetFactory.createConstant(algebra.bot());
		VariableTable vt = kripke.getSymbolTable();

		for (Iterator it = kripke.getTransitions().iterator(); it.hasNext();) {
			//Hier: Einfach die Funktion buildTransition benutzen
			//		-> Analog vorgehen nur dass die Transitionen dynamisch geadded werden
			result = result.or(buildTransition(mvSetFactory, algebra, (XKripkeTransition) it.next(), vt));
		}

		assert result.size() > 1 : "Transition relation is constant!";

		return result;

	}

	private static MvSet buildTransition(MvSetFactory mvSetFactory, IAlgebra algebra, XKripkeTransition trans, VariableTable vt) {
		int idx = 0; // -- variable indexing

		// -- this keeps the variable values on transitions
		MvSet transition = mvSetFactory.createConstant(algebra.getValue(trans.getValue()));
		

		for (Iterator it = trans.getSrc().getProps().values().iterator(); it.hasNext();) {
			XKripkeProp prop = (XKripkeProp) it.next();
			String name = prop.getName();
			String value = prop.getValue();
			transition = transition.and(vt.getByName(name).eq(value));
		}

		for (Iterator it = trans.getDst().getProps().values().iterator(); it.hasNext();) {
			XKripkeProp prop = (XKripkeProp) it.next();
			String name = prop.getName();
			String value = prop.getValue();
			transition = transition.and(vt.getByName(name).getNext().eq(value));
		}

		return transition;
	}

	private MvSet computeInit(MvSetFactory mvSetFactory, XKripke kripke) {
		XKripkeState[] init = kripke.getInitialStates();
		IAlgebra algebra = kripke.getAlgebra();

		MvSet result = mvSetFactory.createConstant(algebra.bot());
		for (int i = 0; i < init.length; i++)
			result = result.or(buildState(mvSetFactory, kripke, init[i]));

		System.out.println("Size of the initial state: " + result.size());
		return result;
	}

	private static MvSet buildState(MvSetFactory mvSetFactory, XKripke kripke, XKripkeState state) {
		IAlgebra algebra = kripke.getAlgebra();
		VariableTable vt = kripke.getSymbolTable();

		MvSet result = mvSetFactory.createConstant(algebra.top());

		for (Iterator it = state.getProps().values().iterator(); it.hasNext();) {
			XKripkeProp prop = (XKripkeProp) it.next();
			String name = prop.getName();
			String value = prop.getValue();
			result = result.and(vt.getByName(name).eq(value));
		}
		return result;
	}

}
