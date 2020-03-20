package myproject.repositories;

import myproject.models.TblRoles;
import myproject.models.Tblusers;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<Tblusers, Integer> {

    //Returns a user if they exist in the database
    @Query(value = "SELECT * FROM tblUsers WHERE Username = :username AND Password = :password", nativeQuery = true)
    Tblusers findUserLogin(@Param("username") String user, @Param("password") String password);

    //Find by username only
    @Query(value = "SELECT * FROM tblUsers WHERE Username = :username", nativeQuery = true)
    Tblusers findUsername(@Param("username") String user);

    @Modifying
    @Query(value = "DELETE FROM tblUsers WHERE user_id = :id", nativeQuery = true)
    void deleteUser(@Param("id") int id);

    @Query(value = "SELECT * FROM tblusers", nativeQuery = true)
    List<Tblusers> findAll();

}
