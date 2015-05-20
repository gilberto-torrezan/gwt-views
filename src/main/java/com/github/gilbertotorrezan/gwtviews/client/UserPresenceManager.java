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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The UserPresenceManager is called by the {@link NavigationManager} to determine which {@link View}s
 * the user is alowed to access. Only non-public Views are subject of this class. 
 * The {@link #isUserInAnyRole(URLToken, String[], AsyncCallback)} method is asynchronous 
 * to allow a communication with the server if needed.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 * @see NavigationManager#setUserPresenceManager(UserPresenceManager)
 * @see View#publicAccess()
 * @see View#rolesAllowed()
 */
public interface UserPresenceManager {
	
	/**
	 * Returns if the user is logged in and in any of the defined roles. Remember to access the callback to inform if the user is in 
	 * any of the roles by calling {@link AsyncCallback#onSuccess(Object)}.
	 * 
	 * @param url The current URLToken.
	 * @param roles The roles defined at {@link View#rolesAllowed()} or empty array (never <code>null</code>).
	 * @param callback The callback of the operation. Use the {@link AsyncCallback#onSuccess(Object)} to inform if the user is any role.
	 */
	void isUserInAnyRole(URLToken url, String[] roles, AsyncCallback<Boolean> callback);

}
