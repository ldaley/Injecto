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
import injecto.annotation.*
import org.codehaus.groovy.runtime.MetaClassHelper

/**
 * Provides the dynamic dispatch logic as well as some util type stuff.
 * 
 * @author Luke Daley <ld@ldaley.com>
 */
class DynamicDispatchHelper
{
	/**
	 * For debugging dynamic method dispatches. 
	 * 
	 * If your having trouble working out why the method that gets called for a
	 * dynamic dispatch gets picked, set this to true
	 */
	static boolean logEvaluations = false
	
	/**
	 * Scans the passed in dispatch table and returns the name of the method in the table that 
	 * is the best fit.
	 * 
	 * The first thing that happens is that the dispatch table is filtered by filterDispatchTable().
	 * 
	 * Out of the remaining mappings, the first one whose pattern gets a match against the method name is picked.
	 * 
	 * If no candidate is found, null is returned
	 * 
	 * @return The name of the method to dispatch to, or null if none could be found. 
	 */
	static String evaluateDynamicDispatchDestination(DynamicDispatchTable dispatchTable, String methodName, Object[] args)
	{
		def filteredDispatchTable = filterDispatchTable(dispatchTable, methodName, args)
		
		def dispatchTo
		filteredDispatchTable.table.find { String injectoClass, Set mappings ->
			mappings.find { DynamicDispatchMapping mapping ->
				if (mapping.pattern.matcher(methodName).matches())
				{
					dispatchTo = mapping.name
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		return dispatchTo
	}
	
	/**
	 * Filters a dispatch table to only the entries that have the *best* match between signature and the arguments.
	 * 
	 * The MetaClassHelper.parametersAreCompatible() and MetaClassHelper.calculateParameterDistance() methods are used
	 * to determine what the best match will be.
	 * 
	 * The search is global to the whole dispatch table. There is no precedence in terms of filtering.
	 * 
	 * @return A cut down version of the passed in dispatch table, may be empty if no suitable entries were found.
	 */
	static DynamicDispatchTable filterDispatchTable(DynamicDispatchTable dispatchTable, String methodName, Object[] args)
	{
		def shortestDistance = -1
		def filteredDispatchTable = new DynamicDispatchTable()
		def argTypes = MetaClassHelper.convertToTypeArray(args)
		
		dispatchTable.table.each { String injectoClassName, Set mappings ->
			mappings.each { DynamicDispatchMapping mapping ->
				if (MetaClassHelper.parametersAreCompatible(argTypes, mapping.argTypes))
				{
					long mappingDistance = MetaClassHelper.calculateParameterDistance(argTypes, mapping.argTypes)

					if (filteredDispatchTable.isEmpty) 
					{
						filteredDispatchTable.add(injectoClassName, mapping)
						shortestDistance = mappingDistance
					} 
					else if (mappingDistance < shortestDistance) 
					{
						shortestDistance = mappingDistance
						filteredDispatchTable.clear()
						filteredDispatchTable.add(injectoClassName, mapping)
					} 
					else if (mappingDistance == shortestDistance) 
					{
						filteredDispatchTable.add(injectoClassName, mapping)
					}
				}
			}
		}
		return filteredDispatchTable
	}
	
	/**
	 * Convenience method to alter the argument list by prepending the method name.
	 */
	static Object[] prependMethodNameToArgs(String methodName, Object[] args)
	{
		def realArgs = [methodName]
		args.each { realArgs << it }
		return realArgs as Object[]
	}
}