package locsharex.login;

import javax.inject.Named;
import javax.inject.Singleton;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.security.oauth2.endpoint.token.response.DefaultOpenIdUserDetailsMapper;
import locsharex.AppUserService;

@Singleton
@Named(OktaAppOpenIdUserDetailsMapper.OKTA)
@Replaces(DefaultOpenIdUserDetailsMapper.class)
@Requires(env = OktaAppOpenIdUserDetailsMapper.OKTA)
public class OktaAppOpenIdUserDetailsMapper extends AppOpenIdUserDetailsMapper {
    public static final String OKTA = "okta";

    public OktaAppOpenIdUserDetailsMapper(AppUserService appUserService) {
        super(appUserService);
    }
}