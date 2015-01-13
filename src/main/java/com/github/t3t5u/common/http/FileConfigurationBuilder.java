package com.github.t3t5u.common.http;

import java.io.File;

import com.github.t3t5u.common.util.CopyProgressListener;

public class FileConfigurationBuilder extends ConfigurationBuilder<File, FileConfiguration, FileConfigurationBuilder> {
	public FileConfigurationBuilder() {
		this(null);
	}

	public FileConfigurationBuilder(final FileConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected FileConfiguration clone(final FileConfiguration configuration) {
		return new FileConfiguration(configuration);
	}

	public FileConfigurationBuilder setFile(final File file) {
		getConfiguration().setFile(file);
		return this;
	}

	public FileConfigurationBuilder setCopyProgressListener(final CopyProgressListener copyProgressListener) {
		getConfiguration().setCopyProgressListener(copyProgressListener);
		return this;
	}

	public FileConfigurationBuilder setProgress(final long progress) {
		getConfiguration().setProgress(progress);
		return this;
	}
}
