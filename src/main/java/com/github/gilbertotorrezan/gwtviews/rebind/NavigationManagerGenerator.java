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
package com.github.gilbertotorrezan.gwtviews.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.gilbertotorrezan.gwtviews.client.HasViews;
import com.github.gilbertotorrezan.gwtviews.client.NavigationManager;
import com.github.gilbertotorrezan.gwtviews.client.Presenter;
import com.github.gilbertotorrezan.gwtviews.client.URLInterceptor;
import com.github.gilbertotorrezan.gwtviews.client.View;
import com.github.gilbertotorrezan.gwtviews.client.ViewContainer;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Class used by the code generator to create the {@link NavigationManager}.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
public class NavigationManagerGenerator extends Generator {

	@SuppressWarnings("rawtypes")
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		
		final TypeOracle typeOracle = context.getTypeOracle();
		JClassType mainType = typeOracle.findType(typeName);
		
		PrintWriter writer = context.tryCreate(logger, mainType.getPackage().getName(), mainType.getName()+"Impl");
		if (writer == null){
			return mainType.getQualifiedSourceName()+"Impl";
		}
		
		ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(mainType.getPackage().getName(), mainType.getName()+"Impl");
		factory.addImplementedInterface(typeName);
		
		factory.addImport(Presenter.class.getPackage().getName()+".*");
		factory.addImport("com.google.gwt.user.client.History");
		factory.addImport("com.google.gwt.user.client.ui.Widget");
		factory.addImport("com.google.gwt.user.client.ui.Panel");
		factory.addImport("com.google.gwt.http.client.URL");
		factory.addImport("com.google.gwt.user.client.rpc.AsyncCallback");
		factory.addImport("com.google.gwt.core.client.*");
		factory.addImport("com.google.gwt.event.logical.shared.*");
		factory.addImport("java.util.*");
		
		SourceWriter sourceWriter = factory.createSourceWriter(context, writer);
		
		sourceWriter.println("//AUTO GENERATED FILE BY GWT-VIEWS AT " + getClass().getName() + ". DO NOT EDIT!\n");
		
		sourceWriter.println("private Panel rootContainer;");
		sourceWriter.println("private UserPresenceManager userPresenceManager;");
		sourceWriter.println("private final Map<String, Presenter<?>> presentersMap = new HashMap<>();");
		sourceWriter.println("private URLToken currentToken = new URLToken(\"\");");
		sourceWriter.println("private URLInterceptor currentInterceptor;\n");
		
		List<ViewPage> viewPages = new ArrayList<>();
		Map<String, HasViewPages> viewContainers = new HashMap<>();
		
		Set<ViewPage> viewsInNeedOfPresenters = new LinkedHashSet<>();
		Set<HasViewPages> containersInNeedOfPresenters = new LinkedHashSet<>();
		
		ViewPage defaultViewPage = null;
		ViewPage notFoundViewPage = null;
		HasViewPages defaultViewContainerPage = null;
		
		JClassType containerType = typeOracle.findType(HasViews.class.getName());
		
		JClassType[] types = typeOracle.getTypes();
		for (JClassType type : types) {
			if (type.isAnnotationPresent(View.class)){
				if (!type.isDefaultInstantiable()){
					logger.log(Type.WARN, type.getName()+" must have an empty constructor to be a valid "+View.class.getSimpleName()+".");
					continue;
				}
				View view = type.getAnnotation(View.class);
				ViewPage page = new ViewPage(view, type);
				viewPages.add(page);
				if (view.defaultView()){
					defaultViewPage = page;
				}
				if (view.notFoundView()){
					notFoundViewPage = page;
				}
			}
			else if (type.isAnnotationPresent(ViewContainer.class)){
				if (!type.isAssignableTo(containerType)){
					logger.log(Type.WARN, type.getName()+" must implement "+containerType.getName()+" to be a valid "+ViewContainer.class.getSimpleName()+".");
					continue;
				}
				if (!type.isDefaultInstantiable()){
					logger.log(Type.WARN, type.getName()+" must have an empty constructor to be a valid "+ViewContainer.class.getSimpleName()+".");
					continue;
				}
				ViewContainer container = type.getAnnotation(ViewContainer.class);
				HasViewPages hasViews = new HasViewPages(container, type);
				viewContainers.put(type.getName(), hasViews);
				if (container.defaultContainer()){
					defaultViewContainerPage = hasViews;
				}
			}
		}
		
