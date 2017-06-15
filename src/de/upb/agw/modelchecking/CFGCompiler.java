package de.upb.agw.modelchecking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.upb.agw.jni.Abstractor;
import de.upb.agw.jni.Assignment;
import de.upb.agw.jni.CFGraph;
import de.upb.agw.jni.EnumeratorOfAssignment;
import de.upb.agw.jni.EnumeratorOfCFGraph;
import de.upb.agw.jni.EnumeratorOfExpression;
import de.upb.agw.jni.EnumeratorOfState;
import de.upb.agw.jni.EnumeratorOfTransition;
import de.upb.agw.jni.Expression;
import de.upb.agw.jni.Operation;
import de.upb.agw.jni.PartialExpression;
import de.upb.agw.jni.State;
import de.upb.agw.jni.Transition;
import de.upb.agw.util.ProgramCounterEncoder;
import edu.toronto.cs.algebra.AlgebraCatalog;
import edu.toronto.cs.algebra.AlgebraValue;
import edu.toronto.cs.algebra.IAlgebra;
import edu.toronto.cs.modelchecker.XKripkeStructure;
import edu.toronto.cs.mvset.MDDMvSetFactory;
import edu.toronto.cs.mvset.MvRelation;
import edu.toronto.cs.mvset.MvSet;
import edu.toronto.cs.mvset.MvSetFactory;
import edu.toronto.cs.mvset.MvSetMvRelation;
import edu.toronto.cs.smv.VariableTable;
import edu.toronto.cs.smv.VariableTable.StateVariable;

/**
 * Class takes cfgs + abstractor + spotlight and generates a MDD model of it.
 * @author Daniel Wonisch
 *
 */

public class CFGCompiler {
	private Abstractor abstractor;
	private EnumeratorOfCFGraph cfgs;
	private EnumeratorOfCFGraph abstractedCFGs;
	private EnumeratorOfExpression predicates;
	private IAlgebra algebra;
	private AlgebraValue maybeValue;
	private MvSetFactory mvSetFactory;
	private VariableTable vt;
	private boolean[] spotlightVec;
	private Vector<String> fairnessPreds;
	private Map<Integer,ProgramCounterEncoder> encoderMap;
	
	/**
	 * 
	 * @param cfgs Plain CFGs (directly from CParser; not abstracted)
	 * @param abs Abstractor which should be used to abstract the given CFGs
	 * @param spotlight spotlight[i] = true iff process i should be in the spotlight 
	 */
	
	public CFGCompiler(EnumeratorOfCFGraph cfgs, Abstractor abs, boolean[] spotlight) {
		this.abstractor = abs;
//		abs.getAbstractedCFGs().getNext().getStates().getNumberofElements()
//		abs.getPredicateInitValue()
		this.cfgs = cfgs;
		this.abstractedCFGs = abs.getAbstractedCFGs();
		this.predicates = abs.getPredicates();	
		this.algebra = AlgebraCatalog.getAlgebra("Kleene");
		this.maybeValue = algebra.getValue(1);	
		this.spotlightVec = spotlight;
		this.encoderMap = new HashMap<Integer, ProgramCounterEncoder>();
		buildVariableTable();
		this.mvSetFactory = MDDMvSetFactory.newMvSetFactory(algebra, vt.getNumDDVars());
		vt.setMvSetFactory(mvSetFactory);



	}
	
	/**
	 * 
	 * @return Vector of Strings. One String for each MvSet of each fairness constraint.
	 */
	
	public Vector<String> getFairnessMvSets() {
		return fairnessPreds;
	}
	
	/**
	 * 
	 * @return Spotlight vec as given in constructor
	 */
	
	public boolean[] getSpotlightVec() {
		return spotlightVec;
	}
	
	/**
	 * 
	 * @return AlgebraValue used for "mabye"="\bottom"
	 */
	
	public AlgebraValue getMaybeValue() {
		return maybeValue;
	}
	
	/**
	 * 
	 * @return VariableTable constructed
	 */
	
	public VariableTable getVariableTable() {
		return vt;
	}
	
	/**
	 * 
	 * @return MvSetFactory constructed
	 */
	
	public MvSetFactory getMvSetFactory() {
		return mvSetFactory;
	}
	
	/**
	 * 
	 * @return AbstractedCFGs as supplied by the Abstractor
	 */
	
	public EnumeratorOfCFGraph getAbstractedCFGs() {
		return abstractedCFGs;
	}
	
	/**
	 * 
	 * @return Algebra used (Kleene)
	 */
	
	public IAlgebra getAlgebra() {
		return algebra;
	}
	
