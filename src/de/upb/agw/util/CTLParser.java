package de.upb.agw.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.upb.agw.jni.CFGraph;
import de.upb.agw.jni.EnumeratorOfCFGraph;
import de.upb.agw.jni.EnumeratorOfString;
import de.upb.agw.jni.EnumeratorOfint;
import edu.toronto.cs.ctl.CTLAtomPropNode;
import edu.toronto.cs.ctl.CTLFactory;
import edu.toronto.cs.ctl.CTLNode;
import edu.toronto.cs.ctl.CTLUnaryNode;
import edu.toronto.cs.ctl.antlr.CTLNodeParser;
import edu.toronto.cs.ctl.antlr.CTLNodeParser.CTLNodeParserException;
import edu.toronto.cs.modelchecker.CTLReWriter;
import edu.toronto.cs.modelchecker.CTLUntilExpander;
import edu.toronto.cs.modelchecker.CTLWeakUntilExpander;
import edu.toronto.cs.modelchecker.ExistentialRewriter;
import edu.toronto.cs.modelchecker.SyntaxChecker;
import edu.toronto.cs.modelchecker.XKripkeStructure;
import edu.toronto.cs.smv.VariableTable;
import edu.toronto.cs.smv.VariableTable.StateVariable;
import edu.toronto.cs.smv.VariableTable.Variable;

/**
 * Helper class to parse user supplied CTL formulas
 * @author Daniel Wonisch
 *
 */
public class CTLParser {
	private List<CTLReWriter> ctlReWriters;

	private XKripkeStructure xkripke;
	private VariableTable vt;
	private EnumeratorOfCFGraph cfgs;
	
	private Map<String, String> predMap;
	private Map<String, String> userPredicateMap;
	
	private Map<Integer, ProgramCounterEncoder> encoderMap;
	
	private Vector<CTLNode> fairnessNodes;
	
	/**
	 * Note: Variables in the CTL formula are resolved to MvSets which depend on the VariableTable and model used.
	 * @param xkripke XKripkeStructure used for this CTL formula. 
	 * @param vt VariableTable
	 * @param userPredicateMap Maps user defined predicates (like "x > 0") to internal predicates as used in the variable table (like "(> x 0)")
	 * @param cfgs CFGs as supplied by CParser. Note: Needed for resolving labels like "END"
	 * @param fairness Fairness which should be used..
	 */
		
	public CTLParser(XKripkeStructure xkripke, VariableTable vt, Map<String, String> userPredicateMap, EnumeratorOfCFGraph cfgs, Vector<String> fairness, Map<Integer, ProgramCounterEncoder> encoderMap) {
		this.xkripke = xkripke;
		this.vt = vt;
		this.userPredicateMap = userPredicateMap;
		this.cfgs = cfgs;
		this.encoderMap = encoderMap;
		initCTLRewriters();
		initMap();
		constructFairnessNodes(fairness);
	}
	
	private void constructFairnessNodes(Vector<String> fairness) {
		fairnessNodes = new Vector<CTLNode>(fairness.size());
		for(String var : fairness) {
			CTLAtomPropNode setNode = CTLFactory.createCTLAtomPropNode(var); 
			setNode.setMvSet(vt.getByName(var).eq(xkripke.getAlgebra().top().getName()));	
			
			fairnessNodes.add(setNode);
		}
	}

	private void initMap() {
		predMap = new HashMap<String, String>();
		int counter = 0;
		for(Object var : vt.getVariables()) {
			predMap.put("__var"+counter, ((Variable)var).getName());		
			
			counter++;
		}
		
	}

	private void initCTLRewriters() {
		ctlReWriters = new LinkedList<CTLReWriter>();

		ctlReWriters.add(xkripke);

		// -- add rewriters that get rid of operators we never want to see 
		// -- later on
		ctlReWriters.add(new CTLWeakUntilExpander());

		ctlReWriters.add(new ExistentialRewriter());
	
		// -- remove EF, AF and ->
		ctlReWriters.add(new CTLUntilExpander(xkripke.getMvSetFactory().top()));
		ctlReWriters.add(new DeMorganRewriter());

	}
	
	/**
	 * 
	 * @param ctlStr User defined CTL formula as string
	 * @return Internal representation of this CTL formula
	 */

	public CTLNode prepareCTL(String ctlStr) {
		ctlStr = preCompile(ctlStr);
		CTLNode ctl = parseCTL(ctlStr);
		postCompile(ctl);
		return rewriteCTL(ctl);
	}

	/**
	 * Replaces all predicates with easy parseable keys. Use postcompile to restore
	 * the predicates semantics.
	 * @param ctlStr
	 * @return
	 */
	private String preCompile(String ctlStr) {
		ctlStr = replaceLabels(ctlStr);
		ctlStr = encodeProgramCounter(ctlStr);
		for(String pred : userPredicateMap.keySet()) {
			String internPred = userPredicateMap.get(pred);
			ctlStr = ctlStr.replaceAll("\\Q"+pred+"\\E", internPred);	
		}
		for(String key : predMap.keySet()) {
			String pred = predMap.get(key);					
			ctlStr = ctlStr.replaceAll("\\Q"+pred+"\\E", key);					
		}
		return ctlStr;
	}

