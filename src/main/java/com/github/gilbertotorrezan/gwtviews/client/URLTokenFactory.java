/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Gilberto Torrezan Filho
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

import com.google.gwt.user.client.History;

/**
 * <p>
 * URLTokenFactory is a factory class that creates {@link URLToken}s based on an input
 * String. That String is usually the return of the {@link History#getToken()} method.
 * </p>
 * <p>
 * This class que be overriden to provide subclasses of {@link URLToken} with custom behaviour,
 * such as different token schemas and layouts.
 * </p>
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.4.0
 */
public class URLTokenFactory {
	
	/**
	 * Creates a new {@link URLToken} based on the {@link History#getToken()} String.
	 * By default it creates a new URLToken by using the {@link #URLToken(String)} constructor.
	 * 
	 * @param completeToken The token String to be used
	 * @return a new URLToken (or any subclass)
	 */
	public URLToken createToken(String completeToken) {
		return new URLToken(completeToken);
	}

}
