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

    @Query(value = "SELECT e.email FROM tblemployee e EXCEPT SELECT e.email FROM tblusers u JOIN tblemployee e ON u.employee_id = e.id", nativeQuery = true)
    List<String> listOfEmployeeWithoutAccounts();

    @Query(value = "SELECT e.email FROM tblemployee e JOIN tblusers u ON e.id = u.employee_id WHERE u.user_id = :name", nativeQuery = true)
    String findEmailFromUser(@Param("name") int name);

/*    @Query(value = "SELECT password FROM tblusers WHERE employee_id = :empId", nativeQuery = true)
    String findPasswordFromUserId(@Param("empId") int employeeId);*/

    @Query(value = "SELECT salt_password FROM tblusers WHERE employee_id = :empId", nativeQuery = true)
    byte[] findSaltFromUserId(@Param("empId") int empId);

    @Query(value = "SELECT hashed_password FROM tblusers WHERE employee_id = :empId", nativeQuery = true)
    String findHashFromUserId(@Param("empId") int empId);

    @Modifying
    @Query(value = "UPDATE tblUsers " +
            "SET username = :username, " +
            "password = :password, " +
            "salt_password = :salt, " +
            "hashed_password = :hash_password, " +
            "employee_id = :employee_id " +
            "WHERE user_id = :userid", nativeQuery = true)
    void updateUserAccount(@Param("username") String user,
                           @Param("password") String pass,
                           @Param("employee_id") int empId,
                           @Param("salt") byte[] salt,
                           @Param("hash_password") String hashed_password,
                           @Param("userid") int userId
    );

    @Modifying
    @Query(value = "UPDATE tblusers " +
            "SET salt_password = :salt, " +
            "hashed_password = :hashed_password " +
            "WHERE user_id = :userid", nativeQuery = true)
    void updateUserPassword(@Param("salt") byte[] salt,
                            @Param("hashed_password") String hashed_password,
                            @Param("userid") int userId
    );

    @Modifying
    @Query(value = "DELETE FROM tblUsers WHERE user_id = :id", nativeQuery = true)
    void deleteUser(@Param("id") int id);

    @Query(value = "SELECT * FROM tblusers", nativeQuery = true)
    List<Tblusers> findAll();

}
