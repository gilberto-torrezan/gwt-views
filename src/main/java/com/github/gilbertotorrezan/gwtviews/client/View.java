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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.user.client.History;

/**
 * Defines a View of the application. A View is a Widget associated with a unique URL token that can be shown
 * at the page. A View is created by a {@link Presenter}.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface View {
	
	/**
	 * The unique URL token that points to this View. 
	 * 
	 * @see History#newItem(String) 
	 */
	String value();
	
	/**
	 * A public View can be accessed by any user (on-line or off-line), with any role.
	 * Defaults to <code>false</code>. 
	 * 
	 * @see UserPresenceManager#isUserInAnyRole(URLToken, String[], com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	boolean publicAccess() default false;
	
	/**
	 * A cacheable View can be cached by the {@link Presenter} to improve performance and save
	 * the state of the View when the user leaves the page. Defaults to <code>CachePolicy.SAME_URL</code>.
	 * Note that this property has no effect when a {@link #customPresenter()} is defined.
	 */
	CachePolicy cache() default CachePolicy.SAME_URL;

	/**
	 * The defaultView is the first View showed by the application, when the {@link NavigationManager} is started.
	 * There can be only one defaultView. Defaults to <code>false</code>.
	 */
	boolean defaultView() default false;
	
	/**
	 * The notFoundView is the View showed when the URL token cannot be matched to any registered View.
	 * Defaults to <code>false</code>.
	 */
	boolean notFoundView() default false;
	
	/**
	 * Defines if this View uses or not a {@link ViewContainer} to be rendered. 
	 * Defaults to <code>true</code>.
	 */
	boolean usesViewContainer() default true;
	
	/**
	 * Defines the roles the user must have to access the View. If the user has any of the defined roles, the access is granted.
	 * This property has precedence over {@link #publicAccess()}.
	 * 
	 * @see UserPresenceManager#isUserInAnyRole(URLToken, String[], com.google.gwt.user.client.rpc.AsyncCallback) 
	 */
	String[] rolesAllowed() default {};
	
	/**
	 * Defines a custom {@link ViewContainer} for this View.
	 * If {@link #usesViewContainer()} is <code>true</code> (default behaviour) and this property is not set, the default
	 * ViewContainer is used.
	 * 
	 * @see ViewContainer#defaultContainer()
	 */
	Class<? extends HasViews> viewContainer() default HasViews.class;
	
	/**
	 * Defines a custom {@link Presenter} for this View.
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Presenter> customPresenter() default Presenter.class;
	
	/**
	 * Defines a {@link URLInterceptor} for this View.
	 */
	Class<? extends URLInterceptor> urlInterceptor() default URLInterceptor.class;
	
	/**
	 * Defines a injector to be used to instantiate this View. Useful when using dependency injection frameworks such as GIN.
	 */
	Class<?> injector() default void.class;
	
	/**
	 * Defines the method to be called on the {@link #injector()} to instantiate this View. You only have to use this property when 
	 * there's more than one method that returns this View type.
	 */
	String injectorMethod() default "";
	
}
