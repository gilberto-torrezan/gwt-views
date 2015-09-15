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

import com.arcbees.analytics.client.AnalyticsModule;
import com.google.gwt.inject.client.AbstractGinModule;

/**
 * {@link AbstractGinModule} used to configure the {@link AnalyticsModule}, used internally by the
 * {@link UniversalAnalyticsTracker}. 
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.3.0
 * @see UniversalAnalyticsTracker#configure(String)
 */
public class UniversalAnalyticsTrackerModule extends AbstractGinModule {
	
	@Override
	protected void configure() {
		install(new AnalyticsModule.Builder("").autoCreate(false).build());
	}
}
