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
package injecto;
import injecto.annotation.*;


/**
 * 
 * 
 * @since 1.0
 */
abstract class Injecto
{
	
	/**
	 * Maintains a record of which class have been injected with what
	 */
	static registry = [:]

	/**
	 * Copies the readable properties from one class (injecto) to another (injectee)
	 * 
	 * @param injectee The class that will be injected into
	 * @param injecto The class that contains the things to be injected
	 */
	static void inject(Class injectee, Class injecto)
	{	
		if (isInjected(injectee, injecto) == false)
		{
			injectDependencies(injectee, injecto)
			tryPreInjectHook(injectee, injecto)
			
			Injectable.allFor(injectee, injecto).each { it.inject() }
			
			addToRegistry(injectee, injecto)
			tryPostInjectHook(injectee, injecto)
		}
	}
	
	static injectDependencies(Class injectee, Class injecto)
	{
		def dependencies = []

		injecto.getAnnotation(InjectoDependencies)?.value().each { dependencies << it }
		if (injecto.getAnnotation(InjectoDependency)) dependencies << injecto.getAnnotation(InjectoDependency).value()
		
		dependencies.each { Injecto.inject(injectee, it) }
	}
	
	private static tryPreInjectHook(Class injectee, Class injecto)
	{
		try
		{
			injecto.preInject(injectee)
		}
		catch (MissingMethodException e)
		{
			// TODO Need to check if the missing method is due to preInject or not
		}
	}

	private static tryPostInjectHook(Class injectee, Class injecto)
	{
		try
		{
			injecto.postInject(injectee)
		}
		catch (MissingMethodException e)
		{
			// TODO Need to check if the missing method is due to postInject or not
		}
	}
	
	private static addToRegistry(Class injectee, Class injecto)
	{
		if (registry[injectee.name] == null) registry[injectee.name] = []
		registry[injectee.name] << injecto.name
	}

	/**
	 * 
	 */
	static boolean isInjected(Class injectee, Class injecto)
	{
		return registry[injectee.name]?.contains(injecto.name)
	}

}