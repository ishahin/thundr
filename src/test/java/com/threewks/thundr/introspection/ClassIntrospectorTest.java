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
package com.threewks.thundr.introspection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import com.threewks.thundr.route.RouteType;

public class ClassIntrospectorTest {

	@Test
	public void shouldReturnTrueIfClassExistsFalseOtherwise() {
		assertThat(ClassIntrospector.classExists(null), is(false));
		assertThat(ClassIntrospector.classExists("doesnt.Exist"), is(false));
		assertThat(ClassIntrospector.classExists("invalid class name"), is(false));
		assertThat(ClassIntrospector.classExists("com.threewks.thundr.introspection.ClassIntrospector"), is(true));
		assertThat(ClassIntrospector.classExists("ClassIntrospector"), is(false));
	}

	@Test
	public void shouldListClassContructorsAndOrderByNumberOfParamersFollowedByName() {
		List<Constructor<TestC>> constructors = new ClassIntrospector().listConstructors(TestC.class);
		assertThat(constructors.size(), is(4));
		assertThat(constructors.get(0).toString(), is("public com.threewks.thundr.introspection.ClassIntrospectorTest$TestC()"));
		assertThat(constructors.get(1).toString(), is("public com.threewks.thundr.introspection.ClassIntrospectorTest$TestC(java.lang.String)"));
		assertThat(constructors.get(2).toString(), is("public com.threewks.thundr.introspection.ClassIntrospectorTest$TestC(java.lang.String,int)"));
		assertThat(constructors.get(3).toString(), is("public com.threewks.thundr.introspection.ClassIntrospectorTest$TestC(java.lang.String,java.lang.String)"));
	}

	@Test
	public void shouldListSetters() {
		List<Method> setters = new ClassIntrospector().listSetters(TestCA.class);
		assertThat(setters.size(), is(3));
		List<String> setterNames = new ArrayList<String>();
		for (Method method : setters) {
			setterNames.add(method.getName());
		}
		assertThat(setterNames, hasItems("setA", "setB", "setC"));
	}

	@Test
	public void shouldListInjectionFields() {
		List<Field> listInjectionFields = new ClassIntrospector().listInjectionFields(TestCA.class);
		assertThat(listInjectionFields.size(), is(2));
		assertThat(listInjectionFields.get(0).getName(), is("fieldC"));
		assertThat(listInjectionFields.get(1).getName(), is("fieldA"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnClassesAndInterfacesInPriorityOrder() {
		List<Class<?>> types = new ClassIntrospector().listImplementedTypes(TestCA.class);
		assertThat(types, is(Arrays.asList(TestCA.class, TestC.class, Object.class, TestBA.class, TestB.class, TestAA.class, TestA.class)));
	}

	@Test
	public void shouldReturnTrueForIsAJavabeanIfIsANormalObjectWithANoArgsCtor() {
		assertThat(ClassIntrospector.isAJavabean(null), is(false));
		assertThat(ClassIntrospector.isAJavabean(Object.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(Void.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(int.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(Integer.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(String[].class), is(false));
		assertThat(ClassIntrospector.isAJavabean(String.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(NoDefaultCtor.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(PrivateCtor.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(RouteType.class), is(false));
		assertThat(ClassIntrospector.isAJavabean(NoDefaultCtor.class), is(false));

		assertThat(ClassIntrospector.isAJavabean(Date.class), is(true));
		assertThat(ClassIntrospector.isAJavabean(TestC.class), is(true));
		assertThat(ClassIntrospector.isAJavabean(TestCA.class), is(true));
	}

	static interface TestA {

	}

	static interface TestB {

	}

	static interface TestAA extends TestA {

	}

	static interface TestBA extends TestB {

	}

	static class TestC implements TestAA {
		@Inject private String fieldA;
		@SuppressWarnings("unused") private String fieldB;

		public TestC() {
		}

		public TestC(String a) {
		}

		public TestC(String a, String b) {
		}

		public TestC(String a, int b) {
		}

		public void setC(String c) {
		}
	}

	static class TestCA extends TestC implements TestBA {
		@Inject private String fieldC;
		@SuppressWarnings("unused") private String fieldD;

		public void setA(String a) {

		}

		public void setB(int b) {

		}
	}

	public static class DefaultCtor {
	}

	public static class NoDefaultCtor {
		public NoDefaultCtor(String something) {
		}
	}

	public static class PrivateCtor {
		private PrivateCtor() {

		}
	}
}
