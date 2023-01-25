package com.redhat.lsptrace.trace;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

public class LSPPayloadRequest extends LSPPayload {

	public LSPPayloadRequest(String method) {
		super(method);
	}

}
