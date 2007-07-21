import gynamo.*

@GynamoDependencies([OtherGynamo, YetAnotherGynamo])
class TestGynamo extends Gynamo 
{
	def getObjectProperty = {->
		return GynamoPropertyStorage[delegate].objectProperty
	}

	def setObjectProperty = {
		GynamoPropertyStorage[delegate].objectProperty = it
	}
	
	static getStaticProperty = {->
		return GynamoPropertyStorage[delegate].staticProperty
	}

	static setStaticProperty = {
		GynamoPropertyStorage[delegate].staticProperty = it
	}
		
	def literalString = "aaa"
	
	void postGynamize(Class clazz)
	{
		clazz.setStaticProperty('12345')
	}
}