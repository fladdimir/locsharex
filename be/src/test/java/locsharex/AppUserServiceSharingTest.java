package locsharex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest(transactional = true, rollback = false)
class AppUserServiceSharingTest {

    @Inject
    AppUserService service;

    @Inject
    AppUserRepository repository;

    private UUID id1;
    private UUID id2;
    private UUID id3;

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
        assertThat(repository.count()).isZero();
        id1 = service.findOrCreate("issuerName", "providerSub1", "user1").getId();
        id2 = service.findOrCreate("issuerName", "providerSub2", "user2").getId();
        id3 = service.findOrCreate("issuerName", "providerSub3", "user3").getId();
        service.shareWith(id1, id2);
        service.shareWith(id2, id1);
        service.shareWith(id3, id1);
        service.shareWith(id3, id2);
        repository.flush();
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void testSharing1() {
        AppUser user1 = repository.findById(id1).get();
        assertThat(user1.getSharedWith()).extracting(AppUser::getId).containsExactlyInAnyOrder(id2);
        assertContains(user1.getSharedWith(), id2);
        assertContains(user1.getSharedBy(), id2, id3);
    }

    @Test
    void testSharing2() {
        AppUser user2 = repository.findById(id2).get();
        assertContains(user2.getSharedWith(), id1);
        assertContains(user2.getSharedBy(), id1, id3);
    }

    @Test
    void testSharing3() {
        AppUser user3 = repository.findById(id3).get();
        assertContains(user3.getSharedWith(), id1, id2);
        assertContains(user3.getSharedBy());
    }

    @Test
    void testStopSharing1() {
        service.stopShareWith(id1, id2);
        AppUser user1 = repository.findById(id1).get();
        AppUser user2 = repository.findById(id2).get();
        AppUser user3 = repository.findById(id3).get();
        assertContains(user1.getSharedWith());
        assertContains(user1.getSharedBy(), id2, id3);
        assertContains(user2.getSharedWith(), id1);
        assertContains(user2.getSharedBy(), id3);
        assertContains(user3.getSharedWith(), id1, id2);
        assertContains(user3.getSharedBy());
    }

    @Test
    void testDelete1() {
        service.delete(id1);
        assertThat(repository.findById(id1)).isEmpty();
        AppUser user2 = repository.findById(id2).get();
        AppUser user3 = repository.findById(id3).get();
        assertContains(user2.getSharedWith());
        assertContains(user2.getSharedBy(), id3);
        assertContains(user3.getSharedWith(), id2);
        assertContains(user3.getSharedBy());
    }

    @Test
    void testSharingWithNull() {
        UUID randomUUID = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> service.shareWith(randomUUID, null));
    }

    @Test
    void testSharingWithSelf() {
        UUID randomUUID = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> service.shareWith(randomUUID, randomUUID));
    }

    private void assertContains(Set<AppUser> set, UUID... ids) {
        assertThat(set).extracting(AppUser::getId).containsExactlyInAnyOrder(ids);
    }

}
