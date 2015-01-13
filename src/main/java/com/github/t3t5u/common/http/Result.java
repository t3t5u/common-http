package com.github.t3t5u.common.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Result<T> {
	T getResponse();

	int getResponseCode();

	String getResponseMessage();

	Map<String, List<String>> getHeaderFields();

	IOException getException();

	String getUrl();

	boolean isOk();

	boolean isTimeout();
}
