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

import java.util.HashMap;
import java.util.Map;

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
	private static final Map<Class<?>, Object> injectorsMap = new HashMap<>();
	
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
	 * Clears all the {@link Presenter} cache. All Presenters are stored in the cache (which, in turn, can cache {@link View}s). 
	 * It is usually a good idea to clear the cache when the current user logs out the application.
	 */
	public static void clearCache() {
		manager.clearCache();
	}

	/**
	 * Clears the {@link Presenter} associated with the tokenId from the cache. If the same tokenId is called again, a new Presenter
	 * will be created to handle the request.
	 */
	public static void clearCache(String tokenId) {
		manager.clearCache(tokenId);
	}
	
	/**
	 * Sets the injector instance to be used internally by the framework to inject views. If an injector instance is not declared here,
	 * a new instance is created every time a view is invoked.
	 * 
	 * @param injectorClass The class literal of the injector.
	 * @param injectorInstance The injector instance to be used.
	 * 
	 * @see View#injector()
	 * @since v.1.3.1
	 */
	public static <T> void setInjectorInstance(Class<T> injectorClass, T injectorInstance) {
		if (injectorInstance == null){
			injectorsMap.remove(injectorClass);
		}
		injectorsMap.put(injectorClass, injectorInstance);
	}
	
	/**
	 * Gets the current injector instance associated to the injector class literal. This method is called internally by the framework
	 * to determine which injector instance should be used when injecting views.
	 * 
	 * @param injectorClass The class literal of the injector.
	 * @return The injector instance previously set on {@link #setInjectorInstance(Class, Object)}
	 * 
	 * @since v.1.3.1 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInjectorInstance(Class<T> injectorClass) {
		return (T) injectorsMap.get(injectorClass);
	}
}
