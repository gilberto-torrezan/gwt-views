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

/**
 * A ViewContainer is a layout manager for {@link View}s. The ViewContainer must implement {@link HasViews}.
 * The Viewcontainer is created by a {@link Presenter}.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ViewContainer {
	
	/**
	 * <p>
	 * Sets if this ViewContainer is the default container of the application. {@link View}s without a custom ViewContainer
	 * will use the default one.
	 * </p>
	 * <p>
	 * Defaults to <code>false</code>. If only one ViewContainer is defined at the application, that one is considered the default.
	 * </p>
	 * @see View#usesViewContainer()
	 * @see View#viewContainer()
	 */
	boolean defaultContainer() default false;
	
	/**
	 * Sets a custom {@link Presenter} for this ViewContainer.
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Presenter> customPresenter() default Presenter.class;
	
	/**
	 * Defines a injector to be used to instantiate this ViewContainer. Useful when using dependency injection frameworks such as GIN.
	 */
	Class<?> injector() default void.class;
	
	/**
	 * Defines the method to be called on the {@link #injector()} to instantiate this ViewContainer. You only have to use this property when 
	 * there's more than one method that returns this ViewContainer type.
	 */
	String injectorMethod() default "";

}
