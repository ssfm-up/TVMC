package edu.toronto.cs.modelchecker;

import edu.toronto.cs.ctl.CTLAFNode;
import edu.toronto.cs.ctl.CTLAGNode;
import edu.toronto.cs.ctl.CTLAUNode;
import edu.toronto.cs.ctl.CTLAUiNode;
import edu.toronto.cs.ctl.CTLAXNode;
import edu.toronto.cs.ctl.CTLNode;

public class ExistentialRewriter extends CloningRewriter {
	public Object visitAFNode(CTLAFNode node, Object o) {
		// --  AF p == ~EG ~p
		CTLNode[] fairness = (CTLNode[]) node.getFairness().clone();
		for (int i = 0; i < fairness.length; i++)
			fairness[i] = rewrite(fairness[i]);
		return rewrite(node.getRight()).neg().eg(fairness).neg();
	}

	public Object visitAGNode(CTLAGNode node, Object o) {
		CTLNode[] fairness = (CTLNode[]) node.getFairness().clone();
		for (int i = 0; i < fairness.length; i++)
			fairness[i] = rewrite(fairness[i]);

		// -- AG p == ~EF ~p
		return rewrite(node.getRight()).neg().ef(fairness).neg();
	}

	public Object visitAUNode(CTLAUNode node, Object o) {
		CTLNode[] fairness = (CTLNode[]) node.getFairness().clone();
		for (int i = 0; i < fairness.length; i++)
			fairness[i] = rewrite(fairness[i]);

		// -- A[p U q] = ~ (EG ~q \/ E[~q /\ p U ~p /\ ~q])
		CTLNode p = rewrite(node.getLeft());
		CTLNode q = rewrite(node.getRight());

		return q.neg().eg(fairness).or(p.and(q.neg()).eu(p.neg().and(q.neg()), fairness)).neg();
	}

	public Object visitAUiNode(CTLAUiNode node, Object o) {
		assert false : "Don't know how to rewrite AUi node";
		return null;
	}

	public Object visitAXNode(CTLAXNode node, Object o) {
		CTLNode[] fairness = (CTLNode[]) node.getFairness().clone();
		for (int i = 0; i < fairness.length; i++)
			fairness[i] = rewrite(fairness[i]);

		// -- AX p == ~EX~p
		return rewrite(node.getRight()).neg().ex(fairness).neg();
	}

}
