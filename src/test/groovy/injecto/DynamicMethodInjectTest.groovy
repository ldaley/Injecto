package injecto;
import injecto.annotation.*

class DynamicMethodInjectTest extends GroovyTestCase 
{
	def instance
	
	DynamicMethodInjectTest()
	{
		Injecto.logInjections = true
		use(Injecto) { DMe1.inject(DMo1) }
		instance = new DMe1()
	}
	
	void testInstance() 
	{
		assertEquals("simpleHandler", instance.simpleX(new Integer(3)))
		assertEquals("simpleHandler2", instance.simpleXX(new Integer(3)))
	}
}

class DMe1{}
class DMo1
{
	@InjectoDynamicMethod(
		pattern = "simple(.{1})",
		precedence = 1
	)
	def simpleHandler = { String methodName, Integer number ->
		return "simpleHandler"
	}
	
	@InjectoDynamicMethod(
		pattern = "simple(.{2})",
		precedence = 2
	)
	def simpleHandler2 = { String methodName, Integer number ->
		return "simpleHandler2"
	}

/*	@InjectoDynamicMethod(
		pattern = "simple(.{1})",
		precedence = 1
	)
	static simpleHandlerStatic = { String methodName, Integer number ->
		return "simpleHandlerStatic"
	}
	
	@InjectoDynamicMethod(
		pattern = "simple(.{2})",
		precedence = 2
	)
	static simpleHandlerStatic2 = { String methodName, Integer number ->
		return "simpleHandlerStatic2"
	}*/
	
}
