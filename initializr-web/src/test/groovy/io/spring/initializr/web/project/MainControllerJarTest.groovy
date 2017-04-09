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

package io.spring.initializr.web.project

import org.junit.Test
import org.springframework.test.context.ActiveProfiles
import org.junit.Test
import io.spring.initializr.web.MainControllerTest


import org.springframework.test.context.ActiveProfiles


/**
 * @author Stephane Nicoll
 */

@ActiveProfiles('test-default')

class MainControllerJarTest extends MainControllerTest {

    @Test
    void generateWebDataJpaGradleProject() {

        downloadTgz('/starter.tgz?data-jpa&type=gradle-project&baseDir=my-dir')
                .hasBaseDir('my-dir')
//                .isJavaProject()
//                .isGradleProject().hasStaticAndTemplatesResources(true)
//                .gradleBuildAssert()
    }

}