		if (defaultViewPage == null){
			logger.log(Type.ERROR, "No default view page defined!");
			throw new UnableToCompleteException();
		}
		
		if (defaultViewContainerPage == null && viewContainers.size() > 1){
			logger.log(Type.ERROR, "There are more than one "+ViewContainer.class.getSimpleName()+" but no one is the default!");
			throw new UnableToCompleteException();
		}
		
		if (defaultViewContainerPage == null && !viewContainers.isEmpty()){
			defaultViewContainerPage = viewContainers.values().iterator().next();
		}
		
		sourceWriter.println("public void onValueChange(ValueChangeEvent<String> event){");
		sourceWriter.indent();
		sourceWriter.println("final URLToken token = new URLToken(event.getValue());");
		
		sourceWriter.println("if (currentInterceptor != null){");
		sourceWriter.indent();
				
		sourceWriter.println("History.newItem(currentToken.toString(), false);");
		sourceWriter.println("currentInterceptor.onUrlChanged(currentToken, token, new URLInterceptorCallback(){");
		sourceWriter.indent();
		sourceWriter.println("@Override\npublic void proceedTo(URLToken destination){");
		sourceWriter.indent();
		sourceWriter.println("History.newItem(destination.toString(), false);");
		sourceWriter.println("proceedToImpl(destination);");
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		sourceWriter.outdent();
		sourceWriter.println("});");
		
		sourceWriter.println("return;");
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		sourceWriter.println("this.proceedToImpl(token);");
		
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		sourceWriter.println("private void proceedToImpl(final URLToken token){");
		sourceWriter.indent();
		
		sourceWriter.println("this.currentToken = token;");
		
		sourceWriter.println("switch (token.getId()){");
		sourceWriter.indent();
		
		int defaultViewIndex = -1;
		int notFoundViewIndex = -1;
		
