package com.atomicleopard.webFramework.action.redirect;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.atomicleopard.webFramework.action.Action;
import com.atomicleopard.webFramework.route.Route;

public class RedirectAction implements Action {
	private String redirectTo;

	public RedirectAction(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public String getRedirectTo(Map<String, String> pathVars) {
		String finalRedirect = redirectTo;

		Matcher matcher = Route.PathParameterToken.matcher(redirectTo);
		while (matcher.find()) {
			String token = matcher.group(1);
			finalRedirect = finalRedirect.replaceAll(Pattern.quote("{" + token + "}"), Matcher.quoteReplacement(StringUtils.trimToEmpty(pathVars.get(token))));
		}
		return finalRedirect;
	}

	@Override
	public String toString() {
		return "Redirect:" + redirectTo;
	}

}