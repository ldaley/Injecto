package injecto.support;
import injecto.Injecto
import injecto.dynamicmethod.*
import injecto.annotation.*
import injecto.property.*
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
	def propertyAnnotation
	def thingToAttach
	boolean isStatic

	Injection(Class injectee, Class injecto, Object injectoInstance, Method getter)
	{
		this.injectee = injectee
		this.injecto = injecto
		this.injectoInstance = injectoInstance
		this.getter = getter
		this.isStatic = Modifier.isStatic(getter.modifiers)
		this.thingToAttach = (this.isStatic) ? getter.invoke(injecto) : getter.invoke(injectoInstance)
		this.fieldName = getter.name[3].toLowerCase() + getter.name.substring(4)
		this.field = injecto.getDeclaredField(this.fieldName)
		
		this.name = getField()?.getAnnotation(InjectAs)?.value()
		if (this.name == null) this.name = getFieldName()
		
		this.dynamicMethodAnnotation = getField()?.getAnnotation(InjectoDynamicMethod)
		
		this.propertyAnnotation = getField()?.getAnnotation(InjectoProperty)
	}
		
	void inject()
	{
		if (this.dynamicMethodAnnotation != null && this.propertyAnnotation != null)
		{
			throw IllegalStateException("A Injecto can not define something that is a dynamic method and a property, " + this.fieldName + " on" + injecto.name + " has both annotations")
		}
		else if (this.dynamicMethodAnnotation != null) 
		{
			injectAsDynamicMethod()
		}
		else if (this.propertyAnnotation != null)
		{
			injectAsProperty()
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

	private void injectAsProperty()
	{
		def propertyName = this.name
		def propertyNameCapitalised = this.name[0].toUpperCase() + this.name.substring(1)
		use (Injecto) {
			(this.isStatic) ? injectee.inject(StaticPropertyInjecto) : injectee.inject(InstancePropertyInjecto)
		}
		
		if (this.propertyAnnotation.read())
		{
			try
			{
				injecto.getDeclaredField("getGet" + propertyNameCapitalised)
			}
			catch (NoSuchFieldException e)
			{
				def g = { -> return delegate.getInjectoProperty(propertyName) }
				attach("get" + propertyNameCapitalised, g, this.isStatic)
			}
		}
		
		if (this.propertyAnnotation.write())
		{
			try
			{
				injecto.getDeclaredField("getSet" + propertyNameCapitalised)
			}
			catch (NoSuchFieldException e)
			{
				def s = { delegate.setInjectoProperty(propertyName, it) }
				attach("set" + propertyNameCapitalised, s, this.isStatic)
			}
		}
		
		if (this.thingToAttach != null)
		{
			if (this.isStatic)
			{
				InjectoPropertyStorage[injectee][propertyName] = thingToAttach
			}
			else
			{
				InjectoPropertyStorage.addDefaultFor(injectee,injecto,propertyName, this.fieldName)
			}
		}
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
				injectables << new Injection(injectee, injecto, injectoInstance, method)
			}
		}
		
		return injectables
	}
}