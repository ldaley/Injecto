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
import gynamo.annotation.*;
import java.beans.Introspector
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Class that takes a Gynamo and copies all its readable properties to a Gynamees meta class
 * @author ld@ldaley.com
 * @since 1.0
 */
abstract class Gynamizer
{
	
	static registry = [:]
	
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
		if (isGynamized(gynameeClass, gynamoClass) == false)
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
			
			try
			{
				gynamoClass.preGynamize(gynameeClass)
			}
			catch (MissingMethodException e)
			{
				
			}

			def gynamo = gynamoClass.newInstance()

			getCopyablePropertiesGettersOfGynamo(gynamoClass).each { Method getter ->
				def propertyName = getter.name[3].toLowerCase() + getter.name.substring(4)
				def field = gynamoClass.getDeclaredField(propertyName) 
				def gynamizeAsAnnotation = field?.getAnnotation(GynamizeAs)
				def gynamizeAs = (gynamizeAsAnnotation) ? gynamizeAsAnnotation.value() : propertyName
				
				if (Modifier.isStatic(getter.modifiers))
				{
					gynameeClass.metaClass."static"[gynamizeAs] = getter.invoke(gynamoClass)
				}
				else
				{
					gynameeClass.metaClass[gynamizeAs] = getter.invoke(gynamo)
				}
			}

			// Gives the Gynamo a chance to do any setup with the new methods
			try
			{
				gynamoClass.postGynamize(gynameeClass)
			}
			catch (MissingMethodException e)
			{
				
			}

			if (registry[gynameeClass] == null) registry[gynameeClass] = []
			registry[gynameeClass] << gynamoClass
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
	
	/**
	 * 
	 */
	static boolean isGynamized(Class gynamee, Class gynamo)
	{
		return registry[gynamee]?.contains(gynamo)
	}
}