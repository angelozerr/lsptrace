package com.redhat.lsptrace.trace;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

public class LSPPayload {

	private StringBuilder body;

	private final String method;

	public LSPPayload(String method) {
		this.method = method;
		this.body = new StringBuilder();
	}

	public String getMethod() {
		return method;
	}

	public void append(String line) {
		if (!body.isEmpty()) {
			body.append(System.lineSeparator());
		}
		body.append(line);
	}

	public String getBody() {
		return body.toString();
	}

	@Override
	@Pure
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.add("method", this.method);
		b.add("body", this.body);
		return b.toString();
	}
}
