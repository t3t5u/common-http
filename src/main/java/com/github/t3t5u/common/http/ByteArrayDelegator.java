package com.github.t3t5u.common.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

public class ByteArrayDelegator implements Delegator<byte[], ByteArrayConfiguration> {
	private static final ByteArrayDelegator INSTANCE = new ByteArrayDelegator();

	public static ByteArrayDelegator getInstance() {
		return INSTANCE;
	}

	@Override
	public RunnableFuture<Result<byte[]>> get(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return HttpUtils.getAsByteArrayOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<byte[]>> post(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return HttpUtils.postAsByteArrayOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<byte[]>> put(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return HttpUtils.putAsByteArrayOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<byte[]>> delete(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return HttpUtils.deleteAsByteArrayOrNull(url, queryString, requestProperties, configuration);
	}
}
