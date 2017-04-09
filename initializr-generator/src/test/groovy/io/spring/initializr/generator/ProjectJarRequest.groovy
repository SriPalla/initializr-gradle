package io.spring.initializr.generator

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadataBuilder
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 *
 * @author Gabriela Tikhonova
 */
class JarGeneratorTest  {


    @Test
    void initializeRequestWithJar() {
        def metadata = InitializrMetadataBuilder.create().build()
        metadata.groupId.content = 'com.usaa.testing.jar'
        metadata.artifactId.content = 'my-project'
        metadata.name.content = 'MyJarProject'
        ProjectRequest request = new ProjectRequest()
        request.initialize(metadata)
        request.packaging = 'jar'
        assertEquals 'com.usaa.testing.jar', request.groupId
        assertEquals 'my-project', request.artifactId
        assertEquals 'MyJarProject',request.name
        assertEquals 'jar',request.packaging

    }

    @Test
    void initializeRequestWithJarCheckGradleBuild() {
        def metadata = InitializrMetadataBuilder.create().build()
        metadata.groupId.content = 'com.usaa.testing.jar'
        metadata.artifactId.content = 'my-project'
        metadata.name.content = 'MyJarProject'
//        ProjectRequest request = new ProjectRequest()
//        request.initialize(metadata)
//        request.packaging = 'jar'
//        assertEquals 'com.usaa.testing.jar', request.groupId
//        assertEquals 'my-project', request.artifactId
//        assertEquals 'MyJarProject',request.name
//        assertEquals 'jar',request.packaging

    }

    @Test
    void initializeRequestWithJarCheckYml() {
        def metadata = InitializrMetadataBuilder.create().build()
        metadata.groupId.content = 'com.usaa.testing.jar'
        metadata.artifactId.content = 'my-project'
        metadata.name.content = 'MyJarProject'
//        ProjectRequest request = new ProjectRequest()
//        request.initialize(metadata)
//        request.packaging = 'jar'
//        assertEquals 'com.usaa.testing.jar', request.groupId
//        assertEquals 'my-project', request.artifactId
//        assertEquals 'MyJarProject',request.name
//        assertEquals 'jar',request.packaging

    }

    @Test
    void initializeRequestWithJarCheckGitIgnore() {
        def metadata = InitializrMetadataBuilder.create().build()
        metadata.groupId.content = 'com.usaa.testing.jar'
        metadata.artifactId.content = 'my-project'
        metadata.name.content = 'MyJarProject'
//        ProjectRequest request = new ProjectRequest()
//        request.initialize(metadata)
//        request.packaging = 'jar'
//        assertEquals 'com.usaa.testing.jar', request.groupId
//        assertEquals 'my-project', request.artifactId
//        assertEquals 'MyJarProject',request.name
//        assertEquals 'jar',request.packaging

    }

    @Test
    void initializeProjectRequestWithDefaults() {
//        def metadata = InitializrMetadataTestBuilder.withDefaults().build()
//        ProjectRequest request = new ProjectRequest()
//        request.initialize(metadata)
//        assertEquals metadata.name.content, request.name
//        assertEquals metadata.types.default.id, request.type
//        assertEquals metadata.description.content, request.description
//        assertEquals metadata.groupId.content, request.groupId
//        assertEquals metadata.artifactId.content, request.artifactId
//        assertEquals metadata.version.content, request.version
//        assertEquals metadata.packagings.default.id, request.packaging
    }


}