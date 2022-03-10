package locsharex;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import locsharex.AppUser.Position;
import locsharex.AppUserDtos.Mapper;
import locsharex.AppUserDtos.UserWithSharedLocationsDto;

class AppUserMapperTest {

    @Test
    void testUsersWithLocationsMapper() {
        AppUser user1 = createUser();
        AppUser user2 = createUser();

        UserWithSharedLocationsDto dto1 = Mapper.toUserWithSharedLocationsDto(user1);
        UserWithSharedLocationsDto dto2 = Mapper.toUserWithSharedLocationsDto(user2);
        assertThat(dto1.getOthers()).isEmpty();
        assertThat(dto2.getOthers()).isEmpty();

        user1.shareWith(user2);
        dto1 = Mapper.toUserWithSharedLocationsDto(user1);
        dto2 = Mapper.toUserWithSharedLocationsDto(user2);
        assertThat(dto1.getOthers()).isEmpty();
        assertThat(dto2.getOthers()).isEmpty();

        user2.shareWith(user1);
        dto1 = Mapper.toUserWithSharedLocationsDto(user1);
        dto2 = Mapper.toUserWithSharedLocationsDto(user2);
        assertThat(dto1.getOthers()).containsExactly(Mapper.toSimpleUserWithLocationDto(user2));
        assertThat(dto2.getOthers()).containsExactly(Mapper.toSimpleUserWithLocationDto(user1));
    }

    private AppUser createUser() {
        AppUser user1 = new AppUser();
        user1.setId(UUID.randomUUID());
        user1.setIssuerName("issuerName");
        user1.setName("name");
        user1.setPosition(new Position(1d, 1d));
        user1.setSub("sub");
        return user1;
    }

}
