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

import groovy.util.logging.Slf4j
import io.spring.initializr.InitializrException
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.util.GroovyTemplate
import io.spring.initializr.util.Version
import io.spring.initializr.packaging.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.util.Assert

import static io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom


/**
 * Generate a project based on the configured metadata.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @contributor Gabriela Tikhonova (USAA)
 * @since 1.0
 */
@Slf4j
class ProjectGenerator {

	private static final VERSION_1_2_0_RC1 = Version.parse('1.2.0.RC1')

	private static final VERSION_1_3_0_M1 = Version.parse('1.3.0.M1')

	private static final VERSION_1_4_0_M2 = Version.parse('1.4.0.M2')

	private static final VERSION_1_4_0_M3 = Version.parse('1.4.0.M3')

	private static final VERSION_1_4_2_M1 = Version.parse('1.4.2.M1')

	@Autowired
	ApplicationEventPublisher eventPublisher

	@Autowired
	InitializrMetadataProvider metadataProvider

	@Autowired
	ProjectRequestResolver requestResolver

	@Autowired
	GroovyTemplate groovyTemplate = new GroovyTemplate()

	@Autowired
	ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator()

	@Value('${TMPDIR:.}')
	String tmpdir

	private transient Map<String, List<File>> temporaryFiles = [:]

