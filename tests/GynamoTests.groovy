import gynamo.*
class GynamoTests extends GroovyTestCase {
	
	GynamoTests()
	{
		Gynamo.gynamize(TestGynamee,TestGynamo)
	}
	
	void testDependencyHandling()
	{
		assertEquals(true, TestGynamo.newInstance().isGynamized(TestGynamee))
		assertEquals(true, OtherGynamo.newInstance().isGynamized(TestGynamee))
		assertEquals(true, YetAnotherGynamo.newInstance().isGynamized(TestGynamee))
	}
	
	void testAttachment()
	{
		assertEquals(true, TestGynamee.metaClass.hasMetaMethod("otherGynamoMethod"))
		assertEquals(true, TestGynamee.metaClass.hasMetaMethod("yetAnotherGynamoMethod"))
		assertEquals(true, TestGynamee.metaClass.hasMetaMethod("getObjectProperty"))
		assertEquals(true, TestGynamee.metaClass.hasMetaMethod("getStaticProperty"))
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