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
package injecto.support;

/**
 * A psuedo collection that represents the possible handlers for a missing method.
 * 
 * The underlying storage is a Map that has the name of the injecto class that added the mapping
 * where the value is a sorted set of mappings for that injecto.
 * 
 * This structure means that when searching through the table for a match, the first 
 * injecto's additions gets searched first. The set of DynamicDispatchMapping for each injecto are kept in
 * TreeSet ordered by the precedence key.
 * 
 * @author Luke Daley <ld@ldaley.com>
 * @todo The underlying storage should be completely hidden here.
 */
class DynamicDispatchTable 
{
	/**
	 * Used to keep the DynamicDispatchMapping sorted by precedence ascending.
	 */
	static mappingComparator = [compare:{a,b -> a.precedence <=> b.precedence}] as Comparator
	
	/**
	 * The storage for the table.
	 */
	Map table = [:]
	
	/**
	 * The only way anything should be added.
	 * 
	 * If there are no entries for 'key', a new TreeSet is added under that key and the mapping added.
	 * Otherwise the mapping is added to the existing set.
	 */
	void add(String key, DynamicDispatchMapping mapping)
	{
		if(table.containsKey(key) == false) table[key] = new TreeSet(mappingComparator)
		table[key] << mapping
	}
	
	/**
	 * Empties the table.
	 */
	void clear()
	{
		table.clear()
	}
	
	/**
	 * Used to see if there is anything in the table.
	 */
	boolean getIsEmpty()
	{
		return table.size() == 0
	}
}