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
import java.util.Date;

import com.github.gilbertotorrezan.gwtviews.client.AutoPresenter;
import com.github.gilbertotorrezan.gwtviews.client.CachePolicy;
import com.github.gilbertotorrezan.gwtviews.client.CachedPresenter;
import com.github.gilbertotorrezan.gwtviews.client.Presenter;
import com.github.gilbertotorrezan.gwtviews.client.View;
import com.github.gilbertotorrezan.gwtviews.client.ViewContainer;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Class used by the code generator to create the default {@link Presenter}s of Views and ViewContainers.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 * @see AutoPresenter
 */
public class PresenterGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {

		final TypeOracle typeOracle = context.getTypeOracle();
		JClassType mainType = typeOracle.findType(typeName);
		
		JParameterizedType parameterized = mainType.getImplementedInterfaces()[0].isParameterized();
		JClassType viewType = parameterized.getTypeArgs()[0];
		final String className = viewType.getQualifiedSourceName();

		String name = mainType.getName().substring(mainType.getName().lastIndexOf('.')+1);
		name = name.substring(0, name.length() - "Presenter".length());
		name = name + "_" + name + "PresenterImpl";

		PrintWriter writer = context.tryCreate(logger, viewType.getPackage().getName(), name);
		if (writer == null){
			return viewType.getPackage().getName() + "." + name;
		}

		ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(viewType.getPackage().getName(), name);
		factory.addImplementedInterface(AutoPresenter.class.getName());

		factory.addImport(Presenter.class.getPackage().getName()+".*");
		factory.addImport("com.google.gwt.user.client.History");
		factory.addImport("com.google.gwt.core.client.GWT");
		factory.addImport("com.google.gwt.user.client.ui.Widget");
		factory.addImport("javax.annotation.Generated");
		factory.addImport("java.util.*");
		
		factory.addAnnotationDeclaration("@Generated("
				+ "value=\""+PresenterGenerator.class.getName()+"\", "
				+ "date=\""+new Date()+"\", "
				+ "comments=\"Generated by GWT-Views project.\")");
		
		View view = viewType.getAnnotation(View.class);
		ViewContainer viewContainer = viewType.getAnnotation(ViewContainer.class);
		
		CachePolicy cache;
		JClassType injectorType = null;
		String injectorMethod = null;
		if (view == null){
			cache = CachePolicy.ALWAYS;
		}
		else {
			cache = view.cache();
		}
		
		if (cache == CachePolicy.SAME_URL){
			factory.setSuperclass(CachedPresenter.class.getName());
		}

		SourceWriter sourceWriter = factory.createSourceWriter(context, writer);

		sourceWriter.println("//AUTO GENERATED FILE BY GWT-VIEWS AT " + getClass().getName() + ". DO NOT EDIT!\n");

		if (cache == CachePolicy.ALWAYS){
			sourceWriter.println("private Widget view; //the cached view");
		}
		
		Class<?> injector = view == null ? void.class : view.injector();
		if (injector.equals(void.class)){
			injector = viewContainer == null ? void.class : viewContainer.injector();
		}
		
		if (!injector.equals(void.class)){
			try {
				injectorType = typeOracle.findType(injector.getName());
				injectorMethod = view != null ? view.injectorMethod() : viewContainer.injectorMethod();
				injectorMethod = getInjectorMethod(logger, injectorType, injectorMethod, className);
			}
			catch (Exception e) {
				logger.log(Type.ERROR, "Error loading the injector class \"" + injector.getName() + "\": " + e, e);
				throw new UnableToCompleteException();
			}
		}
		
		if (cache == CachePolicy.SAME_URL){
			sourceWriter.println("\n@Override\npublic Widget createNewView(URLToken url) {");
		}
		else {
			sourceWriter.println("\n@Override\npublic Widget getView(URLToken url) {");
		}
		sourceWriter.indent();
		
		switch (cache) {
		case NEVER: {
			sourceWriter.println("//code for the CachePolicy.NEVER:");
			printInjectorMethod(sourceWriter, className, injectorType, injectorMethod);
		}
		break;
		case ALWAYS: {
			sourceWriter.println("//code for the CachePolicy.ALWAYS:");
			sourceWriter.println("if (this.view == null) {");
			sourceWriter.indent();
			printInjectorMethod(sourceWriter, className, injectorType, injectorMethod);
			sourceWriter.println("this.view = view;");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		break;
		case SAME_URL: {
			sourceWriter.println("//code for the CachePolicy.SAME_URL:");
			printInjectorMethod(sourceWriter, className, injectorType, injectorMethod);
		}
		break;
		}
		
		sourceWriter.println("return view;");
		sourceWriter.outdent();
		sourceWriter.println("}\n");

		sourceWriter.outdent();
		sourceWriter.println("}\n");

		context.commit(logger, writer);

		return factory.getCreatedClassName();
	}

	private String getInjectorMethod(TreeLogger logger, JClassType injector, String injectorMethod, String className) throws UnableToCompleteException {
		if (injectorMethod != null && !injectorMethod.isEmpty()){
			try {
				injector.getMethod(injectorMethod, new JType[0]);
			}
			catch (NotFoundException e) {
				logger.log(Type.WARN, "The injector method \"" + injectorMethod + "\" was not found on class " + injector.getQualifiedSourceName());
				//a compiler error will be trigged if the method really doesn't exist
			}
			return injectorMethod;
		}
		else {
			String methodName = null;
			JMethod[] methods = injector.getInheritableMethods();
			for (JMethod method : methods) {
				JType returnType = method.getReturnType();
				if (returnType.getQualifiedSourceName().equals(className)){
					if (methodName != null){
						logger.log(Type.ERROR, "The injector " + injector.getName() + " has more than one method with " + className 
								+ " as return type. Use the \"injectorMethod\" property to specify which one should be used.");
						throw new UnableToCompleteException();
					}
					methodName = method.getName();
				}
			}
			if (methodName == null){
				logger.log(Type.INFO, "The injector " + injector.getName() + " has no methods with " + className 
								+ " as return type. The View will not be injected.");
				return null;
			}
			return methodName;
		}
	}
	
	private void printInjectorMethod(SourceWriter sourceWriter, String className, JClassType injectorType, String injectorMethod){
		if (injectorType != null && injectorMethod != null){
			sourceWriter.println(injectorType.getQualifiedSourceName() + " injector = NavigationManager.getInjectorInstance(" + injectorType.getQualifiedSourceName() + ".class);");
			sourceWriter.println("if (injector == null) injector = GWT.create(" + injectorType.getQualifiedSourceName() + ".class);");
			sourceWriter.println("Widget view = (" + className + ") injector." + injectorMethod + "();");
		}
		else {
			sourceWriter.println("Widget view = GWT.create( " + className + ".class);");				
		}
	}

}
