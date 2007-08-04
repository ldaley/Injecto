package injecto;

class RegistrationTest extends GroovyTestCase 
{
	void testRegistration() 
	{
		use(Injecto) {
			Re1.inject(Ro1)
			assertEquals(true, Re1.isInjected(Ro1))
		}
	}
}

class Re1 {}
class Ro1 {}