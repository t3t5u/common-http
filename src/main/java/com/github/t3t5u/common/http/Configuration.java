package com.github.t3t5u.common.http;

import java.util.concurrent.TimeUnit;

public abstract class Configuration<T> {
	private static final int DEFAULT_CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);
	private static final int DEFAULT_READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(90);
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = DEFAULT_READ_TIMEOUT;
	private boolean followRedirects;

	Configuration(final Configuration<T> configuration) {
		if (configuration == null) {
			return;
		}
		connectTimeout = configuration.connectTimeout;
		readTimeout = configuration.readTimeout;
		followRedirects = configuration.followRedirects;
	}

	public long getConnectTimeout(final TimeUnit unit) {
		return unit.convert(connectTimeout, TimeUnit.MILLISECONDS);
	}

	void setConnectTimeout(final long connectTimeout, final TimeUnit unit) {
		this.connectTimeout = (int) unit.toMillis(connectTimeout);
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	void setConnectTimeout(final int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public long getReadTimeout(final TimeUnit unit) {
		return unit.convert(readTimeout, TimeUnit.MILLISECONDS);
	}

	void setReadTimeout(final long readTimeout, final TimeUnit unit) {
		this.readTimeout = (int) unit.toMillis(readTimeout);
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	void setReadTimeout(final int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	void setFollowRedirects(final boolean followRedirects) {
		this.followRedirects = followRedirects;
	}
}
