package com.github.t3t5u.common.http;

import java.io.InputStream;

public class InputStreamConfigurationBuilder extends ConfigurationBuilder<InputStream, InputStreamConfiguration, InputStreamConfigurationBuilder> {
	public InputStreamConfigurationBuilder() {
		this(null);
	}

	public InputStreamConfigurationBuilder(final InputStreamConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected InputStreamConfiguration clone(final InputStreamConfiguration configuration) {
		return new InputStreamConfiguration(configuration);
	}
}
