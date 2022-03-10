package locsharex;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByIssuerNameAndSub(String issuerName, String sub);

    @Query(value = "SELECT * " //
            + "FROM app_user " //
            + "WHERE lower(name) like lower(concat('%', :s,'%')) " //
            + "ORDER BY name ASC " //
            + "LIMIT 10 " //
            , nativeQuery = true)
    List<AppUser> findTop10ByNameContainingIgnoreCaseOrderByName(String s);

    int countByName(String s);

}
