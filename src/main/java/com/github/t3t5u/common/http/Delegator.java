package com.github.t3t5u.common.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

public interface Delegator<T, CONFIGURATION extends Configuration<T>> {
	RunnableFuture<Result<T>> get(String url, String queryString, Map<String, List<String>> requestProperties, CONFIGURATION configuration);

	RunnableFuture<Result<T>> post(String url, String queryString, Map<String, List<String>> requestProperties, CONFIGURATION configuration);

	RunnableFuture<Result<T>> put(String url, String queryString, Map<String, List<String>> requestProperties, CONFIGURATION configuration);

	RunnableFuture<Result<T>> delete(String url, String queryString, Map<String, List<String>> requestProperties, CONFIGURATION configuration);
}
