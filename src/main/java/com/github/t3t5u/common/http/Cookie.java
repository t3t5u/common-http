package com.github.t3t5u.common.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Cookie implements Serializable {
	public static final String REQUEST = "Cookie";
	public static final String RESPONSE = "Set-Cookie";
	private static final Logger LOGGER = LoggerFactory.getLogger(Cookie.class);
	private final String name;
	private final String value;

	public Cookie(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object o) {
		return (o == this) || ((o instanceof Cookie) && new EqualsBuilder().append(getName(), ((Cookie) o).getName()).append(getValue(), ((Cookie) o).getValue()).build());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getName()).append(getValue()).build();
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}

	public static List<Cookie> parse(final Map<String, List<String>> headerFields) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("headerFields: " + headerFields);
		}
		if ((headerFields == null) || headerFields.isEmpty()) {
			return null;
		}
		return parse(headerFields.get(StringUtils.lowerCase(RESPONSE)));
	}

	public static List<Cookie> parse(final List<String> headerList) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("headerList: " + headerList);
		}
		if ((headerList == null) || headerList.isEmpty()) {
			return null;
		}
		final Map<String, Cookie> cookieMap = new LinkedHashMap<String, Cookie>();
		for (final String header : headerList) {
			put(cookieMap, header);
		}
		final List<Cookie> cookieList = new ArrayList<Cookie>(cookieMap.values());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("cookieList: " + cookieList);
		}
		return cookieList;
	}

	public static List<String> format(final List<Cookie> cookieList) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("cookieList: " + cookieList);
		}
		if ((cookieList == null) || cookieList.isEmpty()) {
			return null;
		}
		final Map<String, String> headerMap = new LinkedHashMap<String, String>();
		for (final Cookie cookie : cookieList) {
			put(headerMap, cookie);
		}
		final List<String> headerList = new ArrayList<String>(headerMap.values());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("headerList: " + headerList);
		}
		return headerList;
	}

	private static void put(final Map<String, Cookie> cookieMap, final String header) {
		if (header == null) {
			return;
		}
		final Cookie cookie = new Cookie(header.replaceFirst("=.*$", "").trim(), header.replaceFirst("^[^=]+=", "").replaceFirst(";.*$", "").trim());
		cookieMap.put(cookie.getName(), cookie);
	}

	private static void put(final Map<String, String> headerMap, final Cookie cookie) {
		if (cookie == null) {
			return;
		}
		headerMap.put(cookie.getName(), cookie.toString());
	}
}
