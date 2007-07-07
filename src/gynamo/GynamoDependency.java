package gynamo;
import java.lang.annotation.*;

/**
 * Specifies a single Gynamo that this Gynamo depends on
 * 
 * e.g. @GynamoDependency(SomeOtherGynamo)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface GynamoDependency {
    Class value();
}