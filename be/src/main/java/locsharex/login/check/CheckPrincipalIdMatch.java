package locsharex.login.check;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;

/**
 * Checks that the name of the Principal argument matches the UUID of the
 * request data. Compares the <b>first</b> UUID or UserIdHolder argument.
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD })
@Around
@Type(CheckPrincipalIdMatchInterceptor.class)
public @interface CheckPrincipalIdMatch {
}
