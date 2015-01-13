package com.github.t3t5u.common.http;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface Invoker<V> {
	Future<V> invoke(Executor executor);

	Future<V> invoke(Executor executor, int retryCount);

	Future<V> invoke(Executor executor, int retryCount, long interval, TimeUnit unit);
}
