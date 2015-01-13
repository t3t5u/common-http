package com.github.t3t5u.common.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

public class InputStreamDelegator implements Delegator<InputStream, InputStreamConfiguration> {
	private static final InputStreamDelegator INSTANCE = new InputStreamDelegator();

	public static InputStreamDelegator getInstance() {
		return INSTANCE;
	}

	@Override
	public RunnableFuture<Result<InputStream>> get(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return HttpUtils.getAsInputStreamOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<InputStream>> post(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return HttpUtils.postAsInputStreamOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<InputStream>> put(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return HttpUtils.putAsInputStreamOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<InputStream>> delete(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return HttpUtils.deleteAsInputStreamOrNull(url, queryString, requestProperties, configuration);
	}
}
