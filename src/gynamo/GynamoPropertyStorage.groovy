package gynamo;

/**
 * Provides storage for dynamically added properties.
 * 
 * E.g. GynamoPropertyStorage[delegate].someAttribute = "blah"
 */
class GynamoPropertyStorage 
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
	static Object getAt(caller)
	{
		def identity = System.identityHashCode(caller)
		if (properties[identity] == null)
		{
			properties[identity] = [:]
		}
 		return properties[identity]
	}
}