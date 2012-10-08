package de.prob.model.eventb;

import org.eventb.emf.core.EventBNamedCommentedElement;

import de.prob.model.representation.Formula;

public class EventBFormula extends Formula {

	private final EventBNamedCommentedElement emfComponent;

	public EventBFormula(final String label, final String value,
			final EventBNamedCommentedElement emfComponent) {
		super(label, value);
		this.emfComponent = emfComponent;
	}

	public EventBNamedCommentedElement getEmfComponent() {
		return emfComponent;
	}

}