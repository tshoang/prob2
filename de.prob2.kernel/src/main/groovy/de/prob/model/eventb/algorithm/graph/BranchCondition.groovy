package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement
import de.prob.util.Tuple2

public class BranchCondition implements INode {
	def List<EventB> conditions
	def List<Statement> statements
	def INode outNode
	def List<Assertion> assertions = []

	def BranchCondition(List<EventB> conditions, List<Statement> statements, INode outNode) {
		this.conditions = conditions
		this.statements = statements
		this.outNode = outNode
	}

	@Override
	public String toString() {
		return conditions.toString()
	}

	@Override
	public INode getOutNode() {
		outNode
	}

	@Override
	public List<Statement> getStatements() {
		return statements
	}

	@Override
	public void setEndNode(INode node) {
		this.outNode = node
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}

	public List<Tuple2<EventB, Statement>> condAndStmts() {
		List<Tuple2<EventB, Statement>> conds = []
		conditions.eachWithIndex { item, index ->
			conds.add(new Tuple2<EventB,Statement>(item, statements[index]))
		}
		conds
	}
}
