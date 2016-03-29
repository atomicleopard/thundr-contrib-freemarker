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

import static com.atomicleopard.expressive.Expressive.list;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.http.Cookies;
import com.threewks.thundr.http.StatusCode;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;
import com.threewks.thundr.test.mock.servlet.MockHttpServletResponse;
import com.threewks.thundr.view.GlobalModel;
import com.threewks.thundr.view.ViewResolutionException;

import freemarker.template.Configuration;

public class FreemarkerViewResolverTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private GlobalModel globalModel;
	private FreemarkerViewResolver viewResolver;
	private Configuration configuration;
	private MockHttpServletRequest req = new MockHttpServletRequest();
	private MockHttpServletResponse resp = new MockHttpServletResponse();

	@Before
	public void before() {
		FreemarkerModule freemarkerModule = new FreemarkerModule();
		configuration = freemarkerModule.createConfiguration(null);
		globalModel = new GlobalModel();
		viewResolver = new FreemarkerViewResolver(configuration, globalModel);
	}

	@Test
	public void shouldRenderBasicExample() {
		FreemarkerView view = new FreemarkerView("/test.ftl", model("message", "Test", "countries", list("australia", "brazil")));
		viewResolver.resolve(req, resp, view);

		String content = resp.content();
		// @formatter:off
		String expected = 
			"FreeMarker Template example: Test\n"+  
			"\n"+ 
			"=======================\n"+
			"===  County List   ====\n"+
			"=======================\n"+
			"    1. australia\n"+
			"    2. brazil\n";
		// @formatter:on
		assertThat(content, is(expected));
		assertThat(resp.status(), is(StatusCode.OK.getCode()));
		assertThat(resp.getContentType(), is("text/html"));
	}

	@Test
	public void shouldRenderExampleWithDirectives() {
		Animal cat = new Animal("Cat", 100l, false);
		Animal dog = new Animal("Dog", 200l, false);
		Animal whiteRhino = new Animal("White Rhino", 30000l, true);

		FreemarkerView view = new FreemarkerView("/test2.ftl", model("animals", list(cat, dog, whiteRhino)));
		viewResolver.resolve(req, resp, view);

		String content = resp.content();
		// @formatter:off
		String expected = 
				"\t<div>\n"+
				"\t\tCat for 100 Euros\n"+
				"\t</div>\n"+  
				"\t<div>\n"+
				"\t\tDog for 200 Euros\n"+
				"\t</div>\n"+  
				"\t<div class=\"protected\">\n"+
				"\t\tWhite Rhino for 30,000 Euros\n"+
				"\t</div>\n";  
		// @formatter:on
		assertThat(content, is(expected));
		assertThat(resp.status(), is(StatusCode.OK.getCode()));
		assertThat(resp.getContentType(), is("text/html"));
	}

	@Test
	public void shouldRenderExampleWithBuiltIns() {
		Animal cat = new Animal("cat", 100l, false);
		Animal dog = new Animal("dog", null, false);
		Animal whiteRhino = new Animal("white rhino", 30000l, true);

		FreemarkerView view = new FreemarkerView("/test3.ftl", model("animals", list(cat, dog, whiteRhino)));
		viewResolver.resolve(req, resp, view);

		String content = resp.content();
		// @formatter:off
		String expected = 
				"\t<div class=\"protected-N\">\n"+
				"\t\tCat for 100 Euros\n"+
				"\t</div>\n"+  
				"\t<div class=\"protected-N\">\n"+
				"\t\tDog for No Euros\n"+
				"\t</div>\n"+  
				"\t<div class=\"protected-Y\">\n"+
				"\t\tWhite rhino for 30,000 Euros\n"+
				"\t</div>\n";  
		// @formatter:on
		assertThat(content, is(expected));
		assertThat(resp.status(), is(StatusCode.OK.getCode()));
		assertThat(resp.getContentType(), is("text/html"));
	}

	@Test
	public void shouldRetainHandlebarsAndGlobalModel() {
		FreemarkerViewResolver viewResolver = new FreemarkerViewResolver(configuration, globalModel);
		assertThat(viewResolver.getGlobalModel(), is(globalModel));
		assertThat(viewResolver.getFreemarkerConfiguration(), is(configuration));
	}

	@Test
	public void shouldRenderTemplateFromClasspathWithRelativePath() throws IOException {
		FreemarkerViewResolver viewResolver = new FreemarkerViewResolver(configuration, globalModel);
		Map<String, Object> localModel = Expressive.map("message", "Message");
		FreemarkerView view = new FreemarkerView("basic-relative.ftl", localModel);
		viewResolver.resolve(req, resp, view);

		assertThat(resp.content(), is("Message"));
		assertThat(resp.status(), is(StatusCode.OK.getCode()));
		assertThat(resp.getContentType(), is("text/html"));
	}

	@Test
	public void shouldApplyGlobalModelInTemplateButAllowLocalModelToOverride() throws IOException {
		globalModel.put("global", "global");
		globalModel.put("request", "still-global");
		globalModel.put("local", "still-global");

		FreemarkerViewResolver viewResolver = new FreemarkerViewResolver(configuration, globalModel);
		Map<String, Object> localModel = Expressive.map("local", "local");
		FreemarkerView view = new FreemarkerView("/variable.ftl", localModel);
		viewResolver.resolve(req, resp, view);

		assertThat(resp.content(), is("Template with global and still-global and local"));
		assertThat(resp.status(), is(StatusCode.OK.getCode()));
		assertThat(resp.getContentType(), is("text/html"));
	}

	@Test
	public void shouldPutRequestAttributesIntoTemplateAllowingLocalModelToOverride() {
		FreemarkerViewResolver viewResolver = new FreemarkerViewResolver(configuration, globalModel);

		globalModel.put("global", "global");
		globalModel.put("request", "global");
		globalModel.put("local", "global");
		req.attribute("request", "request");
		req.attribute("local", "request");
		Map<String, Object> localModel = Expressive.map("local", "local");

		FreemarkerView view = new FreemarkerView("/variable.ftl", localModel);
		viewResolver.resolve(req, resp, view);

		assertThat(resp.content(), is("Template with global and request and local"));
		assertThat(resp.status(), is(StatusCode.OK.getCode()));
		assertThat(resp.getContentType(), is("text/html"));
	}

	@Test
	public void shouldThrowViewResolutionExceptionWhenFailedToApplyTemplate() throws IOException {
		thrown.expect(ViewResolutionException.class);
		thrown.expectMessage("Failed to render Freemarker template '/wont-compile.ftl': The following has evaluated to null or missing:");

		FreemarkerView view = new FreemarkerView("/wont-compile.ftl");
		viewResolver.resolve(req, resp, view);
	}

	@Test
	public void shouldThrowViewResolutionExceptionWhenFailedToFindTemplate() throws IOException {
		thrown.expect(ViewResolutionException.class);
		thrown.expectMessage("Failed to render Freemarker template '/ftl/non-existant.ftl': Template not found for name");

		FreemarkerView view = new FreemarkerView("non-existant.ftl");
		viewResolver.resolve(req, resp, view);
	}

	@Test
	public void shouldReturnClassNameForToString() {
		assertThat(new FreemarkerViewResolver(configuration, globalModel).toString(), is("FreemarkerViewResolver"));
	}

	@Test
	public void shouldThrowViewResolutionExceptionWhenFailedToCompileTemplate() throws IOException {
		thrown.expect(ViewResolutionException.class);
		thrown.expectMessage("Failed to render Freemarker template '/wont-compile.ftl': ");

		FreemarkerView view = new FreemarkerView("/wont-compile.ftl");
		viewResolver.resolve(req, resp, view);
	}

	@Test
	public void shouldRespectExtendedViewValues() {
		FreemarkerView view = new FreemarkerView("/basic.ftl", model("message", "Message"));
		Cookie cookie = Cookies.build("cookie").withValue("value2").build();
		view.withContentType("content/type").withCharacterEncoding("UTF-16").withHeader("header", "value1").withCookie(cookie);

		viewResolver.resolve(req, resp, view);
		assertThat(resp.getContentType(), is("content/type"));
		assertThat(resp.getCharacterEncoding(), is("UTF-16"));
		assertThat(resp.header("header"), is((Object) "value1"));
		assertThat(resp.getCookies(), hasItem(cookie));
	}

	private static Map<String, Object> model(Object... keyValues) {
		return Expressive.map(keyValues);
	}

	public static class Animal {
		private boolean protectd;
		private String name;
		private Long price;

		public Animal(String name, Long price, boolean protectd) {
			super();
			this.protectd = protectd;
			this.name = name;
			this.price = price;
		}

		public boolean isProtected() {
			return protectd;
		}

		public String getName() {
			return name;
		}

		public Long getPrice() {
			return price;
		}
	}
}
