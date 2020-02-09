package myproject.repositories;

import myproject.models.TblUsers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<TblUsers, Integer> {

    //Returns a user if they exist in the database
    @Query(value = "SELECT * FROM tblUsers WHERE Username = :username AND Password = :password", nativeQuery = true)
    TblUsers findUserLogin(@Param("username") String user, @Param("password") String password);

    //Find by username only
    @Query(value = "SELECT * FROM tblUsers WHERE Username = :username", nativeQuery = true)
    TblUsers findUsername(@Param("username") String user);

}
