/*
 * Copyright 2008-2010 the original author or authors.
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

package org.codehaus.griffon.test;

import griffon.util.GriffonUtil;
import junit.framework.Test;
import org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter;

/**
 * JUnit plain text formatter that sanitises the stack traces generated
 * by tests.
 *
 * @author Andres Almiray
 */
public class PlainFormatter extends PlainJUnitResultFormatter {
    public void addFailure(Test test, Throwable throwable) {
        GriffonUtil.deepSanitize(throwable);
        super.addFailure(test, (Throwable)throwable);
    }

    public void addError(Test test, Throwable throwable) {
        GriffonUtil.deepSanitize(throwable);
        super.addError(test, throwable);
    }
}
