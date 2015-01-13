package com.github.t3t5u.common.http;

public class ByteArrayConfigurationBuilder extends ConfigurationBuilder<byte[], ByteArrayConfiguration, ByteArrayConfigurationBuilder> {
	public ByteArrayConfigurationBuilder() {
		this(null);
	}

	public ByteArrayConfigurationBuilder(final ByteArrayConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected ByteArrayConfiguration clone(final ByteArrayConfiguration configuration) {
		return new ByteArrayConfiguration(configuration);
	}
}
