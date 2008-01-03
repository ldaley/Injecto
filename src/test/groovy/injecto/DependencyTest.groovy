package injecto;
import injecto.annotation.*

class DependencyTest extends GroovyTestCase {
    
    void testDependencyResolution() 
    {
        use(Injecto) { 
            De1.inject(Do1)
            assertEquals(true, De1.isInjected(Do2))
            assertEquals(true, De1.isInjected(Do3))
            assertEquals(true, De1.isInjected(Do4))
        }
        
        def e = new De1()
        
        1.upto(4) {
            assertEquals("Do$it", e."fromDo$it"())
        }
        
    }
}

class De1 {}

@InjectoDependencies([Do2, Do3])
class Do1 
{
    def fromDo1 = { -> "Do1" }
}

class Do2 
{
    def fromDo2 = { -> "Do2" }
}

@InjectoDependency(Do4)
class Do3 
{
    def fromDo3 = { -> "Do3" }
}

class Do4 
{
    def fromDo4 = { -> "Do4" }
}