package com.github.t3t5u.common.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class InputStreamInvoker extends AbstractInconvertibleInvoker<InputStream, InputStreamConfiguration> {
	public InputStreamInvoker(final Method method, final String url) {
		this(method, url, null, null, new InputStreamConfigurationBuilder().build());
	}

	public InputStreamInvoker(final Method method, final String url, final InputStreamConfiguration configuration) {
		this(method, url, null, null, configuration);
	}

	public InputStreamInvoker(final Method method, final String url, final String queryString) {
		this(method, url, queryString, null, new InputStreamConfigurationBuilder().build());
	}

	public InputStreamInvoker(final Method method, final String url, final String queryString, final InputStreamConfiguration configuration) {
		this(method, url, queryString, null, configuration);
	}

	public InputStreamInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties) {
		this(method, url, queryString, requestProperties, new InputStreamConfigurationBuilder().build());
	}

	public InputStreamInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		super(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected Delegator<InputStream, InputStreamConfiguration> getDelegator() {
		return InputStreamDelegator.getInstance();
	}
}
