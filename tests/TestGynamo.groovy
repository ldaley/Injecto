import gynamo.*

@GynamoDependencies([OtherGynamo, YetAnotherGynamo])
class TestGynamo extends Gynamo 
{
	def getSomething = {
		return GynamoPropertyStorage[delegate].something
	}

	def setSomething = {
		GynamoPropertyStorage[delegate].something = it
	}
	
	def getStaticSomething = {
		return GynamoPropertyStorage[delegate].something
	}

	def setStaticSomething = {
		GynamoPropertyStorage[delegate].something = it
	}
		
	void postGynamize(Class clazz)
	{
		clazz.staticSomething = '12345'
	}
}