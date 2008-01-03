/*
* Copyright 2007 the original author or authors.
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
package injecto.dynamicmethod;
import injecto.annotation.InjectoProperty 

/**
 * An injecto that handles the dynamic dispatch on classes.
 * 
 * @author Luke Daley 
 */
class DynamicStaticMethodsInjecto 
{
    @InjectoProperty(write = false)
    static dynamicStaticMethodDispatchTable = new DynamicDispatchTable() 
    
    static staticMethodMissingDynamicDispatch = { String name, args ->

        def dispatchTable = delegate.getDynamicStaticMethodDispatchTable()
        def realArgs = DynamicDispatchHelper.prependMethodNameToArgs(name, args)
        
        def destination = DynamicDispatchHelper.evaluateDynamicDispatchDestination(dispatchTable, name, realArgs)
        if (destination)
        {
            def m = delegate.metaClass.getMetaMethod(destination, realArgs)
            return m.invoke(delegate, realArgs)
        }
        else
        {
            throw new MissingMethodException(name, delegate.class, args)
        }
    }
    
    static methodMissing = { String name, args ->
        return delegate.staticMethodMissingDynamicDispatch(name, args)
    }
}