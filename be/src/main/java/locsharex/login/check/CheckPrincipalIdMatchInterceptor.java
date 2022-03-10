package locsharex.login.check;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.http.HttpResponse;
import locsharex.AppUserDtos.UserIdHolder;

/**
 * @see CheckPrincipalIdMatch
 */
@Singleton
public class CheckPrincipalIdMatchInterceptor implements MethodInterceptor<Object, HttpResponse<?>> {

    private boolean isMatchingUser(Principal principal, UUID id) {
        return UUID.fromString(principal.getName()).equals(id);
    }

    private <T> Optional<T> findFirstInParameters(MethodInvocationContext<?, ?> context, Class<T> clazz) {
        return Arrays.stream(context.getParameterValues()).filter(clazz::isInstance).map(clazz::cast).findFirst();
    }

    @Override
    public HttpResponse<?> intercept(MethodInvocationContext<Object, HttpResponse<?>> context) { // NOSONAR

        Optional<Principal> principal = findFirstInParameters(context, Principal.class);

        Optional<UUID> id = findFirstInParameters(context, UUID.class);
        if (id.isEmpty()) {
            id = findFirstInParameters(context, UserIdHolder.class).map(UserIdHolder::getId);
        }

        if (principal.isEmpty() || id.isEmpty() || !isMatchingUser(principal.get(), id.get())) {
            return HttpResponse.unauthorized();
        }
        return context.proceed();
    }

}
