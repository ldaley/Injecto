package injecto;
import injecto.annotation.*

class InjectoPropertyTest extends GroovyTestCase 
{
	def instance1
	def instance2
	
	InjectoPropertyTest()
	{
		use (Injecto) { IPe1.inject(IPo1) }
		instance1 = new IPe1()
		instance2 = new IPe1()
	}
	
	void testInstanceReadWriteNoDefault() 
	{
		assertEquals(true, IPe1.metaClass.hasMetaMethod("getInstanceReadWriteNoDefault"))
		assertEquals(true, IPe1.metaClass.hasMetaMethod("setInstanceReadWriteNoDefault"))
		
		assertNull(instance1.instanceReadWriteNoDefault)
		instance1.instanceReadWriteNoDefault = 'changed'
		assertEquals('changed', instance1.instanceReadWriteNoDefault)
		
		assertNull(instance2.instanceReadWriteNoDefault)
		instance2.instanceReadWriteNoDefault = 'changed2'
		assertEquals('changed2', instance2.instanceReadWriteNoDefault)
		assertEquals('changed', instance1.instanceReadWriteNoDefault)
	}
	
	void testStaticReadWriteNoDefault() 
	{
		assertEquals(true, IPe1.metaClass.hasMetaMethod("getStaticReadWriteNoDefault"))
		assertEquals(true, IPe1.metaClass.hasMetaMethod("setStaticReadWriteNoDefault"))
		
		assertNull(IPe1.staticReadWriteNoDefault)
		IPe1.staticReadWriteNoDefault = 'changed'
		assertEquals('changed', IPe1.staticReadWriteNoDefault)
	}
	
	void testInstanceReadWriteWithDefault() 
	{
		assertEquals(true, IPe1.metaClass.hasMetaMethod("getInstanceReadWriteWithDefault"))
		assertEquals(true, IPe1.metaClass.hasMetaMethod("setInstanceReadWriteWithDefault"))
		
		assertEquals("instanceDefault", instance1.instanceReadWriteWithDefault)
		instance1.instanceReadWriteWithDefault = 'changed'
		assertEquals('changed', instance1.instanceReadWriteWithDefault)
		
		assertEquals("instanceDefault", instance2.instanceReadWriteWithDefault)
		instance2.instanceReadWriteWithDefault = 'changed2'
		assertEquals('changed2', instance2.instanceReadWriteWithDefault)
		assertEquals('changed', instance1.instanceReadWriteWithDefault)
	}
	
	void testStaticReadWriteWithDefault() 
	{
		assertEquals(true, IPe1.metaClass.hasMetaMethod("getStaticReadWriteWithDefault"))
		assertEquals(true, IPe1.metaClass.hasMetaMethod("setStaticReadWriteWithDefault"))
		
		assertEquals("staticDefault", IPe1.staticReadWriteWithDefault)
		IPe1.staticReadWriteWithDefault = 'changed'
		assertEquals('changed', IPe1.staticReadWriteWithDefault)
	}
	
	void testInstanceReadNoDefault()
	{
		assertEquals(true, instance1.metaClass.hasMetaMethod("getInstanceReadNoDefault"))
		assertEquals(false, instance1.metaClass.hasMetaMethod("setInstanceReadNoDefault"))
	}
	
	void testStaticReadNoDefault()
	{
		assertEquals(true, IPe1.metaClass.hasMetaMethod("getStaticReadNoDefault"))
		assertEquals(false, IPe1.metaClass.hasMetaMethod("setStaticReadNoDefault"))
	}
	
	void testInstanceWriteNoDefault()
	{
		assertEquals(false, instance1.metaClass.hasMetaMethod("getInstanceWriteNoDefault"))
		assertEquals(true, instance1.metaClass.hasMetaMethod("setInstanceWriteNoDefault"))
	}
	
	void testStaticWriteNoDefault()
	{
		assertEquals(false, IPe1.metaClass.hasMetaMethod("getStaticWriteNoDefault"))
		assertEquals(true, IPe1.metaClass.hasMetaMethod("setStaticWriteNoDefault"))
	}
	
	void testInstanceReadWriteCustomGetterWithDefault()
	{
		assertEquals("customWithCustomGetter", instance1.instanceReadWriteCustomGetterWithDefault)
		instance1.instanceReadWriteCustomGetterWithDefault = "changed"
		assertEquals("customchanged", instance1.instanceReadWriteCustomGetterWithDefault)
	}
	
	void testStaticReadWriteCustomGetterWithDefault()
	{
		assertEquals("customWithCustomGetter", IPe1.staticReadWriteCustomGetterWithDefault)
		IPe1.staticReadWriteCustomGetterWithDefault = "changed"
		assertEquals("customchanged", IPe1.staticReadWriteCustomGetterWithDefault)
	}
}

class IPe1 {}

class IPo1
{
	@InjectoProperty
	def instanceReadWriteNoDefault
	
	@InjectoProperty
	static staticReadWriteNoDefault
	
	@InjectoProperty
	def instanceReadWriteWithDefault = "instanceDefault"
	
	@InjectoProperty
	static staticReadWriteWithDefault = "staticDefault"
	
	@InjectoProperty(write = false)
	def instanceReadNoDefault
	
	@InjectoProperty(write = false)
	static staticReadNoDefault

	@InjectoProperty(read = false)
	def instanceWriteNoDefault
	
	@InjectoProperty(read = false)
	static staticWriteNoDefault
	
	@InjectoProperty
	def instanceReadWriteCustomGetterWithDefault = "WithCustomGetter"
	
	def getInstanceReadWriteCustomGetterWithDefault = { -> 
		"custom" + delegate.getInjectoProperty("instanceReadWriteCustomGetterWithDefault")
	}
	
	@InjectoProperty
	static staticReadWriteCustomGetterWithDefault = "WithCustomGetter"
	
	static getStaticReadWriteCustomGetterWithDefault = { -> 
		"custom" + delegate.getInjectoProperty("staticReadWriteCustomGetterWithDefault")
	}
}