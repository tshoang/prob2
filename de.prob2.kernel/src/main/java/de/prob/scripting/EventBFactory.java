package de.prob.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBDatabaseTranslator;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.statespace.StateSpace;

public class EventBFactory extends ModelFactory {

	private final Provider<EventBModel> modelProvider;

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider,
			final FileHandler fileHandler) {
		super(fileHandler);
		this.modelProvider = modelProvider;
	}

	public EventBModel load(final String file, final Map<String, String> prefs,
			final boolean loadVariables) {
		EventBModel model = modelProvider.get();

		new EventBDatabaseTranslator(model, file);
		model.isFinished();

		List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

		for (Entry<String, String> pref : getPreferences(model, prefs)
				.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		AbstractCommand loadcmd = new LoadEventBProjectCommand(
				new EventBModelTranslator(model));

		cmds.add(loadcmd);
		cmds.add(new StartAnimationCommand());
		StateSpace s = model.getStateSpace();
		s.execute(new ComposedCommand(cmds));
		s.setLoadcmd(loadcmd);

		if (loadVariables) {
			model.subscribeFormulasOfInterest();
		}
		return model;
	}
}