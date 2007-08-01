import gynamo.annotation.*
import gynamo.GynamoPropertyStorage

@GynamoDependencies([OtherGynamo, YetAnotherGynamo])
class TestGynamo 
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
	
	static void postGynamize(Class clazz)
	{
		clazz.setStaticProperty('12345')
	}
}