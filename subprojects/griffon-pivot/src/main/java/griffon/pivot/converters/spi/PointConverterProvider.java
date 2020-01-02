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
package griffon.pivot.converters.spi;

import griffon.pivot.converters.PointConverter;
import org.apache.pivot.wtk.Point;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.application.converter.Converter;
import javax.application.converter.spi.ConverterProvider;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
@ServiceProviderFor(ConverterProvider.class)
public class PointConverterProvider implements ConverterProvider<Point> {
    @Override
    public Class<Point> getTargetType() {
        return Point.class;
    }

    @Override
    public Class<? extends Converter<Point>> getConverterType() {
        return PointConverter.class;
    }
}
