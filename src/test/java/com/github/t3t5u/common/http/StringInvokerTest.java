package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.junit.Test;

import com.github.t3t5u.common.util.CopyProgressListener;
import com.github.t3t5u.common.util.ExtraIOUtils;

public class StringInvokerTest extends InvokerTestCase<String, StringConfiguration, StringConfigurationBuilder> {
	@Test
	public void test() throws Exception {
		test("てすと");
	}

	@Override
	protected int getPort() {
		return 18080;
	}

	@Override
	protected String getPathName() {
		return DIR + "/.StringInvokerTest";
	}

	@Override
	protected void write(final String response, final File file) {
		ExtraIOUtils.write(response, file);
	}

	@Override
	protected StringConfigurationBuilder newConfigurationBuilder(final CopyProgressListener copyProgressListener, final long progress, final int index) {
		return new StringConfigurationBuilder();
	}

	@Override
	protected Invoker<Result<String>> newInvoker(final Method method, final String url, final int index) {
		return new StringInvoker(method, url);
	}

	@Override
	protected Invoker<Result<String>> newInvoker(final Method method, final String url, final StringConfiguration configuration) {
		return new StringInvoker(method, url, configuration);
	}

	@Override
	protected Invoker<Result<String>> newInvoker(final Method method, final String url, final String queryString, final int index) {
		return new StringInvoker(method, url, queryString);
	}

	@Override
	protected Invoker<Result<String>> newInvoker(final Method method, final String url, final String queryString, final StringConfiguration configuration) {
		return new StringInvoker(method, url, queryString, configuration);
	}

	@Override
	protected Invoker<Result<String>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final int index) {
		return new StringInvoker(method, url, queryString, requestProperties);
	}

	@Override
	protected Invoker<Result<String>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return new StringInvoker(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected void assertFuture(final Future<Result<String>> future, final String response, final int index) throws Exception {
		assertThat(future.get().getResponse(), is("てすと"));
		assertThat(response, is("てすと"));
		assertThat(future.get().getResponse(), is(response));
	}
}
