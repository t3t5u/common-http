package com.github.t3t5u.common.http;

import java.nio.charset.Charset;

public class StringConfigurationBuilder extends ConfigurationBuilder<String, StringConfiguration, StringConfigurationBuilder> {
	public StringConfigurationBuilder() {
		this(null);
	}

	public StringConfigurationBuilder(final StringConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected StringConfiguration clone(final StringConfiguration configuration) {
		return new StringConfiguration(configuration);
	}

	public StringConfigurationBuilder setCharset(final Charset charset) {
		getConfiguration().setCharset(charset);
		return this;
	}
}
