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

package griffon.core

import griffon.util.internal.GriffonApplicationHelper
import griffon.util.EventRouter
import griffon.util.Metadata

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 */
class BaseGriffonApplication implements GriffonApplication {
    Map<String, ?> addons = [:]
    Map<String, String> addonPrefixes = [:]

    Map<String, Map<String, String>> mvcGroups = [:]
    Map models      = [:]
    Map views       = [:]
    Map controllers = [:]
    Map builders    = [:]
    Map groups      = [:]

    Binding bindings = new Binding()
    ConfigObject config
    ConfigObject builderConfig
    Object eventsConfig

    private final EventRouter eventRouter = new EventRouter()
    private final List<ShutdownHandler> shutdownHandlers = []
    final GriffonApplication appDelegate

    BaseGriffonApplication(GriffonApplication appDelegate) {
        this.appDelegate = appDelegate
    }

    Metadata getMetadata() {
        return Metadata.current 
    }

    Class getConfigClass() {
        return getClass().classLoader.loadClass("Application")
    }

    Class getBuilderClass() {
        return getClass().classLoader.loadClass("Builder")
    }

    Class getEventsClass() {
        try{
           return getClass().classLoader.loadClass("Events")
        } catch( ignored ) {
           // ignore - no global event handler will be used
        }
        return null
    }

    void initialize() {
        GriffonApplicationHelper.runScriptInsideUIThread("Initialize", appDelegate)
    }

    void ready() {
        event("ReadyStart",[appDelegate])
        GriffonApplicationHelper.runScriptInsideUIThread("Ready", appDelegate)
        event("ReadyEnd",[appDelegate])
    }

    boolean canShutdown() {
        event("ShutdownRequested",[appDelegate])
        for(handler in shutdownHandlers) {
            if(!handler.canShutdown(appDelegate)) {
                event("ShutdownAborted",[appDelegate])
                return false
            }
        }
        return true
    }

    void shutdown() {
        event("ShutdownStart",[appDelegate])
        for(handler in shutdownHandlers) {
            handler.onShutdown(appDelegate)
        }

        List mvcNames = []
        mvcNames.addAll(groups.keySet())
        mvcNames.each { 
            GriffonApplicationHelper.destroyMVCGroup(appDelegate, it)
        }
        GriffonApplicationHelper.runScriptInsideUIThread("Shutdown", appDelegate)
    }

    void startup() {
        event("StartupStart",[appDelegate])
        GriffonApplicationHelper.runScriptInsideUIThread("Startup", appDelegate)
        event("StartupEnd",[appDelegate])
    }

    void event( String eventName, List params = [] ) {
        eventRouter.publish(eventName, params)
    }

    void addApplicationEventListener( listener ) {
       eventRouter.addEventListener(listener)
    }

    void removeApplicationEventListener( listener ) {
       eventRouter.removeEventListener(listener)
    }

    void addApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.addEventListener(eventName,listener)
    }

    void removeApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.removeEventListener(eventName,listener)
    }

    void addMvcGroup(String mvcType, Map<String, String> mvcPortions) {
       mvcGroups[mvcType] = mvcPortions
    }

    Object createApplicationContainer() {
        null
    }

    void addShutdownHandler(ShutdownHandler handler) {
        if(handler && !shutdownHandlers.contains(handler)) shutdownHandlers << handler
    }    

    void removeShutdownHandler(ShutdownHandler handler) {
        if(handler) shutdownHandlers.remove(handler)
    }    
}
