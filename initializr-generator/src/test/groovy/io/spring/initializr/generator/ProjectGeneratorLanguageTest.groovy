/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.springframework.core.io.ClassPathResource

/**
 * Project generator tests for supported languages.
 *
 * @author Stephane Nicoll
 * @contributor plq6883
 */
@RunWith(Parameterized.class)
class ProjectGeneratorLanguageTest extends AbstractProjectGeneratorTest {

	@Parameterized.Parameters(name = "{0}")
	public static Object[] parameters() {
		Object[] java = ["java", "java"]
		//Object[] groovy = ["groovy", "groovy"]
		//Object[] kotlin = ["kotlin", "kt"]
		Object[] parameters = [java] //, groovy, kotlin]
		parameters
	}

	private final String language
	private final String extension
	private final String expectedExtension

	ProjectGeneratorLanguageTest(String language, String extension) {
		this.language = language
		this.extension = extension
		this.expectedExtension = extension + '.gen'
	}

	@Test
	public void standardJar() {
		def request = createProjectRequest()
		request.language = language
		request.build = 'gradle'
		generateProject(request).isGenericProject('com.usaa.template', 'TemplateCreationApplication',
				language, extension)
	}

	@Test
	@Ignore
	public void standardWar() {
		def request = createProjectRequest('web')
		request.language = language
		request.build = 'gradle'
		request.packaging = 'war'
		generateProject(request).isGenericWarProject('com.usaa.template', 'TemplateCreationApplication',
				language, extension)
	}

	@Test
	public void standardMainClass() {
		def request = createProjectRequest()
		request.language = language
		request.build = 'gradle'
		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/usaa/template/TemplateCreationApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTests.$expectedExtension"))
	}

	@Test
	public void standardTestClass() {
		def request = createProjectRequest()
		request.language = language
		request.build = 'gradle'
		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/usaa/template/TemplateCreationApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTests.$expectedExtension"))
	}

	@Test
	public void test14TestClass() {
		def request = createProjectRequest()
		request.language = language
		request.bootVersion = '1.4.0.M2'
		request.build = 'gradle'
		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/usaa/template/TemplateCreationApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTests.$expectedExtension"))
	}

	@Test
	public void test14TestClassWeb() {
		def request = createProjectRequest('web')
		request.language = language
		request.bootVersion = '1.4.0.M2'
		request.build = 'gradle'
		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/usaa/template/TemplateCreationApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTests.$expectedExtension"))
	}

}
