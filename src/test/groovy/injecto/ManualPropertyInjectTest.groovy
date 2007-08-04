package injecto;
import injecto.annotation.*
import injecto.InjectoPropertyStorage

/**
 * Some tests currently disabled because of Groovy bug.
 */
class ManualPropertyInjectTest extends GroovyTestCase 
{
	def instance1
	def instance2
	
	ManualPropertyInjectTest()
	{
		use(Injecto) { MPe1.inject(MPo1) }
		instance1 = new MPe1()
		instance2 = new MPe1()
	}
	
	void testInstanceProperty() 
	{
		assertEquals(null, instance1.instanceProperty)
		assertEquals(null, instance2.instanceProperty)
		instance1.instanceProperty = "1"
		instance2.instanceProperty = "2"
		assertEquals("1", instance1.instanceProperty)
		assertEquals("2", instance2.instanceProperty)
		
		instance1.instanceProperty = 1
	}
	
	void testStaticProperty() 
	{
		assertEquals(null, MPe1.staticProperty)
		MPe1.staticProperty = "changed"
		assertEquals("changed", MPe1.staticProperty)
	}
}

class MPe1 {}
class MPo1 
{
	def getInstanceProperty = { -> 
		return InjectoPropertyStorage[delegate]["instanceProperty"]
	}
	
	def setInstanceProperty = { String it ->
		InjectoPropertyStorage[delegate]["instanceProperty"] = it
	}
	
	static getStaticProperty = { -> 
		return InjectoPropertyStorage[delegate]["staticProperty"]
	}
	
	static setStaticProperty = { String it ->
		InjectoPropertyStorage[delegate]["staticProperty"] = it
	}
}