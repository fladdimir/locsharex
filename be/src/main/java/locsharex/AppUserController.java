package locsharex;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import locsharex.AppUserDtos.Mapper;
import locsharex.AppUserDtos.SimpleUserDto;
import locsharex.AppUserDtos.SimpleUserWithLocationDto;
import locsharex.AppUserDtos.UserWithSharedLocationsDto;
import locsharex.AppUserDtos.UserWithSharedUsersDto;
import locsharex.login.LogoutController;
import locsharex.login.check.CheckPrincipalIdMatch;

@Controller("/users")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class AppUserController {

    private AppUserService service;

    public AppUserController(AppUserService service) {
        this.service = service;
    }

    @Get("/login-info")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public Map<String, UUID> userInfo(@Nullable Principal principal) {
        if (principal == null)
            return Collections.singletonMap("userId", null);
        AppUser user = service.findOrThrowError(principal);
        return Collections.singletonMap("userId", user.getId());
    }

    @Get("/{id}")
    @Transactional
    @CheckPrincipalIdMatch
    public HttpResponse<UserWithSharedUsersDto> findById(Principal principal, UUID id) {
        return HttpResponse.ok(Mapper.toUserWithSharedUsersDto(service.findOrThrowError(id)));
    }

    @Get("/{id}/locations")
    @Transactional
    @CheckPrincipalIdMatch
    public HttpResponse<UserWithSharedLocationsDto> getLocations(Principal principal, UUID id) {
        return HttpResponse.ok(Mapper.toUserWithSharedLocationsDto(service.findOrThrowError(id)));
    }

    @Put("/")
    @CheckPrincipalIdMatch
    public HttpResponse<SimpleUserWithLocationDto> update(Principal principal, @Body SimpleUserWithLocationDto dto) {
        return HttpResponse.ok(Mapper.toSimpleUserWithLocationDto(service.update(dto.getId(), user -> {
            user.setName(dto.getName());
            user.setPosition(dto.getPosition());
        })));
    }

    @Put("/simple-data")
    @CheckPrincipalIdMatch
    public HttpResponse<SimpleUserDto> updateSimpleUserData(Principal principal, @Body SimpleUserDto dto) {
        return HttpResponse
                .ok(Mapper.toSimpleUserDto(service.update(dto.getId(), user -> user.setName(dto.getName()))));
    }

    @Delete("/{id}")
    @Transactional
    @CheckPrincipalIdMatch
    public HttpResponse<?> delete(Principal principal, UUID id) { // NOSONAR
        service.delete(id);
        return HttpResponse.seeOther(URI.create(LogoutController.APP_LOGOUT));
    }

    @Put("/{id}/share-with/{contactId}")
    @Transactional
    @CheckPrincipalIdMatch
    public HttpResponse<UserWithSharedUsersDto> shareWith(Principal principal, UUID id, UUID contactId) {
        return HttpResponse.ok(Mapper.toUserWithSharedUsersDto(service.shareWith(id, contactId)));
    }

    @Delete("/{id}/share-with/{contactId}")
    @Transactional
    @CheckPrincipalIdMatch
    public HttpResponse<UserWithSharedUsersDto> stopShareWith(Principal principal, UUID id, UUID contactId) {
        return HttpResponse.ok(Mapper.toUserWithSharedUsersDto(service.stopShareWith(id, contactId)));
    }

    @Get("/search")
    @Transactional
    public HttpResponse<List<SimpleUserDto>> findByName(@QueryValue(value = "name") String name) {
        return HttpResponse
                .ok(service.findByName(name).stream().map(Mapper::toSimpleUserDto).collect(Collectors.toList()));
    }

    @Get("/check-name")
    public Map<String, Boolean> validateUserName(@QueryValue(value = "name") String name) {
        return Collections.singletonMap("valid", service.validateUserName(name));
    }
}
