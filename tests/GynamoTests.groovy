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
		assertEquals(true, TestGynamee.metaClass.hasMetaMethod("getSomething"))
		assertEquals(true, TestGynamee.metaClass.hasMetaMethod("getStaticSomething"))
	}
	
	void testIsGynamized() 
	{
		def gynamo = new TestGynamo()
		assertEquals(true, gynamo.isGynamized(TestGynamee))
	}
}