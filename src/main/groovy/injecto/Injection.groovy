package injecto;
import injecto.support.*
import injecto.annotation.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Field
import java.util.regex.Pattern

class Injection 
{
	
	/**
	 * Getter names that are excluded from being considered for injection
	 */
	static exclusions = ["getMetaClass", "getProperty"]
	
	Class injectee
	Class injecto
	Object injectoInstance
	Method getter
	String fieldName
	Field field
	String name
	def dynamicMethodAnnotation
	def thingToAttach
	boolean isStatic

	Injectable(Class injectee, Class injecto, Object injectoInstance, Method getter)
	{
		this.injectee = injectee
		this.injecto = injecto
		this.injectoInstance = injectoInstance
		this.getter = getter
		this.thingToAttach = (getIsStatic()) ? getter.invoke(injecto) : getter.invoke(injectoInstance)
		this.field = injecto.getDeclaredField(getFieldName())
		this.fieldName = getter.name[3].toLowerCase() + getter.name.substring(4)
		
		this.name = getField()?.getAnnotation(InjectAs)?.value()
		if (this.name == null) this.name = getFieldName()
		
		this.isStatic = Modifier.isStatic(getter.modifiers)
		
		this.dynamicMethodAnnotation = getField()?.getAnnotation(InjectoDynamicMethod)
	}
		
	void inject()
	{
		if (this.dynamicMethodAnnotation != null) 
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
		if ((this.thingToAttach instanceof Closure) == false)
		{
			throw new IllegalStateException("An InjectoDynamicMethod must be a closure, " + this.fieldName + " on" + injecto.name + " is not")
		}
		if (this.thingToAttach.parameterTypes[0].equals(String) == false)
		{
			throw new IllegalStateException("An InjectoDynamicMethod must have 'String' as the first parameter type, " + this.fieldName + " on" + injecto.name + " doesn't")
		}
		
		def dispatchTable
		if (this.isStatic)
		{
			Injecto.inject(injectee, DynamicStaticMethodsInjecto)
			dispatchTable = injectee.dynamicStaticMethodDispatchTable
		}
		else
		{
			Injecto.inject(injectee, DynamicInstanceMethodsInjecto)
			dispatchTable = injectee.dynamicInstanceMethodDispatchTable
		}

		def mapping = new DynamicDispatchMapping(
			pattern: Pattern.compile(this.dynamicMethodAnnotation.pattern()),
			name: this.name,
			precedence: this.dynamicMethodAnnotation.precedence(),
			argTypes: this.thingToAttach.parameterTypes
		)
		
		dispatchTable.add(injecto.name, mapping)
		
		injectPlainly()
	}
		
	private injectPlainly()
	{
		attach(this.name, this.thingToAttach, this.isStatic)
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
	
	private void attachManagedPropertyWithGetterAndSetter(String propertyName, Object initialValue, boolean statically)
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