package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.junit.Test;

import com.github.t3t5u.common.util.CopyProgressListener;
import com.github.t3t5u.common.util.EncodingUtils;
import com.github.t3t5u.common.util.ExtraIOUtils;

public class ByteArrayInvokerTest extends InvokerTestCase<byte[], ByteArrayConfiguration, ByteArrayConfigurationBuilder> {
	@Test
	public void test() throws Exception {
		test(EncodingUtils.getBytes("てすと"));
	}

	@Override
	protected int getPort() {
		return 18081;
	}

	@Override
	protected String getPathName() {
		return DIR + "/.ByteArrayInvokerTest";
	}

	@Override
	protected void write(final byte[] response, final File file) {
		ExtraIOUtils.write(response, file);
	}

	@Override
	protected ByteArrayConfigurationBuilder newConfigurationBuilder(final CopyProgressListener copyProgressListener, final long progress, final int index) {
		return new ByteArrayConfigurationBuilder();
	}

	@Override
	protected Invoker<Result<byte[]>> newInvoker(final Method method, final String url, final int index) {
		return new ByteArrayInvoker(method, url);
	}

	@Override
	protected Invoker<Result<byte[]>> newInvoker(final Method method, final String url, final ByteArrayConfiguration configuration) {
		return new ByteArrayInvoker(method, url, configuration);
	}

	@Override
	protected Invoker<Result<byte[]>> newInvoker(final Method method, final String url, final String queryString, final int index) {
		return new ByteArrayInvoker(method, url, queryString);
	}

	@Override
	protected Invoker<Result<byte[]>> newInvoker(final Method method, final String url, final String queryString, final ByteArrayConfiguration configuration) {
		return new ByteArrayInvoker(method, url, queryString, configuration);
	}

	@Override
	protected Invoker<Result<byte[]>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final int index) {
		return new ByteArrayInvoker(method, url, queryString, requestProperties);
	}

	@Override
	protected Invoker<Result<byte[]>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return new ByteArrayInvoker(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected void assertFuture(final Future<Result<byte[]>> future, final byte[] response, final int index) throws Exception {
		assertThat(future.get().getResponse(), is(EncodingUtils.getBytes("てすと")));
		assertThat(response, is(EncodingUtils.getBytes("てすと")));
		assertThat(future.get().getResponse(), is(response));
	}
}