	private void postCompile(CTLNode ctlNode) {
		if(ctlNode == null)
			return;
		if(ctlNode instanceof CTLAtomPropNode) {
			CTLAtomPropNode node = (CTLAtomPropNode)ctlNode;
			String variableName = predMap.get(node.getName());
			if(variableName == null)
				return;
			
			node.setMvSet(((StateVariable)vt.getByName(variableName)).getMvSet());
			node.setName(variableName);
		}
		else {
			postCompile(ctlNode.getLeft());
			postCompile(ctlNode.getRight());
		}		
	}

	private CTLNode parseCTL(String ctlStr) {
		CTLNode[] fairness = fairnessNodes.toArray(new CTLNode[0]);
		//CTLNode[] fairness = CTLAbstractNode.EMPTY_ARRAY;
		try {
			CTLNode ctl = CTLNodeParser.parse(ctlStr, fairness);
			return ctl;
		} catch (CTLNodeParserException ex) {
			throw new IllegalArgumentException("CTL Parsing Error:" + ex);
		}
	}

	private CTLNode rewriteCTL(CTLNode ctl) {
		CTLNode result = ctl;
		for (Iterator<CTLReWriter> it = ctlReWriters.iterator(); it.hasNext();) {
			CTLReWriter rewriter = it.next();
			//System.out.println ("Rewriting using: " + rewriter.getClass ());
			result = rewriter.rewrite(result);
			//System.out.println ("After rewriting fairness is: " + Arrays.asList (result.getFairness ()));
			assert (result != null) : rewriter.getClass() + ": returned null";
		}

		new SyntaxChecker().rewrite(result);
		//System.out.println ("After syntax checking: " + Arrays.asList (result.getFairness ()));

		return result;
	}
	
	private String encodeProgramCounter(String ctlStr) {
		StringBuffer buffer = new StringBuffer(ctlStr.length());
		int currentPos, startIndex, processIndex, stateIdIndex;
		
		currentPos = 0;
		
		while(true) {
			startIndex = ctlStr.indexOf("pc_", currentPos);
			if(startIndex == -1)
				break;
			processIndex = startIndex + "pc_".length();
			stateIdIndex = ctlStr.indexOf("=", processIndex) + "=".length();
			
			buffer.append(ctlStr.substring(currentPos, startIndex));
			buffer.append('(');
			
			int processNumber = Integer.parseInt(ctlStr.substring(processIndex, stateIdIndex-1));
			String stateIdString = getStateId(ctlStr, stateIdIndex);
			int stateId = Integer.parseInt(stateIdString); 
			
			ProgramCounterEncoder encoder = encoderMap.get(processNumber);
			ArrayList<String> list = encoder.encodeAsString(stateId);
			
			boolean first = true;
			for(String item : list) {
				if(first) {
					first = false;
				} else {
					buffer.append(" /\\ ");
				}
				buffer.append(item);
			}
			
			buffer.append(')');
			
			currentPos = stateIdIndex + stateIdString.length();
		}
		
		buffer.append(ctlStr.substring(currentPos));
		
		return buffer.toString();
	}
	
	private String getStateId(String ctlStr, int stateIdIndex) {
		int currentIndex = stateIdIndex;
		while(Character.isDigit(ctlStr.charAt(currentIndex))) {
			currentIndex++;
		}
		return ctlStr.substring(stateIdIndex, currentIndex);
	}

	private String replaceLabels(String expr) {
		cfgs.reset();
		while(cfgs.hasNext()) {
			CFGraph graph = cfgs.getNext();
			
			int index = graph.getProgramNumber();
			
			EnumeratorOfString labels = graph.getRegisteredLabels();
			while(labels.hasNext()) {
				String label = labels.getNext();
				String replaceString = "pc_"+index+"=";
			//	System.out.println("Replacing " + replaceString+label);
				StringBuilder builder = new StringBuilder("(");
				EnumeratorOfint states = graph.findStateByLabel(label);
				states.reset();
				boolean first = true;
				while(states.hasNext()) {
					int state = states.getNext();
					
					if(!first) {
						builder.append(" \\\\/ ");
					}
					else {
						first = false;
					}
					builder.append(replaceString);
					builder.append(state);					
				}
				states.delete();
				builder.append(")");
				String result = builder.toString();
			//	System.out.println("Replaced with: " + result);
				
				expr = expr.replaceAll(replaceString+label, result);
			}
			labels.delete();
		}
		//System.out.println("Returning " + expr);
		return expr;
	}
	
	public static void addDeepFairness(CTLNode node, CTLNode[] fairness) {
		if(node == null)
			return;
		if(node instanceof CTLUnaryNode) {
			CTLUnaryNode fairnode = (CTLUnaryNode)node;
			fairnode.setFairness(fairness);
		}
		addDeepFairness(node.getLeft(), fairness);
		addDeepFairness(node.getRight(), fairness);
	}

}
