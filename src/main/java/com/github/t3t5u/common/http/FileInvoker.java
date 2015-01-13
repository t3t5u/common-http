package com.github.t3t5u.common.http;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileInvoker extends AbstractInconvertibleInvoker<File, FileConfiguration> {
	public FileInvoker(final Method method, final String url, final File file) {
		this(method, url, null, null, new FileConfigurationBuilder().setFile(file).build());
	}

	public FileInvoker(final Method method, final String url, final FileConfiguration configuration) {
		this(method, url, null, null, configuration);
	}

	public FileInvoker(final Method method, final String url, final String queryString, final File file) {
		this(method, url, queryString, null, new FileConfigurationBuilder().setFile(file).build());
	}

	public FileInvoker(final Method method, final String url, final String queryString, final FileConfiguration configuration) {
		this(method, url, queryString, null, configuration);
	}

	public FileInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final File file) {
		this(method, url, queryString, requestProperties, new FileConfigurationBuilder().setFile(file).build());
	}

	public FileInvoker(final Method method, final String url, final String queryString, final Map<String, List<String>> requestProperties, final FileConfiguration configuration) {
		super(method, url, queryString, requestProperties, configuration);
	}

	@Override
	protected Delegator<File, FileConfiguration> getDelegator() {
		return FileDelegator.getInstance();
	}
}
