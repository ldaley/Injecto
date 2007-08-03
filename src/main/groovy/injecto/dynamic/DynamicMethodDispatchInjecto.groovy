package injecto.dynamic;

class DynamicMethodDispatchInjecto 
{
	List dynamicInjectedInstanceMethodDispatchTable = []
	
	def methodMissing = { String methodName, Object[] args ->
		
		def dispatchTo = delegate.dynamicInjectedInstanceMethodDispatchTable.find {
			it.find { dispatchToMethodName, dispatchPattern ->
				methodName ==~ ~/$dispatchPattern/
			}
		}
		
		if (dispatchTo)
		{
			return delegate."$dispatchTo"(methodName, args)
		}
		else
		{
			throw new MissingMethodException(methodName, delegate.class, args, false);
		}
	}
}