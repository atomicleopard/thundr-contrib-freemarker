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

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.http.StatusCode;
import com.threewks.thundr.view.TemplateView;
import com.threewks.thundr.view.View;

import jodd.util.StringPool;

public class FreemarkerView extends TemplateView<FreemarkerView> implements View {

	public FreemarkerView(String view) {
		this(view, Collections.<String, Object> emptyMap());
	}

	public FreemarkerView(String view, Map<String, Object> model) {
		super(view, model);
		withContentType(ContentType.TextHtml.value());
		withCharacterEncoding(StringPool.UTF_8);
		withStatusCode(StatusCode.OK);
	}

	@Override
	public String getView() {
		return completeViewName(view);
	}

	private String completeViewName(String view) {
		if (!StringUtils.startsWith(view, "/")) {
			view = "/ftl/" + view;
		}
		if (!StringUtils.contains(view, ".")) {
			view = view + ".ftl";
		}
		return view;
	}

	@Override
	public String toString() {
		String completeView = completeViewName(view);
		return completeView.equals(view) ? view : String.format("%s (%s)", view, completeView);
	}

}
