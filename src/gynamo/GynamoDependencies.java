package gynamo;
import java.lang.annotation.*;

/**
 * Specifies an array Gynamos that this Gynamo depends on
 * 
 * e.g. @GynamoDependencies([SomeOtherGynamo, AnotherGynamo]) (groovy)
 * e.g. @GynamoDependencies({SomeOtherGynamo, AnotherGynamo}) (java)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface GynamoDependencies {
    Class[] value();
}