package locsharex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
class AppUserServiceTest {

    @Inject
    AppUserService service;

    @Inject
    AppUserRepository repository;

    @BeforeEach
    void cleanupDb() {
        repository.deleteAll();
        assertThat(repository.count()).isZero();
    }

    @Test
    void testCreation() {
        service.findOrCreate("issuerName", "providerSub", "preferredUsername");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void testCreation3x() {
        String preferredUsername = "preferredUsername";
        AppUser user1 = service.findOrCreate("issuerName", "providerSub1", preferredUsername);
        AppUser user2 = service.findOrCreate("issuerName", "providerSub2", preferredUsername);
        AppUser user3 = service.findOrCreate("issuerName", "providerSub3", preferredUsername);
        assertThat(repository.count()).isEqualTo(3);
        assertThat(user1.getName()).isEqualTo(preferredUsername);
        assertThat(user2.getName()).isEqualTo(preferredUsername + "2");
        assertThat(user3.getName()).isEqualTo(preferredUsername + "3");
    }

    @Test
    void testCreationSame() {
        String providerSub = "providerSub1";
        AppUser user1 = service.findOrCreate("issuerName", providerSub, "preferredUsername");
        AppUser user2 = service.findOrCreate("issuerName", providerSub, "preferredUsername");
        assertThat(repository.count()).isEqualTo(1);
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testFind() {
        AppUser user1 = service.findOrCreate("issuerName", "providerSub", "preferredUsername");
        AppUser user2 = service.findOrThrowError(user1.getId());
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testFindOrThrow() {
        UUID randomUUID = UUID.randomUUID();
        assertThrows(NoSuchElementException.class, () -> service.findOrThrowError(randomUUID));
    }

    @Test
    void testFindByName3x() {
        String preferredUsername = "preferredUsername";
        service.findOrCreate("issuerName", "providerSub1", preferredUsername);
        service.findOrCreate("issuerName", "providerSub2", preferredUsername);
        service.findOrCreate("issuerName", "providerSub3", preferredUsername);
        assertThat(service.findByName(preferredUsername)).hasSize(3);
        assertThat(service.findByName("differentName")).isEmpty();
        assertThat(service.findByName("")).isEmpty();
    }

    @Test
    void validateUsernames() {
        String preferredUsername = "preferredUsername";
        service.findOrCreate("issuerName", "providerSub1", preferredUsername);
        assertThat(service.validateUserName(preferredUsername)).isFalse();// existing
        assertThat(service.validateUserName(preferredUsername + "2")).isTrue();
        assertThat(service.validateUserName("cr@zyName")).isFalse();
        assertThat(service.validateUserName("12345")).isTrue();
        assertThat(service.validateUserName(null)).isFalse();
    }

}
