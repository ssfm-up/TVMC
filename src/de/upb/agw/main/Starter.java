package de.upb.agw.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextArea;

import de.upb.agw.analysis.Analyser;
import de.upb.agw.analysis.ProofAnalyser;
import de.upb.agw.gui.project.SpotlightProject;
import de.upb.agw.jni.Abstractor;
import de.upb.agw.jni.BooleanProgram;
import de.upb.agw.jni.CFGraph;
import de.upb.agw.jni.CParser;
import de.upb.agw.jni.EnumeratorOfBooleanProgram;
import de.upb.agw.jni.EnumeratorOfCFGraph;
import de.upb.agw.jni.Expression;
import de.upb.agw.modelchecking.CFGCompiler;
import de.upb.agw.util.CTLParser;
import de.upb.agw.util.ProgramCounterEncoder;
import edu.toronto.cs.algebra.AlgebraValue;
import edu.toronto.cs.ctl.CTLNode;
import edu.toronto.cs.modelchecker.MvSetModelChecker;
import edu.toronto.cs.modelchecker.XKripkeStructure;
import edu.toronto.cs.mvset.MvSet;
import edu.toronto.cs.proof.LinearWitnessVisitor;
import edu.toronto.cs.proof.ProofStep;
import edu.toronto.cs.proof.ProofStepFactory;
import edu.toronto.cs.proof.SimpleNameGenerator;
import za.ac.up.cs.ThreeValuedModelChecker;

/**
 * Start the fun!
 * @author Daniel Wonisch
 *
 */

public class Starter {

	private static Map<String, String> map = new HashMap<String, String>();
	private static CFGCompiler compiler;
	private static EnumeratorOfCFGraph cfgs;
	private static MvSetModelChecker checker;
	private static CTLParser ctlParser;
	private static AlgebraValue[] initValueArray;
	private static CParser cParser;
	private static Abstractor abstractor;
	private static String ctlString;
	private static boolean[] spotlightVec;
	private static long startTime;
	private static long abstractTime;
	private static long convertTime;
	private static long mcTime;
	private static long anaTime;
	private static boolean generateBPs = false;
	
	public static JTextArea console;
		
	private static void initialise(String filename) throws IOException {
		System.out.println("initialise");
		System.out.println("Initialising...");
		
		startTime = System.currentTimeMillis();
		
		System.loadLibrary("Spotlight");
		
		System.out.println("Parsing c file...");
		long time = System.currentTimeMillis();
		cParser = new CParser("input/" + filename + ".c");
		long timeUsed = (System.currentTimeMillis()-time);
		System.out.println("Finished parsing c file (" + timeUsed + "ms).");
		
		abstractor = new Abstractor();
	
		System.out.println("Optimising CFGs...");
		time = System.currentTimeMillis();
		cfgs = cParser.getCFGEnumerator();	
		//displayOutputln(cParser.getInitCFG().__toString());
		int counter = 0;
		while(cfgs.hasNext()) {
			CFGraph graph = cfgs.getNext();
			graph.reduce();
			
			//displayOutput(graph.__toString());
			abstractor.addCFG(graph, false);			
			counter++;
		}
		timeUsed = (System.currentTimeMillis()-time);
		System.out.println("Finished optimising CFGs (" + timeUsed + "ms).");
				
		
		//fill spotlightVec
		spotlightVec = new boolean[counter];
		for(counter = 0; counter < spotlightVec.length; counter++) {
			spotlightVec[counter] = false;
		}				
		
		abstractor.setInitialiserCFG(cParser.getInitCFG());
		
		System.out.println("Reading predicates...");
		//add predicates
		Vector<Expression> vec = readPredicates("input/" + filename + ".pred"); 
		for(Expression pred : vec) {			
			abstractor.addPredicate(pred);
			pred.delete();
		}
		
		compiler = null;
		
		System.out.println("Reading CTL...");
		ctlString = readCTLFormula("input/" + filename + ".ctl");
				
		System.out.println("Finished initialising.");
		
	}
	
