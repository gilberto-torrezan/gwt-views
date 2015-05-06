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

/**
 * @author Gilberto Torrezan Filho
 * @since v.1.0.0
 */
public class GoogleAnalyticsTracker {
	
	private static GoogleAnalyticsTracker INSTANCE;
	
	private final String trackerId;
	private final String domainName;
	private final String module;
	private boolean allowAnchor;
	
    /**
     * Instantiate new Google Analytics tracker by id and domain.
     *
     * @param trackerId
     *            The tracking id from Google Analytics. Something like
     *            'UA-658457-8'.
     * @param domainName
     *            The name of the domain to be tracked. Something like
     *            'example.com'.
     * @param module
     *			  The name of the GWT module to be tracked, or <code>null</code> if not needed.         
     */

    private GoogleAnalyticsTracker(String trackerId, String domainName, String module) {
    	this.trackerId = trackerId;
        this.domainName = domainName;
        this.module = module;
    }

    /**
     * Get the Google Analytics tracking id.
     *
     * @return Tracking id like 'UA-658457-8'.
     */
    public String getTrackerId() {
        return trackerId;
    }

    /**
     * Get the domain name associated with this tracker.
     *
     * @return
     */
    public String getDomainName() {
        return domainName;
    }
    
    public String getModule() {
		return module;
	}

    /**
     * Track a single page view. This effectively invokes the 'trackPageview' in
     * ga.js file.
     *
     * Note that when ever the component is repainted (for example during
     * explicit page reload), a new track event is generated.
     *
     * @param pageId
     *            The page id. Use a scheme like '/topic/page' or
     *            '/view/action'.
     */
    private void trackPageviewImpl(String pageId) {
    	if (module != null){
    		if (pageId != null && !pageId.isEmpty()){
    			pageId = module+"/"+pageId;
    		}
    		else {
    			pageId = module;    			
    		}
    	}
        String res = trackPageviewImpl(trackerId, pageId, domainName, allowAnchor);
		if (res != null) {
			GWT.log("GoogleAnalytics.trackPageview(" + trackerId + "," + pageId + "," + domainName +","+allowAnchor+") FAILED: " + res);
		}
    }

    /** Allow anchors in tracked URLs.
     *  As specified in http://code.google.com/apis/analytics/docs/gaJS/gaJSApiCampaignTracking.html#_gat.GA_Tracker_._setAllowAnchor
     *
     * @param allowAnchor
     */
    public void setAllowAnchor(boolean allowAnchor) {
        this.allowAnchor = allowAnchor;
    }

    /** Allow anchors in tracked URLs.
     *  As specified in http://code.google.com/apis/analytics/docs/gaJS/gaJSApiCampaignTracking.html#_gat.GA_Tracker_._setAllowAnchor
     *
     */
    public boolean isAllowAnchor() {
        return allowAnchor;
    }

	/** Native JS call to invoke _trackPageview from ga.js.
	 *
	 * @param trackerId
	 * @param pageId
	 * @param domainName
	 * @param allowAnchors
	 * @return
	 */
	private native String trackPageviewImpl(String trackerId, String pageId,
			String domainName, boolean allowAnchor)
	/*-{
       if (!$wnd._gat) {
           return "Tracker not found (running offline?)";
       }
       try {
           var pageTracker = $wnd._gat._getTracker(trackerId);
           if (!pageTracker) {
               return "Failed to get tracker for "+trackerId;
           }

           if (domainName) {
               pageTracker._setDomainName(domainName);
           }

           pageTracker._setAllowAnchor(allowAnchor);

           if (pageId) {
               pageTracker._trackPageview(pageId);
           } else {
               pageTracker._trackPageview();
           }
           return null;
       } catch(err) {
           return ""+err;
       }
   }-*/;
	
	public static void configure(String trackerId, String domainName, String module){
		INSTANCE = new GoogleAnalyticsTracker(trackerId, domainName, module);
	}
	
	public static void trackPageview(String pageId) {
		if (INSTANCE != null){
			INSTANCE.trackPageviewImpl(pageId);
		}
	}
}
