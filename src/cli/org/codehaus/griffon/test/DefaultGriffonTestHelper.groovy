/*
 * Copyright 2009-2010 the original author or authors.
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

package org.codehaus.griffon.test

import junit.framework.TestSuite
import junit.framework.Test
import org.springframework.core.io.Resource
import griffon.util.BuildSettings
import java.lang.reflect.Modifier
import junit.framework.TestCase

/**
 * Standard implementation of the Griffon test helper, which uses standard
 * JUnit classes to load the tests
 *
 * @author Andres Almiray
 */
class DefaultGriffonTestHelper implements GriffonTestHelper {

    String testSuffix = "Tests"

    protected final File baseDir
    protected final File testClassesDir
    protected final ClassLoader parentLoader
    protected final Closure resourceResolver

    private ClassLoader currentClassLoader

    DefaultGriffonTestHelper(
            BuildSettings settings,
            ClassLoader classLoader,
            Closure resourceResolver) {
        this.baseDir = settings.baseDir
        this.testClassesDir = settings.testClassesDir
        this.parentLoader = classLoader
        this.resourceResolver = resourceResolver
    }

    TestSuite createTests(List<String> testNames, String type) {
        def testSrcDir = "${baseDir.absolutePath}/test/$type"
        def testSuite = new TestSuite("Griffon Test Suite")

        currentClassLoader = createClassLoader(type)

        def potentialTests = testNames.collect { String name -> new PotentialTest(name) }
        potentialTests.findAll { it.hasMethodName() }.each { PotentialTest test ->
            def resources = resourceResolver("file:${testSrcDir}/${test.filePattern}${testSuffix}.groovy") as List
            resources += resourceResolver("file:${testSrcDir}/${test.filePattern}${testSuffix}.java") as List

            if (resources) {
                def className = fileToClassName(resources[0].file, new File(testSrcDir))
                def clazz = currentClassLoader.loadClass(className)

                if (TestCase.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.modifiers)) {
                    def suite = createTestSuite()
                    suite.name = className
                    suite.addTest(TestSuite.createTest(clazz, test.methodName))
                    testSuite.addTest(suite)
                }
            }
        }

        def nonMethodTests = potentialTests.findAll { !it.hasMethodName() }
        def nmTestResources = nonMethodTests*.filePattern.inject([]) { resources, String filePattern ->
            resources += resourceResolver("file:${testSrcDir}/${filePattern}${testSuffix}.groovy") as List
            resources += resourceResolver("file:${testSrcDir}/${filePattern}${testSuffix}.java") as List
        }
        nmTestResources.findAll { it.exists() }.each { Resource resource ->
            def className = fileToClassName(resource.file, new File(testSrcDir))
            def clazz = currentClassLoader.loadClass(className)

            if (TestCase.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.modifiers)) {
                testSuite.addTest(createTestSuite(clazz))
            }
        }

        return testSuite
    }

    /**
     * Creates a new test suite from the given test. The given class
     * typically extends <code>junit.framework.TestCase</code>. This
     * standard implementation returns a new <code>junit.framework.TestSuite</code>.
     */
    TestSuite createTestSuite(Class clazz) {
        new TestSuite(clazz)
    }

    /**
     * Creates a new test suite. This standard implementation just
     * returns a new <code>junit.framework.TestSuite</code>.
     */
    TestSuite createTestSuite() {
        new TestSuite()
    }

    /**
     * Given the location of a test file, and the directory that it is
     * relative to, this method returns the fully qualifed class name
     * of that tests. So for example, if you have "test/unit/org/example/MyTest.groovy"
     * with a base directory of "test/unit", the method returns the
     * string "org.example.MyTest".
     */
    String fileToClassName(File file, File baseDir) {
        def filePath = file.canonicalFile.absolutePath
        def basePath = baseDir.canonicalFile.absolutePath
        if (!filePath.startsWith(basePath)) {
            throw new IllegalArgumentException("File path (${filePath}) is not descendent of base path (${basePath}).")
        }

        filePath = filePath.substring(basePath.size() + 1)
        def suffixPos = filePath.lastIndexOf(".")
        return filePath[0..(suffixPos - 1)].replace(File.separatorChar, '.' as char)
    }

    ClassLoader getCurrentClassLoader() {
        return currentClassLoader
    }

    /**
     * Creates a class loader that the tests of the given type can be
     * loaded from.
     */
    protected ClassLoader createClassLoader(String type) {
        return new URLClassLoader([
                new File("test/$type").toURI().toURL(),
                new File(testClassesDir, type).toURI().toURL()
        ] as URL[], parentLoader)
    }
}
