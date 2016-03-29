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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.view.GlobalModel;
import com.threewks.thundr.view.ViewModule;
import com.threewks.thundr.view.ViewResolver;
import com.threewks.thundr.view.ViewResolverRegistry;

import freemarker.template.Configuration;

public class FreemarkerModuleTest {
	private FreemarkerModule module = new FreemarkerModule();

	private UpdatableInjectionContext injectionContext = new InjectionContextImpl();
	private DependencyRegistry dependencyRegistry = new DependencyRegistry();
	private ViewResolverRegistry viewResolverRegistry = new ViewResolverRegistry();
	private GlobalModel globalModel = new GlobalModel();

	@Test
	public void shouldDependOnViewModule() {
		module.requires(dependencyRegistry);

		assertThat(dependencyRegistry.hasDependency(ViewModule.class), is(true));
	}

	@Test
	public void shouldInjectFreemarkerConfigurationAtInitialise() {
		module.initialise(injectionContext);

		assertThat(injectionContext.get(Configuration.class), is(notNullValue()));
	}

	@Test
	public void shouldAddViewResolverAtStart() {

		injectionContext.inject(viewResolverRegistry).as(ViewResolverRegistry.class);
		injectionContext.inject(globalModel).as(GlobalModel.class);
		injectionContext.inject(module.createConfiguration(injectionContext)).as(Configuration.class);

		module.start(injectionContext);

		ViewResolver<FreemarkerView> viewResolver = viewResolverRegistry.findViewResolver(new FreemarkerView("view"));
		assertThat(viewResolver, is(notNullValue()));

	}
}
