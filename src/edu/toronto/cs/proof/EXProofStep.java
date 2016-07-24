package edu.toronto.cs.proof;

// represents a proof of 'v: s |= phi' for a CTL formula phi, 
//  state s, and algebra value v
import java.util.*;

import edu.toronto.cs.mvset.*;
import edu.toronto.cs.modelchecker.*;
import edu.toronto.cs.ctl.*;
import edu.toronto.cs.algebra.*;

public class EXProofStep extends ProofStep {

	// We know from model-checking that [consequent](state) = v
	// Since consequent = EX psi
	// psi = consequent.leftAnt
	// so we need the map:
	// \lambda s. R(state,s) /\ [consequent.leftAnt](s)

	protected EXProofStep(AlgebraValue _v, MvSet _state, String _stateName,
			CTLNode _consequent) {
		super(_v, _state, _stateName, _consequent);
	}

	public boolean unfold() {
		IAlgebra alg = state.getAlgebra();
		if (consequent.getFairness().equals(CTLAbstractNode.EMPTY_ARRAY)) {
			// some things we'll need
			MvSetFactory fac = state.getFactory();
			
			MvSetModelChecker mc = ProofStepFactory.getMC();
			XKripkeStructure mod = ProofStepFactory.getStructure();

			// things we'll be computing
			MvSet succMap; // the map of successors to state
			Set img; //

			succMap = mc.getTrans().fwdImage(state).and(
					mc.checkCTL(consequent.getRight()));
			//succMap = mc.checkCTL(consequent.getRight().preEX()).and (state).
			//existAbstract(mod.getUnPrimeCube()).renameArgs(mod.getUnPrime());

			img = alg.getJoinIrredundant(succMap.getImage());

			for (Iterator it = img.iterator(); it.hasNext();) {
				// transition+next value
				AlgebraValue tv = (AlgebraValue) it.next();
				// witness
				// 	Set xm = succMap.getPreImageArray (tv); 

				// 	// there needs to be a strategy for this
				// 	AlgebraValue[] ns = (AlgebraValue[])xm.iterator ().next ();
				// 	int[] prime = mod.getPrime ();

				// 	for (int i=0; i < ns.length; i++)
				// 	  // -- if i is unprimed variable (cute way to find this)
				// 	  // -- then let it be \bot
				// 	  if (prime [i] != i && ns [i].equals(alg.noValue()))
				// 	    ns[i] = alg.bot();

				// 	// need to make an MvState out of each of them
				// 	MvSet nxt = fac.createPoint (ns, alg.top());

				try {
					MvSet nxt = (MvSet) succMap.mintermIterator(
							mod.getUnPrimeCube(), tv).next();
					// -- instead of 'tv' we should use 
					// -- checkCTL (consequent.getRight (), nxt)
					// 	antecedents.add 
					// 	  (ProofStepFactory.makeProofStep(tv,
					// 					  nxt,
					// 					  consequent.getRight()));
					antecedents.add(ProofStepFactory.makeProofStep(mc.checkCTL(
							consequent.getRight(), nxt).getValue(), nxt,
							consequent.getRight()));
				} catch (NoSuchElementException ex) {
					System.err
							.println("Warning: Potential problem generating a cex. (Ignored)");
				}
			}
		} else {
			CTLNode top = CTLFactory.createCTLConstantNode(alg.top());
			CTLNode exp = consequent.getRight().and(top.eg(consequent.getFairness())).ex();
			antecedents.add(ProofStepFactory.makeProofStep(getValue(), state, exp));
		}

		unfolded = true;

		// -- return true if we added any children
		return !antecedents.isEmpty();

	}

	public Object accept(ProofVisitor pv, Object info) {
		return pv.visitEXStep(this, info);
	}

}
