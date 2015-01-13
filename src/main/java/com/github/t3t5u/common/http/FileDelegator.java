package com.github.t3t5u.common.http;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

public class FileDelegator implements Delegator<File, FileConfiguration> {
	private static final FileDelegator INSTANCE = new FileDelegator();

	public static FileDelegator getInstance() {
		return INSTANCE;
	}

	@Override
	public RunnableFuture<Result<File>> get(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return HttpUtils.getAsFileOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<File>> post(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return HttpUtils.postAsFileOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<File>> put(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return HttpUtils.putAsFileOrNull(url, queryString, requestProperties, configuration);
	}

	@Override
	public RunnableFuture<Result<File>> delete(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return HttpUtils.deleteAsFileOrNull(url, queryString, requestProperties, configuration);
	}
}
