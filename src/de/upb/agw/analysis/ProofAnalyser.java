package de.upb.agw.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.upb.agw.jni.CFGraph;
import de.upb.agw.jni.EnumeratorOfCFGraph;
import de.upb.agw.jni.Transition;
import de.upb.agw.main.Starter;
import de.upb.agw.util.ProgramCounterEncoder;
import edu.toronto.cs.algebra.AlgebraValue;
import edu.toronto.cs.algebra.IAlgebra;
import edu.toronto.cs.proof.LinearWitnessVisitor;
import edu.toronto.cs.proof.ProofStep;
import edu.toronto.cs.proof.LinearWitnessVisitor.WitnessStep;
import edu.toronto.cs.smv.VariableTable;
import edu.toronto.cs.smv.VariableTable.StateVariable;

/**
 * Analyse a given proof. Currently uses proof package and LinearWitnessVisitor to get a "linear" proof.
 * @author Daniel Wonisch
 *
 */
public class ProofAnalyser {
	/**
	 * Helper classes to represet a transition in the model (a CFG) which is for some reason interesting for the ProofAnalyser.
	 * @author Daniel Wonisch
	 *
	 */
	public static class ProofTransition {
		private Transition trans;
		private int startIndex;
		private int endIndex;
		private ProofTransition(Transition transition, int startIndex, int endIndex) {
			this.trans = transition;
			this.startIndex = startIndex;
			this.endIndex = endIndex;			
		}
		
		/**
		 * Delete ProofTransition (please do so!)
		 */
		
		public void delete() {
			if(trans != DUMMY_TRANSITION)
				trans.delete();
		}
		
		/**
		 * 
		 * @return Endstate of the transition
		 */
		
		public int getEndIndex() {
			return endIndex;
		}
		
		@SuppressWarnings("unused")
		private void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}
		
		/**
		 * 
		 * @return Startstate of the transition
		 */
		public int getStartIndex() {
			return startIndex;
		}
		
		@SuppressWarnings("unused")
		private void setStartIndex(int startIndex) {
			this.startIndex = startIndex;
		}
		
		/**
		 * Links the ProofTransition to the real Transition of a (real) CFG
		 * @return
		 */
		public Transition getTrans() {
			return trans;
		}
		
