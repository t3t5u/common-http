package com.github.t3t5u.common.http;

import java.util.List;
import java.util.Map;

public class StringInvoker extends AbstractInconvertibleInvoker<String, StringConfiguration> {
	public StringInvoker(final Method method, final String url) {
		this(method, url, null, null, new StringConfigurationBuilder().build());
	}

	public StringInvoker(final Method method, final String url, final StringConfiguration configuration) {
		this(method, url, null, null, configuration);
	}

	public StringInvoker(final Method method, final String url, final String queryString) {
		this(method, url, queryString, null, new StringConfigurationBuilder().build());
	}

	public StringInvoker(final Method method, final String url, final String queryString, final StringConfiguration configuration) {
		this(method, url, queryString, null, configuration);
	}

	public StringInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties) {
		this(method, url, queryString, requestProperties, new StringConfigurationBuilder().build());
	}

	public StringInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		super(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected Delegator<String, StringConfiguration> getDelegator() {
		return StringDelegator.getInstance();
	}
}
