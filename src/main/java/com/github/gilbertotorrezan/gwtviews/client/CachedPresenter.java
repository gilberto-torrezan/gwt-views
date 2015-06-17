package com.github.gilbertotorrezan.gwtviews.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

/**
 * Utility Presenter that implements the {@link CachePolicy#SAME_URL} policy. When using this class,
 * override the {@link #createNewView(URLToken)} method instead of the {@link #getView(URLToken)} to use the cache.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.2.0
 */
public abstract class CachedPresenter<T extends Widget> implements Presenter<T> {
	
	protected Map<String, T> viewCache = new HashMap<>();

	@Override
	public T getView(URLToken url) {
		String token = url.toString();
		T cached = viewCache.get(token);
		if (cached == null){
			cached = createNewView(url);
			viewCache.put(token, cached);
		}
		return cached;
	}
	
	/**
	 * Called when a new View needs to be created.
	 * 
	 * @param url The current URL state of the application
	 * @return The created View to be shown at the page
	 */
	public abstract T createNewView(URLToken url);

}
