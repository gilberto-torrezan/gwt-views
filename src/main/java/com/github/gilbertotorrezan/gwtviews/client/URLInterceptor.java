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

import com.google.gwt.user.client.History;

/**
 * <p>
 * Intercepts URL changes and allows modifications and blocks of those changes.
 * A common use case is to show a dialog to the user asking if he really wants to exit
 * a {@link View}.
 * </p>
 * <p>
 * To setup a URLInterceptor, use the attribute {@link View#urlInterceptor()}. If the interceptor is the same class
 * of the View, the same instance of the View is used to call the {@link #onUrlChanged(URLToken, URLToken, URLInterceptorCallback)} method.
 * The same occur if the interceptor is the same class of a {@link View#customPresenter()}. Otherwise, a new instance of the interceptor is created
 * everytime the URL changes.
 * </p>  
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
public interface URLInterceptor {

	/**
	 * <p>
	 * Called when the user leaves the current View. The URL change is intercepted and can be modified. To send the user to the proper destination,
	 * use the {@link URLInterceptorCallback#proceedTo(URLToken)} method. You can send the user to any {@link View} you want, with any parameters.
	 * To cancel the URL change, just don't call anything.
	 * </p>
	 * <p>
	 * Warning: when using this method, don't call {@link History#newItem(String)}: another change event will be triggered and your code will run in loop.
	 * </p>
	 * <p>
	 * Example:
	 * </p>
	 * <pre>{@code
	 * public void onUrlChanged(URLToken current, URLToken destination, URLInterceptorCallback callback) {
	 * 	if (Window.confirm("Do you really want to exit? This page is so nice!")){
	 *  		callback.proceedTo(destination);
     *  	}
     * }
     * }
	 * </pre>
	 * 
	 * @param current The state of the URL before the change
	 * @param destination The URL where the user wants to go
	 * @param callback The controller used to properly send the user to the right destination
	 */
	void onUrlChanged(URLToken current, URLToken destination, URLInterceptorCallback callback);
}
