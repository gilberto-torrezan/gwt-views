/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Gilberto Torrezan Filho
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package com.github.gilbertotorrezan.gwtviews.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;

/**
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since @since v.1.0.0
 */
public class URLToken extends Place {

	private String id = "";
	private Map<String, String> parameters = new LinkedHashMap<>();

	private enum TokenParseState {
		PARSING_ID, PARSING_KEY, PARSING_VALUE, PARSING_COMPLEX_VALUE, PARSED_COMPLEX_VALUE;
	}

	public URLToken() {
		setToken(History.getToken());
	}

	public URLToken(String completeToken) {
		setToken(completeToken);
	}

	public void setToken(String completeToken) {
		if (completeToken != null) {
			StringBuilder builder = new StringBuilder();

			char[] chs = completeToken.toCharArray();
			String currentKey = null;
			TokenParseState state = TokenParseState.PARSING_ID;
			for (char ch : chs) {
				switch (state) {
				case PARSING_ID:
					if (ch == '&') {
						state = TokenParseState.PARSING_KEY;
						id = builder.toString();
						builder.delete(0, builder.length());
					} else {
						builder.append(ch);
					}
					break;
				case PARSING_KEY:
					if (ch == '=') {
						state = TokenParseState.PARSING_VALUE;
						currentKey = builder.toString();
						if (!currentKey.isEmpty()) {
							parameters.put(currentKey, "");
							builder.delete(0, builder.length());
						}
					} else if (ch == '&') {
						currentKey = builder.toString();
						if (!currentKey.isEmpty()) {
							parameters.put(currentKey, "");
							builder.delete(0, builder.length());
						}
					} else {
						builder.append(ch);
					}
					break;
				case PARSING_VALUE:
					if (ch == '\'') {
						state = TokenParseState.PARSING_COMPLEX_VALUE;
					} else if (ch == '&') {
						state = TokenParseState.PARSING_KEY;
						parameters.put(currentKey, builder.toString());
						builder.delete(0, builder.length());
					} else {
						builder.append(ch);
					}
					break;
				case PARSING_COMPLEX_VALUE:
					if (ch == '\'') {
						state = TokenParseState.PARSED_COMPLEX_VALUE;
						parameters.put(currentKey, builder.toString());
						builder.delete(0, builder.length());
					} else {
						builder.append(ch);
					}
					break;
				case PARSED_COMPLEX_VALUE:
					if (ch == '&') {
						state = TokenParseState.PARSING_KEY;
					}
					break;
				}
			}

			switch (state) {
			case PARSING_ID:
				id = builder.toString();
				break;
			case PARSING_KEY:
				currentKey = builder.toString();
				if (!currentKey.isEmpty()) {
					parameters.put(currentKey, "");
				}
				break;
			case PARSING_COMPLEX_VALUE:
			case PARSING_VALUE:
				String value = builder.toString();
				if (!value.isEmpty() && currentKey != null) {
					parameters.put(currentKey, value);
				}
				break;
			default:
				break;
			}
		}
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public String setParameter(String name, String value) {
		return parameters.put(name, value);
	}

	public void removeParameter(String name) {
		parameters.remove(name);
	}

	public String getId() {
		return id;
	}

	public void go() {
		History.newItem(toString(), true);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(id);

		Set<Entry<String, String>> entrySet = parameters.entrySet();
		for (Entry<String, String> entry : entrySet) {
			builder.append("&").append(entry.getKey());
			String v = entry.getValue();
			if (v != null && !v.isEmpty()) {
				builder.append("=");
				if (v.contains("&") || v.contains("=") || v.contains(" ") || v.contains(":") || v.contains("#") || v.contains("?")) {
					builder.append("'").append(v).append("'");
				} else {
					builder.append(v);
				}
			}
		}

		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Place) {
			return this.toString().equals(obj.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
