package myproject.repositories;

import myproject.models.Tblemployee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.security.PermitAll;
import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Tblemployee, Integer> {

    //Returns a list of all the employees
    @Query(value = "SELECT * FROM tblemployee", nativeQuery = true)
    List<Tblemployee> findAllEmployee();

    //Returns the user for an employee
    @Query(value = "SELECT * FROM tblemployee e JOIN tblusers u ON e.id=u.employee_id WHERE username = :username", nativeQuery = true)
    List<Tblemployee> findUserForEmployee(@Param("username") String user);

    @Query(value = "SELECT * FROM tblemployee WHERE id = :id ", nativeQuery = true)
    Tblemployee findEmployeeById(@Param("id") int id);

    @Modifying
    @Query(value = "UPDATE tblemployee SET name = :name, email = :email, address = :address, phone = :phone WHERE id = :id", nativeQuery = true)
    void updateEmployee(@Param("name") String name, @Param("email") String email, @Param("address") String address, @Param("phone") String phone, @Param("id") int id);


}