	/**
	 * Deletes internal Enumerators including abstractedCFGs (!)
	 *
	 */
	
	public void delete() {
		abstractedCFGs.delete();
		predicates.delete();
	}
	
	
	private void buildVariableTable() {
		vt = new VariableTable();
		//first declare one variable for each predicate
		predicates.reset();
		while (predicates.hasNext()) {
			Expression pred = predicates.getNext();
			String exprString = pred.getExpressionCString();
			//System.out.println(exprString);
			vt.declareParentlessPropositional(exprString);
			pred.delete();
		}
		//next declare programcounter variables
		cfgs.reset();
		while(cfgs.hasNext()) {
			CFGraph currentCFG = cfgs.getNext();
			if(!spotlightVec[currentCFG.getProgramNumber()]) {
				continue;
			}
			ProgramCounterEncoder encoder = new ProgramCounterEncoder(currentCFG.getProgramNumber(), currentCFG.getStateCount());
			encoderMap.put(currentCFG.getProgramNumber(), encoder);
			for(int j = 0; j < encoder.getNumberOfTrits(); j++) {
				vt.declarePropositional(encoder.getPCString(j));
			}
		}	
		
		fairnessPreds = new Vector<String>();
		abstractedCFGs.reset();
		while(abstractedCFGs.hasNext()) {
			CFGraph currentCFG = abstractedCFGs.getNext();
			//System.out.println(currentCFG.__toString());
			String fairnessString = "exec_" + currentCFG.getProgramNumber() + ";";
			//System.out.println(fairnessString);
			fairnessPreds.add(fairnessString);
			vt.declarePropositional(fairnessString);
		}		
	}
	
	/**
	 * 
	 * @return The init values for each predicate. Note: the order is the same as used in the VariableTable.
	 */
	
	public AlgebraValue[] getInitValueArray() {
		AlgebraValue[] valueArray = new AlgebraValue[vt.getNumDDVars()];
		int counter = 0;
		predicates.reset();
		while (predicates.hasNext()) {
			Expression pred = predicates.getNext();
			int value = abstractor.getPredicateInitValue(pred);
			AlgebraValue algValue = value==0?algebra.bot():value==1?maybeValue:algebra.top();
			valueArray[counter++] = algValue;
			valueArray[counter++] = maybeValue; //variable.getNext() doesnt matter -> maybe
			pred.delete();
		}
		//next declare programcounter variables
		cfgs.reset();
		while(cfgs.hasNext()) {			
			CFGraph currentCFG = cfgs.getNext();
			if(!spotlightVec[currentCFG.getProgramNumber()])
				continue;
			ProgramCounterEncoder encoder = encoderMap.get(currentCFG.getProgramNumber());
			ArrayList<String[]> list = encoder.encodeAsPairs(currentCFG.getBeginStateId());
			for(String[] pair : list) {
				AlgebraValue algValue = algebra.getValue(pair[1]);
				valueArray[counter++] = algValue;		
				valueArray[counter++] = maybeValue; //variable.getNext() doesnt matter -> maybe
			}			
		}	
		//fairness variables
		for(int i = 0; i < fairnessPreds.size(); ++i) {
			valueArray[counter++] = algebra.bot();
			valueArray[counter++] = maybeValue; //variable.getNext() doesnt matter -> maybe
		}
		return valueArray;
	}
	
	/**
	 * 
	 * @return XKripkeStructure
	 */

	public XKripkeStructure compile() {
		return new XKripkeStructure(computeTransitionRelation(), computeInitState(), vt.getPrimeMap(), mvSetFactory.buildCube(vt.getPrimedVariablesIds()), mvSetFactory.buildCube(vt.getUnPrimedVariablesIds()), vt.getVarNames(), algebra, vt.getNumVars(), vt.getNumVars(), vt.getCtlReWriter(), vt.getStatePresenter());
	}	

	private MvRelation computeTransitionRelation() {
		return new MvSetMvRelation(convertToMvSet(abstractedCFGs), mvSetFactory.buildCube(vt.getUnPrimedVariablesIds()), mvSetFactory.buildCube(vt.getPrimedVariablesIds()), vt.getPrimeMap(), vt.getUnPrimeMap());		
	}
	
	private MvSet convertToMvSet(EnumeratorOfCFGraph enumerator) {		
		MvSet trans = mvSetFactory.createConstant(algebra.bot());
		enumerator.reset();
		while(enumerator.hasNext()) {
			CFGraph cfg = enumerator.getNext();
			trans = trans.or(convertToMvSet(cfg));			
		}
		//System.out.println(trans.toDaVinci());
		return trans;
	}
	
