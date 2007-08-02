package injecto;
import injecto.annotation.*
import injecto.InjectoPropertyStorage

class InjectoTest extends GroovyTestCase {
	
	InjectoTest()
	{
		Gynamizer.gynamize(ExampleInjectee,ExampleInjecto)
	}
	
	void testDependencyHandling()
	{
		assert(Injecto.isInjected(ExampleInjectee, ExampleInjecto))
		assert(Injecto.isInjected(ExampleInjectee, OtherInjecto))
		assert(Injecto.isInjected(ExampleInjectee, YetAnotherInjecto))
	}
	
	void testAttachment()
	{
		assert(ExampleInjectee.metaClass.hasMetaMethod("yetAnotherInjectoMethod"))
		assert(ExampleInjectee.metaClass.hasMetaMethod("getObjectProperty"))
		assert(ExampleInjectee.metaClass.hasMetaMethod("getStaticProperty"))
	}
	
	void testInjectAs()
	{
		assert(ExampleInjectee.metaClass.hasMetaMethod("aliasedMethod"))
		assert(ExampleInjectee.metaClass.hasMetaMethod("aliasedStaticMethod"))
	}
	
	void testProperties()
	{
		assertEquals("aaa", ExampleInjectee.newInstance().literalString)
	}
	
	void testGettersSettersAndPropertyStorage()
	{
		def g1 = new ExampleInjectee()
		def g2 = new ExampleInjectee()
		
		g1.objectProperty = "g1"
		assertEquals("g1", g1.objectProperty) 
		
		g2.objectProperty = "g2"
		assertEquals("g2", g2.objectProperty) 
		
		assertEquals("12345", ExampleInjectee.getStaticProperty()) // Value set in ExampleInjecto postGynamize
		ExampleInjectee.setStaticProperty("54321")
		assertEquals("54321", ExampleInjectee.getStaticProperty()) // Value set in ExampleInjecto postGynamize
	}
}

class ExampleInjectee {}

@InjectoDependencies([OtherInjecto, YetAnotherInjecto])
class ExampleInjecto 
{
	def getObjectProperty = {->
		return InjectoPropertyStorage[delegate].objectProperty
	}

	def setObjectProperty = {
		InjectoPropertyStorage[delegate].objectProperty = it
	}
	
	static getStaticProperty = {->
		return InjectoPropertyStorage[delegate].staticProperty
	}

	static setStaticProperty = {
		InjectoPropertyStorage[delegate].staticProperty = it
	}
		
	def literalString = "aaa"
	
	static void postGynamize(Class clazz)
	{
		clazz.setStaticProperty('12345')
	}
}

class OtherInjecto {

	@GynamizeAs("aliasedMethod")
	def otherInjectoMethod = {

	}

	@GynamizeAs("aliasedStaticMethod")
	static otherInjectoStaticMethod = {

	}
	
}

class YetAnotherInjecto {

	def yetAnotherInjectoMethod = {
	}
	
}