package locsharex.login;

import java.net.URI;
import java.util.Optional;

import javax.inject.Singleton;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.oauth2.client.OpenIdProviderMetadata;
import io.micronaut.security.oauth2.configuration.endpoints.EndSessionConfiguration;

/**
 * https://github.com/jhipster/generator-jhipster-micronaut/blob/c2eb3ae3aa7fcf76981e5dba30ae7f721f12ebe8/generators/server/templates/src/main/java/package/security/KeycloakEndSessionEndpoint.java.ejs
 */
public abstract class AppEndSessionEndpoint {

    @Singleton
    public static class KeycloakEndSessionEndpoint extends AppEndSessionEndpoint {

        private static final String PARAM_REDIRECT_URI = "redirect_uri";

        public KeycloakEndSessionEndpoint(Optional<OpenIdProviderMetadata> openIdProviderMetadata,
                Optional<EndSessionConfiguration> endSessionConfiguration, HttpHostResolver httpHostResolver) {
            super(openIdProviderMetadata, endSessionConfiguration, httpHostResolver);
        }

        @Override
        protected String getRedirectParam() {
            return PARAM_REDIRECT_URI;
        }

    }

    @Singleton
    public static class OktaEndSessionEndpoint extends AppEndSessionEndpoint {

        private static final String PARAM_REDIRECT_URI = "fromURI";

        public OktaEndSessionEndpoint(Optional<OpenIdProviderMetadata> openIdProviderMetadata,
                Optional<EndSessionConfiguration> endSessionConfiguration, HttpHostResolver httpHostResolver) {
            super(openIdProviderMetadata, endSessionConfiguration, httpHostResolver);
        }

        @Override
        protected String getRedirectParam() {
            return PARAM_REDIRECT_URI;
        }

    }

    private final OpenIdProviderMetadata openIdProviderMetadata;
    private final EndSessionConfiguration endSessionConfiguration;
    private final HttpHostResolver httpHostResolver;

    protected AppEndSessionEndpoint(Optional<OpenIdProviderMetadata> openIdProviderMetadata,
            Optional<EndSessionConfiguration> endSessionConfiguration, HttpHostResolver httpHostResolver) {
        this.openIdProviderMetadata = openIdProviderMetadata.orElse(null);
        this.endSessionConfiguration = endSessionConfiguration.orElse(null);
        this.httpHostResolver = httpHostResolver;
    }

    public URI getUrl(HttpRequest<?> originating) {
        return UriBuilder.of(URI.create(openIdProviderMetadata.getEndSessionEndpoint())).queryParam(getRedirectParam(),
                httpHostResolver.resolve(originating) + endSessionConfiguration.getRedirectUri()).build();
    }

    protected abstract String getRedirectParam();
}
