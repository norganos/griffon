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
package org.codehaus.griffon.runtime.core.configuration

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.Configuration
import org.codehaus.griffon.runtime.core.MapResourceBundle
import org.junit.Rule
import org.kordamp.jsr377.converter.DateConverter
import org.kordamp.jsr377.converter.DefaultConverterRegistry
import org.kordamp.jsr377.converter.IntegerConverter
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.ConverterRegistry
import javax.inject.Inject

import static com.google.inject.util.Providers.guicify
import static griffon.util.AnnotationUtils.named

@Unroll
class ConfigurationSpec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private Configuration configuration

    @Inject
    private ConverterRegistry converterRegistry

    def cleanup() {
        converterRegistry.clear()
    }

    def 'Calling configuration.get(#key, #defaultValue) returns #expectedValue'() {
        expect:
        expectedValue == configuration.get(key, defaultValue)

        where:
        key            | defaultValue || expectedValue
        'key.string'   | null         || 'string'
        'key.string'   | 'foo'        || 'string'
        'key.int.type' | null         || 42
        'key.int.type' | 'foo'        || 42
        'key.foo'      | null         || null
        'key.foo'      | 'foo'        || 'foo'
    }

    def 'Calling configuration.getConverted(#key) returns converted #expectedValue'() {
        given:
        converterRegistry.clear()
        converterRegistry.registerConverter(Integer, IntegerConverter)

        when:
        Integer value = configuration.getConverted(key, Integer)

        then:
        value == expectedValue

        where:
        key              || expectedValue
        'key.int.null'   || null
        'key.int.type'   || 42
        'key.int.string' || 21
    }

    def 'Calling configuration.getConverted(#key) with format returns converted #expectedValue'() {
        given:
        converterRegistry.clear()
        converterRegistry.registerConverter(Date, DateConverter)

        when:
        Date value = configuration.getConverted(key, Date, 'YYYY-MM-dd')

        then:
        value == expectedValue

        where:
        key               || expectedValue
        'key.date.string' || Date.parse('YYYY-MM-dd', '1970-12-24')
    }

    def 'Calling configuration.getAsString(#key, #defaultValue) returns #expectedValue'() {
        expect:
        expectedValue == configuration.getAsString(key, defaultValue)

        where:
        key            | defaultValue || expectedValue
        'key.string'   | null         || 'string'
        'key.string'   | 'foo'        || 'string'
        'key.int.type' | null         || '42'
        'key.int.type' | 'foo'        || '42'
        'key.foo'      | null         || null
        'key.foo'      | 'foo'        || 'foo'
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ResourceBundle)
                .annotatedWith(named('applicationResourceBundle'))
                .to(MapResourceBundle)
            bind(ConfigurationDecoratorFactory)
                .to(DefaultConfigurationDecoratorFactory)
            bind(Configuration)
                .toProvider(guicify(new ResourceBundleConfigurationProvider()))
            bind(ConverterRegistry)
                .to(DefaultConverterRegistry)
        }
    }
}
