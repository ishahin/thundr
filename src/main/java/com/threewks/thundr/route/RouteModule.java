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
package com.threewks.thundr.route;

import java.util.Map;

import com.threewks.thundr.action.Action;
import com.threewks.thundr.action.ActionModule;
import com.threewks.thundr.exception.BaseException;
import com.threewks.thundr.injection.BaseModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.util.Streams;
import com.threewks.thundr.view.ViewModule;

public class RouteModule extends BaseModule {
	private static final String RoutesJsonFilename = "routes.json";
	private String filename = RoutesJsonFilename;

	public RouteModule() {
	}

	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		dependencyRegistry.addDependency(ActionModule.class);
		dependencyRegistry.addDependency(ViewModule.class);
	}

	@Override
	public void initialise(UpdatableInjectionContext injectionContext) {
		super.initialise(injectionContext);
		injectionContext.inject(new Routes()).as(Routes.class);
	}

	@Override
	public void start(UpdatableInjectionContext injectionContext) {
		Routes routes = injectionContext.get(Routes.class);
		addRoutes(routes, injectionContext, filename);
	}

	protected void addRoutes(Routes routes, UpdatableInjectionContext injectionContext, String routesFile) {
		String routesSource = null;
		try {
			routesSource = Streams.getResourceAsString(routesFile);
		} catch (BaseException e) {
			Logger.info("Routes file %s not found", routesFile);
		}
		if (routesSource != null) {
			Logger.info("Loading routes from %s", routesFile);
			Map<Route, Action> routeMap = routes.parseJsonRoutes(routesSource);
			routes.addRoutes(routeMap);
		}
	}
}
