package com.atomicleopard.webFramework.mail;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.mail.Message.RecipientType;
import javax.servlet.http.HttpServletRequest;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.Mixin;

import com.atomicleopard.expressive.Expressive;
import com.atomicleopard.webFramework.collection.Triplets;
import com.atomicleopard.webFramework.view.ViewResolver;

public class MailBuilder {
	private Mailer mail;
	private HttpServletRequest request;
	private String content;
	private String subject;
	private Map<String, String> from = new HashMap<String, String>();
	private Triplets<RecipientType, String, String> recipients = new Triplets<RecipientType, String, String>();
	// TODO - These require MIME types
	private Map<String, byte[]> attachments = new LinkedHashMap<String, byte[]>();

	public MailBuilder(Mailer mail, HttpServletRequest request) {
		this.mail = mail;
		from.put("", null);
		this.request = request;
	}

	public <T> MailBuilder body(T view) {
		try {
			ViewResolver<T> viewResolver = mail.viewResolverRegistry().findViewResolver(view);

			final Map<String, Object> attributes = new HashMap<String, Object>();
			for (Object attribute : Expressive.iterable(request.getAttributeNames())) {
				attributes.put((String) attribute, request.getAttribute((String) attribute));
			}

			//HttpServletRequest req = new MailHttpServletRequest(request);
			HttpServletRequest req = request;
			/*
			This throws NoClassDefFoundException - i have no idea why
			HttpServletRequest req = (HttpServletRequest) Enhancer.create(request.getClass(), new MethodInterceptor() {
				@Override
				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
					if ("getAttribute".equals(method.getName())) {
						return attributes.get(args[0]);
					}
					if ("setAttribute".equals(method.getName())) {
						attributes.put((String) args[0], args[1]);
						return null;
					}
					if ("removeAttribute".equals(method.getName())) {
						attributes.remove(args[0]);
						return null;
					}
					if ("getAttributeNames".equals(method.getName())) {
						return Collections.enumeration(attributes.keySet());
					}

					return proxy.invoke(obj, args);
				}
			});
			*/

			MailHttpServletResponse resp = new MailHttpServletResponse();
			viewResolver.resolve(req, resp, view);
			content = resp.getResponseContent();
		} catch (Exception e) {
			throw new MailException(e, "Failed to render email body: %s", e.getMessage());
		}
		return this;
	}

	public void send() {
		mail.send(this);
	}

	public MailBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	public MailBuilder from(String emailAddress, String name) {
		this.from.clear();
		this.from.put(emailAddress, name);
		return this;
	}

	public MailBuilder to(String emailAddress, String name) {
		recipients.put(RecipientType.TO, emailAddress, name);
		return this;
	}

	public MailBuilder to(Map<String, String> to) {
		for (Map.Entry<String, String> entry : to.entrySet()) {
			recipients.put(RecipientType.TO, entry.getKey(), entry.getValue());
		}
		return this;
	}

	public MailBuilder cc(String emailAddress, String name) {
		recipients.put(RecipientType.CC, emailAddress, name);
		return this;
	}

	public MailBuilder cc(Map<String, String> cc) {
		for (Map.Entry<String, String> entry : cc.entrySet()) {
			recipients.put(RecipientType.CC, entry.getKey(), entry.getValue());
		}
		return this;
	}

	public MailBuilder bcc(String emailAddress, String name) {
		recipients.put(RecipientType.BCC, emailAddress, name);
		return this;
	}

	public MailBuilder bcc(Map<String, String> bcc) {
		for (Map.Entry<String, String> entry : bcc.entrySet()) {
			recipients.put(RecipientType.BCC, entry.getKey(), entry.getValue());
		}
		return this;
	}

	Map.Entry<String, String> from() {
		return from.entrySet().iterator().next();
	}

	Triplets<RecipientType, String, String> recipients() {
		return recipients;
	}

	String subject() {
		return subject;
	}

	String content() {
		return content;
	}
}
