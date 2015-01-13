package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.mockito.Matchers;

import com.github.t3t5u.common.util.ConcurrentUtils;
import com.github.t3t5u.common.util.CopyProgressListener;
import com.github.t3t5u.common.util.ExtraFileUtils;
import com.github.t3t5u.common.util.ExtraIOUtils;

public abstract class InvokerTestCase<T, CONFIGURATION extends Configuration<T>, CONFIGURATION_BUILDER extends ConfigurationBuilder<T, CONFIGURATION, CONFIGURATION_BUILDER>> {
	protected static final String DIR = "target";
	private static final int MAX_THREADS = 3 * 3;
	private static final Map<String, Object> PARAMETERS = new LinkedHashMap<String, Object>();
	private static final Map<String, List<String>> REQUEST_PROPERTIES = new LinkedHashMap<String, List<String>>();
	static {
		PARAMETERS.put("parameter1", 1);
		PARAMETERS.put("parameter2", "あ");
		PARAMETERS.put("parameter3", new int[] { 2, 3, 4 });
		PARAMETERS.put("parameter4", new String[] { "い", "う", "え" });
		PARAMETERS.put("parameter5", Arrays.asList(5, 6, 7));
		PARAMETERS.put("parameter6", Arrays.asList("お", "か", "き"));
		REQUEST_PROPERTIES.put("requestProperty1", Arrays.asList("a"));
		REQUEST_PROPERTIES.put("requestProperty2", Arrays.asList("b", "c"));
		REQUEST_PROPERTIES.put("requestProperty3", Arrays.asList("d", "e", "f"));
	}
	private Server server;
	private TestHandler handler;
	private ThreadPool queuedThreadPool;
	private ExecutorService cachedThreadPool;

	protected void before(final Method method, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final long timeout, final TimeUnit unit, final T response, final int responseCode, final File file) throws Exception {
		ExtraFileUtils.mkdirs(DIR);
		queuedThreadPool = new QueuedThreadPool(MAX_THREADS);
		cachedThreadPool = Executors.newCachedThreadPool();
		write(response, file);
		handler = new TestHandler(method.toString(), parameters, requestProperties, timeout, unit, responseCode, file);
		server = new Server(queuedThreadPool);
		final ServerConnector connector = new ServerConnector(server);
		connector.setHost("localhost");
		connector.setPort(getPort());
		server.addConnector(connector);
		server.setHandler(handler);
		server.start();
	}

	protected void after() throws Exception {
		destroy(server);
		destroy(handler);
		shutdown(cachedThreadPool);
		queuedThreadPool.join();
	}

