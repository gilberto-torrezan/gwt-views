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

import com.github.gilbertotorrezan.gwtviews.client.Presenter;
import com.github.gilbertotorrezan.gwtviews.client.View;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
public class PresenterGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		
		final TypeOracle typeOracle = context.getTypeOracle();
		JClassType mainType = typeOracle.findType(typeName);
		
		String name = mainType.getName().substring(mainType.getName().lastIndexOf('.')+1)+"Impl";
		
		PrintWriter writer = context.tryCreate(logger, mainType.getPackage().getName(), name);
		if (writer == null){
			return mainType.getPackage().getName() + "." + name;
		}
		
		ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(mainType.getPackage().getName(), name);
		factory.addImplementedInterface(Presenter.class.getName());
		
		factory.addImport(Presenter.class.getPackage().getName()+".*");
		factory.addImport("com.google.gwt.user.client.History");
		factory.addImport("com.google.gwt.user.client.ui.Widget");
		factory.addImport("java.util.*");
		
		mainType = mainType.getImplementedInterfaces()[0];
		
		JParameterizedType parameterized = mainType.isParameterized();
		JClassType viewType = parameterized.getTypeArgs()[0];
		
		SourceWriter sourceWriter = factory.createSourceWriter(context, writer);
		
		sourceWriter.println("//AUTO GENERATED FILE BY GWT-VIEWS. DO NOT EDIT!\n");
		
		View view = viewType.getAnnotation(View.class);
		boolean cacheable;
		if (view == null){
			cacheable = true;
		}
		else {
			cacheable = view.cacheable();
		}
		
		if (cacheable){
			sourceWriter.println("private Widget view;");
		}
		sourceWriter.println("\n@Override\npublic Widget getWidget() {");
		sourceWriter.indent();
		if (cacheable){
			sourceWriter.println("if (view == null) {");
			sourceWriter.indent();
			sourceWriter.println("view = new "+viewType.getQualifiedSourceName()+"();");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("return view;");
		}
		else {
			sourceWriter.println("return new "+viewType.getQualifiedSourceName()+"();");
		}			
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		sourceWriter.outdent();
		sourceWriter.println("}\n");
		
		
		context.commit(logger, writer);
		
		return factory.getCreatedClassName();
	}
	
}
