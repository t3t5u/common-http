package com.github.t3t5u.common.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import com.github.t3t5u.common.util.ExtraIOUtils;

public class StringResult extends AbstractResult<String> {
	public StringResult(final IOException exception) {
		super(exception);
	}

	public StringResult(final HttpURLConnection connection, final StringConfiguration configuration) throws IOException {
		super(getResponse(connection, configuration, false), connection.getResponseCode(), connection.getResponseMessage(), connection.getHeaderFields(), connection.getURL());
	}

	public StringResult(final HttpURLConnection connection, final StringConfiguration configuration, final IOException exception) {
		super(getResponse(connection, configuration, true), getResponseCode(connection), getResponseMessage(connection), connection.getHeaderFields(), connection.getURL(), exception);
	}

	private static String getResponse(final HttpURLConnection connection, final StringConfiguration configuration, final boolean error) {
		final Charset charset = configuration.getCharset();
		final String response = error ? ExtraIOUtils.readAsStringOrNull(connection.getErrorStream(), charset) : ExtraIOUtils.readAsStringOrNull(getInputStream(connection), charset);
		return response != null ? response : error ? ExtraIOUtils.readAsStringOrNull(getInputStream(connection), charset) : ExtraIOUtils.readAsStringOrNull(connection.getErrorStream(), charset);
	}
}
