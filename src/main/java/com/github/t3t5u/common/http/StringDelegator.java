package com.github.t3t5u.common.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

public class StringDelegator implements Delegator<String, StringConfiguration> {
	private static final StringDelegator INSTANCE = new StringDelegator();

	public static StringDelegator getInstance() {
		return INSTANCE;
	}

	@Override
	public RunnableFuture<Result<String>> get(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return HttpUtils.getOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<String>> post(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return HttpUtils.postOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<String>> put(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return HttpUtils.putOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<String>> delete(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return HttpUtils.deleteOrNull(url, queryString, requestProperties, configuration);
	}
}
