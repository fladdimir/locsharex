package locsharex.login;

import java.util.Collections;

import javax.inject.Singleton;

import com.nimbusds.jwt.JWTClaimsSet;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.DefaultOpenIdUserDetailsMapper;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdTokenResponse;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdUserDetailsMapper;
import locsharex.AppUser;
import locsharex.AppUserService;

@Singleton
@Replaces(DefaultOpenIdUserDetailsMapper.class)
public class AppOpenIdUserDetailsMapper implements OpenIdUserDetailsMapper {

    public AppOpenIdUserDetailsMapper(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    public static final String ID_PROVIDER = "ID_PROVIDER";
    private AppUserService appUserService;

    @Override
    public AuthenticationResponse createAuthenticationResponse(String providerName, OpenIdTokenResponse tokenResponse,
            OpenIdClaims openIdClaims, State state) {
        return this.createUserDetails(providerName, tokenResponse, openIdClaims);
    }

    @Override
    public UserDetails createUserDetails(String providerName, OpenIdTokenResponse tokenResponse,
            OpenIdClaims openIdClaims) {

        // create new user if necessary
        String providerSub = openIdClaims.getSubject();
        String preferredUsername = "changeMeInSettings";
        AppUser user = appUserService.findOrCreate(providerName, providerSub, preferredUsername);

        // just use minimal claims
        String internalSub = user.getId().toString();
        OpenIdClaims internalClaims = new JWTOpenIdClaims(new JWTClaimsSet.Builder().subject(internalSub)
                .expirationTime(openIdClaims.getExpirationTime()).claim(ID_PROVIDER, providerName).build());

        return new UserDetails(internalSub, Collections.emptyList(), internalClaims.getClaims());
    }

}
