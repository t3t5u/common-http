package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.t3t5u.common.util.ConcurrentUtils;
import com.github.t3t5u.common.util.CopyProgressListener;
import com.github.t3t5u.common.util.ExtraIOUtils;

public class FileInvokerTest extends InvokerTestCase<File, FileConfiguration, FileConfigurationBuilder> {
	private static final File IN = new File(DIR + "/.FileInvokerTest.in");
	private static final File[] OUTS = new File[] { new File(DIR + "/.FileInvokerTest.out.0"), new File(DIR + "/.FileInvokerTest.out.1"), new File(DIR + "/.FileInvokerTest.out.2") };
	private static final Logger LOGGER = LoggerFactory.getLogger(FileInvokerTest.class);

	@Test
	public void test() throws Exception {
		ExtraIOUtils.copy(getClass().getResourceAsStream("/test.zip"), ExtraIOUtils.openOutputStream(IN));
		test(IN);
	}

	@Override
	protected int getPort() {
		return 18083;
	}

	@Override
	protected String getPathName() {
		return DIR + "/.FileInvokerTest";
	}

	@Override
	protected void write(final File response, final File file) {
		ExtraIOUtils.copy(ExtraIOUtils.openInputStream(response), ExtraIOUtils.openOutputStream(file));
	}

	@Override
	protected FileConfigurationBuilder newConfigurationBuilder(final CopyProgressListener copyProgressListener, final long progress, final int index) {
		return new FileConfigurationBuilder().setFile(OUTS[index]).setCopyProgressListener(copyProgressListener).setProgress(progress);
	}

	@Override
	protected Invoker<Result<File>> newInvoker(final Method method, final String url, final int index) {
		return new FileInvoker(method, url, OUTS[index]);
	}

	@Override
	protected Invoker<Result<File>> newInvoker(final Method method, final String url, final FileConfiguration configuration) {
		return new FileInvoker(method, url, configuration);
	}

	@Override
	protected Invoker<Result<File>> newInvoker(final Method method, final String url, final String queryString, final int index) {
		return new FileInvoker(method, url, queryString, OUTS[index]);
	}

	@Override
	protected Invoker<Result<File>> newInvoker(final Method method, final String url, final String queryString, final FileConfiguration configuration) {
		return new FileInvoker(method, url, queryString, configuration);
	}

	@Override
	protected Invoker<Result<File>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final int index) {
		return new FileInvoker(method, url, queryString, requestProperties, OUTS[index]);
	}

	@Override
	protected Invoker<Result<File>> newInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return new FileInvoker(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected void assertFuture(final Future<Result<File>> future, final File response, final int index) throws Exception {
		assertThat(future.get().getResponse(), is(OUTS[index]));
		assertThat(response, is(IN));
		assertThat(FileUtils.contentEquals(future.get().getResponse(), response), is(true));
	}

	@Override
	protected boolean onProgress(final long totalCopied, final boolean finished, final Future<Result<File>> future) {
		if ((totalCopied > 10) && future.cancel(true)) {
			LOGGER.info("totalCopied: " + totalCopied);
			return false;
		}
		assertThat(finished, is(false));
		return super.onProgress(totalCopied, finished, future);
	}

	@Override
	protected void assertFuture(final Future<Result<File>> future, final long timeout, final TimeUnit unit, final boolean mayInterruptIfRunning) throws Exception {
		int count = 0;
		while (!future.isCancelled() && ConcurrentUtils.sleepInterruptibly(timeout, unit)) {
			LOGGER.info("count: " + ++count);
		}
		assertThat(future.isCancelled(), is(true));
		super.assertFuture(future, timeout, unit, mayInterruptIfRunning);
	}
}
