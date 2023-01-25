package com.redhat.lsptrace.trace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class LSPTraceLoader {
	private static final String PARAMS = "Params:";
	private static final String RESULT = "Result:";

	private static final String SENDING_REQUEST = "Sending request '";

	private static final String SENDING_NOTIFICATION = "Sending notification '";

	private static final String RECEIVED_RESPONSE = "Received response '";

	public List<LSPPayload> load(Reader reader) throws IOException {
		List<LSPPayload> data = new ArrayList<>();
		try (var br = new BufferedReader(reader)) {

			String line;
			LSPPayload message = null;
			while ((line = br.readLine()) != null) {
				if (message == null) {
					if (line.startsWith("[Trace")) {
						String method = getMethod(line, SENDING_REQUEST, " ");
						if (method == null) {
							method = getMethod(line, SENDING_NOTIFICATION, "'");
						}
						if (method != null) {
							// [Trace - 11:28:59 PM] Sending request 'textDocument/linkedEditingRange -
							// (72)'.
							message = new LSPPayloadRequest(method);
							data.add(message);
						} else {
							method = getMethod(line, RECEIVED_RESPONSE, " ");
							if (method != null) {
								// [Trace - 11:28:59 PM] Received response 'textDocument/linkedEditingRange -
								// (72)' in 2ms.
								message = new LSPPayloadResponse(method);
							}
						}
					}
				} else {
					if (line.startsWith(PARAMS)) {
						line = line.substring(PARAMS.length(), line.length()).trim();
					} else if (line.startsWith(RESULT)) {
						line = line.substring(RESULT.length(), line.length()).trim();
					}
					message.append(line);
					if (line.startsWith("}") || line.equals("{}") || line.startsWith("]") || line.equals("[]") || line.equals("No result returned.")) {
						message = null;
					}

				}
				// System.out.println(line);
			}
		}
		return data;
	}

	private static String getMethod(String line, String startPattern, String endPattern) {
		int index = line.indexOf(startPattern);
		if (index != -1) {
			index += startPattern.length();
			// [Trace - 11:28:59 PM] Sending request 'textDocument/linkedEditingRange -
			// (72)'.
			return line.substring(index, line.indexOf(endPattern, index));
		}
		return null;
	}
}
