package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.t3t5u.common.util.CopyProgressListener;
import com.github.t3t5u.common.util.EncodingUtils;
import com.github.t3t5u.common.util.ExtraIOUtils;

public class InputStreamInvokerTest extends InvokerTestCase<InputStream, InputStreamConfiguration, InputStreamConfigurationBuilder> {
	@Test
	public void test() throws Exception {
		test(new ByteArrayInputStream(EncodingUtils.getBytes("てすと")));
	}

	@Override
	protected int getPort() {
		return 18082;
	}

	@Override
	protected String getPathName() {
		return DIR + "/.InputStreamInvokerTest";
	}

	@Override
	protected void write(final InputStream response, final File file) {
		ExtraIOUtils.copy(response, ExtraIOUtils.openOutputStream(file));
		((ByteArrayInputStream) response).reset();
	}

	@Override
	protected InputStreamConfigurationBuilder newConfigurationBuilder(final CopyProgressListener copyProgressListener, final long progress, final int index) {
		return new InputStreamConfigurationBuilder();
	}

	@Override
	protected Invoker<Result<InputStream>> newInvoker(final Method method, final String url, final int index) {
		return new InputStreamInvoker(method, url);
	}

	@Override
	protected Invoker<Result<InputStream>> newInvoker(final Method method, final String url, final InputStreamConfiguration configuration) {
		return new InputStreamInvoker(method, url, configuration);
	}

	@Override
	protected Invoker<Result<InputStream>> newInvoker(final Method method, final String url, final String queryString, final int index) {
		return new InputStreamInvoker(method, url, queryString);
	}

	@Override
	protected Invoker<Result<InputStream>> newInvoker(final Method method, final String url, final String queryString, final InputStreamConfiguration configuration) {
		return new InputStreamInvoker(method, url, queryString, configuration);
	}

	@Override
	protected Invoker<Result<InputStream>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final int index) {
		return new InputStreamInvoker(method, url, queryString, requestProperties);
	}

	@Override
	protected Invoker<Result<InputStream>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return new InputStreamInvoker(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected void assertFuture(final Future<Result<InputStream>> future, final InputStream response, final int index) throws Exception {
		assertThat(IOUtils.contentEquals(future.get().getResponse(), new ByteArrayInputStream(EncodingUtils.getBytes("てすと"))), is(true));
		((ByteArrayInputStream) future.get().getResponse()).reset();
		assertThat(IOUtils.contentEquals(response, new ByteArrayInputStream(EncodingUtils.getBytes("てすと"))), is(true));
		((ByteArrayInputStream) response).reset();
		assertThat(IOUtils.contentEquals(future.get().getResponse(), response), is(true));
		((ByteArrayInputStream) future.get().getResponse()).reset();
		((ByteArrayInputStream) response).reset();
	}
}
