/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.swing.groovy

import griffon.test.swing.GriffonFestRule
import org.fest.swing.fixture.FrameFixture
import org.junit.Rule
import org.junit.Test

class SampleIntegrationTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonFestRule fest = new GriffonFestRule()

    private FrameFixture window

    @Test
    void typeNameAndClickButton() {
        // given:
        window.textBox('inputField').enterText('Griffon')

        // when:
        window.button('sayHelloButton').click()

        // then:
        window.label('outputLabel').requireText('Hello Griffon')
    }

    @Test
    void doNotTypeNameAndClickButton() {
        // given:
        window.textBox('inputField').enterText('')

        // when:
        window.button('sayHelloButton').click()

        // then:
        window.label('outputLabel').requireText('Howdy stranger!')
    }
}
