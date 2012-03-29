package de.prob.animator.command;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class EvaluateCommand implements ICommand {

	private final String expression;
	private String result;

	public EvaluateCommand(final String expression) {
		this.expression = expression;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.printAtom("evaluate").printAtom(expression).printVariable("Result")
				.printVariable("Warnings");
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {

		result = PrologTerm.atomicString(bindings.get("Result"));

	}

	public String getResult() {
		return result;
	}
}
