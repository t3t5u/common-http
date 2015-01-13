package com.github.t3t5u.common.http;

public abstract class AbstractByteArrayConvertibleInvoker<V> extends AbstractConvertibleInvoker<V, byte[], ByteArrayConfiguration> {
	private ByteArrayConfiguration configuration;

	protected AbstractByteArrayConvertibleInvoker() {
		this(new ByteArrayConfigurationBuilder().build());
	}

	protected AbstractByteArrayConvertibleInvoker(final ByteArrayConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	protected Delegator<byte[], ByteArrayConfiguration> getDelegator() {
		return ByteArrayDelegator.getInstance();
	}

	@Override
	protected ByteArrayConfiguration getConfiguration() {
		return configuration;
	}
}
