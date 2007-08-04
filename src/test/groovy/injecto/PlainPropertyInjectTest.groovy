package injecto;
import injecto.annotation.*

/**
 * Some tests currently disabled because of Groovy bug.
 */
class PlainPropertyInjectTest extends GroovyTestCase 
{
	def instance
	
	PlainPropertyInjectTest()
	{
		use(Injecto) { PPe1.inject(PPo1) }
		instance = new PPe1()
	}
	
	void testInstanceProperty() 
	{
		assertEquals("instanceProperty", instance.instanceProperty)
		instance.instanceProperty = "changed"
		assertEquals("changed", instance.instanceProperty)
	}
	
/*	void testStaticProperty() 
	{
		assertEquals("staticProperty", PPe1.staticProperty)
		instance.staticProperty = "changed"
		assertEquals("changed", PPe1.staticProperty)
	}
*/
	void testAliasedInstanceProperty() 
	{
		assertEquals("aliasedInstanceProperty", instance.aliasedInstanceProperty)
	}
	
/*	void testAliasedStaticProperty() 
	{
		assertEquals("aliasedStaticProperty", PPe1.aliasedStaticProperty)
	}
*/	
}

class PPe1 {}
class PPo1 
{
	def instanceProperty = "instanceProperty"
	static staticProperty = "staticProperty"
	
	@InjectAs("aliasedInstanceProperty")
	def xxx = "aliasedInstanceProperty"
	
	@InjectAs("aliasedStaticProperty")
	static xxxx = "aliasedStaticProperty"
}