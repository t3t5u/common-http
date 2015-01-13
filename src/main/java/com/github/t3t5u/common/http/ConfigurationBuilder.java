package com.github.t3t5u.common.http;

import java.util.concurrent.TimeUnit;

public abstract class ConfigurationBuilder<T, CONFIGURATION extends Configuration<T>, CONFIGURATION_BUILDER extends ConfigurationBuilder<T, CONFIGURATION, CONFIGURATION_BUILDER>> {
	private CONFIGURATION configuration;

	protected ConfigurationBuilder(final CONFIGURATION configuration) {
		setConfiguration(clone(configuration));
	}

	public CONFIGURATION build() {
		final CONFIGURATION configuration = getConfiguration();
		setConfiguration(clone(configuration));
		return configuration;
	}

	protected abstract CONFIGURATION clone(CONFIGURATION configuration);

	protected CONFIGURATION getConfiguration() {
		return configuration;
	}

	protected void setConfiguration(final CONFIGURATION configuration) {
		this.configuration = configuration;
	}

	@SuppressWarnings("unchecked")
	public CONFIGURATION_BUILDER setConnectTimeout(final long connectTimeout, final TimeUnit unit) {
		getConfiguration().setConnectTimeout(connectTimeout, unit);
		return (CONFIGURATION_BUILDER) this;
	}

	@SuppressWarnings("unchecked")
	public CONFIGURATION_BUILDER setConnectTimeout(final int connectTimeout) {
		getConfiguration().setConnectTimeout(connectTimeout);
		return (CONFIGURATION_BUILDER) this;
	}

	@SuppressWarnings("unchecked")
	public CONFIGURATION_BUILDER setReadTimeout(final long readTimeout, final TimeUnit unit) {
		getConfiguration().setReadTimeout(readTimeout, unit);
		return (CONFIGURATION_BUILDER) this;
	}

	@SuppressWarnings("unchecked")
	public CONFIGURATION_BUILDER setReadTimeout(final int readTimeout) {
		getConfiguration().setReadTimeout(readTimeout);
		return (CONFIGURATION_BUILDER) this;
	}

	@SuppressWarnings("unchecked")
	public CONFIGURATION_BUILDER setFollowRedirects(final boolean followRedirects) {
		getConfiguration().setFollowRedirects(followRedirects);
		return (CONFIGURATION_BUILDER) this;
	}
}
