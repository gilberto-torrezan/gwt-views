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
 * <p>
 * A URLToken represents the state of a URL. It parses the parameters in the following form:
 * <pre>{@code tokenId&param1=value1&param2&param3='complex&value' }</pre>
 * </p>
 * <p>
 * Calling {@link #toString()} will return a valid token with all parameters to be used at {@link History#newItem(String)} or anchors. 
 * </p> 
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
public class URLToken extends Place implements Cloneable {

	private String id = "";
	private Map<String, String> parameters = new LinkedHashMap<>();

	private enum TokenParseState {
		PARSING_ID, PARSING_KEY, PARSING_VALUE, PARSING_COMPLEX_VALUE, PARSED_COMPLEX_VALUE;
	}

	/**
	 * Creates a new URLToken using the current state of the application.
	 * 
	 * @see History#getToken()
	 */
	public URLToken() {
		this(History.getToken());
	}

	/**
	 * Creates a new URLToken using the provided token.
	 * 
	 * @param completeToken The History token to be parsed, in the form of: <pre>{@code tokenId&param1=value1&param2&param3='complex&value' }</pre>
	 */
	public URLToken(String completeToken) {
		setToken(completeToken);
	}
	
	/**
	 * Clones a URLToken, copying the id and all parameters.
	 * 
	 * @see #clone()
	 */
	public URLToken(URLToken source) {
		this.id = source.id;
		this.parameters.putAll(source.parameters);
	}

	/**
	 * Sets the current token, causing it to parse the parameters and the tokenId.
	 * 
	 * @param completeToken The History token to be parsed, in the form of: <pre>{@code tokenId&param1=value1&param2&param3='complex&value' }</pre>
	 */
	public void setToken(String completeToken) {
		clearParameters();
		id = "";
		
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

	/**
	 * Gets a parameter extracted from the History token.
	 * For example, if the token is: <pre>{@code tokenId&param1=value1 }</pre>the call to <code>getParameter("param1")</code> will return <code>value1</code>.
	 * 
	 * @param name The name of the parameter
	 * @return The value of the parameter, or <code>null</code> if not found.
	 * When the token is something like <pre>{@code tokenId&param1&param2 }</pre> with a name without a explicit value, an empty String is returned. 
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	/**
	 * Gets a parameter extracted from the History token.
	 * For example, if the token is: <pre>{@code tokenId&param1=value1 }</pre>the call to <code>getParameter("param1", "def")</code> will return <code>value1</code>.
	 * 
	 * @param name The name of the parameter
	 * @param defaultValue The value to be returned when the parameter value is <code>null</code>
	 * @return The value of the parameter, or <code>defaultValue</code> if not found.
	 * When the token is something like <pre>{@code tokenId&param1&param2 }</pre> with a name without a explicit value, an empty String is returned. 
	 */
	public String getParameter(String name, String defaultValue) {
		String value = parameters.get(name);
		if (value == null){
			value = defaultValue;
		}
		return value;
	}
	
	/**
	 * Gets an integer parameter extracted from the History token.
	 * For example, if the token is: <pre>{@code tokenId&param1=1 }</pre>the call to <code>getParameterAsInt("param1", 0)</code> will return <code>1</code>.
	 * 
	 * @param name The name of the parameter
	 * @param defaultValue The value to be returned when the parameter is <code>null</code> or not parseable to int
	 * @return The integer value of the parameter, or <code>defaultValue</code> if not parseable.
	 * When the token is something like <pre>{@code tokenId&param1&param2 }</pre> with a name without a explicit value, the default value is returned. 
	 */
	public int getParameterAsInt(String name, int defaultValue){
		String value = parameters.get(name);
		if (value == null || value.isEmpty()){
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Gets a boolean parameter extracted from the History token.
	 * For example, if the token is: <pre>{@code tokenId&param1=true }</pre>the call to <code>getParameterAsBoolean("param1")</code> will return <code>true</code>.
	 * 
	 * @param name The name of the parameter
	 * @return <code>true</code> if the value is equals ignoring case to the String "true", <code>false</code> otherwise 
	 */
	public boolean getParameterAsBoolean(String name){
		String value = parameters.get(name);
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * Gets a double parameter extracted from the History token.
	 * For example, if the token is: <pre>{@code tokenId&param1=0.1 }</pre>the call to <code>getParameterAsBoolean("param1", 0)</code> will return <code>0.1</code>.
	 * 
	 * @param name The name of the parameter
	 * @param defaultValue The value to be returned when the parameter is <code>null</code> or not parseable to double
	 * @return The boolean value of the parameter, or <code>defaultValue</code> if not parseable.
	 * When the token is something like <pre>{@code tokenId&param1&param2 }</pre> with a name without a explicit value, the default value is returned. 
	 */
	public double getParameterAsDouble(String name, double defaultValue){
		String value = parameters.get(name);
		if (value == null || value.isEmpty()){
			return defaultValue;
		}
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Sets a new value to a parameter, or put a new parameter if not existant.
	 * If the value is <code>null</code>, the parameter is removed (same as calling the {@link #removeParameter(String)} method).
	 * 
	 * @param name The name of the parameter
	 * @param value The value of the parameter
	 * @return the previous value associated with <code>name</code>, or <code>null</code> if there 
	 * wasn't an associated value to the name.
	 */
	public String setParameter(String name, String value) {
		if (value == null){
			return removeParameter(name);
		}
		return parameters.put(name, value);
	}

	/**
	 * Removes a parameter from the URLToken.
	 * 
	 * @param name The name of the parameter to be removed
	 * @return the previous value associated with <code>name</code>, or <code>null</code> if there 
	 * wasn't an associated value to the name.
	 */
	public String removeParameter(String name) {
		return parameters.remove(name);
	}
	
	/**
	 * Clears all the parameters (but not the tokenId).
	 */
	public void clearParameters(){
		parameters.clear();
	}
	
	/**
	 * Verifies if a parameter with the defined name is present on the URL.
	 * 
	 * @param name The name of the parameter
	 * @return <code>true</code> if the parameter is present, even with an empty value, <code>false</code> otherwise
	 */
	public boolean containsParameter(String name){
		return parameters.containsKey(name);
	}

	/**
	 * Gets the tokenId of the URL. The tokenId is the value associated with a {@link View#value()}.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the tokenId of the URL. The tokenId is the value associated with a {@link View#value()}.
	 */
	public void setId(String id) {
		if (id == null){
			id = "";
		}
		this.id = id;
	}
	
	/**
	 * Same as calling {@link #setId(String)}, but returning this instance for method chaining.
	 */
	public URLToken withId(String id) {
		setId(id);
		return this;
	}
	
	/**
	 * Same as calling {@link #setParameter(String, String)}, but returning this instance for method chaining.
	 */
	public URLToken withParameter(String name, String value) {
		setParameter(name, value);
		return this;
	}

	/**
	 * Changes the URL of the application to match the state of this URLToken. It has the same effect as calling
	 * <code>History.newItem(urlToken.toString(), true)</code>.
	 * 
	 * @see History#newItem(String, boolean)
	 */
	public void go() {
		History.newItem(this.toString(), true);
	}

	/**
	 * Creates a valid History token with the tokenId and all the parameters. Can be used in anchors (with the <code>#</code> sign) or at
	 * calls to {@link History#newItem(String)}.
	 * 
	 * @return The current URL represented by this URLToken, without the <code>#</code> sign
	 */
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
		if (obj instanceof String){
			return this.toString().equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	/**
	 * Clones this URLToken, using the {@link #URLToken(URLToken)} constructor.
	 */
	@Override
	public URLToken clone() {
		URLToken clone = new URLToken(this);
		return clone;
	}

}
