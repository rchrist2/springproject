package myproject.repositories;

import myproject.models.Tblusers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<Tblusers, Integer> {

    //Returns a user if they exist in the database
    @Query(value = "SELECT * FROM tblUsers WHERE Username = :username AND Password = :password", nativeQuery = true)
    Tblusers findUserLogin(@Param("username") String user, @Param("password") String password);

    //Find by username only
    @Query(value = "SELECT * FROM tblUsers WHERE Username = :username", nativeQuery = true)
    Tblusers findUsername(@Param("username") String user);

}
