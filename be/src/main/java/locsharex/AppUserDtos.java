package locsharex;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Introspected;
import locsharex.AppUser.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@Introspected // necessary for native image
@UtilityClass
public class AppUserDtos {

    public static interface UserIdHolder {
        public UUID getId();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Introspected
    public static class SimpleUserDto implements UserIdHolder {
        private UUID id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Introspected
    public static class SimpleUserWithLocationDto implements UserIdHolder {
        private UUID id;
        private String name;
        private Position position;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Introspected
    public static class UserWithSharedLocationsDto implements UserIdHolder {
        private SimpleUserWithLocationDto self;
        private Set<SimpleUserWithLocationDto> others;

        @Override
        public UUID getId() {
            return self.getId();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Introspected
    public static class UserWithSharedUsersDto implements UserIdHolder {
        private UUID id;
        private String name;
        private Set<SimpleUserDto> sharedWith;
        private Set<SimpleUserDto> sharedBy;
    }

    @UtilityClass
    public static class Mapper {
        public static UserWithSharedUsersDto toUserWithSharedUsersDto(AppUser user) {
            return new UserWithSharedUsersDto(user.getId(), user.getName(),
                    user.getSharedWith().stream().map(Mapper::toSimpleUserDto).collect(Collectors.toSet()),
                    user.getSharedBy().stream().map(Mapper::toSimpleUserDto).collect(Collectors.toSet()));
        }

        public static SimpleUserDto toSimpleUserDto(AppUser user) {
            return new SimpleUserDto(user.getId(), user.getName());
        }

        public static SimpleUserWithLocationDto toSimpleUserWithLocationDto(AppUser user) {
            return new SimpleUserWithLocationDto(user.getId(), user.getName(), user.getPosition());
        }

        public static UserWithSharedLocationsDto toUserWithSharedLocationsDto(AppUser user) {
            var self = toSimpleUserWithLocationDto(user);
            var others = new HashSet<>(user.getSharedBy());
            // only reveal to whom can also see you..
            others.retainAll(user.getSharedWith());
            return new UserWithSharedLocationsDto(self,
                    others.stream().map(Mapper::toSimpleUserWithLocationDto).collect(Collectors.toSet()));
        }
    }
}
