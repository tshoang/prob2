package de.prob.model.representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DirectedMultigraph;

import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.StateSpace;

public abstract class AbstractModel implements IFormula {

	protected StateSpace statespace;
	protected HashMap<String, AbstractElement> components;
	protected DirectedMultigraph<String, RefType> graph;
	private final FormulaUUID uuid = new FormulaUUID();

	public StateSpace getStatespace() {
		return statespace;
	}

	public HashMap<String, AbstractElement> getComponents() {
		return components;
	}

	public DirectedMultigraph<String, RefType> getGraph() {
		return graph;
	}

	public ERefType getRelationship(final String comp1, final String comp2) {
		return getEdge(comp1, comp2);
	}

	public ERefType getEdge(final String comp1, final String comp2) {
		final RefType edge = graph.getEdge(comp1, comp2);
		if (edge == null)
			return null;

		return edge.getRelationship();
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	@Override
	public String getLabel() {
		return uuid.uuid;
	}

	@Override
	public String getValue() {
		return "";
	}

	@Override
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getAllSubformulas() {
		final List<IFormula> subformulas = new ArrayList<IFormula>();
		subformulas.addAll(components.values());
		return subformulas;
	}

	@Override
	public List<IFormula> getVisibleSubformulas() {
		// TODO Auto-generated method stub
		return null;
	}
}