	private static void initialise1(SpotlightProject spotlightProject) throws IOException {
		displayOutputln("initialise1");
		displayOutputln("Initialising...");
		
		startTime = System.currentTimeMillis();
		
		
		System.loadLibrary("Spotlight");
		
		
		displayOutputln("Parsing c file...");
		long time = System.currentTimeMillis();
		cParser = new CParser(spotlightProject.getcFile().getAbsolutePath());
		long timeUsed = (System.currentTimeMillis()-time);
		displayOutputln("Finished parsing c file (" + timeUsed + "ms).");
		
		abstractor = new Abstractor();
	
		displayOutputln("Optimising CFGs...");
		time = System.currentTimeMillis();
		cfgs = cParser.getCFGEnumerator();	
		displayOutputln("HIIIEER: " + cParser.getInitCFG().__toString());
		int counter = 0;
		while(cfgs.hasNext()) {
			CFGraph graph = cfgs.getNext();
			graph.reduce();
			
			displayOutput("$GRAPH$");
			displayOutput(graph.__toString());

			abstractor.addCFG(graph, false);			
			counter++;
		}
		
		// parse information for the gui to split the graph from normal output
		if(counter > 0){
			displayOutput("$GRAPH$");
		}
		
		timeUsed = (System.currentTimeMillis()-time);
		displayOutputln("Finished optimising CFGs (" + timeUsed + "ms).");
				
		
		//fill spotlightVec
		spotlightVec = new boolean[counter];
		for(counter = 0; counter < spotlightVec.length; counter++) {
			spotlightVec[counter] = false;
		}				
		
		abstractor.setInitialiserCFG(cParser.getInitCFG());
		
		displayOutputln("Reading predicates...");
		//add predicates
		Vector<Expression> vec = readPredicates(spotlightProject.getinitFile().getAbsolutePath()); 
		for(Expression pred : vec) {			
			abstractor.addPredicate(pred);
			pred.delete();
		}
		
		compiler = null;
		
		displayOutputln("Reading CTL...");
		ctlString = readCTLFormula(spotlightProject.getctlFile().getAbsolutePath());
				
		displayOutputln("Finished initialising.");
		
	}
	
	private static void alterAbstraction(Vector<Expression> newPredicates, Vector<Integer> newProgramIds) {
		displayOutputln("Altering abstraction...");
		
		//fill spotlightVec
		for(int id : newProgramIds) {
			spotlightVec[id] = true;
			abstractor.setSpotlight(id, true);
		}				
		
		displayOutputln("Adding new predicates...");
		for(Expression pred : newPredicates) {			
			abstractor.addPredicate(pred);
			pred.delete();
		}
		
		if(compiler != null)
			compiler.delete();
		
		displayOutputln("Abstracting CFGs...");
		long time = System.currentTimeMillis();
		compiler = new CFGCompiler(cfgs, abstractor, spotlightVec);
		long timeUsed = (System.currentTimeMillis()-time);
		abstractTime += timeUsed;
		displayOutputln("Finished abstracting CFGs (" + timeUsed + "ms).");
		
		displayOutputln("Transforming CFGs => Kripke...");
		time = System.currentTimeMillis();
		XKripkeStructure structure = compiler.compile();
		//System.out.println(structure.getStatePresenter());
		timeUsed = (System.currentTimeMillis()-time);
		convertTime += timeUsed;
		displayOutputln("Finished transforming CFGs => Kripke (" + timeUsed + "ms).");
		
		initValueArray = compiler.getInitValueArray();
		
		checker = new MvSetModelChecker(structure);
		ProofStepFactory.setMC(checker);
		ProofStepFactory.setSNG(new SimpleNameGenerator());
		ProofStepFactory.setStructure(checker.getXKripke());
		
		displayOutputln("Initialising CTL parser...");
		time = System.currentTimeMillis();
		ctlParser = new CTLParser(structure, compiler.getVariableTable(), map, cfgs, compiler.getFairnessMvSets(), compiler.getEncoderMap());
		timeUsed = (System.currentTimeMillis()-time);
		displayOutputln("Finished initialising CTL parser (" + timeUsed + "ms).");
		
		displayOutputln("Finished Altering abstraction.");
		
		if(generateBPs) {
			displayOutputln("Generating boolean programs...");
			EnumeratorOfBooleanProgram enumerator = abstractor.getBooleanProgramEnumerator();
			enumerator.reset();
			while(enumerator.hasNext()) {
				BooleanProgram program = enumerator.getNext();
				displayOutputln(program.__toString());
			}			
			enumerator.delete();
			displayOutputln("Finished generating boolean programs.");
		}
	}
	
	private static void cleanup() {
		compiler.delete();
		cfgs.delete();
		cParser.delete();
		abstractor.delete();
	}
	