		for (int i = 0; i< viewPages.size(); i++) {
			ViewPage viewPage = viewPages.get(i);
			final View view = viewPage.getView();
			logger.log(Type.DEBUG, "Processing view " + view.value() + "...");
			
			if (view.defaultView()){
				defaultViewIndex = i;
				sourceWriter.println("case \"\":");
			}
			if (view.notFoundView()){
				notFoundViewIndex = i;
			}
			
			sourceWriter.println("case \"" + view.value() + "\": {");
			sourceWriter.indent();
			
			if (!view.publicAccess()){
				sourceWriter.println("if (userPresenceManager != null) {");
				sourceWriter.indent();
				
				if (view.rolesAllowed() != null && view.rolesAllowed().length > 0){
					String[] roles = view.rolesAllowed();
					StringBuilder params = new StringBuilder("new String[]{ ");
					String sep = "";
					for (String role : roles){
						params.append(sep).append("\"").append(role).append("\"");
						sep = ", ";
					}
					params.append(" }");
					sourceWriter.println("userPresenceManager.isUserInAnyRole(token, "+params.toString()+", new AsyncCallback<Boolean>(){");
				}
				else {
					sourceWriter.println("userPresenceManager.isUserLoggedIn(token, new AsyncCallback<Boolean>(){");
				}
				sourceWriter.indent();
				sourceWriter.println("@Override");
				sourceWriter.println("public void onSuccess(Boolean allowed){");
				sourceWriter.indent();
				sourceWriter.println("if (allowed == null || !allowed){");
				sourceWriter.indent();
				sourceWriter.println("History.newItem(\""+defaultViewPage.getView().value()+"&next='\"+URL.encodeQueryString(token.toString())+\"'\", true);");
				sourceWriter.outdent();
				sourceWriter.println("}");
				sourceWriter.println("else {");
				sourceWriter.indent();
				sourceWriter.println("showPresenter" + i + "(token);");
				sourceWriter.outdent();
				sourceWriter.println("}");
				sourceWriter.outdent();
				sourceWriter.println("}");
				sourceWriter.println("@Override");
				sourceWriter.println("public void onFailure(Throwable error){");
				sourceWriter.indent();
				sourceWriter.println("GWT.log(\"Error loading view: \" + error, error);");
				sourceWriter.println("History.newItem(\""+defaultViewPage.getView().value()+"&next='\"+URL.encodeQueryString(token.toString())+\"'\", true);");
				sourceWriter.outdent();
				sourceWriter.println("}");
				sourceWriter.outdent();
				sourceWriter.println("});");
				
				sourceWriter.println("return;");
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
			
			sourceWriter.println("showPresenter" + i + "(token);");
			sourceWriter.outdent();
			sourceWriter.println("}\nbreak;");
		}
		
		sourceWriter.println("default: {");
		sourceWriter.indent();
		
		if (notFoundViewPage != null){
			sourceWriter.println("History.newItem(\""+notFoundViewPage.getView().value()+"\", false);");
			sourceWriter.println("showPresenter" + notFoundViewIndex + "(new URLToken(\""+notFoundViewPage.getView().value()+"\"));");
		}
		else {
			sourceWriter.println("History.newItem(\""+defaultViewPage.getView().value()+"\", false);");
			sourceWriter.println("showPresenter" + defaultViewIndex + "(new URLToken(\""+defaultViewPage.getView().value()+"\"));");			
		}
		sourceWriter.outdent();
		sourceWriter.println("}\nbreak;");
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		for (int i = 0; i< viewPages.size(); i++) {
			ViewPage viewPage = viewPages.get(i);
			final View view = viewPage.getView();
			
			sourceWriter.println("/** Method to show the presenter of the "+view.value()+" view. */");
			sourceWriter.println("private void showPresenter"+i+"(final URLToken token) {");
			sourceWriter.indent();
			
			sourceWriter.println("GWT.runAsync(new RunAsyncCallback() {");
			sourceWriter.indent();
			
			sourceWriter.println("public void onSuccess() {");
			sourceWriter.indent();
			
			sourceWriter.println("GoogleAnalyticsTracker.trackPageview(token.getId());");
			sourceWriter.println("Presenter<?> presenter = presentersMap.get(\""+view.value()+"\");");
			sourceWriter.println("if (presenter == null) {");
			sourceWriter.indent();
			
			Class<? extends Presenter> customPresenter = view.customPresenter();
			if (!Presenter.class.equals(customPresenter)){
				sourceWriter.println("presenter = new "+customPresenter.getName()+"();");
			}
			else {
				viewsInNeedOfPresenters.add(viewPage);
				sourceWriter.println("presenter = (Presenter<?>) GWT.create("+viewPage.getType().getName()+"Presenter.class);");				
			}
			sourceWriter.println("presentersMap.put(\""+view.value()+"\", presenter);");				
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("Widget widget = presenter.getView(token);");
			
			Class<? extends URLInterceptor> urlInterceptor = view.urlInterceptor();
			if (!URLInterceptor.class.equals(urlInterceptor)){
				String interceptorName = urlInterceptor.getName(); 
				if (interceptorName.equals(viewPage.getType().getQualifiedSourceName())){
					sourceWriter.println("currentInterceptor = (URLInterceptor) widget;");
				}
				else if (interceptorName.equals(customPresenter.getName())){
					sourceWriter.println("currentInterceptor = (URLInterceptor) presenter;");
				}
				else {
					sourceWriter.println("currentInterceptor = new "+urlInterceptor.getName()+"();");
				}
			}
			else {
				sourceWriter.println("currentInterceptor = null;");
			}
			
			boolean usesViewContainer = view.usesViewContainer();
			if (usesViewContainer && !viewContainers.isEmpty()){
				Class<?> viewContainer = view.viewContainer();
				HasViewPages hasViews;
				if (HasViews.class.equals(viewContainer)){
					hasViews = defaultViewContainerPage;
				}
				else{
					hasViews = viewContainers.get(viewContainer.getName());
				}
				
				if (hasViews == null){
					logger.log(Type.ERROR, viewContainer.getName()+" is not a valid "+
							ViewContainer.class.getSimpleName()+" for "+View.class.getSimpleName()+" "+viewPage.getType().getQualifiedSourceName()+".");
					throw new UnableToCompleteException();
				}
				sourceWriter.println("Presenter<?> containerPresenter = presentersMap.get(\"@"+hasViews.getType().getName()+"\");");
				sourceWriter.println("if (containerPresenter == null) {");
				sourceWriter.indent();
				if (!Presenter.class.equals(hasViews.getContainer().customPresenter())){
					sourceWriter.println("containerPresenter = new "+hasViews.getContainer().customPresenter().getName()+"();");
				}
				else {
					containersInNeedOfPresenters.add(hasViews);
					sourceWriter.println("containerPresenter = (Presenter<?>) GWT.create("+hasViews.getType().getName()+"Presenter.class);");				
				}
				sourceWriter.println("presentersMap.put(\"@"+hasViews.getType().getName()+"\", containerPresenter);");
				sourceWriter.outdent();
				sourceWriter.println("}");
				sourceWriter.println("Widget container = containerPresenter.getView(token);");
				sourceWriter.println("(("+HasViews.class.getName()+") container).showView(token, widget);");
				sourceWriter.println("if (container.getParent() == null){");
				sourceWriter.indent();
				sourceWriter.println("rootContainer.clear();");
				sourceWriter.println("rootContainer.add(container);");
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
			else {
				sourceWriter.println("rootContainer.clear();");
				sourceWriter.println("rootContainer.add(widget);");
			}
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("public void onFailure(Throwable reason) { GWT.log(\"Error on loading presenter with token: \"+token, reason); }");
			sourceWriter.outdent();
			sourceWriter.println("});");
			
			sourceWriter.outdent();
			sourceWriter.println("}\n");
		}
		
		sourceWriter.println("@Override\npublic void clearCache() {");
		sourceWriter.indent();
		sourceWriter.println("presentersMap.clear();");
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		sourceWriter.println("@Override\npublic void setRootContainer(Panel container) {");
		sourceWriter.indent();
		sourceWriter.println("this.rootContainer = container;");
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		sourceWriter.println("@Override\npublic void setUserPresenceManager(UserPresenceManager umanager) {");
		sourceWriter.indent();
		sourceWriter.println("this.userPresenceManager = umanager;");
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		sourceWriter.println("//View presenters");
		for (ViewPage viewPage : viewsInNeedOfPresenters) {
			sourceWriter.println("public static interface "+viewPage.getType().getName()+"Presenter extends Presenter<"+
					viewPage.getType().getQualifiedSourceName()+">{}");
		}
		if (!containersInNeedOfPresenters.isEmpty()){
			sourceWriter.println("\n//ViewContainer presenters");
			for (HasViewPages container : containersInNeedOfPresenters) {
				sourceWriter.println("public static interface "+container.getType().getName()+"Presenter extends Presenter<"+
						container.getType().getQualifiedSourceName()+">{}");			
			}
		}
		
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		context.commit(logger, writer);
		
		return factory.getCreatedClassName();
	}
	
	private class ViewPage {
		private final View view;
		private final JClassType type;
		
		public ViewPage(View view, JClassType type) {
			this.view = view;
			this.type = type;
		}
		public View getView() {
			return view;
		}
		public JClassType getType() {
			return type;
		}
	}
	
	private class HasViewPages {
		private final ViewContainer container;
		private final JClassType type;
		
		public HasViewPages(ViewContainer container, JClassType type) {
			this.container = container;
			this.type = type;
		}
		public ViewContainer getContainer() {
			return container;
		}
		public JClassType getType() {
			return type;
		}
	}

}
