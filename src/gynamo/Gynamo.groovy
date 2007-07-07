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
package gynamo;
import java.beans.Introspector
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Abstract base for all Gynamos and implementer of the gynamize() method to inject a Gynamo into a class.
 * @author ld@ldaley.com
 * @since 1.0
 */
abstract class Gynamo
{
	/**
	 * Getter names that are excluded from being Gynamo properties.
	 */
	static exclusions = ["getMetaClass", "getProperty"]
	
	/**
	 * Method that takes a class to be "gynamized" with a Gynamo.
	 * 
	 * See the documentation for rules about Gynamos.
	 * 
	 * @param gynameeClass The class to add the methods too
	 * @param gynamoClass The class to copy the methods from
	 */
	static void gynamize(Class gynameeClass, Class gynamoClass)
	{	
		def gynamo = gynamoClass.newInstance()
		if (gynamo.isGynamized(gynameeClass) == false)
		{
			// Resolve dependencies
			def gynamoDependency = gynamoClass.getAnnotation(GynamoDependency)
			if (gynamoDependency)
			{
				gynamize(gynameeClass, gynamoDependency.value())
			}
		
			def gynamoDependencies = gynamoClass.getAnnotation(GynamoDependencies)
			if (gynamoDependencies)
			{
				gynamoDependencies.value().each {
					gynamize(gynameeClass, it)
				}
			}
		
			// Gives the Gynamo a chance to make sure that gynameeClass is fit to be Gynamized
			gynamo.preGynamize(gynameeClass)
			getCopyablePropertiesGettersOfGynamo(gynamoClass).each { Method getter ->
				def propertyName = propertyGetterNameToPropertyName(getter.name)
				if (Modifier.isStatic(getter.modifiers))
				{
					gynameeClass.metaClass."static"[propertyName] = getter.invoke(gynamoClass)
				}
				else
				{
					gynameeClass.metaClass[propertyName] = getter.invoke(gynamo)
				}
			}

			// Gives the Gynamo a chance to do any setup with the new methods
			gynamo.postGynamize(gynameeClass)
		}
	}
	
	static List getCopyablePropertiesGettersOfGynamo(Class gynamoClass)
	{
		def copyablePropertyGetterMethods = []
		gynamoClass.methods.each { Method method ->
			if (
				gynamoClass.isAssignableFrom(method.declaringClass) == true && 
				method.name[0..2].equals("get") && 
				exclusions.contains(method.name) == false
			)
			{
				copyablePropertyGetterMethods << method
			}
		}
		return copyablePropertyGetterMethods
	}
	
	static String propertyGetterNameToPropertyName(String propertyGetterName)
	{
		return propertyGetterName[3].toLowerCase() + propertyGetterName.substring(4)
	}
	
	/**
	 * Subclasses should override if they wish to do something BEFORE a class gets Gynamized,
	 * but after dependant Gynamos have been injected.
	 */
	void preGynamize(Class clazz)
	{
		
	}
	
	/**
	 * Subclasses should override if they want to do something AFTER a class gets Gynamized.
	 */
	void postGynamize(Class clazz)
	{
		
	}
	
	/**
	 * 
	 */
	boolean isGynamized(Class clazz)
	{
		return Gynamo.getCopyablePropertiesGettersOfGynamo(this.class).find { Method getter ->
			def propertyName = propertyGetterNameToPropertyName(getter.name)
			return clazz.metaClass.hasMetaMethod(propertyName) || clazz.metaClass.hasMetaProperty(propertyName)
		} != null
	}
}