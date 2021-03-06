package ${project_package}

import griffon.test.core.GriffonUnitRule
import griffon.test.core.TestFor
import griffon.core.threading.UIThreadManager
import griffon.annotations.inject.BindTo
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager
import org.junit.Rule
import spock.lang.Specification

@TestFor(${project_class_name})
class ${project_class_name}Spec extends Specification {
    private ${project_class_name} ${artifact_type}

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "This is a smoke test" () {
        expect:
            false
    }

    @javax.inject.Singleton
    @BindTo(UIThreadManager.class)
    private DefaultUIThreadManager uiThreadManager
}