	private static void run() {		
		computeInitSpotlight();
		while (true) {
			CTLNode node;
			long time = System.currentTimeMillis();
			try {
				node = ctlParser.prepareCTL(ctlString);
			//	displayOutput(node);
			} catch (IllegalArgumentException e) {
				displayOutputln("Unable to parse given CTL formula: " + e.getLocalizedMessage());
				break;
			}
			long timeUsed = (System.currentTimeMillis() - time);			
			displayOutputln("Finished parsing CTL formula (" + timeUsed + "ms).");

			time = System.currentTimeMillis();
			MvSet set = checker.checkCTL(node);
			timeUsed = (System.currentTimeMillis() - time);
			mcTime += timeUsed;
			AlgebraValue value = set.evaluate(initValueArray);
			displayOutputln(ctlString + " evaluates to " + value + " for the given program (" + timeUsed + "ms).");
			if (value.equals(compiler.getMaybeValue())) {
				//showCounterExampleTree2(node, compiler.getMaybeValue());
				ProofStep proof = generateCounterExample(node);
				analyseProof(proof, node);
			} else {
				break;
			}
		}
		printStatistics();
	}
	
	private static void printStatistics() {
		displayOutputln("");
		displayOutputln("Stats:");
		displayOutputln("~~~~~~");
	
		displayOutputln("Total time: " + (System.currentTimeMillis()-startTime) + "ms.");
		displayOutputln("Abstracting time: " + abstractTime + "ms.");
		displayOutputln("Converting time: " + convertTime + "ms.");
		displayOutputln("Model Checking time: " + mcTime + "ms.");
		displayOutputln("Analysis time: " + anaTime + "ms.");
		
	}

	private static void computeInitSpotlight() {
		Vector<Expression> newPreds = new Vector<Expression>();
		Vector<Integer> newSpot = new Vector<Integer>();
		String ctl = ctlString;		
		while(true) {
			int index = ctl.indexOf("pc_");
			if(index == -1)
				break;
			ctl = ctl.substring(index);
			int processNumber = ProgramCounterEncoder.parseProcessNumber(ctl);
			newSpot.add(processNumber);
			ctl = ctl.substring(ctl.indexOf("=")+1);
		}
		
		displayOutputln("Following programs are added: ");
		displayOutputln(Collections.list(newSpot.elements()).toString());
		
		alterAbstraction(newPreds, newSpot);
	}
	
	private static String readCTLFormula(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while(reader.ready()) {	
			String expr = reader.readLine();
			return expr;
		}
		return null;		
	}
	
	
	private static void analyseProof(ProofStep proof, CTLNode node) {
		
		LinearWitnessVisitor visitor = new LinearWitnessVisitor();		
		long time = System.currentTimeMillis();
		ProofAnalyser ana = new ProofAnalyser(visitor, proof, compiler.getVariableTable(), compiler.getAlgebra(), compiler.getMaybeValue(), cfgs);
		Analyser analyser = new Analyser(abstractor.getPredicates(), node, ana, compiler.getSpotlightVec(), cfgs);
		analyser.analyse();
		
		long timeUsed = System.currentTimeMillis() - time;
		anaTime += timeUsed;
		
		Vector<Expression> newPred = analyser.getNewPredicates();
		Vector<Integer> newProgs = analyser.getNewProgramsIds();
		
		displayOutputln("");
		displayOutputln("----------------------------------");
		displayOutputln("Following predicates are added: ");
		for(Expression expr : newPred) {
			displayOutputln(expr.getExpressionCString());
		}
		displayOutputln("Following programs are added: ");
		displayOutputln(Collections.list(newProgs.elements()).toString());
		displayOutputln("----------------------------------");
		displayOutputln("");
		
		alterAbstraction(newPred, newProgs);
	}

	private static ProofStep generateCounterExample(CTLNode node) {
		long time = System.currentTimeMillis();
		
		ProofStep proofStep = ProofStepFactory.makeProofStep(compiler.getMaybeValue(), checker.getXKripke().getInit(), node);
		proofStep.discharge();
		
		long timeUsed = System.currentTimeMillis() - time;
		anaTime += timeUsed;
		
		return proofStep;
	}

