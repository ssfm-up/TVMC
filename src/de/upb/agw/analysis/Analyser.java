package de.upb.agw.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import de.upb.agw.analysis.ProofAnalyser.ProofTransition;
import de.upb.agw.jni.Assignment;
import de.upb.agw.jni.CFGraph;
import de.upb.agw.jni.EnumeratorOfAssignment;
import de.upb.agw.jni.EnumeratorOfCFGraph;
import de.upb.agw.jni.EnumeratorOfExpression;
import de.upb.agw.jni.Expression;
import de.upb.agw.jni.Operation;
import edu.toronto.cs.ctl.AbstractCTLVisitor;
import edu.toronto.cs.ctl.CTLAtomPropNode;
import edu.toronto.cs.ctl.CTLBinaryNode;
import edu.toronto.cs.ctl.CTLNode;
import edu.toronto.cs.ctl.CTLUnaryNode;

/**
 * Analyses a given CTL formula using a given ProofAnalyser. Decides which predicates and/or programs should be added.
 * @author Daniel Wonisch
 *
 */
public class Analyser extends AbstractCTLVisitor {
	private EnumeratorOfExpression predicates;
	private Vector<Expression> newPredicates;
	private ProofAnalyser analyser;
	private CTLNode ctl;
	private Vector<Integer> newProgramsIds;
	private boolean[] spotlightVec;
	EnumeratorOfCFGraph cfgs;

	/**
	 * 
	 * @param enumerator Predicates known yet
	 * @param ctl CTL formula used
	 * @param analyser ProofAnalyser
	 * @param spotlightVec Spotlightprograms used yet
	 * @param cfgs Plain CFGs (as supplied by CParser)
	 */
	public Analyser(EnumeratorOfExpression enumerator, CTLNode ctl, ProofAnalyser analyser, boolean[] spotlightVec, EnumeratorOfCFGraph cfgs) {
		this.predicates = enumerator;
		this.analyser = analyser;
		this.ctl = ctl;
		this.spotlightVec = spotlightVec;
		this.cfgs = cfgs;
	}
	
	/**
	 * 
	 * @return Vector indicating which new programs should be added to the spotlight.
	 */

	public Vector<Integer> getNewProgramsIds() {
		return newProgramsIds;
	}
	
	/**
	 * 
	 * @return Vector indicating which new predicates should be added to the model.
	 */

	public Vector<Expression> getNewPredicates() {
		return newPredicates;
	}
	
	/**
	 * Do the run..
	 *
	 */

	public void analyse() {
		newPredicates = new Vector<Expression>();
		newProgramsIds = new Vector<Integer>();
		visit(ctl, null);
		if (newPredicates.isEmpty() && newProgramsIds.isEmpty()) {
			analyseAssumeTransition(analyser.backwardsFindMaybeTransition());
		}
		if (newPredicates.isEmpty() && newProgramsIds.isEmpty()) {
			throw new RuntimeException("No new predicates/spotlight found by analyser. Can not check program.");
		}
	}

	@Override
	public Object visitUnaryNode(CTLUnaryNode n, Object o) {
		visit(n.getRight(), o);
		return null;
	}

	@Override
	public Object visitBinaryNode(CTLBinaryNode n, Object o) {
		visit(n.getLeft(), o);
		visit(n.getRight(), o);
		return null;
	}

	@Override
	public Object visitAtomPropNode(CTLAtomPropNode node, Object stateinfo) {
		String predicate = node.getName();
		if (predicate.startsWith("pc_")) {
			//its a programcounter -> skip
			return stateinfo;
		}
		if (analyser.hasPredicateMaybeValue(predicate, analyser.getLastIndex())) {
			analysePredicate(predicate);
		}
		return stateinfo;
	}

