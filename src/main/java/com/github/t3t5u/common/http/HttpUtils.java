package com.github.t3t5u.common.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.t3t5u.common.util.EncodingUtils;
import com.github.t3t5u.common.util.ExtraArrayUtils;
import com.github.t3t5u.common.util.ExtraIOUtils;
import com.google.common.net.HttpHeaders;

public final class HttpUtils {
	private static final Set<Entry<String, List<String>>> EMPTY_SET = Collections.emptySet();
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

	private HttpUtils() {
	}

	public static Map<String, List<String>> fromQueryString(final String queryString) {
		final Map<String, List<String>> parameters = new LinkedHashMap<String, List<String>>();
		if (StringUtils.isBlank(queryString)) {
			return parameters;
		}
		for (final String parameter : queryString.split("&")) {
			put(parameters, parameter);
		}
		return parameters;
	}

	private static void put(final Map<String, List<String>> parameters, final String parameter) {
		if (StringUtils.isBlank(parameter)) {
			return;
		}
		final String[] kv = parameter.split("=", 2);
		final String key = kv.length > 0 ? EncodingUtils.decodeUrl(kv[0]) : "";
		if (StringUtils.isBlank(key)) {
			return;
		}
		final boolean containsKey = parameters.containsKey(key);
		final List<String> values = containsKey ? parameters.get(key) : new ArrayList<String>();
		values.add(kv.length > 1 ? EncodingUtils.decodeUrl(kv[1]) : "");
		if (containsKey) {
			return;
		}
		parameters.put(key, values);
	}

	public static String toQueryString(final Map<?, ?> parameters) {
		final StringBuilder builder = new StringBuilder();
		for (final Entry<?, ?> entry : parameters.entrySet()) {
			append(builder, entry);
		}
		return builder.toString();
	}

	private static void append(final StringBuilder builder, final Entry<?, ?> entry) {
		final Object key = entry.getKey();
		final Object value = entry.getValue();
		final Object[] values = ExtraArrayUtils.toObjectArray(value);
		if (values != null) {
			append(builder, key, values);
		} else if (value instanceof List<?>) {
			append(builder, key, (List<?>) value);
		} else {
			append(builder, key, value);
		}
	}

	private static void append(final StringBuilder builder, final Object key, final Object[] values) {
		for (final Object value : values) {
			append(builder, key, value);
		}
	}

	private static void append(final StringBuilder builder, final Object key, final List<?> values) {
		for (final Object value : values) {
			append(builder, key, value);
		}
	}

	private static void append(final StringBuilder builder, final Object key, final Object value) {
		builder.append(builder.length() == 0 ? "" : "&").append(EncodingUtils.encodeUrl(key)).append("=").append(EncodingUtils.encodeUrl(value));
	}

