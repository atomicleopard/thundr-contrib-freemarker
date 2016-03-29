/*
 * This file is a part of thundr-contrib-freemarker, a software library from Atomic Leopard.
 *
 * Copyright (C) 2016 Atomic Leopard Pty Ltd, <nick@atomicleopard.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomicleopard.thundr.freemarker;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.threewks.thundr.configuration.Environment;
import com.threewks.thundr.injection.InjectionContext;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.view.GlobalModel;
import com.threewks.thundr.view.ViewModule;
import com.threewks.thundr.view.ViewResolverRegistry;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Module class for thundr-contrib-freemarker. Add it to the {@link DependencyRegistry} in your ApplicationModule
 * like this:
 * 
 * <pre>
 * <code>
 * &#64;Override
	public void requires(DependencyRegistry dependencyRegistry) {
		dependencyRegistry.addDependency(FreemarkerModule.class);
	}
	
 * </code>
 * </pre>
 */
public class FreemarkerModule implements com.threewks.thundr.injection.Module {
	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		dependencyRegistry.addDependency(ViewModule.class);
	}

	@Override
	public void initialise(UpdatableInjectionContext injectionContext) {
		Configuration configuration = createConfiguration(injectionContext);
		injectionContext.inject(configuration).as(Configuration.class);
	}

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
	}

	@Override
	public void start(UpdatableInjectionContext injectionContext) {
		ViewResolverRegistry viewResolverRegistry = injectionContext.get(ViewResolverRegistry.class);
		GlobalModel globalModel = injectionContext.get(GlobalModel.class);
		Configuration configuration = injectionContext.get(Configuration.class);
		viewResolverRegistry.addResolver(FreemarkerView.class, createViewResolver(globalModel, configuration));
	}

	protected FreemarkerViewResolver createViewResolver(GlobalModel globalModel, Configuration configuration) {
		FreemarkerViewResolver freemarkerViewResolver = new FreemarkerViewResolver(configuration, globalModel);
		return freemarkerViewResolver;
	}

	@Override
	public void stop(InjectionContext injectionContext) {
	}

	public Configuration createConfiguration(UpdatableInjectionContext injectionContext) {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_24);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);

		List<TemplateLoader> loaders = createTemplateLoaders(injectionContext);
		MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(loaders.toArray(new TemplateLoader[0]));
		cfg.setTemplateLoader(multiTemplateLoader);
		if (Environment.is(Environment.DEV)) {
			cfg.setCacheStorage(new NullCacheStorage());
		}
		return cfg;
	}

	protected List<TemplateLoader> createTemplateLoaders(UpdatableInjectionContext injectionContext) {
		List<TemplateLoader> loaders = new ArrayList<>();
		ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(FreemarkerModule.class.getClassLoader(), "/");
		loaders.add(classTemplateLoader);

		ServletContext servletContext = injectionContext == null ? null : injectionContext.get(ServletContext.class);
		if (servletContext != null) {
			loaders.add(new WebappTemplateLoader(servletContext, "/WEB-INF/"));
		}
		return loaders;
	}
}
