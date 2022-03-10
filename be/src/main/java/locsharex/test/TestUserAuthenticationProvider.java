package locsharex.test;

import static io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims.CLAIMS_PREFERRED_USERNAME;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.inject.Singleton;

import com.nimbusds.jwt.JWTClaimsSet;

import org.reactivestreams.Publisher;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdUserDetailsMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import locsharex.login.AppOpenIdUserDetailsMapper;

/**
 * Provides authentication for a fixed set of test-users, without relying on
 * keycloak
 */
@Singleton
@Requires(env = "testAuth")
public class TestUserAuthenticationProvider implements AuthenticationProvider {

    private static final String PASSWORD = "password";
    private static final String TEST_PROVIDER = "test-provider";
    private static final Map<String, String> TEST_USER_CREDENTIALS = Map.of( //
            "Sherlock", PASSWORD, //
            "Watson", PASSWORD, //
            "Lestrade", PASSWORD);

    private OpenIdUserDetailsMapper openIdUserDetailsMapper;

    public TestUserAuthenticationProvider(AppOpenIdUserDetailsMapper openIdUserDetailsMapper) {
        this.openIdUserDetailsMapper = openIdUserDetailsMapper;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {
        return Flowable.create(emitter -> {

            Object username = authenticationRequest.getIdentity();
            Object password = authenticationRequest.getSecret();

            if (TEST_USER_CREDENTIALS.containsKey(username) && TEST_USER_CREDENTIALS.get(username).equals(password)) {

                // fake OpenId claims
                String providerSub = username.toString();
                Date in10Mins = Date.from(Instant.now().plus(10, MINUTES));
                JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().issuer(TEST_PROVIDER).subject(providerSub)
                        .claim(CLAIMS_PREFERRED_USERNAME, providerSub).expirationTime(in10Mins).build();
                OpenIdClaims claims = new JWTOpenIdClaims(jwtClaimsSet);

                // proceed normally
                emitter.onNext(openIdUserDetailsMapper.createAuthenticationResponse(TEST_PROVIDER, null, claims, null));
                emitter.onComplete();
            } else {
                emitter.onError(new AuthenticationException(new AuthenticationFailed()));
            }
        }, BackpressureStrategy.ERROR);
    }
}