	private MvSet convertToMvSet(CFGraph graph) {
		// System.out.println("Converting CFGraph " + graph.__toString());
		MvSet set = mvSetFactory.createConstant(algebra.bot());
		EnumeratorOfState states = graph.getStates();
		
		states.reset();
		while(states.hasNext()) {
			State state = states.getNext();
			EnumeratorOfTransition transitions = state.getTransitions();
			transitions.reset();
			while(transitions.hasNext()) {
				Transition trans = transitions.getNext();
				set = set.or(convertToMvSet(trans, graph.getProgramNumber()));
				trans.delete();
			}
			transitions.delete();
		}
		
		states.delete();
		//System.out.println(set.toDaVinci());
		return set;

	}
	
	private MvSet convertToMvSet(Transition trans, int processIndex) {
		//System.out.println("Converting Transition (" + trans.getSource() + "," + trans.getDestination() + ")");
		MvSet set = mvSetFactory.createConstant(algebra.top());
		
		//Zunächst die eigentlich Transition (ohne den Operator) eincodieren
		//-> also z.b. nur wenn pc_index = 1 und Nachfolgecounter pc'_index = 2 ist, so 
		//ergibt die Funktion true.
		//Entspricht im Prizip delta(l,i,k)
		set = set.and(createLocationFunction(trans.getSource(), trans.getDestination(), processIndex));
		//System.out.println("Transition from " + trans.getSource() + " to " + trans.getDestination() + " for process " + processIndex + set.toDaVinci());
		set = set.and(convertToMvSet(trans.getOperation()));
		
		set = set.and(setFairnessVar(processIndex));
		
		return set;

	}
	
	private MvSet setFairnessVar(int processIndex) {
		MvSet set = mvSetFactory.createConstant(algebra.top());
		String fairnessVar = "exec_" + processIndex + ";";
		//true for processIndex
		set = set.and(vt.getByName(fairnessVar).getNext().eq(algebra.top().getName()));
		//false for any other processIndex
		for(String var : fairnessPreds) {
			if(!var.equals(fairnessVar)) {
				set = set.and(vt.getByName(var).getNext().eq(algebra.bot().getName()));
			}
		}
		return set;
	}

	private MvSet createLocationFunction(int source, int destination, int processIndex) {
		MvSet set = mvSetFactory.createConstant(algebra.top());
		
		if(processIndex != cfgs.getNumberofElements()) {
			//Note: processIndex == cfgs.getNumberOfElements() iff corresponding
			//		cfg is the "dummy" graph
			set = addProgramCounterToSet(processIndex, source, set, false);
			set = addProgramCounterToSet(processIndex, destination, set, true);			
		}
		
		cfgs.reset();
		while(cfgs.hasNext()) {
			CFGraph graph = cfgs.getNext();



			if(!spotlightVec[graph.getProgramNumber()])
				continue;
			if(graph.getProgramNumber() != processIndex) {
				//for every other programcounter -> just keep it as it was before
				ProgramCounterEncoder encoder = encoderMap.get(graph.getProgramNumber());
				for(int j = 0; j < encoder.getNumberOfTrits(); j++) {
					set = set.and(vt.getByName(encoder.getPCString(j)).eq(vt.getByName(encoder.getPCString(j)).getNext()));
				}				
			}			
		}	
		return set;

	}

	private MvSet convertToMvSet(Operation operation) {
		//System.out.println("Converting Operation " + operation.__toString());
		MvSet set = mvSetFactory.createConstant(algebra.top());
		
		//transform assume cond
		PartialExpression assumeExpr = operation.getCondPExpr(); 
		if(assumeExpr != null)
			set = set.and(convertToMvSet(assumeExpr));
		
		//transform parallel assignments
		EnumeratorOfAssignment assignments = operation.getAssignments();
		while(assignments.hasNext()) {
			Assignment ass = assignments.getNext();			
			set = set.and(convertToMvSet(ass));
		}
		assignments.delete();
		
		EnumeratorOfAssignment skippedAssignments = operation.getSkippedAssignments();
		while(skippedAssignments.hasNext()) {
			Assignment ass = skippedAssignments.getNext();
			set = set.and(convertToMvSet(ass));
		}
		skippedAssignments.delete();
		
		//System.out.println(set.toDaVinci());
		
		return set;

	}

	private MvSet convertToMvSet(Assignment ass) {
		//System.out.println("Converting Assignment " + ass.__toString());
		PartialExpression assExpr = ass.getAssignmentPExpression();
		return vt.getByParentlessName(ass.getIdent()).getNext().eq(convertToMvSet(assExpr));
	}

