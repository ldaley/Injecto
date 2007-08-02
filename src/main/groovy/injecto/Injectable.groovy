package injecto;
import injecto.dynamic.*
import injecto.annotation.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Field

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
		return false;
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
		else
		{
			if (getIsDynamicMethod()) injectAsDynamicMethod()
			if (getIsDynamicGetter()) injectAsDynamicGetter()
			if (getIsDynamicSetter()) injectAsDynamicSetter()

			if (getIsStatic())
			{
				injectStatically()
			}
			else
			{
				injectNonStatically()
			}	
		}
	}
	
	private void injectoAsInjectoProperty()
	{
		def n = getName()
		def nCapitalised = n[0].toUpperCase() + n.substring(1)
		def g = { -> InjectoPropertyStorage[delegate][n] }
		def s = { InjectoPropertyStorage[delegate][n] = it }
		def attachPoint = (getIsStatic()) ? injectee.metaClass.'static' : injectee.metaClass
		
		attachPoint["get" + nCapitalised] = g
		attachPoint["set" + nCapitalised] = s
	}
	
	private void injectAsDynamicMethod()
	{
		
	}
	
	private void injectAsDynamicGetter()
	{
		
	}
	
	private void injectAsDynamicSetter()
	{
		
	}
	
	private void injectStatically()
	{
		injectee.metaClass.'static'[getName()] = getter.invoke(injecto)
	}
	
	private void injectNonStatically()
	{
		injectee.metaClass[getName()] = getter.invoke(injectoInstance)
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