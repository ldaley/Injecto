package injecto;
import injecto.annotation.*

class MethodInjectTest extends GroovyTestCase 
{
    def instance
    MethodInjectTest()
    {
        use(Injecto) { MIe1.inject(MIo1) }
        instance = new MIe1()
    }
    
    void testInstanceMethodInject() 
    {
        assertEquals(true, instance.instanceMethod())
    }
    
    void testStaticMethodInject() 
    {
        assertEquals(true, MIe1.staticMethod())
    }

    void testOverloadedInstanceMethodInject() 
    {
        assertEquals(String, instance.overloadedInstanceMethod("test"))
        assertEquals(Integer, instance.overloadedInstanceMethod(new Integer(5)))
    }
    
    void testOverloadedStaticMethodInject() 
    {
        assertEquals(String, MIe1.overloadedStaticMethod("test"))
        assertEquals(Integer, MIe1.overloadedStaticMethod(new Integer(5)))
    }    
}

class MIe1 {}
class MIo1 
{
    def instanceMethod = { -> true }
    static staticMethod = { -> true }
    
    def overloadedInstanceMethod = { String it -> 
        return String
    }
    
    @InjectAs("overloadedInstanceMethod")
    def overloadedInstanceMethod2 = { Integer it -> 
        return Integer
    }
    
    static overloadedStaticMethod = { String it -> 
        return String
    }
    
    @InjectAs("overloadedStaticMethod")
    static overloadedStaticMethod2 = { Integer it -> 
        return Integer
    }
}