	private MvSet convertToMvSet(PartialExpression expr) {
		//System.out.println("Converting PartialExpression " + expr.__toString());
		MvSet set = mvSetFactory.createConstant(maybeValue);
		
		set = set.or(convertToMvSet(expr.getLeftExpression()).eq(mvSetFactory.createConstant(algebra.top())));
		set = set.and(convertToMvSet(expr.getRightExpression()).eq(mvSetFactory.createConstant(algebra.top())).not());
		
		return set;

	}

	private MvSet convertToMvSet(Expression expr) {
		//System.out.println("Converting Expression " + expr.getExpressionCString());
	//	System.out.println(expr.getExpressionCString());
		if(expr.isPredicate(predicates)) {
			//besteht nur aus einem Prädikat
			String exprString = expr.getExpressionCString();
			if(exprString.equals("true")) {
				return mvSetFactory.createConstant(algebra.top());
			}
			if(exprString.equals("false")) {
				 return mvSetFactory.createConstant(algebra.bot());
			}
			return ((StateVariable)vt.getByParentlessName(exprString)).getMvSet();
		}
		
		Expression leftExpression = expr.getLeftSubExpression();
		int op = expr.getFirstOp();
		if(op == Expression.NOT) {
			MvSet result = convertToMvSet(leftExpression).not();
			leftExpression.delete();
			return result;
		}
		//it is not "not" -> it is binary op
		Expression rightExpression = expr.getRightSubExpression();
		MvSet result;
		switch(op) {
		case Expression.AND:
			result = convertToMvSet(leftExpression).and(convertToMvSet(rightExpression));		
			break;
		case Expression.OR:
			result =  convertToMvSet(leftExpression).or(convertToMvSet(rightExpression));
			break;
		case Expression.EQ:
			result = convertToMvSet(leftExpression).eq(convertToMvSet(rightExpression));
			break;
		default:
			throw new IllegalArgumentException("Invalid Expressionoperator: " + op);
		}
		leftExpression.delete();
		rightExpression.delete();
		return result;
	}
	//(or (not (or (not (<= (select a i) 1)) (= i 0))) (not (or (not (<= x 1)) (not (= i 0)))))
	//(or (not (or (not (<= (select a i) 1)) (= i 0))) (not (or (not (<= x 1)) (not (= i 0)))))
	private MvSet computeInitState() {
		MvSet set = mvSetFactory.createConstant(algebra.top());
		//Zunächst pc festlegen
		cfgs.reset();
		while(cfgs.hasNext()) {
			CFGraph currentCFG = cfgs.getNext();
			if(!spotlightVec[currentCFG.getProgramNumber()])
				continue;
			//Startzustand muss true sein	...
			//-> currentCFG.getBeginStateId() tenär codieren 
			set = addProgramCounterToSet(currentCFG.getProgramNumber(), currentCFG.getBeginStateId(), set, false);				
		}
		//Nachdem Initialprogramcounter festlegt wurden, müssen nun die Initialwerte der Prädikate gesetzt werden
		predicates.reset();
		
		
		while(predicates.hasNext()) {
			Expression pred = predicates.getNext();
			
			
			// System.err.println(pred.getExpressionCString());
			String predicateString = pred.getExpressionCString();
			int value = abstractor.getPredicateInitValue(pred);
			AlgebraValue algebraValue;
			if(value == 0) {
				algebraValue = algebra.bot();
			}
			else if(value == 1) {
				algebraValue = maybeValue;
			}
			else {
				algebraValue = algebra.top();
			}
			set = set.and(vt.getByParentlessName(predicateString).eq(algebraValue.getName()));
			
			pred.delete();
		}
		
		//exec_i = false
		for(String var : fairnessPreds) {
			set = set.and(vt.getByName(var).eq(algebra.bot().getName()));
		}
		
		return set;
	}
	
	private MvSet addProgramCounterToSet(int processNumber, int counter, MvSet set, boolean next) {
		ProgramCounterEncoder encoder = encoderMap.get(processNumber);
		ArrayList<String[]> list = encoder.encodeAsPairs(counter);
		for(String[] pair : list) {
			if(next) {
				set = set.and(vt.getByName(pair[0]).getNext().eq(pair[1]));
			} else {
				set = set.and(vt.getByName(pair[0]).eq(pair[1]));
			}
		}
		return set;
	}

	public Map<Integer, ProgramCounterEncoder> getEncoderMap() {
		return encoderMap;
	}
	
	
}
