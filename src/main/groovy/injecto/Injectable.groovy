package injecto;
import injecto.dynamic.*
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
	boolean isStatic
	boolean isDynamicMethod
	boolean isDynamicGetter
	boolean isDynamicSetter
	int dynamicDispatchPrecedence
	
	Injectable(Class injectee, Class injecto, Object injectoInstance, Method injectableGetter)
	{
		super(injectee: injectee, injecto: injecto, injectoInstance: injectoInstance, getter: getter)
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
			name = getField()?.getAnnotation(InjectWithName)?.value()
			if (name == null) name = getFieldName()
		}
		return name
	}
	
	boolean getIsStatic()
	{
		if (isStatic == null) isStatic = Modifier.isStatic(getter.modifiers)
		return isStatic
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
	
	void inject()
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
		injectee.metaClass[getName()] = getter.invoke(injectoInstance)
	}
	
	private void injectNonStatically()
	{
		injectee.metaClass.'static'[getName()] = getter.invoke(injecto)
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
	}
}