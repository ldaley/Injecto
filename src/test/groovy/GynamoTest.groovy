import gynamo.*
class GynamoTest extends GroovyTestCase {
	
	GynamoTest()
	{
		Gynamizer.gynamize(TestGynamee,TestGynamo)
	}
	
	void testDependencyHandling()
	{
		assert(Gynamizer.isGynamized(TestGynamee, TestGynamo))
		assert(Gynamizer.isGynamized(TestGynamee, OtherGynamo))
		assert(Gynamizer.isGynamized(TestGynamee, YetAnotherGynamo))
	}
	
	void testAttachment()
	{
		assert(TestGynamee.metaClass.hasMetaMethod("yetAnotherGynamoMethod"))
		assert(TestGynamee.metaClass.hasMetaMethod("getObjectProperty"))
		assert(TestGynamee.metaClass.hasMetaMethod("getStaticProperty"))
	}
	
	void testGynamizeAsWorks()
	{
		assert(TestGynamee.metaClass.hasMetaMethod("aliasedMethod"))
		assert(TestGynamee.metaClass.hasMetaMethod("aliasedStaticMethod"))
	}
	
	void testProperties()
	{
		assertEquals("aaa", TestGynamee.newInstance().literalString)
	}
	
	void testGettersSettersAndPropertyStorage()
	{
		def g1 = new TestGynamee()
		def g2 = new TestGynamee()
		
		g1.objectProperty = "g1"
		assertEquals("g1", g1.objectProperty) 
		
		g2.objectProperty = "g2"
		assertEquals("g2", g2.objectProperty) 
		
		assertEquals("12345", TestGynamee.getStaticProperty()) // Value set in TestGynamo postGynamize
		TestGynamee.setStaticProperty("54321")
		assertEquals("54321", TestGynamee.getStaticProperty()) // Value set in TestGynamo postGynamize
	}
}