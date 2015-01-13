package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.github.t3t5u.common.util.ConcurrentUtils;
import com.github.t3t5u.common.util.ExtraArrayUtils;
import com.github.t3t5u.common.util.ExtraIOUtils;

public class TestHandler extends AbstractHandler {
	private final AtomicInteger handles = new AtomicInteger();
	private final String method;
	private final Map<String, Object> parameters;
	private final Map<String, List<String>> headers;
	private final long timeout;
	private final TimeUnit unit;
	private final int status;
	private final File file;
	private final int contentLength;

	TestHandler(final String method, final Map<String, Object> parameters, final Map<String, List<String>> headers, final long timeout, final TimeUnit unit, final int status, final File file) {
		this.method = method;
		this.parameters = parameters;
		this.headers = headers;
		this.timeout = timeout;
		this.unit = unit;
		this.status = status;
		this.file = file;
		contentLength = (int) ExtraIOUtils.copy(ExtraIOUtils.openInputStream(file), NullOutputStream.NULL_OUTPUT_STREAM);
	}

	int getHandles() {
		return handles.get();
	}

	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		handles.addAndGet(1);
		assertThat(request.getMethod(), is(method));
		for (final Entry<String, Object> entry : parameters != null ? parameters.entrySet() : Collections.<Entry<String, Object>> emptySet()) {
			assertParameter(request, entry);
		}
		for (final Entry<String, List<String>> entry : headers != null ? headers.entrySet() : Collections.<Entry<String, List<String>>> emptySet()) {
			assertHeader(request, response, entry);
		}
		ConcurrentUtils.sleepInterruptibly(timeout, unit);
		baseRequest.setHandled(true);
		response.setStatus(status);
		response.setContentLength(contentLength);
		ExtraIOUtils.copy(ExtraIOUtils.openInputStream(file), response.getOutputStream());
	}

	private static void assertParameter(final HttpServletRequest request, final Entry<String, Object> entry) {
		final String name = entry.getKey();
		final Object value = entry.getValue();
		final Object[] values = ExtraArrayUtils.toObjectArray(value);
		if (values != null) {
			assertParameter(request, name, values);
		} else if (value instanceof List<?>) {
			assertParameter(request, name, (List<?>) value);
		} else {
			assertParameter(request, name, value);
		}
	}

	private static void assertParameter(final HttpServletRequest request, final String name, final Object[] values) {
		for (int index = 0; index < values.length; index++) {
			assertParameter(request, name, values, index);
		}
	}

	private static void assertParameter(final HttpServletRequest request, final String name, final Object[] values, final int index) {
		assertThat(name, request.getParameterValues(name)[index], is(String.valueOf(values[index])));
	}

	private static void assertParameter(final HttpServletRequest request, final String name, final List<?> values) {
		for (int index = 0; index < values.size(); index++) {
			assertParameter(request, name, values, index);
		}
	}

	private static void assertParameter(final HttpServletRequest request, final String name, final List<?> values, final int index) {
		assertThat(name, request.getParameterValues(name)[index], is(String.valueOf(values.get(index))));
	}

	private static void assertParameter(final HttpServletRequest request, final String name, final Object value) {
		assertThat(name, request.getParameter(name), is(String.valueOf(value)));
	}

	private static void assertHeader(final HttpServletRequest request, final HttpServletResponse response, final Entry<String, List<String>> entry) {
		final String name = entry.getKey();
		final List<String> values = entry.getValue();
		for (int index = 0; index < values.size(); index++) {
			assertHeader(request, response, name, values, index);
		}
	}

	private static void assertHeader(final HttpServletRequest request, final HttpServletResponse response, final String name, final List<String> values, final int index) {
		final String value = values.get(index);
		assertThat(name, Collections.list(request.getHeaders(name)).get(index), is(value));
		response.addHeader(name, value);
	}
}
