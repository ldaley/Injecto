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

/**
 * Provides storage for dynamically added properties.
 * 
 * E.g. InjectoPropertyStorage[delegate].someAttribute = "blah"
 * @author ld@ldaley.com
 * @since 1.0
 */
class InjectoPropertyStorage 
{
	/**
	 * The properties
	 */
	static Map properties = Collections.synchronizedMap(new WeakHashMap())
	
	/**
	 * Allows use of the [] operator on the class.
	 * 
	 * @return Will always be a map.
	 */
	static Object getAt(owner)
	{
		if (properties[owner] == null)
		{
			properties[owner] = [:]
		}
 		return properties[owner]
	}
}