		@SuppressWarnings("unused")
		private void setTrans(Transition trans) {
			this.trans = trans;
		}	
		
		
	}
	
	
	private LinearWitnessVisitor witness;
	private List<LinearWitnessVisitor.WitnessStep> list;
	private ListIterator<WitnessStep> iterator;
	private VariableTable vt;
	private AlgebraValue maybeValue;
	private AlgebraValue trueValue;
	//private AlgebraValue falseValue;
	private AlgebraValue noValue;
	private EnumeratorOfCFGraph cfgs;
	public static Transition DUMMY_TRANSITION = new Transition(0L);
	private static ArrayList<String> predicates = new ArrayList<String>();
	private static int unknownPredicate = -1;
	
	
	/**
	 * 
	 * @param linearWitness LinearWitnessVisitor which should be used for analysis
	 * @param ps ProofStep which should be traversed by the LinearWitnessVisitor
	 * @param vt VariableTable of model
	 * @param algebra Algebra of model
	 * @param maybeValue "maybe"-value of the Algebra supplied
	 * @param cfgs Plain CFGs (as supplied by CParser). Needed to link ProofTransitions to real Transitions
	 */
	@SuppressWarnings("unchecked")
	public ProofAnalyser(LinearWitnessVisitor linearWitness, ProofStep ps, VariableTable vt, IAlgebra algebra, AlgebraValue maybeValue, EnumeratorOfCFGraph cfgs) {
		witness = linearWitness;
		list = witness.traverse(ps);
		iterator = list.listIterator();
		this.vt = vt;
		this.maybeValue = maybeValue;
		this.noValue = algebra.noValue();
		this.trueValue = algebra.top();
		this.cfgs = cfgs;	
		
		Starter.displayOutputln("\nPretty printing counter example...");
		long time = System.currentTimeMillis();		
		
		// remove the old predicates
		predicates.clear(); 
		printVariables();
		
		// remove the last predicate
		unknownPredicate = -1;
		printCounterExample();
		
		long timeUsed = (System.currentTimeMillis()-time);
		Starter.displayOutputln("");
		Starter.displayOutputln("Finished pretty printing counter example (" + timeUsed + "ms).");
	}	
	
	private void printVariables() {
		String[] variables = vt.getVarNames();
		Set<Integer> processes = new TreeSet<Integer>();
		
		
		for(String var : variables) {	
			if(var.charAt(var.length()-1) == '\'') {
				continue;
			}
			if(var.startsWith("pc_")) {				
				processes.add(ProgramCounterEncoder.parseProcessNumber(var));
			}
		}
						
		Starter.displayOutput("[");
		boolean first = true;
		for(int i : processes) {			
			if(first) {
				first = false;
			} else {
				Starter.displayOutput(", ");
			}
			
			String predicateName = "PC_" + i;
			Starter.displayOutput(predicateName);
			predicates.add(predicateName);
		}
		
		for(String var : variables) {	
			if(var.charAt(var.length()-1) == '\'') {
				continue;
			}
			if(!var.startsWith("pc_") && !var.startsWith("exec_")) {
				if(first) {
					first = false;
				} else {
					Starter.displayOutput(", ");
				}
				Starter.displayOutput(var);
				predicates.add(var);
			}
		}
		
		for(String var : variables) {
			if(var.charAt(var.length()-1) == '\'') {
				continue;
			}
			if(var.startsWith("exec_")) {
				if(first) {
					first = false;
				} else {
					Starter.displayOutput(", ");
				}
				Starter.displayOutput(var);
				predicates.add(var);
			}
		}
		
		Starter.displayOutput("]");
	}
	
	private void printCounterExample() {
		String[] variables = vt.getVarNames();
//		System.out.println("VARIABLES");
//		for(int i = 0; i < variables.length; i++) {
//			if(variables[i].charAt(variables[i].length()-1) == '\'') {
//				continue;
//			}
//			System.out.println(variables[i]);
//		}
		
		
		for(WitnessStep step : list) {
			AlgebraValue[] values = step.getSuccessor();
			Map<Integer, ArrayList<String>> countersMap = new TreeMap<Integer, ArrayList<String>>();
			Map<Integer, ArrayList<String>> counterValuesMap = new TreeMap<Integer, ArrayList<String>>();
			
			int count = 0;
			
			for(int i = 0; i < values.length; i++) {
				if(variables[i].charAt(variables[i].length()-1) == '\'') {
					continue;
				}
				if(variables[i].startsWith("pc_")) {
					int process = ProgramCounterEncoder.parseProcessNumber(variables[i]);					
					ArrayList<String> counters = countersMap.get(process);
					if(counters == null) {
						counters = new ArrayList<String>();
						countersMap.put(process, counters);
					}
					ArrayList<String> counterValues = counterValuesMap.get(process);
					if(counterValues == null) {
						counterValues = new ArrayList<String>();
						counterValuesMap.put(process, counterValues);
					}					
					counters.add(variables[i]);
					counterValues.add(values[i].getName());					
				}
			}			
			
			if (!step.getTransValue().equals(noValue)) {
				Starter.displayOutput(" --"+step.getTransValue()+"-> ");
			}			
			Starter.displayOutput("(");
			
			boolean first = true;
			for(int process : countersMap.keySet()) {
				if(first) {
					first = false;
				} else {
					Starter.displayOutput(",");
				}
				int stateId = ProgramCounterEncoder.decodeCountFromPC(countersMap.get(process), counterValuesMap.get(process));
				Starter.displayOutput("" + stateId);
				count++;
				
			}
			for(int i = 0; i < values.length; i++) {
				if(variables[i].charAt(variables[i].length()-1) == '\'') {
					continue;
				}
				if(!variables[i].startsWith("pc_") && !variables[i].startsWith("exec_")) {
					if(first) {
						first = false;
					} else {
						Starter.displayOutput(",");
					}
					Starter.displayOutput("" + values[i]);
					checkUnkownValue(count, values[i]);
					count++;
				}
			}
			for(int i = 0; i < values.length; i++) {
				if(variables[i].charAt(variables[i].length()-1) == '\'') {
					continue;
				}
				if(variables[i].startsWith("exec_")) {
					if(first) {
						first = false;
					} else {
						Starter.displayOutput(",");
					}
					Starter.displayOutput("" + values[i]);
					checkUnkownValue(count, values[i]);
					count++;
				}
			}
			Starter.displayOutput(")");
		}
		Starter.displayOutput("");
	}

	/**
	 * Check if a unknown predicate occurs
	 * @param index
	 * @param value
	 */
	private void checkUnkownValue(int index, AlgebraValue value){
		if(unknownPredicate != -1){
			return;
		}
		
		if(value.equals(maybeValue)){
			unknownPredicate = index;
		}
	}
	
	private void moveIteratorToIndex(int index) {
		if(index >= iterator.nextIndex()) {
			while(iterator.hasNext()) {
				if(iterator.nextIndex() == index) {
					iterator.next();
					return;
				} else {
					iterator.next();
				}
			}			
		}
		if(index < iterator.previousIndex()) {
			while(iterator.hasPrevious()) {
				if(iterator.previousIndex() == index) {
					return;
				} else {
					iterator.previous();
				}
			}	
		}
	}
	
	private WitnessStep getCurrentElement() {
		WitnessStep step = iterator.previous();
		iterator.next();
		return step;
	}
	
	private AlgebraValue getPredicateValue(String predicate) {
		WitnessStep step = getCurrentElement();
		AlgebraValue[] values = step.getSuccessor();
				
		int id = ((StateVariable)vt.getByParentlessName(predicate)).getId();
		
		return values[id];		
	}
	
	/*private Transition findTransition(WitnessStep step, WitnessStep nextStep) {
		AlgebraValue[] values = step.getSuccessor();
		AlgebraValue[] nextValues = nextStep.getSuccessor();
		String[] variableNames = vt.getVarNames();
		String previousCounter = null;
		String nextCounter = null;
		for(int i : vt.getUnPrimedVariablesIds()) {
			if(variableNames[i].startsWith("pc_")) {
				if(values[i].equals(noValue)) {
					continue;
				}
				//check which programcoutner switches from false to true
				if(values[i].equals(falseValue) && nextValues[i].equals(trueValue)) {
					nextCounter = variableNames[i];
				}	
				else if(values[i].equals(trueValue) && nextValues[i].equals(falseValue)) {
					previousCounter = variableNames[i];
				}
			}
		}
		if(previousCounter == null || nextCounter == null) {
			//dummy Transition
			return DUMMY_TRANSITION;
		}
		return findTransition(previousCounter, nextCounter);		
	}*/
	
	private Transition findTransition(WitnessStep step, WitnessStep nextStep) {
		AlgebraValue[] values = step.getSuccessor();
		AlgebraValue[] nextValues = nextStep.getSuccessor();
		String[] variableNames = vt.getVarNames();
		int programNumber = -1;		
		//Find programNumber
		for(int i : vt.getUnPrimedVariablesIds()) {
			if(variableNames[i].startsWith("exec_")) {
				if(nextValues[i].equals(trueValue)) {
					String process = variableNames[i].substring(variableNames[i].indexOf("_")+1, variableNames[i].indexOf(";"));
					programNumber = Integer.parseInt(process);
				}
			}
		}
		assert programNumber != -1 : "Invalid kripkestate: " + nextStep;
		ArrayList<String> counters = new ArrayList<String>();
		ArrayList<String> previousCounterValues = new ArrayList<String>();		
		ArrayList<String> nextCounterValues = new ArrayList<String>();		
		//find state
		for(int i : vt.getUnPrimedVariablesIds()) {
			if(variableNames[i].startsWith("pc_") && ProgramCounterEncoder.parseProcessNumber(variableNames[i]) == programNumber) {
				if(values[i].equals(noValue)) {
					continue;
				}	
				counters.add(variableNames[i]);
				previousCounterValues.add(values[i].getName());
				nextCounterValues.add(nextValues[i].getName());				
			}
		}		
		if(counters.size() == 0) {
			//dummy Transition
			return DUMMY_TRANSITION;
		}
		return findTransition(counters, previousCounterValues, nextCounterValues, programNumber);
	}
	
	private Transition findTransition(ArrayList<String> counters, ArrayList<String> previousCounterValues, ArrayList<String> nextCounterValues, int programNumber) {
		int previousStateId = ProgramCounterEncoder.decodeCountFromPC(counters, previousCounterValues);
		int nextStateId = ProgramCounterEncoder.decodeCountFromPC(counters, nextCounterValues);
		
		CFGraph graph;
		cfgs.reset();
		while(cfgs.hasNext()) {
			graph = cfgs.getNext();
			if(graph.getProgramNumber() == programNumber) {
				return graph.findTransition(previousStateId, nextStateId);
			}
		}
		throw new IllegalArgumentException("Invalid programcounter: " + previousCounterValues + ", " + nextCounterValues);		
	}
	
	/**
	 * 
	 * @return Last index of the proof
	 */
	public int getLastIndex() {
		return list.size()-1;
	}
	
	/**
	 * Checks if the given predicate has value "maybe" at the last proof state.
	 * @param predicate
	 * @param index
	 * @return
	 */

	public boolean hasPredicateMaybeValue(String predicate, int index) {
		moveIteratorToIndex(index);
		return getPredicateValue(predicate).equals(maybeValue);
	}
	
	/**
	 * Predicate must have value "maybe" at the given state index <code>(hasPredicateMaybeValue(predicate, index) == true)</code>
	 * @param predicate
	 * @param index Index where the search should start at
	 * @return Returns the Transition of the process, which caused the predicate change
	 * in the proof. Returns <code>null</code> if none is found. 
	 * Returns <code>DUMMY_TRANSITION</code> if the dummyprocess caused the predicate change.
	 */
	public ProofTransition backwardsFindMaybePredicate(String predicate, int index) {
		moveIteratorToIndex(index);
		
		WitnessStep nextElement = iterator.previous();
		
		while(getPredicateValue(predicate).equals(maybeValue)) {
			nextElement = iterator.previous();
			if(!iterator.hasPrevious()) {				
				return null;
			}
		}
		
		WitnessStep currentElement = getCurrentElement();		
		
		//predicate value changes from t/f to maybe in currentElement to nextElement
		Transition trans = findTransition(currentElement, nextElement);
		return new ProofTransition(trans, iterator.previousIndex(), iterator.nextIndex());
	}
	/**
	 * 
	 * @return Transition with maybeValue, or <code>null</code> if none is found
	 */
	public ProofTransition backwardsFindMaybeTransition() {
		moveIteratorToIndex(getLastIndex());
		WitnessStep step = null;
		while(iterator.hasPrevious()) {
			step = iterator.previous();
			if(step.getTransValue().equals(maybeValue)) {
				break;
			}
		}
		if(step == null) {
			return null;
		}		
		Transition trans = findTransition(getCurrentElement(), step);
		return new ProofTransition(trans, iterator.previousIndex(), iterator.nextIndex());
	}
	
	public static String getUnkwonPredicate(){
		if(unknownPredicate != -1){
			
			System.out.println("unknownPredicate: " + unknownPredicate);
			return predicates.get(unknownPredicate);
		}
		
		return null;
	}
}
