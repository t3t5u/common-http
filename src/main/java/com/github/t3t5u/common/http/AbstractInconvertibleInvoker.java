package com.github.t3t5u.common.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.t3t5u.common.util.ConcurrentUtils;

public abstract class AbstractInconvertibleInvoker<T, CONFIGURATION extends Configuration<T>> extends AbstractInvoker<Result<T>, T, CONFIGURATION> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInconvertibleInvoker.class);
	private final Method method;
	private final String url;
	private final String queryString;
	private final Map<String, List<String>> requestProperties;
	private final CONFIGURATION configuration;

	protected AbstractInconvertibleInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final CONFIGURATION configuration) {
		this.method = method;
		this.url = url;
		this.queryString = queryString;
		this.requestProperties = requestProperties;
		this.configuration = configuration;
	}

	@Override
	public Future<Result<T>> invoke(final Executor executor, final int retryCount, final long interval, final TimeUnit unit) {
		return execute(executor, new Callable<Result<T>>() {
			@Override
			public Result<T> call() throws Exception {
				return AbstractInconvertibleInvoker.this.call(retryCount, interval, unit);
			}
		});
	}

	protected Result<T> call(final int retryCount, final long interval, final TimeUnit unit) {
		Result<T> result = null;
		int retry = 0;
		do {
			LOGGER.info("retry: " + retry);
			result = perform();
		} while (isRetry(result) && (++retry < retryCount) && !isCanceled() && ConcurrentUtils.sleepInterruptibly(interval, unit));
		return result;
	}

	@Override
	protected Method getMethod() {
		return method;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected String getQueryString() {
		return queryString;
	}

	@Override
	protected Map<String, List<String>> getRequestProperties() {
		return requestProperties;
	}

	@Override
	protected CONFIGURATION getConfiguration() {
		return configuration;
	}
}
