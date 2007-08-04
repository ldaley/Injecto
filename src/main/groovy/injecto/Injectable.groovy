package injecto;
import injecto.support.*
import injecto.annotation.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Field
import java.util.regex.Pattern

class Injectable 
{
	
	/**
	 * Getter names that are excluded from being considered for injection
	 */
	static exclusions = ["getMetaClass", "getProperty"]
	
	Class injectee
	Class injecto
	Object injectoInstance
	Method getter
	Field field
	String fieldName
	String name
	boolean isDynamicMethod
	def thingToAttach

	Injectable(Class injectee, Class injecto, Object injectoInstance, Method getter)
	{
		this.injectee = injectee
		this.injecto = injecto
		this.injectoInstance = injectoInstance
		this.getter = getter
		this.thingToAttach = (getIsStatic()) ? getter.invoke(injecto) : getter.invoke(injectoInstance)
	}
	
	String getFieldName()
	{
		if (fieldName == null) fieldName = getter.name[3].toLowerCase() + getter.name.substring(4)
		return fieldName
	}
	
	Field getField()
	{
		if (field == null) field = injecto.getDeclaredField(getFieldName())
		return field 
	}
	
	String getName()
	{
		if (name == null)
		{
			name = getField()?.getAnnotation(InjectAs)?.value()
			if (name == null) name = getFieldName()
		}
		return name
	}
	
	boolean getIsStatic()
	{
		return Modifier.isStatic(getter.modifiers)
	}
	
	boolean getIsDynamicMethod()
	{
		return getField()?.getAnnotation(InjectoDynamicMethod)
	}
		
	void inject()
	{
		if (getIsDynamicMethod()) 
		{
			injectAsDynamicMethod()
		}
		else
		{
			injectPlainly()
		}
	}
		
	private void injectAsDynamicMethod()
	{
		if ((thingToAttach instanceof Closure) == false)
		{
			throw new IllegalStateException("An InjectoDynamicMethod must be a closure, " + getFieldName() + " on" + injecto.name + " is not")
		}
		if (thingToAttach.parameterTypes[0].equals(String) == false)
		{
			throw new IllegalStateException("An InjectoDynamicMethod must have 'String' as the first parameter type, " + getFieldName() + " on" + injecto.name + " doesn't")
		}
		
		def dispatchTable
		if (getIsStatic())
		{
			Injecto.inject(injectee, DynamicStaticMethodsInjecto)
			dispatchTable = injectee.dynamicStaticMethodDispatchTable

		}
		else
		{
			Injecto.inject(injectee, DynamicInstanceMethodsInjecto)
			dispatchTable = injectee.dynamicInstanceMethodDispatchTable
			println ""
			println "$dispatchTable"
			println ""
		}
		
		def annotation = getField().getAnnotation(InjectoDynamicMethod)

		def mapping = new DynamicDispatchMapping(
			pattern: Pattern.compile(annotation.pattern()),
			name: getName(),
			precedence: annotation.precedence(),
			argTypes: thingToAttach.parameterTypes
		)
		
		dispatchTable.add(injecto.name, mapping)
		
		injectPlainly()
	}
		
	private injectPlainly()
	{
		attach(getName(), thingToAttach, getIsStatic())
	}
	
	private void attach(String name, Object thing, boolean statically)
	{
		if (Injecto.logInjections)
		{
			println "Injecting '${name}'(${thing.toString()}) into '${injectee.name}' from '${injecto.name}'(static: ${statically})"
		}
		
		if (statically)
		{
			injectee.metaClass.'static'[name] = thing
		}
		else
		{
			injectee.metaClass[name] = thing
		}
	}
	
	private void attachPropertyWithGetterAndSetter(String propertyName, Object initialValue, boolean statically)
	{

		def propertyNameCapitalised = propertyName[0].toUpperCase() + propertyName.substring(1)
		println "attaching property $propertyNameCapitalised"
		def g = { -> 
			if (InjectoPropertyStorage[delegate].containsKey(propertyName) == false) InjectoPropertyStorage[delegate][propertyName] = initialValue
			return InjectoPropertyStorage[delegate][propertyName] 
		}
		def s = { InjectoPropertyStorage[delegate][propertyName] = it }
		
		attach("get" + propertyNameCapitalised, g, statically)
		attach("set" + propertyNameCapitalised, s, statically)
	}
	
	private static List allFor(Class injectee, Class injecto)
	{
		def injectables = []
		def injectoInstance = injecto.newInstance()
				
		injecto.methods.each { Method method ->
			if (
				method.name[0..2].equals("get") &&
				exclusions.contains(method.name) == false &&
				injecto.isAssignableFrom(method.declaringClass) == true
			)
			{
				injectables << new Injectable(injectee, injecto, injectoInstance, method)
			}
		}
		
		return injectables
	}
}