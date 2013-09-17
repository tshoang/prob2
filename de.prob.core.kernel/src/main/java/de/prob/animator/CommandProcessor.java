package de.prob.animator;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.IRawCommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

class CommandProcessor {
	
	private final Query qry;
	
	public CommandProcessor() {
		qry = new Query();
	}
	
	public ISimplifiedROMap<String, PrologTerm> sendCommand(
			final AbstractCommand command) {

		qry.clear();
		if (command instanceof IRawCommand) {
			IRawCommand rawrCommand = (IRawCommand) command;
			qry.printRaw(rawrCommand.getCommand(), rawrCommand.getVariables());
		} else {
			command.writeCommand(qry);
		}

		qry.execute();

		return new SimplifiedROMap<String, PrologTerm>(qry.getBinding());
	}

	
}
