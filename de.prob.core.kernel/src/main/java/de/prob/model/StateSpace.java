package de.prob.model;

import java.util.Collection;
import java.util.List;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.OpInfo;
import de.prob.animator.command.Variable;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class StateSpace extends DirectedSparseMultigraph<String, Operation>
		implements IAnimator {

	private static final long serialVersionUID = -9047891508993732222L;
	private transient IAnimator animator;

	private String currentState = "root";

	@Inject
	public StateSpace(final IAnimator animator) {
		this.animator = animator;
		addVertex("root");
	}

	/**
	 * Takes a state id and calculates the successor states, the invariant,
	 * timeout, etc.
	 * 
	 * @param id
	 * @throws ProBException
	 */
	public void explore(final String id) throws ProBException {
		ExploreStateCommand command = new ExploreStateCommand(id);
		animator.execute(command);
		List<OpInfo> enabledOperations = command.getEnabledOperations();
		// (id,name,src,dest,args)
		for (OpInfo ops : enabledOperations) {
			Operation op = new Operation(ops.id, ops.name, ops.params);
			if (!containsEdge(op)) {
				addEdge(op, ops.src, ops.dest, EdgeType.DIRECTED);
			}

		}

		List<Variable> variables = command.getVariables();
		System.out.println("State: " + id);
		for (Variable variable : variables) {
			System.out.println(variable);
		}
		System.out
				.println("======================================================");
	}

	public void exec(final int i) throws ProBException {
		String opId = String.valueOf(i);
		Collection<Operation> outEdges = getOutEdges(currentState);
		String dst = null;
		for (Operation operation : outEdges) {
			if (operation.getId().equals(opId)) {
				dst = getDest(operation);
			}
		}
		if (dst != null) {
			explore(dst);
		} else {
			System.out.println("Error: Illegal Operation");
		}

		currentState = dst;
		Collection<Operation> out = getOutEdges(currentState);
		for (Operation operation : out) {
			System.out.println(operation.getId() + ": " + operation);
		}

	}

	public void explore(final int i) throws ProBException {
		String si = String.valueOf(i);
		explore(si);
	}

	@Override
	public void execute(final ICommand command) throws ProBException {
		animator.execute(command);
	}

	@Override
	public void execute(final ICommand... commands) throws ProBException {
		animator.execute(commands);
	}

}
