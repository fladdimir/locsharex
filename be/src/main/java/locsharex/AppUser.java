package locsharex;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
@Table(name = "APP_USER", indexes = { @Index(columnList = "name", unique = true),
        @Index(columnList = "sub, issuerName", unique = true) })
public class AppUser {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id; // internal id, pk

    @Column(nullable = false)
    private String issuerName; // e.g. "keycloak"

    @Column(nullable = false)
    private String sub; // provider-assigned user-id

    @Column(name = "name", nullable = false)
    private String name; // app-internally shown name of the user

    public void setName(String name) {
        new AppUsernameValidator().validateOrThrow(name);
        this.name = name;
    }

    @Embedded
    private Position position;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<AppUser> sharedWith = new HashSet<>(); // users with whom this user shared his position

    public Set<AppUser> getSharedWith() {
        return Collections.unmodifiableSet(sharedWith);
    }

    @Setter(AccessLevel.NONE)
    @ManyToMany(mappedBy = "sharedWith", fetch = FetchType.LAZY)
    private Set<AppUser> sharedBy = new HashSet<>(); // all users who shared their position with this user

    public Set<AppUser> getSharedBy() {
        return Collections.unmodifiableSet(sharedBy);
    }

    public void shareWith(AppUser contact) {
        sharedWith.add(Objects.requireNonNull(contact));
        contact.sharedBy.add(this); // sychronize mapped side
    }

    public void stopShareWith(AppUser contact) {
        sharedWith.remove(Objects.requireNonNull(contact));
        contact.sharedBy.remove(this); // sychronize mapped side
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class Position {
        private Double lat;
        private Double lng;
    }
}
