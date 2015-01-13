package com.github.t3t5u.common.http;

import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;

public class StringConfiguration extends Configuration<String> {
	private Charset charset = Charsets.UTF_8;

	StringConfiguration(final StringConfiguration configuration) {
		super(configuration);
		if (configuration == null) {
			return;
		}
		charset = configuration.charset;
	}

	public Charset getCharset() {
		return charset;
	}

	void setCharset(final Charset charset) {
		this.charset = charset;
	}
}
