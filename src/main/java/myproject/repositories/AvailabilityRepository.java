package myproject.repositories;

import myproject.models.TblAvailability;
import myproject.models.TblUsers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends CrudRepository<TblAvailability, Integer> {
    @Query(value = "SELECT * FROM tblAvailability a JOIN tblUsers u ON a.User_ID=u.User_ID WHERE Username = :username", nativeQuery = true)
    TblAvailability findTblAvailabilitiesByUser(@Param("username") String user);
}
