package com.github.t3t5u.common.http;

public abstract class AbstractStringConvertibleInvoker<V> extends AbstractConvertibleInvoker<V, String, StringConfiguration> {
	private StringConfiguration configuration;

	protected AbstractStringConvertibleInvoker() {
		this(new StringConfigurationBuilder().build());
	}

	protected AbstractStringConvertibleInvoker(final StringConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	protected Delegator<String, StringConfiguration> getDelegator() {
		return StringDelegator.getInstance();
	}

	@Override
	protected StringConfiguration getConfiguration() {
		return configuration;
	}
}