	private static void shutdown(final ExecutorService executorService) throws Exception {
		executorService.shutdownNow();
		if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
			return;
		}
		// もう一度試す
		executorService.shutdownNow();
		if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
			throw new Exception("await termination failed.");
		}
	}

	private static void destroy(final Server server) throws Exception {
		if (server == null) {
			return;
		}
		server.stop();
		destroy(server.getHandler());
		server.setHandler(null);
		server.join();
		server.destroy();
	}

	private static void destroy(final Handler handler) throws Exception {
		if (handler == null) {
			return;
		}
		handler.stop();
		handler.destroy();
	}

	protected Executor getExecutor() {
		return cachedThreadPool;
	}

	protected String getUrl() {
		return "http://localhost:" + getPort() + "/";
	}

	protected abstract int getPort();

	protected File getFile() {
		return new File(getPathName());
	}

	protected abstract String getPathName();

	protected void test(final T response) throws Exception {
		test(Method.GET, response, HttpURLConnection.HTTP_OK);
		test(Method.GET, response, HttpURLConnection.HTTP_BAD_REQUEST);
		test(Method.GET, response, HttpURLConnection.HTTP_INTERNAL_ERROR);
		test(Method.POST, response, HttpURLConnection.HTTP_OK);
		test(Method.POST, response, HttpURLConnection.HTTP_BAD_REQUEST);
		test(Method.POST, response, HttpURLConnection.HTTP_INTERNAL_ERROR);
		test(Method.PUT, response, HttpURLConnection.HTTP_OK);
		test(Method.PUT, response, HttpURLConnection.HTTP_BAD_REQUEST);
		test(Method.PUT, response, HttpURLConnection.HTTP_INTERNAL_ERROR);
		test(Method.DELETE, response, HttpURLConnection.HTTP_OK);
		test(Method.DELETE, response, HttpURLConnection.HTTP_BAD_REQUEST);
		test(Method.DELETE, response, HttpURLConnection.HTTP_INTERNAL_ERROR);
	}

	protected void test(final Method method, final T response, final int responseCode) throws Exception {
		final String url = getUrl();
		final File file = getFile();
		test(method, url, null, null, response, responseCode, file);
		test(method, url, PARAMETERS, null, response, responseCode, file);
		test(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testRetry(method, url, null, null, response, responseCode, file);
		testRetry(method, url, PARAMETERS, null, response, responseCode, file);
		testRetry(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testRetryInterval(method, url, null, null, response, responseCode, file);
		testRetryInterval(method, url, PARAMETERS, null, response, responseCode, file);
		testRetryInterval(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testCancel(method, url, null, null, response, responseCode, file);
		testCancel(method, url, PARAMETERS, null, response, responseCode, file);
		testCancel(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testCancelRetry(method, url, null, null, response, responseCode, file);
		testCancelRetry(method, url, PARAMETERS, null, response, responseCode, file);
		testCancelRetry(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testCancelRetryInterval(method, url, null, null, response, responseCode, file);
		testCancelRetryInterval(method, url, PARAMETERS, null, response, responseCode, file);
		testCancelRetryInterval(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testTimeout(method, url, null, null, response, responseCode, file);
		testTimeout(method, url, PARAMETERS, null, response, responseCode, file);
		testTimeout(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testTimeoutRetry(method, url, null, null, response, responseCode, file);
		testTimeoutRetry(method, url, PARAMETERS, null, response, responseCode, file);
		testTimeoutRetry(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
		testTimeoutRetryInterval(method, url, null, null, response, responseCode, file);
		testTimeoutRetryInterval(method, url, PARAMETERS, null, response, responseCode, file);
		testTimeoutRetryInterval(method, url, PARAMETERS, REQUEST_PROPERTIES, response, responseCode, file);
	}

	protected void test(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 0));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 1));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 2));
		assertFuture(futureList, requestProperties, response, responseCode, file, 0);
		assertFuture(futureList, requestProperties, response, responseCode, file, 1);
		assertFuture(futureList, requestProperties, response, responseCode, file, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(3));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final int index) {
		return newInvoker(method, url, parameters, requestProperties, null, index).invoke(executorList.get(index));
	}

	protected void testRetry(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 0, 2));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 1, 2));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 2, 2));
		assertFuture(futureList, requestProperties, response, responseCode, file, 0);
		assertFuture(futureList, requestProperties, response, responseCode, file, 1);
		assertFuture(futureList, requestProperties, response, responseCode, file, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(3));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final int index, final int retryCount) {
		return newInvoker(method, url, parameters, requestProperties, null, index).invoke(executorList.get(index), retryCount);
	}

	protected void testRetryInterval(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 0, 3, 100L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 1, 3, 100L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 2, 3, 100L, TimeUnit.MILLISECONDS));
		assertFuture(futureList, requestProperties, response, responseCode, file, 0);
		assertFuture(futureList, requestProperties, response, responseCode, file, 1);
		assertFuture(futureList, requestProperties, response, responseCode, file, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(3));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final int index, final int retryCount, final long interval, final TimeUnit unit) {
		return newInvoker(method, url, parameters, requestProperties, null, index).invoke(executorList.get(index), retryCount, interval, unit);
	}

	protected void testCancel(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 0));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 1));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 2));
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 0);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 1);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(lessThanOrEqualTo(3)));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final List<Future<Result<T>>> futureList, final int index) {
		return newInvoker(method, url, parameters, requestProperties, newConfigurationBuilder(new CopyProgressListener() {
			@Override
			public boolean onProgress(final long totalCopied, final boolean finished) {
				return InvokerTestCase.this.onProgress(totalCopied, finished, futureList.get(index));
			}

			@Override
			public void onFailure(final IOException e) {
			}
		}, 5, index).build(), index).invoke(executorList.get(index));
	}

	protected void testCancelRetry(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 0, 2));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 1, 2));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 2, 2));
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 0);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 1);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(CoreMatchers.any(Integer.class)));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final List<Future<Result<T>>> futureList, final int index, final int retryCount) {
		return newInvoker(method, url, parameters, requestProperties, newConfigurationBuilder(new CopyProgressListener() {
			@Override
			public boolean onProgress(final long totalCopied, final boolean finished) {
				return InvokerTestCase.this.onProgress(totalCopied, finished, futureList.get(index));
			}

			@Override
			public void onFailure(final IOException e) {
			}
		}, 5, index).build(), index).invoke(executorList.get(index), retryCount);
	}

	protected void testCancelRetryInterval(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 0, 3, 100L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 1, 3, 100L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, futureList, 2, 3, 100L, TimeUnit.MILLISECONDS));
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 0);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 1);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, true, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(CoreMatchers.any(Integer.class)));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final List<Future<Result<T>>> futureList, final int index, final int retryCount, final long interval,
			final TimeUnit unit) {
		return newInvoker(method, url, parameters, requestProperties, newConfigurationBuilder(new CopyProgressListener() {
			@Override
			public boolean onProgress(final long totalCopied, final boolean finished) {
				return InvokerTestCase.this.onProgress(totalCopied, finished, futureList.get(index));
			}

			@Override
			public void onFailure(final IOException e) {
			}
		}, 5, index).build(), index).invoke(executorList.get(index), retryCount, interval, unit);
	}

	protected void testTimeout(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 0, 10L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 1, 10L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 2, 10L, TimeUnit.MILLISECONDS));
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 0);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 1);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(3));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final int index, final long readTimeout, final TimeUnit unit) {
		return newInvoker(method, url, parameters, requestProperties, newConfigurationBuilder(null, 0, index).setReadTimeout(readTimeout, unit).build(), index).invoke(executorList.get(index));
	}

	protected void testTimeoutRetry(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 0, 10L, 2, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 1, 10L, 2, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 2, 10L, 2, TimeUnit.MILLISECONDS));
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 0);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 1);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(3));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final int index, final long readTimeout, final int retryCount, final TimeUnit unit) {
		return newInvoker(method, url, parameters, requestProperties, newConfigurationBuilder(null, 0, index).setReadTimeout(readTimeout, unit).build(), index).invoke(executorList.get(index), retryCount);
	}

	protected void testTimeoutRetryInterval(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final T response, final int responseCode, final File file) throws Exception {
		before(method, parameters, requestProperties, 100L, TimeUnit.MILLISECONDS, response, responseCode, file);
		final List<Executor> executorList = new ArrayList<Executor>();
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		executorList.add(spy(getExecutor()));
		final List<Future<Result<T>>> futureList = new ArrayList<Future<Result<T>>>();
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 0, 10L, 3, 100L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 1, 10L, 3, 100L, TimeUnit.MILLISECONDS));
		futureList.add(invoke(method, url, parameters, requestProperties, executorList, 2, 10L, 3, 100L, TimeUnit.MILLISECONDS));
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 0);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 1);
		assertFuture(futureList, 10L, TimeUnit.MILLISECONDS, 2);
		verifyExecutor(executorList, 0);
		verifyExecutor(executorList, 1);
		verifyExecutor(executorList, 2);
		assertHandler(is(3));
		after();
	}

	protected Future<Result<T>> invoke(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final List<Executor> executorList, final int index, final long readTimeout, final int retryCount, final long interval, final TimeUnit unit) {
		return newInvoker(method, url, parameters, requestProperties, newConfigurationBuilder(null, 0, index).setReadTimeout(readTimeout, unit).build(), index).invoke(executorList.get(index), retryCount, interval, unit);
	}

	protected abstract void write(T response, File file);

	protected abstract CONFIGURATION_BUILDER newConfigurationBuilder(CopyProgressListener copyProgressListener, long progress, int index);

	protected Invoker<Result<T>> newInvoker(final Method method, final String url, final Map<String, Object> parameters, final Map<String, List<String>> requestProperties, final CONFIGURATION configuration, final int index) {
		if ((parameters != null) && (requestProperties != null) && (configuration != null)) {
			return newInvoker(method, url, HttpUtils.toQueryString(parameters), requestProperties, configuration);
		} else if ((parameters != null) && (requestProperties != null)) {
			return newInvoker(method, url, HttpUtils.toQueryString(parameters), requestProperties, index);
		} else if ((parameters != null) && (configuration != null)) {
			return newInvoker(method, url, HttpUtils.toQueryString(parameters), configuration);
		} else if (parameters != null) {
			return newInvoker(method, url, HttpUtils.toQueryString(parameters), index);
		} else if (configuration != null) {
			return newInvoker(method, url, configuration);
		} else {
			return newInvoker(method, url, index);
		}
	}

	protected abstract Invoker<Result<T>> newInvoker(Method method, String url, int index);

	protected abstract Invoker<Result<T>> newInvoker(Method method, String url, CONFIGURATION configuration);

	protected abstract Invoker<Result<T>> newInvoker(Method method, String url, String queryString, int index);

	protected abstract Invoker<Result<T>> newInvoker(Method method, String url, String queryString, CONFIGURATION configuration);

	protected abstract Invoker<Result<T>> newInvoker(Method method, String url, String queryString, Map<String, List<String>> requestProperties, int index);

	protected abstract Invoker<Result<T>> newInvoker(Method method, String url, String queryString, Map<String, List<String>> requestProperties, CONFIGURATION configuration);

	protected void assertFuture(final List<Future<Result<T>>> futureList, final Map<?, ?> requestProperties, final T response, final int responseCode, final File file, final int index) throws Exception {
		assertFuture(futureList.get(index), requestProperties, response, responseCode, file, index);
	}

	protected void assertFuture(final Future<Result<T>> future, final Map<?, ?> requestProperties, final T response, final int responseCode, final File file, final int index) throws Exception {
		assertFuture(future, response, index);
		assertThat(future.get().getResponseCode(), is(responseCode));
		assertThat(future.get().isOk(), is(responseCode == HttpURLConnection.HTTP_OK));
		assertThat(future.get().isTimeout(), is(false));
		assertThat(future.get().getException(), is(nullValue()));
		assertThat(future.get().getHeaderFields().get("content-length").get(0), is(String.valueOf(ExtraIOUtils.copy(ExtraIOUtils.openInputStream(file), NullOutputStream.NULL_OUTPUT_STREAM))));
		if (requestProperties == null) {
			return;
		}
		assertThat(future.get().getHeaderFields().get("requestproperty1").get(0), is("a"));
		assertThat(future.get().getHeaderFields().get("requestproperty2").get(1), is("b"));
		assertThat(future.get().getHeaderFields().get("requestproperty2").get(0), is("c"));
		assertThat(future.get().getHeaderFields().get("requestproperty3").get(2), is("d"));
		assertThat(future.get().getHeaderFields().get("requestproperty3").get(1), is("e"));
		assertThat(future.get().getHeaderFields().get("requestproperty3").get(0), is("f"));
		assertThat(future.get().getHeaderFields().get("requestproperty2").get(0), anyOf(is("b"), is("c")));
		assertThat(future.get().getHeaderFields().get("requestproperty2").get(1), anyOf(is("b"), is("c")));
		assertThat(future.get().getHeaderFields().get("requestproperty3").get(0), anyOf(is("d"), is("e"), is("f")));
		assertThat(future.get().getHeaderFields().get("requestproperty3").get(1), anyOf(is("d"), is("e"), is("f")));
		assertThat(future.get().getHeaderFields().get("requestproperty3").get(2), anyOf(is("d"), is("e"), is("f")));
	}

	protected abstract void assertFuture(Future<Result<T>> future, T response, int index) throws Exception;

	protected boolean onProgress(final long totalCopied, final boolean finished, final Future<Result<T>> future) {
		return true;
	}

	protected void assertFuture(final List<Future<Result<T>>> futureList, final long timeout, final TimeUnit unit, final boolean mayInterruptIfRunning, final int index) throws Exception {
		assertFuture(futureList.get(index), timeout, unit, mayInterruptIfRunning);
	}

	protected void assertFuture(final Future<Result<T>> future, final long timeout, final TimeUnit unit, final boolean mayInterruptIfRunning) throws Exception {
		ConcurrentUtils.sleepInterruptibly(timeout, unit);
		future.cancel(mayInterruptIfRunning);
		assertThat(future.isCancelled(), is(true));
	}

	protected void assertFuture(final List<Future<Result<T>>> futureList, final long timeout, final TimeUnit unit, final int index) throws Exception {
		assertFuture(futureList.get(index), timeout, unit);
	}

	protected void assertFuture(final Future<Result<T>> future, final long timeout, final TimeUnit unit) throws Exception {
		ConcurrentUtils.sleepInterruptibly(timeout, unit);
		assertThat(future.get().getResponse(), is(nullValue()));
		assertThat(future.get().isOk(), is(false));
		assertThat(future.get().isTimeout(), is(true));
		assertThat(future.get().getException(), is(instanceOf(SocketTimeoutException.class)));
	}

	protected void verifyExecutor(final List<Executor> executorList, final int index) {
		verify(executorList.get(index), times(1)).execute(Matchers.any(Runnable.class));
	}

	protected void assertHandler(final Matcher<Integer> matcher) {
		assertThat(handler.getHandles(), matcher);
	}
}
