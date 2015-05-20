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
import java.lang.reflect.Method;

import com.github.gilbertotorrezan.gwtviews.client.CachePolicy;
import com.github.gilbertotorrezan.gwtviews.client.Presenter;
import com.github.gilbertotorrezan.gwtviews.client.View;
import com.github.gilbertotorrezan.gwtviews.client.ViewContainer;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Class used by the code generator to create the default {@link Presenter}s of Views and ViewContainers.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
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
		factory.addImplementedInterface(Presenter.class.getName());

		factory.addImport(Presenter.class.getPackage().getName()+".*");
		factory.addImport("com.google.gwt.user.client.History");
		factory.addImport("com.google.gwt.core.client.GWT");
		factory.addImport("com.google.gwt.user.client.ui.Widget");
		factory.addImport("java.util.*");

		SourceWriter sourceWriter = factory.createSourceWriter(context, writer);

		sourceWriter.println("//AUTO GENERATED FILE BY GWT-VIEWS AT " + getClass().getName() + ". DO NOT EDIT!\n");

		View view = viewType.getAnnotation(View.class);
		ViewContainer viewContainer = viewType.getAnnotation(ViewContainer.class);
		
		CachePolicy cache;
		String injectorMethod = null;
		if (view == null){
			cache = CachePolicy.ALWAYS;
		}
		else {
			cache = view.cache();
		}
		
		switch (cache) {
		case SAME_URL: sourceWriter.println("private String lastUrl;"); //not break!
		case ALWAYS: sourceWriter.println("private Widget view; //the cached view"); break;
		case NEVER: break;
		}
		
		Class<?> injector = view == null ? void.class : view.injector();
		if (injector.equals(void.class)){
			injector = viewContainer == null ? void.class : viewContainer.injector();
		}
		
		if (!injector.equals(void.class)){
			injectorMethod = getInjectorMethod(logger, injector, injectorMethod, className);
			if (injectorMethod != null){
				sourceWriter.println("private final " + injector.getName() + " injector = GWT.create(" + injector.getName() + ".class);");				
			}
		}
		
		sourceWriter.println("\n@Override\npublic Widget getView(URLToken url) {");
		sourceWriter.indent();
		
		switch (cache) {
		case NEVER: {
			sourceWriter.println("//code for the CachePolicy.NEVER:");
			sourceWriter.print("Widget ");
			printInjectorMethod(sourceWriter, className, injectorMethod);
		}
		break;
		case ALWAYS: {
			sourceWriter.println("//code for the CachePolicy.ALWAYS:");
			sourceWriter.println("if (view == null) {");
			sourceWriter.indent();
			printInjectorMethod(sourceWriter, className, injectorMethod);
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		break;
		case SAME_URL: {
			sourceWriter.println("//code for the CachePolicy.SAME_URL:");
			sourceWriter.println("String token = url.toString();");
			sourceWriter.println("if (view == null || lastUrl == null || !lastUrl.equals(token)) {");
			sourceWriter.indent();
			printInjectorMethod(sourceWriter, className, injectorMethod);
			sourceWriter.println("lastUrl = token;");
			sourceWriter.outdent();
			sourceWriter.println("}");
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

	private String getInjectorMethod(TreeLogger logger, Class<?> injector, String injectorMethod, String className) throws UnableToCompleteException {
		if (injectorMethod != null && !injectorMethod.isEmpty()){
			try {
				injector.getDeclaredMethod(injectorMethod);
				return injectorMethod;
			}
			catch (NoSuchMethodException e) {
				logger.log(Type.ERROR, "The injector method \"" + injectorMethod + "\" was not found on class " + injector.getName(), e);
				throw new UnableToCompleteException(); 
			}
		}
		else {
			String methodName = null;
			Method[] methods = injector.getDeclaredMethods();
			for (Method method : methods) {
				Class<?> returnType = method.getReturnType();
				if (returnType.getName().equals(className)){
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
								+ " as return type. The View will not be injected - it will be created with the \"new\" operator.");
				return null;
			}
			return methodName;
		}
	}
	
	private void printInjectorMethod(SourceWriter sourceWriter, String className, String injectorMethod){
		if (injectorMethod != null){
			sourceWriter.println("view = (" + className + ") injector." + injectorMethod + "();");
		}
		else {
			sourceWriter.println("view = new " + className + "();");				
		}
	}

}
