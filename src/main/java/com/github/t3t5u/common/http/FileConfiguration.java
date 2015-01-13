package com.github.t3t5u.common.http;

import java.io.File;

import com.github.t3t5u.common.util.CopyProgressListener;

public class FileConfiguration extends Configuration<File> {
	private File file;
	private CopyProgressListener copyProgressListener;
	private long progress;

	FileConfiguration(final FileConfiguration configuration) {
		super(configuration);
		if (configuration == null) {
			return;
		}
		file = configuration.file;
		copyProgressListener = configuration.copyProgressListener;
		progress = configuration.progress;
	}

	public File getFile() {
		return file;
	}

	void setFile(final File file) {
		this.file = file;
	}

	public CopyProgressListener getCopyProgressListener() {
		return copyProgressListener;
	}

	void setCopyProgressListener(final CopyProgressListener copyProgressListener) {
		this.copyProgressListener = copyProgressListener;
	}

	public long getProgress() {
		return progress;
	}

	void setProgress(final long progress) {
		this.progress = progress;
	}
}