	private void analyseAssumeTransition(ProofTransition trans) {
		Operation op = trans.getTrans().getOperation();
		Expression assumeCondition = op.getCondExpr();
		
		ArrayList<Expression> subExpressions = new ArrayList<Expression>();
		splitComplexePredicates(new Expression(assumeCondition.getExpressionCString()), subExpressions);
		
		predicates.reset();
		
		System.out.println("-----------------------------------------------");
		System.out.println("alle prädikate:");
		while(predicates.hasNext()){
			System.out.println(predicates.getNext().getExpressionCString());			
		}
		
		System.out.println("-----------------------------------------------");
		System.out.println("einzufügende prädikate:");
		for( Expression expr : subExpressions ){
			System.out.println(expr.getExpressionCString());
		}
		
		subExpressions = removeEqualExpressions(subExpressions);
		removeDuplicateExpressions(subExpressions);
		System.out.println("-----------------------------------------------");
		
		System.out.println("eingefügte prädikate:");
		
		for( Expression expr : subExpressions ){
			System.out.println(expr.getExpressionCString());
		}
		System.out.println("-----------------------------------------------");
		
		if(subExpressions.isEmpty()) { // subExpressions.empty()

			// TODO: Problem! assumeCondition maybe splited?
			//ArrayList<Expression> copy = new ArrayList<Expression>();
			//splitComplexePredicates(assumeCondition, copy);
			
			String unknownPredicate = ProofAnalyser.getUnkwonPredicate();
			System.err.println("kein neues Prädikat gefunden - weiter untersucht wird " + unknownPredicate + " => " + findPredicate(unknownPredicate) );
			
			unknownPredicate = findPredicate(unknownPredicate);
			
			analysePredicate(unknownPredicate, trans.getStartIndex());
			
		} else {
			//System.out.println("AssumeComdition is not a predicate");
			//-> add a copy of assumeCondition as new predicate
			//newPredicates.addAll(subPredicates);
			System.out.println("ADD: " + assumeCondition.getExpressionCString());
			//splitAddToNewPredicates(new Expression(assumeCondition.getExpressionCString()));
			//newPredicates.add(new Expression(assumeCondition.getExpressionCString()));
			
			for( Expression expr : subExpressions ){
				newPredicates.add(expr);
			}
		}
		trans.delete();
	}

	/**
	 * Search the unknownPredicate in the set of predicates. This is important, because the unknownPredicate did not have braces, but the predicates have brace.
	 * Example: Input-> > select a i 1 
	 * 			Output: (> (select a i) 1)
	 * @param unknownPredicate
	 * @return
	 */
	private String findPredicate(String unknownPredicate) {
		predicates.reset();
		
		while( predicates.hasNext() ){
			String pred = predicates.getNext().getExpressionCString();
			int j = 0;
			boolean test = true;
			
			for(int i = 0 ; i < pred.length() ; i++ ){
				if( pred.charAt(i) == '(' || pred.charAt(i) == ')'){
					continue;
				}
				
				if( pred.charAt(i) != unknownPredicate.charAt(j)){
					test = false;
					break;
				}
				j++;
			}
			
			if( test ){
				return pred;
			}			
		}		
		
		return null;
	}

