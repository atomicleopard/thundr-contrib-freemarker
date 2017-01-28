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

import java.io.IOException;
import java.io.OutputStreamWriter;

import com.threewks.thundr.request.Request;
import com.threewks.thundr.request.Response;
import com.threewks.thundr.view.BaseView;
import com.threewks.thundr.view.GlobalModel;
import com.threewks.thundr.view.Model;
import com.threewks.thundr.view.ViewResolutionException;
import com.threewks.thundr.view.ViewResolver;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerViewResolver implements ViewResolver<FreemarkerView> {
	private Configuration configuration;
	private GlobalModel globalModel;

	public FreemarkerViewResolver(Configuration configuration, GlobalModel globalModel) {
		this.configuration = configuration;
		this.globalModel = globalModel;
	}

	public GlobalModel getGlobalModel() {
		return globalModel;
	}

	public Configuration getFreemarkerConfiguration() {
		return configuration;
	}

	@Override
	public void resolve(Request req, Response resp, FreemarkerView viewResult) {
		String view = viewResult.getView();
		try {
			Model model = Model.combine(globalModel, req.getAllData(), viewResult.getModel());
			Template template = configuration.getTemplate(view);
			BaseView.applyToResponse(viewResult, resp);
			template.process(model, new OutputStreamWriter(resp.getOutputStream()));
		} catch (IOException | TemplateException e) {
			throw new ViewResolutionException(e, "Failed to render Freemarker template '%s': %s", view, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
