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
package com.github.gilbertotorrezan.gwtviews.client.analytics;

import com.arcbees.analytics.shared.Analytics;
import com.arcbees.analytics.shared.options.CreateOptions;
import com.github.gilbertotorrezan.gwtviews.client.URLToken;
import com.google.gwt.core.shared.GWT;

/**
 * Class used by the NavigationManager to track page views. Must be configured by calling the 
 * {@link #configure(String)} or the {@link #setAnalytics(Analytics)} methods. 
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.3.0
 * @see Analytics
 */
public class UniversalAnalyticsTracker {
	
	private static Analytics analytics;
	
	private UniversalAnalyticsTracker(){}
	
	private static void autoCreateAnalyticsIfNull(){
		if (analytics == null){
			UniversalAnalyticsTrackerGinjector injector = GWT.create(UniversalAnalyticsTrackerGinjector.class);
			analytics = injector.getAnalytics();
		}
	}
	
	/**
	 * @return The current {@link Analytics} object, or <code>null</code> if the UniversalAnalyticsTracker was not configured.
	 */
	public static Analytics getAnalytics(){
		return analytics;
	}
	
	/**
	 * Manually sets the {@link Analytics} instance to be used by the framework. When calling this method, the caller is
	 * responsible for creating and configuring the Analytics object.
	 * 
	 * @see Analytics#create()
	 */
	public static void setAnalytics(Analytics analytics){
		UniversalAnalyticsTracker.analytics = analytics;
	}
	
	/**
	 * Creates and configures an {@link Analytics} instance with the trackerId to be used by the framework. 
	 * When using this method, the <code>analytics.js</code> is imported automatically. 
	 * 
	 * @param trackerId The tracker ID. For instance, "UA-XXXX-Y"
	 * 
	 * @see Analytics#create(String)
	 */
	public static void configure(String trackerId){
		autoCreateAnalyticsIfNull();
		analytics.create(trackerId).go();
	}
	
	/**
	 * Creates and configures an {@link Analytics} instance with the trackerId and userId to be used by the framework.
	 * When using this method, the <code>analytics.js</code> is imported automatically.
	 * 
	 * @param trackerId The tracker ID. For instance, "UA-XXXX-Y"
	 * @param userId A string which identifies your user, to be used at the analytics dashboard later on
	 * 
	 * @see Analytics#create(String)
	 * @see CreateOptions#userId(String)
	 */
	public static void configure(String trackerId, String userId){
		autoCreateAnalyticsIfNull();
		analytics.create(trackerId).userId(userId).go();
	}
	
	/**
	 * Tracks a page view. Do nothing if the UniversalAnalyticsTracker is not configured.
	 * 
	 * @param pageWithParameters The current page for tracking, including query parameters. Can be obtained by calling {@link URLToken#toString()}.
	 * 
	 * @see Analytics#sendPageView()
	 */
	public static void sendPageView(String pageWithParameters){
		if (analytics == null){
			return;
		}
		analytics.sendPageView().documentPath(pageWithParameters).go();
	}

}
