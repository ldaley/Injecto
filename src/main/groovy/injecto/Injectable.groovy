package injecto;
import injecto.dynamic.*
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

	/**
	 * 
	 */
	static dynamicInstanceMethodDispatchTableProperty = "dynamicInstanceMethodDispatchTable"

	/**
	 * 
	 */
	static dynamicStaticMethodDispatchTableProperty = "dynamicStaticMethodDispatchTable"
	
	/**
	 * 
	 */
	static dynamicDispatchToCalculator = { Map dispatchTable, String name ->

		println "dispatching: $name"
		def dispatchTo
		dispatchTable.find {
			println "testing $name against ${it.value.dispatchPattern}"
			it.find {
				if (it.value.dispatchPattern.matcher(name).matches())
				{
					dispatchTo = it.value.dispatchTo
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
	
	static instanceMethodMissing = { String methodName, Object[] args ->
		println "in dispatch"
		def dispatchTo = delegate.class.dynamicDispatchToCalculator(delegate.class."$dynamicInstanceMethodDispatchTableProperty", methodName)
		if (dispatchTo)
		{
			delegate."$dispatchTo"(methodName, args)
		}
		else
		{
			throw new MissingMethodException(methodName, delegate.class, args)
		}
	}

	static staticMethodMissing = { String methodName, Object[] args ->
		def dispatchTo = delegate.class.dynamicDispatchToCalculator(delegate."$dynamicStaticMethodDispatchTableProperty", methodName)
		if (dispatchTo)
		{
			delegate."$dispatchTo"(methodName, args)
		}
		else
		{
			throw new MissingMethodException(methodName, delegate.class, args)
		}
	}
	
	static dynamicDispatchTableComparator = [compare:{a,b -> a.precedence <=> b.precedence}] as Comparator
	
	Class injectee
	Class injecto
	Object injectoInstance
	Method getter
	Field field
	String fieldName
	String name
	boolean isDynamicMethod
	boolean isDynamicGetter
	boolean isDynamicSetter
	Boolean isInjectoProperty

	Injectable(Class injectee, Class injecto, Object injectoInstance, Method getter)
	{
		this.injectee = injectee
		this.injecto = injecto
		this.injectoInstance = injectoInstance
		this.getter = getter
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
	
	boolean getIsDynamicGetter()
	{
		return false;
	}
	
	boolean getIsDynamicSetter()
	{
		return false;
	}
	
	Boolean getIsInjectoProperty()
	{
		if (isInjectoProperty == null) isInjectoProperty = new Boolean(getField()?.getAnnotation(InjectoProperty) != null)
		return isInjectoProperty
	}
	
	void inject()
	{
		if (getIsInjectoProperty())
		{
			injectoAsInjectoProperty()
		}
		else if (getIsDynamicMethod()) 
		{
			injectAsDynamicMethod()
		}
		else if (getIsDynamicGetter()) 
		{
			injectAsDynamicGetter()
		}
		else if (getIsDynamicSetter()) 
		{
			injectAsDynamicSetter()
		}
		else
		{
			injectPlainly()
		}
	}
	
	private void injectoAsInjectoProperty()
	{
		attachPropertyWithGetterAndSetter(getName(), null, getIsStatic())
	}
	
	private void injectAsDynamicMethod()
	{
		def table
		def dispatchTableProperty = (getIsStatic()) ? dynamicStaticMethodDispatchTableProperty : dynamicInstanceMethodDispatchTableProperty
		try
		{
			table = injectee."$dispatchTableProperty"
		}
		catch (MissingPropertyException)
		{
			println "MME <thrown></thrown>"
		}
		
		if (table == null)
		{
			println "attaching dynamic method dispatcher"
			table = [:]
			
			attachPropertyWithGetterAndSetter(dispatchTableProperty, table, true)
			attach("dynamicDispatchToCalculator", dynamicDispatchToCalculator, true)
			def methodMissingReplacement = getIsStatic() ? staticMethodMissing : instanceMethodMissing
			attach("methodMissing", methodMissingReplacement, getIsStatic())
		}
		
		def tableForInjecto = table.find { println it; it.key.equals(injecto.name) }?.value
		if (tableForInjecto == null)
		{
			println "attaching dynamic method dispatcher for injecto ${injecto.name}"
			tableForInjecto = new TreeSet(dynamicDispatchTableComparator)
			table[injecto.name] = tableForInjecto
		}
		
		def annotation = getField().getAnnotation(InjectoDynamicMethod)

		tableForInjecto.value << [
			dispatchPattern: Pattern.compile(annotation.pattern()),
			dispatchToMethodName: getName(),
			precedence: annotation.precedence()
		]
		
		injectPlainly()
	}
	
	private void injectAsDynamicGetter()
	{
		
	}
	
	private void injectAsDynamicSetter()
	{
		
	}
	
	private injectPlainly()
	{
		attach(getName(), (getIsStatic()) ? getter.invoke(injecto) : getter.invoke(injectoInstance), getIsStatic())
	}
	
	private void attach(String name, Object thing, boolean statically)
	{
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