package locsharex.login;

import java.net.URI;
import java.util.Optional;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import locsharex.login.AppEndSessionEndpoint.KeycloakEndSessionEndpoint;
import locsharex.login.AppEndSessionEndpoint.OktaEndSessionEndpoint;

// custom logout controller, used for id provider logout based on custom cookie info
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class LogoutController {

    public static final String APP_LOGOUT = "/app-logout";

    private KeycloakEndSessionEndpoint keycloakEndSessionEndpoint;
    private OktaEndSessionEndpoint oktaEndSessionEndpoint;

    public LogoutController(Optional<KeycloakEndSessionEndpoint> keycloakEndSessionEndpoint,
            Optional<OktaEndSessionEndpoint> oktaEndSessionEndpoint) {
        this.keycloakEndSessionEndpoint = keycloakEndSessionEndpoint.orElse(null);
        this.oktaEndSessionEndpoint = oktaEndSessionEndpoint.orElse(null);
    }

    @Get(APP_LOGOUT)
    public HttpResponse<URI> logout(Authentication authentication, HttpRequest<?> originating) {

        String provider = (String) authentication.getAttributes().get(AppOpenIdUserDetailsMapper.ID_PROVIDER);

        URI location;

        if (KeycloakAppOpenIdUserDetailsMapper.KEYCLOAK.equals(provider)) {
            location = keycloakEndSessionEndpoint.getUrl(originating);

        } else if (OktaAppOpenIdUserDetailsMapper.OKTA.equals(provider)) {
            location = oktaEndSessionEndpoint.getUrl(originating);

        } else {
            location = URI.create("/api/logout");
        }
        return HttpResponse.seeOther(location);
    }
}
