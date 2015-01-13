package com.github.t3t5u.common.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.github.t3t5u.common.util.CopyProgressListener;
import com.github.t3t5u.common.util.ExtraIOUtils;

public class FileResult extends AbstractResult<File> {
	public FileResult(final IOException exception) {
		super(exception);
	}

	public FileResult(final HttpURLConnection connection, final FileConfiguration configuration) throws IOException {
		super(getResponse(connection, configuration, false), connection.getResponseCode(), connection.getResponseMessage(), connection.getHeaderFields(), connection.getURL());
	}

	public FileResult(final HttpURLConnection connection, final FileConfiguration configuration, final IOException exception) {
		super(getResponse(connection, configuration, true), getResponseCode(connection), getResponseMessage(connection), connection.getHeaderFields(), connection.getURL(), exception);
	}

	private static File getResponse(final HttpURLConnection connection, final FileConfiguration configuration, final boolean error) {
		final File file = configuration.getFile();
		return copy(connection, file, configuration.getCopyProgressListener(), configuration.getProgress(), error) != -1 ? file : null;
	}

	private static long copy(final HttpURLConnection connection, final File file, final CopyProgressListener copyProgressListener, final long progress, final boolean error) {
		final long result = error ? copyErrorStream(connection, file, copyProgressListener, progress) : copyInputStream(connection, file, copyProgressListener, progress);
		return result != -1 ? result : error ? copyInputStream(connection, file, copyProgressListener, progress) : copyErrorStream(connection, file, copyProgressListener, progress);
	}

	private static long copyErrorStream(final HttpURLConnection connection, final File file, final CopyProgressListener copyProgressListener, final long progress) {
		final OutputStream os = ExtraIOUtils.openOutputStreamOrNull(file);
		return os != null ? ExtraIOUtils.copyOrNull(connection.getErrorStream(), os, copyProgressListener, progress) : -1;
	}

	private static long copyInputStream(final HttpURLConnection connection, final File file, final CopyProgressListener copyProgressListener, final long progress) {
		final OutputStream os = ExtraIOUtils.openOutputStreamOrNull(file);
		return os != null ? ExtraIOUtils.copyOrNull(getInputStream(connection), os, copyProgressListener, progress) : -1;
	}
}
