package com.redhat.lsptrace.process;

import java.util.ArrayList;
import java.util.List;

public class JavaProcessStreamConnectionProvider extends ProcessStreamConnectionProvider {

	private Integer debuggerPort;

	public JavaProcessStreamConnectionProvider(String jarPath, String mainClass, Integer debuggerPort) {
		List<String> commands = new ArrayList<>();
		commands.add("java");
		if (debuggerPort != null) {
			commands.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=" + debuggerPort);
		}
		commands.add("-classpath");
		commands.add(jarPath);
		commands.add(mainClass);
		super.setCommands(commands);
	}
}
