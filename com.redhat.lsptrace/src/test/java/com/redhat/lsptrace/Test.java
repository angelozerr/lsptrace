package com.redhat.lsptrace;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.redhat.lsptrace.process.StreamConnectionProvider;
import com.redhat.lsptrace.xml.XMLJavaProcessStreamConnectionProvider;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		Integer debugerPort = null; // fill the port if LemMinx needs to be debugged.
		StreamConnectionProvider provider = new XMLJavaProcessStreamConnectionProvider(debugerPort);

		LSPTraceTester tester = new LSPTraceTester(provider);
		tester.process(new File("trace.txt"));
	}
}
