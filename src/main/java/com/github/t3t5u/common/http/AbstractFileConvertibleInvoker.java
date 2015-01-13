package com.github.t3t5u.common.http;

import java.io.File;

public abstract class AbstractFileConvertibleInvoker<V> extends AbstractConvertibleInvoker<V, File, FileConfiguration> {
	private final FileConfiguration configuration;

	protected AbstractFileConvertibleInvoker(final File file) {
		this(new FileConfigurationBuilder().setFile(file).build());
	}

	protected AbstractFileConvertibleInvoker(final FileConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	protected Delegator<File, FileConfiguration> getDelegator() {
		return FileDelegator.getInstance();
	}

	@Override
	protected FileConfiguration getConfiguration() {
		return configuration;
	}
}
