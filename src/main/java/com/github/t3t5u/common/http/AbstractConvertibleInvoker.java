package com.github.t3t5u.common.http;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.t3t5u.common.util.ConcurrentUtils;

public abstract class AbstractConvertibleInvoker<V, T, CONFIGURATION extends Configuration<T>> extends AbstractInvoker<V, T, CONFIGURATION> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConvertibleInvoker.class);

	@Override
	public Future<V> invoke(final Executor executor, final int retryCount, final long interval, final TimeUnit unit) {
		return execute(executor, new Callable<V>() {
			@Override
			public V call() throws Exception {
				return AbstractConvertibleInvoker.this.call(retryCount, interval, unit);
			}
		});
	}

	protected V call(final int retryCount, final long interval, final TimeUnit unit) {
		V result = null;
		int retry = 0;
		do {
			LOGGER.info("retry: " + retry);
			result = perform(perform());
		} while (isRetry(result) && (++retry < retryCount) && !isCanceled() && ConcurrentUtils.sleepInterruptibly(interval, unit));
		return result;
	}

	protected boolean isRetry(final V result) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isRetry: " + result);
		}
		return result == null;
	}

	protected V perform(final Result<T> result) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("perform: " + result);
		}
		return isRetry(result) ? null : convert(result);
	}

	protected V convert(final Result<T> result) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("convert: " + result);
		}
		final T response = result != null ? result.getResponse() : null;
		return result != null ? convert(result, response) : null;
	}

	protected V convert(final Result<T> result, final T response) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("convert: " + response);
		}
		return response != null ? convert(response) : null;
	}

	protected abstract V convert(T response);
}
