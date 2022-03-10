package locsharex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import locsharex.AppUserDtos.SimpleUserDto;

@Property(name = "micronaut.http.client.followRedirects", value = "false")
@MicronautTest(transactional = true, environments = { "testAuth", "static" }, rollback = false)
class AppUserControllerTest {

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    AppUserRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    void testLoginInfoLoggedOut() {
        Map<?, ?> result = client.toBlocking().retrieve(HttpRequest.GET("/api/users/login-info"), Map.class);
        assertThat(result).isEmpty();
    }

    @Test
    void testLoginTestUser() throws MalformedURLException {
        assertThat(repository.findAll()).isEmpty();
        Cookie cookie = loginSherlock();
        assertThat(repository.findAll()).hasSize(1);
        assertThat(getUserId(cookie)).isNotNull(); // user is logged in
    }

    @Test
    void testLogoutTestUser() throws MalformedURLException {
        Cookie cookie = loginSherlock();
        assertThat(repository.findAll()).hasSize(1);
        assertThat(getUserId(cookie)).isNotNull(); // user is logged in
        HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET("/api/app-logout").cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.SEE_OTHER);
    }

    @Test
    void testGet() {
        Cookie cookie = loginSherlock();
        String userId = getUserId(cookie);
        assertThat(repository.findAll().get(0).getId()).hasToString(userId);
        HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET("/api/users/" + userId).cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
    }

    @Test
    void testGetLocations() {
        Cookie cookie = loginSherlock();
        String userId = getUserId(cookie);
        assertThat(repository.findAll().get(0).getId()).hasToString(userId);
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/api/users/" + userId + "/locations").cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
    }

    @Test
    void testSearch() {
        Cookie cookie = loginSherlock();
        String userId = getUserId(cookie);
        assertThat(repository.findAll().get(0).getId()).hasToString(userId);
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/api/users/search?name=xxx").cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
    }

    @Test
    void testValidateName() {
        Cookie cookie = loginSherlock();
        String userId = getUserId(cookie);
        assertThat(repository.findAll().get(0).getId()).hasToString(userId);
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/api/users/check-name?name=xxx").cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
    }

    @Test
    void testPut() {
        Cookie cookie = loginSherlock();
        String userId = getUserId(cookie);
        String newName = "newName";
        Object body = new SimpleUserDto(UUID.fromString(userId), newName);
        HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.PUT("/api/users", body).cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
        assertThat(repository.findById(UUID.fromString(userId)).get().getName()).isEqualTo(newName);
    }

    @Test
    void testPutSimpleData() {
        Cookie cookie = loginSherlock();
        String userId = getUserId(cookie);
        String newName = "newName";
        Object body = new SimpleUserDto(UUID.fromString(userId), newName);
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/api/users/simple-data", body).cookie(cookie));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
        assertThat(repository.findById(UUID.fromString(userId)).get().getName()).isEqualTo(newName);
    }

    @Test
    void testPutUnauthorized() {
        Cookie cookie = loginSherlock();
        assertThat(repository.findAll()).hasSize(1);
        String userId = getUserId(cookie);
        String replacer = userId.substring(0, 1) == "0" ? "1" : "0";
        Object body = new SimpleUserDto(UUID.fromString(replacer + userId.substring(1)), "newName");
        BlockingHttpClient blockingClient = client.toBlocking();
        HttpRequest<?> request = HttpRequest.PUT("/api/users", body).cookie(cookie);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> blockingClient.exchange(request));
        assertStatusEquality(thrown.getStatus(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testDeletion() {
        Cookie cookie = loginSherlock();
        assertThat(repository.findAll()).hasSize(1);
        String userId = getUserId(cookie);
        client.toBlocking().exchange(HttpRequest.DELETE("/api/users/" + userId).cookie(cookie));
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void testShare() {
        Cookie cookie1 = loginSherlock();
        Cookie cookie2 = loginWatson();
        assertThat(repository.findAll()).hasSize(2);
        String userId1 = getUserId(cookie1);
        String userId2 = getUserId(cookie2);
        HttpResponse<?> response = client.toBlocking().exchange(
                HttpRequest.PUT("/api/users/" + userId1 + "/share-with/" + userId2, Map.of("a", "b")).cookie(cookie1));
        assertStatusEquality(response.getStatus(), HttpStatus.OK);
        assertThat(repository.findById(UUID.fromString(userId1)).get().getSharedWith())
                .containsExactly(repository.findById(UUID.fromString(userId2)).get());
        assertThat(repository.findById(UUID.fromString(userId2)).get().getSharedBy())
                .containsExactly(repository.findById(UUID.fromString(userId1)).get());

        HttpResponse<?> response2 = client.toBlocking()
                .exchange(HttpRequest.DELETE("/api/users/" + userId1 + "/share-with/" + userId2).cookie(cookie1));
        assertStatusEquality(response2.getStatus(), HttpStatus.OK);
        assertThat(repository.findById(UUID.fromString(userId1)).get().getSharedWith())
                .containsExactly(repository.findById(UUID.fromString(userId2)).get());
        assertThat(repository.findById(UUID.fromString(userId2)).get().getSharedBy())
                .containsExactly(repository.findById(UUID.fromString(userId1)).get());
    }

    private Cookie loginSherlock() {
        return login(new UsernamePasswordCredentials("Sherlock", "password"));
    }

    private Cookie loginWatson() {
        return login(new UsernamePasswordCredentials("Watson", "password"));
    }

    private Cookie login(UsernamePasswordCredentials creds) {
        HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("/api/login", creds));
        assertStatusEquality(response.status(), HttpStatus.SEE_OTHER);
        Cookie jwtCookie = response.getCookies().get("JWT");
        assertThat(jwtCookie.isHttpOnly()).isTrue();
        return jwtCookie;
    }

    private String getUserId(Cookie cookie) {
        Map<?, ?> result = client.toBlocking().retrieve(HttpRequest.GET("/api/users/login-info").cookie(cookie),
                Map.class);
        return (String) result.get("userId");
    }

    void assertStatusEquality(HttpStatus status1, HttpStatus status2) {
        assertThat(status1.getCode()).isEqualTo(status2.getCode());
    }

}
