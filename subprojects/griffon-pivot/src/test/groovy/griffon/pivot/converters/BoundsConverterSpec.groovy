/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.pivot.converters

import org.apache.pivot.wtk.Bounds
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.ConversionException

@Unroll
class BoundsConverterSpec extends Specification {
    @Shared
    private Bounds bounds = new Bounds(10, 20, 30, 40)

    void "Bounds format '#format' should be equal to #bounds"() {
        setup:
        BoundsConverter converter = new BoundsConverter()
        when:
        Bounds parsedBounds = converter.fromObject(format)

        then:
        value == parsedBounds

        where:
        value                  | format
        null                   | null
        null                   | ''
        null                   | ' '
        null                   | []
        null                   | [:]
        bounds                 | '10,20,30,40'
        bounds                 | '10, 20, 30, 40'
        bounds                 | ' 10, 20, 30, 40'
        bounds                 | ' 10, 20, 30, 40 '
        bounds                 | [10, 20, 30, 40]
        bounds                 | ['10', '20', '30', '40']
        bounds                 | [x: 10, y: 20, width: 30, height: 40]
        bounds                 | [x: '10', y: '20', width: '30', height: '40']
        bounds                 | [x: 10, y: 20, w: 30, h: 40]
        bounds                 | [x: '10', y: '20', w: '30', h: '40']
        bounds                 | bounds
        new Bounds(0, 0, 0, 0) | [foo: 10, bar: 20]
    }

    void "Invalid bounds format '#format'"() {
        setup:
        BoundsConverter converter = new BoundsConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            '1, 2, 3, 4, 5',
            [1, 2, 3],
            [1, 2, 3, new Object()],
            [1, 2, 3, 4, 5],
            [new Object()],
            [x: new Object()],
            [x: 'a'],
            [y: 'b'],
            new Object()
        ]
    }

    void "Formatted bounds '#expected'"() {
        given:
        BoundsConverter converter = new BoundsConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value  | expected
        null   | null
        bounds | '10, 20, 30, 40'
    }
}
