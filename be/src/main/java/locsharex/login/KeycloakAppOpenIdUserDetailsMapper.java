package locsharex.login;

import javax.inject.Named;
import javax.inject.Singleton;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.security.oauth2.endpoint.token.response.DefaultOpenIdUserDetailsMapper;
import locsharex.AppUserService;

@Singleton
@Named(KeycloakAppOpenIdUserDetailsMapper.KEYCLOAK)
@Replaces(DefaultOpenIdUserDetailsMapper.class)
@Requires(env = KeycloakAppOpenIdUserDetailsMapper.KEYCLOAK)
public class KeycloakAppOpenIdUserDetailsMapper extends AppOpenIdUserDetailsMapper {
    public static final String KEYCLOAK = "keycloak";

    public KeycloakAppOpenIdUserDetailsMapper(AppUserService appUserService) {
        super(appUserService);
    }
}