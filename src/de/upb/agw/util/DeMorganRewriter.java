package de.upb.agw.util;

import edu.toronto.cs.ctl.CTLAndNode;
import edu.toronto.cs.ctl.CTLNegNode;
import edu.toronto.cs.ctl.CTLNode;
import edu.toronto.cs.ctl.CTLOrNode;
import edu.toronto.cs.modelchecker.CloningRewriter;

/**
 * Rewrites a CTL formula according to DeMorgan rule: <br>
 * !(a /\ b) = !a \/ !b <br>
 * !(a \/ b) = !a /\ !b <br>
 * (and also:) <br>
 * !!a = a
 * @author Daniel Wonisch
 *
 */
public class DeMorganRewriter extends CloningRewriter {
	public DeMorganRewriter() {

	}

	public Object visitNegNode(CTLNegNode node, Object o) {
		CTLNode right = node.getRight();
		if(right instanceof CTLAndNode) {
			return rewrite(right.getLeft().neg()).or(rewrite(right.getRight().neg()));
		}
		if(right instanceof CTLOrNode) {
			return rewrite(right.getLeft().neg()).and(rewrite(right.getRight().neg()));
		}
		if(right instanceof CTLNegNode) {
			return rewrite(right.getRight());
		}
		return rewrite(right).neg();
	}
}
