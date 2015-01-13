package com.github.t3t5u.common.http;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.github.t3t5u.common.util.ExtraIOUtils;

public class ByteArrayResult extends AbstractResult<byte[]> {
	public ByteArrayResult(final IOException exception) {
		super(exception);
	}

	public ByteArrayResult(final HttpURLConnection connection, final ByteArrayConfiguration configuration) throws IOException {
		super(getResponse(connection, configuration, false), connection.getResponseCode(), connection.getResponseMessage(), connection.getHeaderFields(), connection.getURL());
	}

	public ByteArrayResult(final HttpURLConnection connection, final ByteArrayConfiguration configuration, final IOException exception) {
		super(getResponse(connection, configuration, true), getResponseCode(connection), getResponseMessage(connection), connection.getHeaderFields(), connection.getURL(), exception);
	}

	private static byte[] getResponse(final HttpURLConnection connection, final ByteArrayConfiguration configuration, final boolean error) {
		final byte[] response = error ? ExtraIOUtils.readOrNull(connection.getErrorStream()) : ExtraIOUtils.readOrNull(getInputStream(connection));
		return response != null ? response : error ? ExtraIOUtils.readOrNull(getInputStream(connection)) : ExtraIOUtils.readOrNull(connection.getErrorStream());
	}
}
