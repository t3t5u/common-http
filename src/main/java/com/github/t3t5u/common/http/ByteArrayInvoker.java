package com.github.t3t5u.common.http;

import java.util.List;
import java.util.Map;

public class ByteArrayInvoker extends AbstractInconvertibleInvoker<byte[], ByteArrayConfiguration> {
	public ByteArrayInvoker(final Method method, final String url) {
		this(method, url, null, null, new ByteArrayConfigurationBuilder().build());
	}

	public ByteArrayInvoker(final Method method, final String url, final ByteArrayConfiguration configuration) {
		this(method, url, null, null, configuration);
	}

	public ByteArrayInvoker(final Method method, final String url, final String queryString) {
		this(method, url, queryString, null, new ByteArrayConfigurationBuilder().build());
	}

	public ByteArrayInvoker(final Method method, final String url, final String queryString, final ByteArrayConfiguration configuration) {
		this(method, url, queryString, null, configuration);
	}

	public ByteArrayInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties) {
		this(method, url, queryString, requestProperties, new ByteArrayConfigurationBuilder().build());
	}

	public ByteArrayInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		super(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected Delegator<byte[], ByteArrayConfiguration> getDelegator() {
		return ByteArrayDelegator.getInstance();
	}
}
