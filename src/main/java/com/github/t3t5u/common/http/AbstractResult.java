package com.github.t3t5u.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public abstract class AbstractResult<T> implements Result<T> {
	private final T response;
	private final int responseCode;
	private final String responseMessage;
	private final Map<String, List<String>> headerFields;
	private String url;
	private final IOException exception;

	protected AbstractResult(final IOException exception) {
		this(null, -1, null, null, null, exception);
	}

	protected AbstractResult(final T response, final int responseCode, final String responseMessage, final Map<String, List<String>> headerFields, final URL url) {
		this(response, responseCode, responseMessage, headerFields, url, null);
	}

	protected AbstractResult(final T response, final int responseCode, final String responseMessage, final Map<String, List<String>> headerFields, final URL url, final IOException exception) {
		this.response = response;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.headerFields = toLowerCase(headerFields);
		this.url = url != null ? url.toString() : null;
		this.exception = exception;
	}

	@Override
	public T getResponse() {
		return response;
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public String getResponseMessage() {
		return responseMessage;
	}

	@Override
	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	@Override
	public IOException getException() {
		return exception;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public boolean isOk() {
		return responseCode == HttpURLConnection.HTTP_OK;
	}

	@Override
	public boolean isTimeout() {
		return exception instanceof SocketTimeoutException;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toStringExclude(this, "response");
	}

	protected static int getResponseCode(final HttpURLConnection connection) {
		try {
			return connection.getResponseCode();
		} catch (final IOException e) {
			return -1;
		}
	}

	protected static String getResponseMessage(final HttpURLConnection connection) {
		try {
			return connection.getResponseMessage();
		} catch (final IOException e) {
			return null;
		}
	}

	protected static InputStream getInputStream(final HttpURLConnection connection) {
		try {
			return connection.getInputStream();
		} catch (final IOException e) {
			return null;
		}
	}

	private static Map<String, List<String>> toLowerCase(final Map<String, List<String>> source) {
		if (source == null) {
			return null;
		}
		final Map<String, List<String>> destination = new HashMap<String, List<String>>();
		for (final Entry<String, List<String>> entry : source.entrySet()) {
			put(destination, entry);
		}
		return destination;
	}

	private static void put(final Map<String, List<String>> destination, final Entry<String, List<String>> entry) {
		if ((entry == null) || (entry.getKey() == null)) {
			return;
		}
		destination.put(StringUtils.lowerCase(entry.getKey()), entry.getValue());
	}
}
