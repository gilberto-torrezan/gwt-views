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

/**
 * Enum that describes how a {@link View} should be cached in its {@link Presenter}. Note that if a
 * {@link View#customPresenter()} is used, these properties have no effect - the developer must take
 * care of the caching himself.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.1.0
 */
public enum CachePolicy {
	
	/**
	 * When using the NEVER cache policy, a new instance of the View is created everytime is needed. No
	 * caching is performed. 
	 */
	NEVER,
	
	/**
	 * When using the SAME_URL cache policy, a new instance of the View is created only when the URL (incluing its
	 * parameters) is changed. If a View URL is always the same, the same View instance will be used. This is the default
	 * behavior. 
	 * 
	 */
	SAME_URL,
	
	/**
	 * When using the ALWAYS cache policy, the same instance of the View is used at all times, regardless of changes in URL parameters.
	 */
	ALWAYS;

}