	public static RunnableFuture<Result<File>> getAsFileOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		try {
			return getAsFile(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("getAsFileOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<File>> getAsFile(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return asFile(Method.GET, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<InputStream>> getAsInputStreamOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		try {
			return getAsInputStream(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("getAsInputStreamOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<InputStream>> getAsInputStream(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return asInputStream(Method.GET, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<byte[]>> getAsByteArrayOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		try {
			return getAsByteArray(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("getAsByteArrayOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<byte[]>> getAsByteArray(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return asByteArray(Method.GET, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<String>> getOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		try {
			return get(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("getOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<String>> get(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return asString(Method.GET, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<File>> postAsFileOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		try {
			return postAsFile(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("postAsFileOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<File>> postAsFile(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return asFile(Method.POST, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<InputStream>> postAsInputStreamOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		try {
			return postAsInputStream(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("postAsInputStreamOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<InputStream>> postAsInputStream(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return asInputStream(Method.POST, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<byte[]>> postAsByteArrayOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		try {
			return postAsByteArray(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("postAsByteArrayOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<byte[]>> postAsByteArray(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return asByteArray(Method.POST, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<String>> postOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		try {
			return post(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("postOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<String>> post(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return asString(Method.POST, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<File>> putAsFileOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		try {
			return putAsFile(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("putAsFileOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<File>> putAsFile(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return asFile(Method.PUT, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<InputStream>> putAsInputStreamOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		try {
			return putAsInputStream(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("putAsInputStreamOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<InputStream>> putAsInputStream(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return asInputStream(Method.PUT, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<byte[]>> putAsByteArrayOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		try {
			return putAsByteArray(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("putAsByteArrayOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<byte[]>> putAsByteArray(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return asByteArray(Method.PUT, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<String>> putOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		try {
			return put(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("putOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<String>> put(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return asString(Method.PUT, true, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<File>> deleteAsFileOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		try {
			return deleteAsFile(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("deleteAsFileOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<File>> deleteAsFile(final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		return asFile(Method.DELETE, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<InputStream>> deleteAsInputStreamOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		try {
			return deleteAsInputStream(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("deleteAsInputStreamOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<InputStream>> deleteAsInputStream(final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		return asInputStream(Method.DELETE, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<byte[]>> deleteAsByteArrayOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		try {
			return deleteAsByteArray(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("deleteAsByteArrayOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<byte[]>> deleteAsByteArray(final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		return asByteArray(Method.DELETE, false, url, queryString, requestProperties, configuration);
	}

	public static RunnableFuture<Result<String>> deleteOrNull(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		try {
			return delete(url, queryString, requestProperties, configuration);
		} catch (final Throwable t) {
			LOGGER.info("deleteOrNull", t);
			return null;
		}
	}

	public static RunnableFuture<Result<String>> delete(final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		return asString(Method.DELETE, false, url, queryString, requestProperties, configuration);
	}

	private static RunnableFuture<Result<File>> asFile(final Method method, final boolean doOutput, final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		try {
			return getRunnableFuture(openConnection(method, doOutput, url, queryString, requestProperties, configuration), doOutput, queryString, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asFile", e);
			return getRunnableFuture(new FileResult(e));
		}
	}

	private static Result<File> asFile(final HttpURLConnection connection, final boolean doOutput, final String queryString, final FileConfiguration configuration) {
		if (connection == null) {
			return null;
		}
		try {
			connect(connection, doOutput, queryString);
			return new FileResult(connection, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asFile", e);
			return new FileResult(connection, configuration, e);
		} finally {
			disconnect(connection);
		}
	}

	private static RunnableFuture<Result<InputStream>> asInputStream(final Method method, final boolean doOutput, final String url, final String queryString, final Map<String, List<String>> requestProperties, final InputStreamConfiguration configuration) {
		try {
			return getRunnableFuture(openConnection(method, doOutput, url, queryString, requestProperties, configuration), doOutput, queryString, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asInputStream", e);
			return getRunnableFuture(new InputStreamResult(e));
		}
	}

	private static Result<InputStream> asInputStream(final HttpURLConnection connection, final boolean doOutput, final String queryString, final InputStreamConfiguration configuration) {
		if (connection == null) {
			return null;
		}
		try {
			connect(connection, doOutput, queryString);
			return new InputStreamResult(connection, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asInputStream", e);
			return new InputStreamResult(connection, configuration, e);
		} finally {
			disconnect(connection);
		}
	}

	private static RunnableFuture<Result<byte[]>> asByteArray(final Method method, final boolean doOutput, final String url, final String queryString, final Map<String, List<String>> requestProperties, final ByteArrayConfiguration configuration) {
		try {
			return getRunnableFuture(openConnection(method, doOutput, url, queryString, requestProperties, configuration), doOutput, queryString, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asByteArray", e);
			return getRunnableFuture(new ByteArrayResult(e));
		}
	}

	private static Result<byte[]> asByteArray(final HttpURLConnection connection, final boolean doOutput, final String queryString, final ByteArrayConfiguration configuration) {
		if (connection == null) {
			return null;
		}
		try {
			connect(connection, doOutput, queryString);
			return new ByteArrayResult(connection, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asByteArray", e);
			return new ByteArrayResult(connection, configuration, e);
		} finally {
			disconnect(connection);
		}
	}

	private static RunnableFuture<Result<String>> asString(final Method method, final boolean doOutput, final String url, final String queryString, final Map<String, List<String>> requestProperties, final StringConfiguration configuration) {
		try {
			return getRunnableFuture(openConnection(method, doOutput, url, queryString, requestProperties, configuration), doOutput, queryString, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asString", e);
			return getRunnableFuture(new StringResult(e));
		}
	}

	private static Result<String> asString(final HttpURLConnection connection, final boolean doOutput, final String queryString, final StringConfiguration configuration) {
		if (connection == null) {
			return null;
		}
		try {
			connect(connection, doOutput, queryString);
			return new StringResult(connection, configuration);
		} catch (final IOException e) {
			LOGGER.warn("asString", e);
			return new StringResult(connection, configuration, e);
		} finally {
			disconnect(connection);
		}
	}

	private static void cancel(final HttpURLConnection connection) {
		if (connection != null) {
			disconnect(connection);
		}
	}

	private static RunnableFuture<Result<File>> getRunnableFuture(final HttpURLConnection connection, final boolean doOutput, final String queryString, final FileConfiguration configuration) {
		return getRunnableFuture(new Callable<Result<File>>() {
			@Override
			public Result<File> call() throws Exception {
				return asFile(connection, doOutput, queryString, configuration);
			}
		}, connection);
	}

	private static RunnableFuture<Result<InputStream>> getRunnableFuture(final HttpURLConnection connection, final boolean doOutput, final String queryString, final InputStreamConfiguration configuration) {
		return getRunnableFuture(new Callable<Result<InputStream>>() {
			@Override
			public Result<InputStream> call() throws Exception {
				return asInputStream(connection, doOutput, queryString, configuration);
			}
		}, connection);
	}

	private static RunnableFuture<Result<byte[]>> getRunnableFuture(final HttpURLConnection connection, final boolean doOutput, final String queryString, final ByteArrayConfiguration configuration) {
		return getRunnableFuture(new Callable<Result<byte[]>>() {
			@Override
			public Result<byte[]> call() throws Exception {
				return asByteArray(connection, doOutput, queryString, configuration);
			}
		}, connection);
	}

	private static RunnableFuture<Result<String>> getRunnableFuture(final HttpURLConnection connection, final boolean doOutput, final String queryString, final StringConfiguration configuration) {
		return getRunnableFuture(new Callable<Result<String>>() {
			@Override
			public Result<String> call() throws Exception {
				return asString(connection, doOutput, queryString, configuration);
			}
		}, connection);
	}

	private static <T> RunnableFuture<Result<T>> getRunnableFuture(final Callable<Result<T>> callable, final HttpURLConnection connection) {
		return new FutureTask<Result<T>>(callable) {
			@Override
			public boolean cancel(final boolean mayInterruptIfRunning) {
				HttpUtils.cancel(connection);
				return super.cancel(mayInterruptIfRunning);
			}
		};
	}

	private static <T> RunnableFuture<Result<T>> getRunnableFuture(final Result<T> result) {
		return new FutureTask<Result<T>>(new Callable<Result<T>>() {
			@Override
			public Result<T> call() throws Exception {
				return result;
			}
		});
	}

	private static HttpURLConnection openConnection(final Method method, final boolean doOutput, final String url, final String queryString, final Map<String, List<String>> requestProperties, final Configuration<?> configuration) throws IOException {
		final String spec = url + (!doOutput && !StringUtils.isBlank(queryString) ? (url.indexOf('?') == -1 ? "?" : "&") + queryString : "");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(method + ": " + spec);
		}
		final HttpURLConnection connection = (HttpURLConnection) new URL(spec).openConnection();
		connection.setRequestMethod(String.valueOf(method));
		connection.setDoOutput(doOutput);
		for (final Entry<String, List<String>> requestProperty : requestProperties == null ? EMPTY_SET : requestProperties.entrySet()) {
			setRequestProperty(connection, requestProperty);
		}
		setContentType(connection, doOutput);
		connection.setConnectTimeout(configuration.getConnectTimeout());
		connection.setReadTimeout(configuration.getReadTimeout());
		connection.setInstanceFollowRedirects(configuration.isFollowRedirects());
		return connection;
	}

	private static void setRequestProperty(final HttpURLConnection connection, final Entry<String, List<String>> requestProperty) {
		final String key = requestProperty != null ? requestProperty.getKey() : null;
		if (key == null) {
			return;
		}
		final List<String> values = requestProperty.getValue();
		if (values == null) {
			return;
		}
		for (final String value : values) {
			addRequestProperty(connection, key, value);
		}
	}

	private static void addRequestProperty(final HttpURLConnection connection, final String key, final String value) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("property: " + key + " = " + value);
		}
		connection.addRequestProperty(key, value);
	}

	private static void setContentType(final HttpURLConnection connection, final boolean doOutput) {
		if ((connection.getRequestProperty(HttpHeaders.CONTENT_TYPE) != null) || !doOutput) {
			return;
		}
		connection.addRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
	}

	private static void connect(final HttpURLConnection connection, final boolean doOutput, final String queryString) throws IOException {
		connection.connect();
		if (doOutput && !StringUtils.isBlank(queryString)) {
			write(connection, queryString);
		}
	}

	private static void write(final HttpURLConnection connection, final String queryString) throws IOException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("write: " + queryString);
		}
		ExtraIOUtils.write(queryString, connection.getOutputStream());
	}

	private static void disconnect(final HttpURLConnection connection) {
		connection.disconnect();
	}
}
