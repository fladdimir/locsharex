package locsharex;

import static java.util.Objects.requireNonNull;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Singleton;

import io.micronaut.core.util.StringUtils;

@Singleton
public class AppUserService {

    private final AppUserRepository repository;
    private final AppUsernameValidator usernameValidator;

    public AppUserService(AppUserRepository repository, AppUsernameValidator usernameValidator) {
        this.repository = repository;
        this.usernameValidator = usernameValidator;
    }

    public AppUser findOrThrowError(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Could not find AppUser with id: " + id));
    }

    public AppUser findOrThrowError(Principal principal) {
        return findOrThrowError(UUID.fromString(principal.getName()));
    }

    public AppUser findOrCreate(String issuerName, String providerSub, String preferredUsername) {
        Optional<AppUser> existingUser = repository.findByIssuerNameAndSub(issuerName, providerSub);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        AppUser newUser = new AppUser();
        newUser.setIssuerName(issuerName);
        newUser.setSub(providerSub);
        newUser.setName(getUniqueUsername(preferredUsername));
        return repository.save(newUser);
    }

    private String getUniqueUsername(String preferredUsername) {
        String uniqueUsername = preferredUsername;
        Integer c = 1;
        while (repository.countByName(uniqueUsername) > 0) {
            if (c > 1) // remove existing postfix
                uniqueUsername = uniqueUsername.substring(0, uniqueUsername.length() - c.toString().length());
            c++;
            uniqueUsername += c; // NOSONAR
        }
        return uniqueUsername;
    }

    public AppUser update(UUID id, Consumer<AppUser> updateProperties) {
        var user = findOrThrowError(id);
        updateProperties.accept(user);
        return repository.update(user);
    }

    public void delete(UUID id) {
        var user = findOrThrowError(id);
        List.of(user.getSharedBy().toArray(new AppUser[] {})).forEach(sharer -> sharer.stopShareWith(user));
        List.of(user.getSharedWith().toArray(new AppUser[] {})).forEach(user::stopShareWith);
        repository.delete(user);
    }

    public AppUser shareWith(UUID id, UUID contactId) {
        if (requireNonNull(id).equals(requireNonNull(contactId)))
            throw new IllegalArgumentException();

        var user = findOrThrowError(id);
        var contact = findOrThrowError(contactId);
        user.shareWith(contact);
        repository.save(contact);
        return repository.save(user);
    }

    public AppUser stopShareWith(UUID id, UUID contactId) {
        var user = findOrThrowError(id);
        var contact = findOrThrowError(contactId);
        user.stopShareWith(contact);
        repository.save(contact);
        return repository.save(user);
    }

    public List<AppUser> findByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return List.of();
        }
        return repository.findTop10ByNameContainingIgnoreCaseOrderByName(name);
    }

    public boolean validateUserName(String name) {
        return usernameValidator.validate(name) && repository.countByName(name) == 0;
    }
}
