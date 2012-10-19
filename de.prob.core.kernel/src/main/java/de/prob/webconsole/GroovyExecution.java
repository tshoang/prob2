package de.prob.webconsole;

import groovy.lang.Binding;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.tools.shell.Interpreter;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.Parser;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.statespace.AnimationSelector;

/**
 * This servlet takes a line from the web interface and evaluates it using
 * Groovy. The Groovy interpreter does not remember import statements, i.e., the
 * input 'import foo.Bar; x = new Bar' will work, but spliting it into two
 * separate lines won't. We thus collect any import statement and prefix every
 * command with all the imports.
 * 
 * @author bendisposto
 * 
 */
@Singleton
public class GroovyExecution {

	private final ArrayList<String> inputs = new ArrayList<String>();
	private final ArrayList<String> imports = new ArrayList<String>();

	private final Interpreter interpreter;

	private final Parser parser;

	private OutputBuffer sideeffects;

	private boolean continued;

	private int genCounter = 0;

	public int nextCounter() {
		return genCounter++;
	}

	private String outputs;

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;" };
	private final ShellCommands shellCommands;

	@Inject
	public GroovyExecution(final Api api, final ShellCommands shellCommands,
			final AnimationSelector selector, OutputBuffer sideeffects) {
		this.shellCommands = shellCommands;
		this.sideeffects = sideeffects;
		final Binding binding = new Binding();
		binding.setVariable("api", api);
		binding.setVariable("animations", selector);
		binding.setVariable("__console", sideeffects);
		this.interpreter = new Interpreter(this.getClass().getClassLoader(),
				binding);

		imports.addAll(Arrays.asList(IMPORTS));

		URL url = Resources.getResource("initscript");

		String script = "";
		try {
			script = Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.parser = new Parser();
		runScript(script);
	}

	public String evaluate(final String input) throws IOException {
		assert input != null;
		final List<String> m = shellCommands.getMagic(input);
		if (m.isEmpty()) {
			return eval(input);
		} else {
			return shellCommands.perform(m, this);
		}
	}

	public String runScript(String content) {
		final ArrayList<String> eval = new ArrayList<String>();
		eval.addAll(imports);
		eval.add(content);
		Object evaluate = null;
		try {
			evaluate = interpreter.evaluate(eval);
		} catch (final Throwable e) {
			printStackTrace(sideeffects, e);
		} finally {
			inputs.clear();
		}
		return evaluate == null ? "null" : evaluate.toString();
	}

	public Object tryevaluate(final String input) throws IOException {
		final Interpreter tinterpreter = new Interpreter(this.getClass()
				.getClassLoader(), interpreter.getContext());

		assert input != null;
		final ArrayList<String> eval = new ArrayList<String>();
		eval.addAll(imports);
		eval.addAll(Collections.singletonList(input));
		return tinterpreter.evaluate(eval);
	}

	public String[] getImports() {
		final String[] result = new String[imports.size()];
		int c = 0;
		for (final String string : imports) {
			result[c++] = " " + string.substring(7, string.length() - 1).trim();
		}
		return result;
	}

	public Binding getBindings() {
		return interpreter.getContext();
	}

	public String getOutputs() {
		return outputs;
	}

	public boolean isContinued() {
		return continued;
	}

	private void printStackTrace(OutputBuffer buffer, Throwable t) {
		String msg = t.toString();

		ArrayList<String> trace = new ArrayList<String>();
		// add each element of the stack trace
		for (StackTraceElement element : t.getStackTrace()) {
			trace.add(element.toString());
		}

		buffer.error(msg, trace);

	}

	private String eval(final String input) {
		Object evaluate = null;
		ParseCode parseCode;
		inputs.add(input);

		final ArrayList<String> eval = new ArrayList<String>();
		eval.addAll(imports);
		eval.addAll(inputs);
		parseCode = parser.parse(eval).getCode();

		if (parseCode.equals(ParseCode.getINCOMPLETE())) {
			continued = true;
			outputs = "";
			return "";
		} else {
			continued = false;
			try {
				evaluate = interpreter.evaluate(eval);
			} catch (final Throwable e) {
				imports.remove(input);
				printStackTrace(sideeffects, e);
			} finally {
				inputs.clear();
			}
			return evaluate == null ? "null" : evaluate.toString();
		}
	}

	public void addImport(final String imp) {
		this.imports.add(imp);
	}

}
