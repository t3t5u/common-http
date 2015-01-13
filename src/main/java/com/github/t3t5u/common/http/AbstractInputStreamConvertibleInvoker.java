package com.github.t3t5u.common.http;

import java.io.InputStream;

public abstract class AbstractInputStreamConvertibleInvoker<V> extends AbstractConvertibleInvoker<V, InputStream, InputStreamConfiguration> {
	private InputStreamConfiguration configuration;

	protected AbstractInputStreamConvertibleInvoker() {
		this(new InputStreamConfigurationBuilder().build());
	}

	protected AbstractInputStreamConvertibleInvoker(final InputStreamConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	protected Delegator<InputStream, InputStreamConfiguration> getDelegator() {
		return InputStreamDelegator.getInstance();
	}

	@Override
	protected InputStreamConfiguration getConfiguration() {
		return configuration;
	}
}
