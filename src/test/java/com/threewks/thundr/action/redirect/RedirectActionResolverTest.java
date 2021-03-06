/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
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
package com.threewks.thundr.action.redirect;

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.threewks.thundr.action.ActionException;
import com.threewks.thundr.route.RouteType;

public class RedirectActionResolverTest {
	@Rule public ExpectedException thrown = ExpectedException.none();

	private RedirectActionResolver resolver = new RedirectActionResolver();
	private Map<String, String> emptyMap = map();

	@Test
	public void shouldCreateRedirectAction() {
		RedirectAction createActionIfPossible = resolver.createActionIfPossible("redirect:/back/to/you");
		assertThat(createActionIfPossible, is(notNullValue()));
		assertThat(createActionIfPossible.getRedirectTo(emptyMap), is("/back/to/you"));
	}

	@Test
	public void shouldCreateRedirectActionRegardlessOfCase() {
		RedirectAction createActionIfPossible = resolver.createActionIfPossible("RedirecT:/back/to/you");
		assertThat(createActionIfPossible, is(notNullValue()));
		assertThat(createActionIfPossible.getRedirectTo(emptyMap), is("/back/to/you"));
	}

	@Test
	public void shouldNotResolveRedirectAction() {
		RedirectAction createActionIfPossible = resolver.createActionIfPossible("other:/back/to/you");
		assertThat(createActionIfPossible, is(nullValue()));
	}

	@Test
	public void shouldSendRedirectToClient() throws IOException {
		RedirectAction action = new RedirectAction("/redirect/{to}");
		RouteType routeType = RouteType.POST;
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, String> pathVars = map("to", "new");
		resolver.resolve(action, routeType, req, resp, pathVars);

		verify(resp).sendRedirect("/redirect/new");
	}

	@Test
	public void shouldThrowActionExceptionWhenRedirectFails() throws IOException {
		thrown.expect(ActionException.class);
		thrown.expectMessage("Failed to redirect /requested/path to /redirect/new");

		RedirectAction action = new RedirectAction("/redirect/{to}");
		RouteType routeType = RouteType.POST;
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/requested/path");
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, String> pathVars = map("to", "new");

		doThrow(new IOException("expected")).when(resp).sendRedirect(anyString());
		resolver.resolve(action, routeType, req, resp, pathVars);
	}
}
