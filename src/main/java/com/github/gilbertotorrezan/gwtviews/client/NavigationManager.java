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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Panel;

/**
 * This class controls all the navigation rules of the application. To start it, call the {@link #start(Panel)} method.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
public class NavigationManager {
	
	private static final INavigationManager manager = GWT.create(INavigationManager.class);
	
	private NavigationManager(){}
	
	/**
	 * Starts the framework, rendering the {@link View#defaultView()} to the page.
	 * 
	 * @param rootContainer The body widget of the page. Usually <code>RootLayoutPanel.get()</code>.
	 */
	public static void start(Panel rootContainer) {
		manager.setRootContainer(rootContainer);
		History.addValueChangeHandler(manager);
		History.fireCurrentHistoryState();
	}

	/**
	 * Sets the {@link UserPresenceManager} to control {@link View}s that aren't public.
	 * 
	 * @param umanager The UserPresenceManager used by the framework to determine if the user has access to non-public Views.
	 */
	public static void setUserPresenceManager(UserPresenceManager umanager) {
		manager.setUserPresenceManager(umanager);
	}

	/**
	 * Clears the {@link Presenter} cache. All Presenters are stored in the cache (which, in turn, can cache {@link View}s). 
	 * It is usually a good idea to clear the cache when the current user logs out the application.
	 */
	public static void clearCache() {
		manager.clearCache();
	}

}
