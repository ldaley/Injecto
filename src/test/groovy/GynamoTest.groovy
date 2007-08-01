import gynamo.*
class GynamoTest extends GroovyTestCase {
	
	GynamoTest()
	{
		Gynamizer.gynamize(ExampleGynamee,ExampleGynamo)
	}
	
	void testDependencyHandling()
	{
		assert(Gynamizer.isGynamized(ExampleGynamee, ExampleGynamo))
		assert(Gynamizer.isGynamized(ExampleGynamee, OtherGynamo))
		assert(Gynamizer.isGynamized(ExampleGynamee, YetAnotherGynamo))
	}
	
	void testAttachment()
	{
		assert(ExampleGynamee.metaClass.hasMetaMethod("yetAnotherGynamoMethod"))
		assert(ExampleGynamee.metaClass.hasMetaMethod("getObjectProperty"))
		assert(ExampleGynamee.metaClass.hasMetaMethod("getStaticProperty"))
	}
	
	void testGynamizeAsWorks()
	{
		assert(ExampleGynamee.metaClass.hasMetaMethod("aliasedMethod"))
		assert(ExampleGynamee.metaClass.hasMetaMethod("aliasedStaticMethod"))
	}
	
	void testProperties()
	{
		assertEquals("aaa", ExampleGynamee.newInstance().literalString)
	}
	
	void testGettersSettersAndPropertyStorage()
	{
		def g1 = new ExampleGynamee()
		def g2 = new ExampleGynamee()
		
		g1.objectProperty = "g1"
		assertEquals("g1", g1.objectProperty) 
		
		g2.objectProperty = "g2"
		assertEquals("g2", g2.objectProperty) 
		
		assertEquals("12345", ExampleGynamee.getStaticProperty()) // Value set in ExampleGynamo postGynamize
		ExampleGynamee.setStaticProperty("54321")
		assertEquals("54321", ExampleGynamee.getStaticProperty()) // Value set in ExampleGynamo postGynamize
	}
}