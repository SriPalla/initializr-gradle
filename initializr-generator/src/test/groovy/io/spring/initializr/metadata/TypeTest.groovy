/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.metadata

import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * @author Stephane Nicoll
 */
class TypeTest {

	@Test
	void parseAction() {
		def type = new Type(id: 'foo', action: 'my-action.zip')
		assertEquals '/my-action.zip', type.action
	}

}
