package com.github.t3t5u.common.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class HttpUtilsTest {
	@Test
	public void testFromQueryString() {
		final Map<String, List<String>> parameters = HttpUtils.fromQueryString("a&b=b&c=c1&c=c2");
		assertThat(parameters.get("a").get(0), is(""));
		assertThat(parameters.get("b").get(0), is("b"));
		assertThat(parameters.get("c").get(0), is("c1"));
		assertThat(parameters.get("c").get(1), is("c2"));
	}

	@Test
	public void testToQueryString() {
		assertThat(HttpUtils.toQueryString(HttpUtils.fromQueryString("a&b=b&c=c1&c=c2")), is("a=&b=b&c=c1&c=c2"));
	}
}
