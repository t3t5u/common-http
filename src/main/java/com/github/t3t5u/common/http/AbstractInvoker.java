package com.github.t3t5u.common.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.t3t5u.common.util.CollectionUtils;
import com.github.t3t5u.common.util.ConcurrentUtils;

public abstract class AbstractInvoker<V, T, CONFIGURATION extends Configuration<T>> implements Invoker<V> {
	private static final long DEFAULT_INTERVAL = 1000;
	private static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInvoker.class);
	private RunnableFuture<Result<T>> runnableFuture;
	private boolean executed;
	private boolean canceled;

	@Override
	public Future<V> invoke(final Executor executor) {
		return invoke(executor, 1);
	}

	@Override
	public Future<V> invoke(final Executor executor, final int retryCount) {
		return invoke(executor, retryCount, DEFAULT_INTERVAL, DEFAULT_UNIT);
	}

	protected synchronized Future<V> execute(final Executor executor, final Callable<V> callable) {
		if (executed) {
			throw new IllegalStateException();
		}
		executed = true;
		final Future<V> future = ConcurrentUtils.executeOrNull(executor, new FutureTask<V>(callable) {
			@Override
			public boolean cancel(final boolean mayInterruptIfRunning) {
				return BooleanUtils.and(new boolean[] { super.cancel(mayInterruptIfRunning), AbstractInvoker.this.cancel() });
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute: " + future);
		}
		return future;
	}

	protected boolean isExecuted() {
		return executed;
	}

	protected boolean isRetry(final Result<T> result) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isRetry: " + result);
		}
		return result == null;
	}

	protected Result<T> perform() {
		return ConcurrentUtils.getOrNull(ConcurrentUtils.executeOrNull(invoke(getMethod())));
	}

	protected synchronized RunnableFuture<Result<T>> invoke(final Method method) {
		cancel();
		runnableFuture = Method.GET.equals(method) ? get() : Method.POST.equals(method) ? post() : Method.PUT.equals(method) ? put() : Method.DELETE.equals(method) ? delete() : null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("invoke: " + runnableFuture);
		}
		return runnableFuture;
	}

	protected synchronized boolean cancel() {
		canceled = true;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("cancel: " + runnableFuture);
		}
		return ConcurrentUtils.cancel(runnableFuture, true);
	}

	protected boolean isCanceled() {
		return canceled;
	}

	protected abstract Method getMethod();

	protected RunnableFuture<Result<T>> get() {
		return getDelegator().get(getUrl(), getQueryString(), getRequestProperties(), getConfiguration());
	}

	protected RunnableFuture<Result<T>> post() {
		return getDelegator().post(getUrl(), getQueryString(), getRequestProperties(), getConfiguration());
	}

	protected RunnableFuture<Result<T>> put() {
		return getDelegator().put(getUrl(), getQueryString(), getRequestProperties(), getConfiguration());
	}

	protected RunnableFuture<Result<T>> delete() {
		return getDelegator().delete(getUrl(), getQueryString(), getRequestProperties(), getConfiguration());
	}

	protected abstract Delegator<T, CONFIGURATION> getDelegator();

	protected abstract String getUrl();

	protected String getQueryString() {
		return HttpUtils.toQueryString(putParameters(new LinkedHashMap<String, Object>()));
	}

	protected Map<String, Object> putParameters(final Map<String, Object> parameters) {
		return parameters;
	}

	protected Map<String, List<String>> getRequestProperties() {
		return putRequestProperties(new LinkedHashMap<String, List<String>>());
	}

	protected Map<String, List<String>> putRequestProperties(final Map<String, List<String>> requestProperties) {
		return requestProperties;
	}

	protected static void putParameter(final Map<String, Object> parameters, final String name, final Object value) {
		CollectionUtils.putIfNotNull(parameters, name, value);
	}

	protected static void putRequestProperty(final Map<String, List<String>> requestProperties, final String name, final List<String> value) {
		CollectionUtils.putIfNotNull(requestProperties, name, value);
	}

	protected abstract CONFIGURATION getConfiguration();
}
