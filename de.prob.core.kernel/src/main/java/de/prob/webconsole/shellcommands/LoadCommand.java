package de.prob.webconsole.shellcommands;

import groovy.lang.Binding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jline.FileNameCompletor;

import com.google.common.base.Joiner;

import de.prob.webconsole.GroovyExecution;

public class LoadCommand extends AbstractShellCommand {

	@Override
	public Object perform(List<String> m, GroovyExecution exec)
			throws IOException {
		assert m.size() > 1;
		String filename = m.get(1);

		if (filename.startsWith("~")) {
			filename = filename.replaceFirst("~", System.getProperty("user.home"));
		}

		int lastDot = filename.lastIndexOf('.');
		String extension = filename.substring(lastDot, filename.length());

		if (extension.equals(".mch") || extension.equals(".ref")
				|| extension.equals(".imp")) {
			String name = freshVar(exec, "model_");
			exec.evaluate(name + " = api.b_load('" + filename + "')");
			Object model = exec.getBindings().getVariable(name);
			if (model != null) {
				return name + " = " + model;
			}
			return model;
		}
		if (extension.equals(".eventb")) {
			String name = freshVar(exec, "model_");
			exec.evaluate(name + " = api.eb_load('" + filename + "')");
			Object model = exec.getBindings().getVariable(name);
			if (model != null) {
				return name + " = " + model;
			}
			return model;
		}
		System.out.println("Unknown filetype: " + extension);
		return null;
	}

	private synchronized String freshVar(GroovyExecution exec, String string) {
		String v;
		Binding bindings = exec.getBindings();
		do {
			v = string + exec.nextCounter();
		} while (bindings.hasVariable(v));
		bindings.setVariable(v, null);
		return v;
	}

	@Override
	public List<String> complete(List<String> args, int pos) {
		ArrayList<String> suggestions = new ArrayList<String>();
		ArrayList<String> s = new ArrayList<String>();
		FileNameCompletor completor = new FileNameCompletor();
		String input = Joiner.on(" ").join(args);
		completor.complete(input, pos, suggestions);
		int lastSlash = input.lastIndexOf(File.separator);
		if (lastSlash > -1) {
			String prefix = input.substring(0, lastSlash);
			for (String string : suggestions) {
				s.add(prefix + File.separator + string);
			}
		}
		if (s.size() == 1) {
			return Collections.singletonList("load " + s.get(0));
		}

		return s;
	}
}