	/**
	 * Main :)
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {		
		if(args.length < 1 || args.length > 2) {
			displayOutputln("java -jar Spotlight.jar [flags] [example].");
			displayOutputln("Flags:");
			displayOutputln("-BooleanProgram: When enabled boolean programs are generated for each refinement step.");
			return;
		}
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("-BooleanProgram") || args[0].equalsIgnoreCase("-BP")) {
				generateBPs = true;
			}
			else {
				displayOutputln("Unknown flag: " + args[0]);
				return;
			}	
			args[0] = args[1];
		} 
		
			initialise(args[0]);
			run();
        EnumeratorOfCFGraph abstractedCFGs = abstractor.getAbstractedCFGs();
        abstractedCFGs.reset();
        int numberOfProcesses = abstractedCFGs.getNumberofElements();
        System.out.println("numberOfProcesses = " + numberOfProcesses);
        ThreeValuedModelChecker modelChecker = new ThreeValuedModelChecker(abstractor.getPredicates(), abstractedCFGs, 1);
		modelChecker.test();
        cleanup();
	}
	
	
	/*private static void showCounterExampleTree2(CTLNode ctl, AlgebraValue value) {
		// -- the initial state where the proof starts
		MvSet initState = checker.getXKripke().getInit();
		MvSetModelChecker mc = checker;

		// -- create a formula for the prover
		Formula formula = new EqualFormula(ctl, value, initState);
		// -- create the root node
		edu.toronto.cs.proof2.ProofStep rootStep = new TreeProofStep(formula, null);

		CTLProver prover = new CTLProver(mc, rootStep);

		// -- initialize the prover with the rules!
		prover.addProofRule(new EqualsProofRule());
		prover.addProofRule(new CheckingTopBottom(mc));
		prover.addProofRule(new NegationProofRule(mc));
		prover.addProofRule(new AtomicProofRule(mc));
		// new!
		prover.addProofRule(new DepthProofRule(mc));

		prover.addProofRule(new AndOrProofRule(mc));
		// new! Do me first!
		// prover.addProofRule (new VisitedEXProofRule(mc, initState));

		// deprecate old EX proofrule
		prover.addProofRule(new EXProofRule(mc));
		prover.addProofRule(new EXCexProofRule(mc));
		prover.addProofRule(new EUProofRule(mc));
		prover.addProofRule(new EUiProofRule(mc));
		prover.addProofRule(new AXProofRule(mc));
		prover.addProofRule(new EGProofRule(mc));
		prover.addProofRule(new AUProofRule(mc));
		prover.addProofRule(new AUiProofRule(mc));

		displayOutput("Added proof rules");

		// -- run the prover
		prover.expand(rootStep);

		DynamicProofDisplay.showProof(prover, rootStep);
		// ProofTreeFrame code temporarily disabled,
		// to make room for even-more-experimental ProofTreeModel code
	}*/	

	/*private static AbstractSet<Integer> readSpotlight(String file) throws IOException {
		AbstractSet<Integer> set = new HashSet<Integer>();
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while(reader.ready()) {	
			String spot = new String();
			
			while(true) {
				int read = reader.read();
				if(read == -1)
					break;
				char buf = (char)read;
				if(buf == ' ' || buf == '\r' || buf == '\n')
					break;
				spot += buf;
			}
			if(spot.isEmpty())
				break;
			set.add(Integer.parseInt(spot));
		}
		return set;		
	}*/

	private static Vector<Expression> readPredicates(String file) throws IOException {
		Vector<Expression> vec = new Vector<Expression>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while(reader.ready()) {	
			String expr = reader.readLine();
			vec.add(new Expression(expr, true));
			
			map.put(expr, vec.lastElement().getExpressionCString());
		}
		return vec;		
	}
	
	public static void startFromGui(SpotlightProject spotlightProject, JTextArea textArea) throws IOException{		
		displayOutputln(spotlightProject.getcFile().getAbsolutePath());
		displayOutputln(spotlightProject.getctlFile().getAbsolutePath());
		displayOutputln(spotlightProject.getinitFile().getAbsolutePath());		
		
		console = textArea;
		
		
		initialise1(spotlightProject);
		
		run();
		cleanup();
	}
	
	public static void displayOutputln(String txt){
		if(console == null){
			System.out.println(txt);
		}
		else{
			console.append(txt + "\n");
		}
	}
	
	public static void displayOutput(String txt){
		if(console == null){
			System.out.print(txt);
		}
		else{
			console.append(txt);
		}
	}
}
