package com.redhat.lsptrace.xml;

import java.io.File;

import com.redhat.lsptrace.process.JavaProcessStreamConnectionProvider;

public class XMLJavaProcessStreamConnectionProvider extends JavaProcessStreamConnectionProvider {

	public XMLJavaProcessStreamConnectionProvider(Integer debuggerPort) {
		super(new File("server/org.eclipse.lemminx-uber.jar").getAbsolutePath(),
				"org.eclipse.lemminx.XMLServerLauncher", debuggerPort);
	}

}