	/**
	 * Generate a Maven pom for the specified {@link ProjectRequest}.
	 */
	byte[] generateMavenPom(ProjectRequest request) {
		try {
			def model = initializeModel(request)
			def content = doGenerateMavenPom(model)
			publishProjectGeneratedEvent(request)
			content
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * Generate a Gradle build file for the specified {@link ProjectRequest}.
	 */
	byte[] generateGradleBuild(ProjectRequest request) {
		try {
			def model = initializeModel(request)
			def content = doGenerateGradleBuild(model)
			publishProjectGeneratedEvent(request)
			content
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns
	 * a directory containing the project.
	 */
	File generateProjectStructure(ProjectRequest request) {
		try {
			doGenerateProjectStructure(request)
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * This method creates the project specific files. Project class creates general files that are
	 * consistent between all the sub-projects(Jar,Ear and Zip)
	 * @param request
	 * @return a created template of files per the user's request type
	 *
	 */
	protected File doGenerateProjectStructure(ProjectRequest request) {
		def model = resolveModel(request)

		def rootDir = File.createTempFile('tmp', '', new File(tmpdir))
		addTempFile(rootDir.name, rootDir)
		rootDir.delete()
		rootDir.mkdirs()

		def dir = initializerProjectDir(rootDir, request)

		Project project = null;

		if (request.packaging =='jar') {
			project = new JarProject(request,model,dir)
		}
		else if (request.packaging =='ear') {
			project = new EarProject(request,model,dir)
		}
		else if (request.packaging =='zip') {
			project = new ZipProject(request,model,dir)
		}
		project.doGenerateProjectDirectories()

		publishProjectGeneratedEvent(request)
		rootDir

	}

	/**
	 * Create a distribution file for the specified project structure
	 * directory and extension
	 */
	File createDistributionFile(File dir, String extension) {
		def download = new File(tmpdir, dir.name + extension)
		addTempFile(dir.name, download)
		download
	}

	/**
	 * Clean all the temporary files that are related to this root
	 * directory.
	 * @see #createDistributionFile
	 */
	void cleanTempFiles(File dir) {
		def tempFiles = temporaryFiles.remove(dir.name)
		if (tempFiles) {
			tempFiles.each { File file ->
				if (file.directory) {
					file.deleteDir()
				} else {
					file.delete()
				}
			}
		}
	}

	private void publishProjectGeneratedEvent(ProjectRequest request) {
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request)
		eventPublisher.publishEvent(event)
	}

	private void publishProjectFailedEvent(ProjectRequest request, Exception cause) {
		ProjectFailedEvent event = new ProjectFailedEvent(request, cause)
		eventPublisher.publishEvent(event)
	}

	/**
	 * Generate a {@code .gitignore} file for the specified {@link ProjectRequest}
	 * @param dir the root directory of the project
	 * @param request the request to handle
	 */
	protected void generateGitIgnore(File dir, ProjectRequest request) {
		def model = [:]
		model['build'] = isGradleBuild(request) ? 'gradle' : 'maven'
		write(new File(dir, '.gitignore'), 'gitignore.tmpl', model)
	}

	/**
	 * Resolve the specified {@link ProjectRequest} and return the model to use
	 * to generate the project
	 * @param request the request to handle
	 * @return a model for that request
	 */
	protected Map resolveModel(ProjectRequest originalRequest) {
		Assert.notNull originalRequest.bootVersion, 'boot version must not be null'
		def model = [:]
		def metadata = metadataProvider.get()

		ProjectRequest request = requestResolver.resolve(originalRequest, metadata)

		// request resolved so we can log what has been requested
		def dependencies = request.resolvedDependencies
		def dependencyIds = dependencies.collect { it.id }
		log.info("Processing request{type=$request.type, dependencies=$dependencyIds}")

		if (isMavenBuild(request)) {
			ParentPom parentPom = metadata.configuration.env.maven.resolveParentPom(request.bootVersion)
			if (parentPom.includeSpringBootBom && !request.boms['spring-boot']) {
				request.boms['spring-boot'] = metadata.createSpringBootBom(
						request.bootVersion, 'spring-boot.version')
			}

			model['mavenParentGroupId'] = parentPom.groupId
			model['mavenParentArtifactId'] = parentPom.artifactId
			model['mavenParentVersion'] = parentPom.version
			model['includeSpringBootBom'] = parentPom.includeSpringBootBom
		}

		model['compileDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_COMPILE)
		model['runtimeDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_RUNTIME)
		model['compileOnlyDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_COMPILE_ONLY)
		model['providedDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_PROVIDED)
		model['testDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_TEST)

		request.boms.each { k, v ->
			if (v.versionProperty) {
				request.buildProperties.versions[v.versionProperty] = { v.version }
			}
		}

		// Add various versions
		model['dependencyManagementPluginVersion'] = metadata.configuration.env.gradle.dependencyManagementPluginVersion
		model['kotlinVersion'] = metadata.configuration.env.kotlin.version

		// @SpringBootApplication available as from 1.2.0.RC1
		model['useSpringBootApplication'] = VERSION_1_2_0_RC1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0

		// Gradle plugin has changed as from 1.3.0
		model['bootOneThreeAvailable'] = VERSION_1_3_0_M1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0

		// Gradle plugin has changed again as from 1.4.2
		model['springBootPluginName'] = (VERSION_1_4_2_M1.compareTo(
				Version.safeParse(request.bootVersion)) <= 0 ? 'org.springframework.boot' : 'spring-boot')

		// New testing stuff
		model['newTestInfrastructure'] = isNewTestInfrastructureAvailable(request)

		// New Servlet Initializer location
		model['newServletInitializer']  = isNewServletInitializerAvailable(request)

		// Append the project request to the model
		request.properties.each { model[it.key] = it.value }

		model
	}

	protected void setupTestModel(ProjectRequest request, Map<String, Object> model) {
		String imports = ''
		String testAnnotations = ''
		def newTestInfrastructure = isNewTestInfrastructureAvailable(request)
		if (newTestInfrastructure) {
			imports += String.format(generateImport('org.springframework.boot.test.context.SpringBootTest',
					request.language) + "%n")
			imports += String.format(generateImport('org.springframework.test.context.junit4.SpringRunner',
					request.language) + "%n")
		} else {
			imports += String.format(generateImport('org.springframework.boot.test.SpringApplicationConfiguration',
					request.language) + "%n")
			imports += String.format(generateImport('org.springframework.test.context.junit4.SpringJUnit4ClassRunner',
					request.language) + "%n")
		}
		if (request.hasWebFacet() && !newTestInfrastructure) {
			imports += String.format(generateImport('org.springframework.test.context.web.WebAppConfiguration',
					request.language) + "%n")
			testAnnotations = String.format('@WebAppConfiguration%n')
		}
		model.testImports = imports
		model.testAnnotations = testAnnotations
	}

	protected String generateImport(String type, String language) {
		String end = (language.equals("groovy") || language.equals("kotlin")) ? '' : ';'
		"import $type$end"
	}

	private static isGradleBuild(ProjectRequest request) {
		return 'gradle'.equals(request.build)
	}

	private static isMavenBuild(ProjectRequest request) {
		return 'maven'.equals(request.build)
	}

	private static boolean isNewTestInfrastructureAvailable(ProjectRequest request) {
		VERSION_1_4_0_M2
				.compareTo(Version.safeParse(request.bootVersion)) <= 0
	}

	private static boolean isNewServletInitializerAvailable(ProjectRequest request) {
		VERSION_1_4_0_M3
				.compareTo(Version.safeParse(request.bootVersion)) <= 0
	}


	private File initializerProjectDir(File rootDir, ProjectRequest request) {
		if (request.baseDir) {
			File dir = new File(rootDir, request.baseDir)
			dir.mkdirs()
			return dir
		} else {
			return rootDir
		}
	}


	private void addTempFile(String group, File file) {
		def content = temporaryFiles[group]
		if (!content) {
			content = []
			temporaryFiles[group] = content
		}
		content << file
	}

	private static def filterDependencies(def dependencies, String scope) {
		dependencies.findAll { dep -> scope.equals(dep.scope) }.sort { a, b -> a.id <=> b.id }
	}


}
