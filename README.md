# GWT Views

[![Join the chat at https://gitter.im/gilberto-torrezan/gwt-views](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/gilberto-torrezan/gwt-views?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A simple but powerful View controller for GWT.

## What does it do?

GWT Views is a project to simplify the navigation of your GWT application. For example, supose you want an unauthenticated page (possibily the login page) for the application, and a bunch of other pages that some users can see, and some can't - like a home page for the default users and an admin page for system administrators.

GWT Views solves that use case by using Java annotations on your Views (Widgets that functions as the page itself).

For example:

This is the `LoginView`, which can be accessed by anybody, and is the default view of the application (the first to be shown):

```java
@View(value = "login", publicAccess = true, defaultView = true)
public class LoginView extends Composite {
//... your code, you can use UIBinder, procedural UI, whatever you like
```

This is the `HomeView`, which can be accessed by any logged user at the URL `#home`:

```java
@View("home")
public class HomeView extends Composite {
//...
```

And this is the `AdminView`, which can be accessed only by logged users with the "ADMIN" role at the URL `#admin`:

```java
@View(value = "admin", rolesAllowed = "ADMIN")
public class AdminView extends Composite {
//...
```

## Features

### ViewContainer

Views can be defined using just the `@View` annotation. If you need a container with header, sidebars, footers and so on, and want the views to be shown inside that container, you can use a `ViewContainer`:

```java
@ViewContainer
public class MainViewConatiner extends Composite implements HasViews {
//...
	@Override
	public void showView(URLToken token, Widget view) {
		//put the view wherever you like in your layout
	}
```

The framework takes care of initiating the container and the views when needed. By default, all Views will be added into the `ViewContainer`. If you want a `View` to be shown outside the `ViewContainer`, you can use:

```java
@View(value = "other", usesViewContainer = false)
```
	
You can use more than a `ViewContainer` as well. In that case, you have to specify which `View` will use each `ViewContainer`:

```java
@View(value = "one", viewContainer = ViewContainerOne.class)

@View(value = "two", viewContainer = ViewContainerTwo.class)
```
	
### UserPresenceManager

When using authenticated Views, the framework need to know if the user has the rights to see the page he is requesting. To do so, you need to setup your `UserPresenceManager`:

```java
public class MyApp implements EntryPoint {
	@Override
	public void onModuleLoad() {
		NavigationManager.setUserPresenceManager(new MyUserPresenceManager());
	}
}
```
	
The `MyUserPresenceManager` class only need to implement two methods:

```java
public class MyUserPresenceManager implements UserPresenceManager {
	
	@Override
	public void isUserLoggedIn(URLToken url, AsyncCallback<Boolean> callback) {
		//call callback.onSucess(true) if the user is logged in, or callback.onSucess(false) or callback.onFailure(Throwable) otherwise. 
	}

	@Override
	public void isUserInAnyRole(URLToken url, String[] roles, AsyncCallback<Boolean> callback) {
		//call callback.onSucess(true) if the user is in any of the roles, or callback.onSucess(false) or callback.onFailure(Throwable) otherwise.
	}
}
```
 
The `isUserLoggedIn` and `isUserInAnyRole` are asynchronous by design. That is the point you will be able to communicate with the server to get more information about your user, if needed.

### UniversalAnalyticsTracker

The framework can log an event at Google Analytics at each change of your Views. To enable that, just configure your tracker ID:

```java
UniversalAnalyticsTracker.configure("MY-TRACKER-ID"); //usually in the "UA-XXXX-Y" format 
```

You don't need to include the `analytics.js` into your host page - the framework includes it automatically when you call the `configure` method.

The internal implementation of the analytics uses the [universal-analytics](https://github.com/ArcBees/universal-analytics) lib from ArcBees. You can setup advanced properties to your Analytics instance if you wish:

```java
public class MyApp implements EntryPoint {
	@Override
	public void onModuleLoad() {
		
		//configuring the universal analytics tracker
		Analytics analytics = //create your instance the way you want it, setting up all the properties needed for your project
		UniversalAnalyticsTracker.setAnalytics(analytics);
		
	}
}
```

You can reuse the Analytics object (either created by the `configure` or set by `setAnalytics` methods) to send custom data:

```java
Analytics analytics = UniversalAnalyticsTracker.getAnalytics();
analytics.sendEvent("button", "click").eventLabel("my cool button was clicked").go();
```

You can also set an userId as well to your tracker, to enable per-user analytics. To do so, simply call:

 ```java
 UniversalAnalyticsTracker.configure("MY-TRACKER-ID", "userId"); //the userId must be a string that you can use to identify your user at the analytics dashboard
 ```
 
You can find more about the universal analytics at the [Google Developers](https://developers.google.com/analytics/devguides/collection/analyticsjs/) page.

### Caching

The Views can be cached by the Presenters to to improve performance and save the state of the View when the user leaves the page. By default, the Views are cached when there are no changes in the URL that points to the View, like URL parameters. In other words, if your user goes to "#1", and then to the View "#2", and goes back to "#1" (by either pressing the back button of the browser, or typing directly at the URL bar, or clicking on an anchor), the "#1" View will be at the same state as when he left it. But if the user access "#1&something", a new View instance is created.

The cache behavior is managed by the `cache` property (the default behavior is CachePolicy.SAME_URL):

```java
@View(value = "login", publicAccess = true, defaultView = true, cache = CachePolicy.NEVER) //disables the caching
public class LoginView extends Composite {
//...
```

```java
@View(value = "cached", cache = CachePolicy.ALWAYS) //always cache, even with different URL parameters
public class CachedView extends Composite {
//...
```

### Dependency injection

If you use a dependency injection framework such as [GIN](https://code.google.com/p/google-gin/), you can setup your Views and ViewContainers to be injected. To do so, just use the `injector` property:

```java
@View(value = "injected", injector = MyInjector.class)
public class InjectedView extends Composite {
//...
```

If your injector has more than one method that returns the same type of your View, you can use the `injectorMethod` to define which method should be called:

```java
@View(value = "injected", injector = MyInjector.class, injectorMethod = "getInjectedView")
public class InjectedView extends Composite {
//...
```

To set a injector instance to be used across the application, you can use the `setInjectorInstance` from the NavigationManager:

```java
MyInjector injectorInstance = GWT.create(MyInjector.class); //or you can create it in another way
NavigationManager.setInjectorInstance(MyInjector.class, injectorInstance);
```

When not set, a new injector is created every time a view is invoked.

### Custom Presenters

If you want a specific functionality for your View that demands a custom `Presenter`, you can setup it too:

```java
@View(value = "custom", customPresenter = MyPresenter.class)
public class CustomView extends Composite {
//...
```
	
Your `Presenter` only needs to implement one method:

```java
public class MyPresenter implements Presenter<CustomView> {
	
	@Override
	public CustomView getView(URLToken url){
		//creates and returns your CustomView. You have to handle the caching and the injection if needed, but the code splitting is still garanteed by the framework.
	}
```

Please note that if you use a custom Presenter, you are responsible for the caching and the injection of your created View. The `cache` and `injector` properties have no effect.

Hint: you can extend the `CachedPresenter` class to get the CachePolicy.SAME_URL behvior in your custom Presenter.

### Code splitting

The framework automatically creates the Presenters in a way that takes care of the code-splitting for you. You don't need to worry about it, even if you use a custom Presenter. Each Presenter and the View it creates are put on the same code fragment, so each page can be handled separately from each other. You can control the number of code fragments your application produces by tweaking the `-XfragmentCount` property of the GWT compiler. Take a look at this [link](http://www.gwtproject.org/articles/fragment_merging.html) for more info about fragment merging.

### Redirection

When the user tries to access a page he is not allowed to (because be doesn't have the desired credentials, or because the session is expired, and so on), he is redirected to the defaultView by default.

### URLInterceptors

If at some point you want to show a confirmation dialog to the user before he leave some View, you can use an `URLInterceptor`:

```java
@View(value = "interceptable", urlInterceptor = MyInterceptor.class)
public class InterceptableView extends Composite {
//...
}

public class MyInterceptor implements URLInterceptor {
	@Override
	public void onUrlChanged(URLToken current, URLToken destination, URLInterceptorCallback callback) {
		if (Window.confirm("Do you really want to exit? This page is so nice!")){
			callback.proceedTo(destination);
		}
	}
}
```

Note that you can change the final destination, or parameters of it, before proceeding. If you want to stay at the same page, just don't call anything.

You can use the `View` or the custom `Presenter` as `URLInterceptor` as well. In those cases, the same object instance will be used to intercept the URL changes:

```java
@View(value = "interceptable", urlInterceptor = InterceptableView.class)
public class InterceptableView extends Composite implements URLInterceptor {
	@Override
	public void onUrlChanged(URLToken current, URLToken destination, URLInterceptorCallback callback) {
		if (Window.confirm("Do you really want to exit? This page is so nice!")){
			callback.proceedTo(destination);
		}
	}
```

### 404 View

You can setup a View to be used when no other View could be found that matches the URL token. That's the "notFoundView":

```java
@View(value = "404", notFoundView = true)
public class NotFoundView extends Composite {
//...
```

## Setup

### For Maven users:

Add the dependency of the GWT Views in your project:

```xml
<dependency>
	<groupId>com.github.gilberto-torrezan</groupId>
	<artifactId>gwt-views</artifactId>
	<version>1.3.2</version>
	<scope>provided</scope>
</dependency>
```

You can download the jar directly from [The Central Repository](http://search.maven.org/#search|gav|1|g%3A%22com.github.gilberto-torrezan%22%20AND%20a%3A%22gwt-views%22) as well. Please note that by downloading separately you need to add all the dependencies manually.

### GWT module

Add the gwtviews module to your project.gwt.xml:

```xml
<inherits name="com.github.gilbertotorrezan.gwtviews.gwtviews"/>
```
	
### Code setup

At your module `EntryPoint`, start the framework:

```java
public class MyApp implements EntryPoint {
	@Override
	public void onModuleLoad() {
		
		//configuring the universal analytics tracker
		UniversalAnalyticsTracker.configure("MY-TRACKER-ID");
		
		//configuring the user presence manager			
		NavigationManager.setUserPresenceManager(new MyUserPresenceManager());
		
		//creating the root container of the application
		RootLayoutPanel root = RootLayoutPanel.get();
		
		//starting up
		NavigationManager.start(root);
	}
}
```

When the user logs out the application, it is a good idea to clear the cache of Presenters at your NavigationManager, to ensure a new session (with a possible new user) will use renewed Views, and not cached ones from previous sessions. To do so, just call the `clearCache` method:

```java
// when the user clicks on a "logout" button
NavigationManager.clearCache();
```

## Javadoc

You can browse the project javadoc at javadoc.io:

[http://www.javadoc.io/doc/com.github.gilberto-torrezan/gwt-views](http://www.javadoc.io/doc/com.github.gilberto-torrezan/gwt-views)
	
## License

The project is licensed under the terms of The MIT License (MIT). So, yes, you can use it wherever you want ;-)

## FAQ

### What happens when the user refreshs the page (F5)?

The state of the application is destroyed, and you have to rebuild the page from scratch. The first thing you'll notice is the framework calling the `isUserLoggedIn` or `isUserInAnyRole` methods of your UserPresenceManager to know if the current page can be shown. At that time you should ask the server for the user data, and then rebuild the application.

### And if the user presses the back button? Or types a URL directly at the address bar?

That's the main reason to use GWT Views: the workflow of the interaction between the address bar and your views is completely abstracted from you. Everything just work, you only need to take care of creating nice Views for your users ;-)

### Which version of GWT is supported?

GWT Views is built using GWT 2.7.0 and Java 7 syntax.

### Why not using Activities and Places from the standard GWT?

That's because the ammount of boilerplate code needed to address common use cases. GWT Views uses simple annotations to get the job done, auto generating all the boilerplate to you under the hood.

### And why not the GWTP framework?

Because it is super complex with a steep learning curve. It is more complete than GWT Views for sure, but if your use cases aren't that complex you can use the humble and simple GWT Views.
