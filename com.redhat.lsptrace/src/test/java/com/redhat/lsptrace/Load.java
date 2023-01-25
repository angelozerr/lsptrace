package com.redhat.lsptrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.redhat.lsptrace.trace.LSPPayload;
import com.redhat.lsptrace.trace.LSPTraceLoader;

public class Load {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		LSPTraceLoader loader = new LSPTraceLoader();
		List<LSPPayload> messages = loader.load(new FileReader(new File("trace.txt")));

		for (LSPPayload message : messages) {
			System.err.println(message);
		}
	}
}
