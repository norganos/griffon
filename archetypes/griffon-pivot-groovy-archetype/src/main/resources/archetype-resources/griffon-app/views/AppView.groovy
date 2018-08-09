package \${groupId}

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonView
import org.kordamp.jipsy.ServiceProviderFor
import java.beans.PropertyChangeListener
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonView

@ServiceProviderFor(GriffonView)
class AppView extends AbstractPivotGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder

    @MVCMember @Nonnull
    AppModel model

    void mvcGroupInit(Map<String, Object> args) {
        model.addPropertyChangeListener('clickCount', { evt ->
            builder.clickLabel.text = model.clickCount
        } as PropertyChangeListener)
        builder.clickLabel.text = model.clickCount
    }

    void initUI() {
        builder.with {
            application(title: application.configuration['application.title'],
                id: 'mainWindow', maximized: true) {
                vbox(styles: "{horizontalAlignment:'center', verticalAlignment:'center'}") {
                    label(id: 'clickLabel')
                    button(clickAction)
                }
            }
        }
    }
}