	private ArrayList<Expression> removeEqualExpressions(ArrayList<Expression> subExpressions) {
		
		ArrayList<Expression> tmpExpressions = new ArrayList<Expression>();
		
		for( Expression expr : subExpressions ){
			
				boolean mustInsert = true;
				
				System.out.println("Check: " + expr.getExpressionCString());
				
				System.out.println("FirstOp: " + expr.getFirstOp());
				System.out.println("Left: " + expr.getLeftSubExpression().getExpressionCString());
				System.out.println("Right: " + expr.getRightSubExpression().getExpressionCString());
				
				// leftSide = rightSide
				if(expr.getLeftSubExpression().getExpressionCString().equals(expr.getRightSubExpression().getExpressionCString())){
					mustInsert = false;
				}
				
				// is insert
				if(mustInsert && isPredicateKnown(expr.getExpressionCString(), tmpExpressions)){
					mustInsert = false;
				}
				
				// "<=" => not ">"
				if(mustInsert && expr.getFirstOp() == Expression.LEQ){
					String checkExpr = "(> " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr);
					
					if(isPredicateKnown(checkExpr ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				// ">=" => not "<"
				if(mustInsert && expr.getFirstOp() == Expression.GEQ){
					String checkExpr = "(< " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr);
					
					if(isPredicateKnown(checkExpr ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				// ">" => not "<="
				if(mustInsert && expr.getFirstOp() == Expression.GREATER){
					String checkExpr = "(<= " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr);
					
					if(isPredicateKnown(checkExpr ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				// "<" => not ">="
				if(mustInsert && expr.getFirstOp() == Expression.LESS){
					String checkExpr = "(>= " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr);
					
					if(isPredicateKnown(checkExpr ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				// "<" => not "=" AND not ">"
				if(mustInsert && expr.getFirstOp() == Expression.LESS){
					String checkExpr1 = "(> " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					String checkExpr2 = "(= " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr1 );
					System.out.println("Check if exist: " + checkExpr2 );
					
					if(isPredicateKnown(checkExpr1 ,tmpExpressions) && isPredicateKnown(checkExpr2 ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				// "=" => not "<" AND not ">"
				if(mustInsert && expr.getFirstOp() == Expression.EQ){
					String checkExpr1 = "(> " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					String checkExpr2 = "(< " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr1 );
					System.out.println("Check if exist: " + checkExpr2 );
					
					if(isPredicateKnown(checkExpr1 ,tmpExpressions) && isPredicateKnown(checkExpr2 ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				// ">" => not "<" AND not "="
				if(mustInsert && expr.getFirstOp() == Expression.GREATER){
					String checkExpr1 = "(= " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					String checkExpr2 = "(< " + expr.getLeftSubExpression().getExpressionCString() + " " + expr.getRightSubExpression().getExpressionCString() + ")";
					System.out.println("Check if exist: " + checkExpr1 );
					System.out.println("Check if exist: " + checkExpr2 );
					
					if(isPredicateKnown(checkExpr1 ,tmpExpressions) && isPredicateKnown(checkExpr2 ,tmpExpressions)){
						mustInsert = false;
					}
				}
				
				System.out.println("");
				
				if(mustInsert){
					tmpExpressions.add(expr);
				}
				else {
					expr.delete();
				}
				
			}
			return tmpExpressions;
	}
	
	/**
	 * Remove double expression from the given vector. 
	 * This method use the overloaded method with an ArrayList as parameter.
	 * @param subExpressions
	 */
	private void removeDuplicateExpressions(Vector<Expression> subExpressions){
		ArrayList<Expression> tmp = new ArrayList<Expression>();
		
		// copy all expressions
		for( Expression expr : subExpressions ){
			tmp.add(expr);
		}
		
		removeDuplicateExpressions(tmp);
		
		// remove all old expresions
		subExpressions.removeAllElements();
		
		// copy all new expression
		for( Expression expr : tmp ){
			subExpressions.add(expr);
		}		
	}
	
	/**
	 * Remove duplicate expressions from the given Arraylist.
	 * @param subExpressions
	 */
	private void removeDuplicateExpressions(ArrayList<Expression> subExpressions){
		ArrayList<Expression> doubleList = new ArrayList<Expression>();
		
		for( int i = 0 ; i < subExpressions.size() ; i++ ){
			Expression expr1 = subExpressions.get(i);
			
			boolean found = false;
			
			for( Expression expr2 : doubleList ){
				
				if(expr1.getExpressionCString().equals(expr2.getExpressionCString())){
					subExpressions.remove(i);
					i--;
					found = true;
					break;
				}
			}
			
			if(found == false){
				doubleList.add(expr1);
			}
			else {
				expr1.delete();
			}
		}
	}
	
	/**
	 * Split a complex Expression into some simple expressions.
	 * The complex expression will split at the following operators: 'not', 'and' and 'or'
	 * The simple expressions will stored in the given ArrayList subExpressions. 
	 * @param assumeCondition
	 * @param subExpressions
	 */
	private void splitComplexePredicates(Expression assumeCondition, ArrayList<Expression> subExpressions) {

		int op = assumeCondition.getFirstOp();
		if(op == Expression.AND || op == Expression.OR) {
			splitComplexePredicates(assumeCondition.getLeftSubExpression(), subExpressions);
			splitComplexePredicates(assumeCondition.getRightSubExpression(), subExpressions);	
			assumeCondition.delete();
		} 
		else if(op == Expression.NOT) {
			splitComplexePredicates(assumeCondition.getLeftSubExpression(), subExpressions);
			assumeCondition.delete();
		}
		else {
			subExpressions.add(assumeCondition);
		}
	}

	private void analysePredicate(String predicate) {
		analysePredicate(predicate, analyser.getLastIndex());
	}

	private void analysePredicate(String predicate, int startIndex) {
		Expression exprPredicate = new Expression(predicate);
		analysePredicate(exprPredicate, startIndex);
		exprPredicate.delete();
	}

	private void analysePredicate(Expression predicate, int startIndex) {
		ProofTransition proofTrans = analyser.backwardsFindMaybePredicate(predicate.getExpressionCString(), startIndex);
		if(proofTrans == null) {
			//no maybepredicate found -> this predicate can not be the reason for "maybe" result
			return;
		}
		if (proofTrans.getTrans() == ProofAnalyser.DUMMY_TRANSITION) {
			//2.(c) => output one process writing on variables occuring in p
			cfgs.reset();
			while (cfgs.hasNext()) {
				CFGraph graph = cfgs.getNext();
				if (!spotlightVec[graph.getProgramNumber()]) {
					//check if this program may have caused the transition
					if (graph.isPredicateModifiedByGraph(predicate)) {
						newProgramsIds.add(graph.getProgramNumber());
						return;
					}
				}
			}
			assert false : "Dummy_Transition found in proof, but no process seems to be responsible for it?!";
		}

		EnumeratorOfAssignment assignments = proofTrans.getTrans().getOperation().getAssignments();
		assert assignments.getNumberofElements() == 1 : "Multiple Assignments not allowed (yet).";
		Assignment assignment = assignments.getNext();
		Expression wpExpr = assignment.computeWeakestPrecondition(predicate);
		
		ArrayList<Expression> subExpressions = new ArrayList<Expression>();
		splitComplexePredicates(new Expression(wpExpr.getExpressionCString()), subExpressions);
		
		subExpressions = removeEqualExpressions(subExpressions);
		removeDuplicateExpressions(subExpressions);
		removeMeaninglessExpressions(subExpressions);
		
		if (subExpressions.isEmpty()) { // wpExpr.isPredicate(predicates)
			//2.(a) => further bachtrack with step 2
			// TODO: Problem! wpExpr maybe splited?
			analysePredicate(wpExpr, proofTrans.getStartIndex());
			wpExpr.delete();
		} else {
			//2.(b) => use wpExpr as new predicate
			System.out.println("ADD: " + wpExpr.getExpressionCString());
//			splitAddToNewPredicates(wpExpr);
//			removeDuplicateExpressions(newPredicates);
			for( Expression expr : subExpressions ){
				newPredicates.add(expr);
			}
			//newPredicates.add(wpExpr);
			wpExpr.delete();			
		}

		proofTrans.delete();
		assignments.delete();
	}
	

	private void removeMeaninglessExpressions(ArrayList<Expression> subExpressions) {
		// TODO Auto-generated method stub
		
	}

	private void splitAddToNewPredicates(Expression expression) {
		
		int op = expression.getFirstOp();
		if(op == Expression.AND || op == Expression.OR) {
			splitAddToNewPredicates(expression.getLeftSubExpression());
			splitAddToNewPredicates(expression.getRightSubExpression());			
		} 
		else if(op == Expression.NOT) {
			splitAddToNewPredicates(expression.getLeftSubExpression());
		}
		else {
			addPredicateIfUnique(expression);
		}
	}

	private void addPredicateIfUnique(Expression expression) {
		
		System.out.println( "prädicate bereits eingefügt? " + expression.isPredicate(predicates));
			
		if( !expression.isPredicate(predicates)){
			newPredicates.add(expression);
			System.out.println("INAERT: " + expression.getExpressionCString());
			
		}
		
		System.out.println("ADD: " + expression.getExpressionCString());
	}
	
	private boolean isPredicateKnown( String ExpressionCString , ArrayList<Expression> subExpressions){
		predicates.reset();
		
		// expressionCString is already a predicate
		while(predicates.hasNext()){
			Expression expr = predicates.getNext();
			
			if(expr.getExpressionCString().equals(ExpressionCString)){
				return true;
			}
		}
		
		// expressionCString previously inserted
		for( Expression expr : subExpressions ){
			if( expr.getExpressionCString().equals(ExpressionCString) ){
				return true;
			}
		}
		return false;
	}
}