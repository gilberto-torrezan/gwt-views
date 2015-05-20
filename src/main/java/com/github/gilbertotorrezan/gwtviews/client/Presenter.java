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

import com.google.gwt.user.client.ui.Widget;

/**
 * <p>
 * Presenters are responsible to create {@link View}s and {@link ViewContainer}s to be rendered at the page. 
 * The framework creates Presenters for all Views and ViewContainers without {@link View#customPresenter()}s.
 * </p>
 * <p>
 * Note that the default caching and injection mechanisms cannot be used when a custom Presenter is set to a View or ViewContainer.
 * The developer is responsible to cache and inject them if needed.
 * </p>
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
public interface Presenter<I extends Widget> {
	
	/**
	 * Creates and returns a {@link View}, based on the {@link URLToken}.
	 * 
	 *  @param url The current URL state of the application
	 *  @return The created View to be shown at the page
	 */
	I getView(URLToken url);

}
