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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FreemarkerViewTest {

	@Test
	public void shouldRetainViewNameAndModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		FreemarkerView view = new FreemarkerView("/path/view", model);
		assertThat(view.getView(), is("/path/view.ftl"));
		assertThat(view.getModel(), is(model));
	}

	@Test
	public void shouldRetainViewNameAndHaveEmptyModel() {
		FreemarkerView view = new FreemarkerView("path/view");
		assertThat(view.getView(), is("/ftl/path/view.ftl"));
		assertThat(view.getModel(), is(notNullValue()));
		assertThat(view.getModel().size(), is(0));
	}

	@Test
	public void shouldHaveViewNameAndCompleteViewNameAsToString() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("thing", "in");
		FreemarkerView view = new FreemarkerView("/path/view", model);
		assertThat(view.toString(), is("/path/view (/path/view.ftl)"));
	}

	@Test
	public void shouldReturnViewPathRelativeToWebInfAndForHbsWhenPartialViewNameGiven() {
		assertThat(new FreemarkerView("view").getView(), is("/ftl/view.ftl"));
		assertThat(new FreemarkerView("view.ftl").getView(), is("/ftl/view.ftl"));
		assertThat(new FreemarkerView("path/view.ftl").getView(), is("/ftl/path/view.ftl"));
		assertThat(new FreemarkerView("path/view").getView(), is("/ftl/path/view.ftl"));
	}

	@Test
	public void shouldReturnViewPathWithSuffixIfNoneProvided() {
		assertThat(new FreemarkerView("view").getView(), is("/ftl/view.ftl"));
		assertThat(new FreemarkerView("path/view").getView(), is("/ftl/path/view.ftl"));
		assertThat(new FreemarkerView("view.html").getView(), is("/ftl/view.html"));
		assertThat(new FreemarkerView("path/view.html").getView(), is("/ftl/path/view.html"));
	}

	@Test
	public void shouldReturnViewNameForToString() {
		assertThat(new FreemarkerView("/ftl/view.ftl").toString(), is("/ftl/view.ftl"));
		assertThat(new FreemarkerView("view").toString(), is("view (/ftl/view.ftl)"));
		assertThat(new FreemarkerView("view.ftl").toString(), is("view.ftl (/ftl/view.ftl)"));
		assertThat(new FreemarkerView("path/view.ftl").toString(), is("path/view.ftl (/ftl/path/view.ftl)"));
		assertThat(new FreemarkerView("path/view").toString(), is("path/view (/ftl/path/view.ftl)"));
	}
	
	@Test
	public void shouldBeAbleToSetExtendedValuesDirectly() {
		FreemarkerView view = new FreemarkerView("/ftl/view.ftl");
		assertThat(view.getContentType(), is("text/html"));
		assertThat(view.getCharacterEncoding(), is("UTF-8"));
		assertThat(view.getHeader("header"), is(nullValue()));
		assertThat(view.getCookie("cookie"), is(nullValue()));

		view.withContentType("content/type").withCharacterEncoding("UTF-16").withHeader("header", "value1").withCookie("cookie", "value2");

		assertThat(view.getContentType(), is("content/type"));
		assertThat(view.getCharacterEncoding(), is("UTF-16"));
		assertThat(view.getHeader("header"), is((Object)"value1"));
		assertThat(view.getCookie("cookie"), is(notNullValue()));
	}
}
