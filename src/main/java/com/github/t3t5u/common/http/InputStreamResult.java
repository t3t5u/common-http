package com.github.t3t5u.common.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.github.t3t5u.common.util.ExtraIOUtils;

public class InputStreamResult extends AbstractResult<InputStream> {
	public InputStreamResult(final IOException exception) {
		super(exception);
	}

	public InputStreamResult(final HttpURLConnection connection, final InputStreamConfiguration configuration) throws IOException {
		super(getResponse(connection, configuration, false), connection.getResponseCode(), connection.getResponseMessage(), connection.getHeaderFields(), connection.getURL());
	}

	public InputStreamResult(final HttpURLConnection connection, final InputStreamConfiguration configuration, final IOException exception) {
		super(getResponse(connection, configuration, true), getResponseCode(connection), getResponseMessage(connection), connection.getHeaderFields(), connection.getURL(), exception);
	}

	private static InputStream getResponse(final HttpURLConnection connection, final InputStreamConfiguration configuration, final boolean error) {
		final byte[] bytes = error ? ExtraIOUtils.readOrNull(connection.getErrorStream()) : ExtraIOUtils.readOrNull(getInputStream(connection));
		return bytes != null ? new ByteArrayInputStream(bytes) : error ? getInputStream(ExtraIOUtils.readOrNull(getInputStream(connection))) : getInputStream(ExtraIOUtils.readOrNull(connection.getErrorStream()));
	}

	private static InputStream getInputStream(final byte[] bytes) {
		return bytes != null ? new ByteArrayInputStream(bytes) : null;
	}
}
